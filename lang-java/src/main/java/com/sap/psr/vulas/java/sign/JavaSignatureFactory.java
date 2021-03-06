package com.sap.psr.vulas.java.sign;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sap.psr.vulas.Construct;
import com.sap.psr.vulas.FileAnalysisException;
import com.sap.psr.vulas.FileAnalyzer;
import com.sap.psr.vulas.FileAnalyzerFactory;
import com.sap.psr.vulas.java.JavaClassId;
import com.sap.psr.vulas.java.JavaId;
import com.sap.psr.vulas.java.decompiler.IDecompiler;
import com.sap.psr.vulas.java.decompiler.ProcyonDecompiler;
import com.sap.psr.vulas.shared.enums.ConstructType;
import com.sap.psr.vulas.shared.enums.ProgrammingLanguage;
import com.sap.psr.vulas.shared.json.model.ConstructId;
import com.sap.psr.vulas.shared.util.FileUtil;
import com.sap.psr.vulas.shared.util.VulasConfiguration;
import com.sap.psr.vulas.sign.Signature;
import com.sap.psr.vulas.sign.SignatureChange;
import com.sap.psr.vulas.sign.SignatureFactory;

import javassist.CtClass;
import javassist.bytecode.ClassFile;

/**
 * Creates construct signatures for Java methods and constructors (other types are not supported).
 * Attention: The signature creation for methods and constructors in nested classes does not work
 * properly. The reason is that the decompiler does not properly created Java class declarations
 * (see https://bitbucket.org/mstrobel/procyon/issues/283).
 */
public class JavaSignatureFactory implements SignatureFactory {

	private static final Log log = LogFactory.getLog(JavaSignatureFactory.class);

	/**
	 * Cache of contructs, so that the decompilation and parsing must not be done over and over again.
	 */
	final Map<ConstructId, Construct> sourceCache = new HashMap<ConstructId, Construct>();

	/**
	 * Cache of contructs, so that the decompilation and parsing must not be done over and over again.
	 */
	final Map<ConstructId, Construct> compiledCache = new HashMap<ConstructId, Construct>();
	
	/**
	 * Java Decompiler, e.g., {@link ProcyonDecompiler}.
	 */
	final IDecompiler decompiler = new ProcyonDecompiler();

	/**
	 * {@inheritDoc}
	 *
	 * Returns true if the given {@link ConstructId} is of type Java method or Java constructor.
	 */
	@Override
	public boolean isSupportedConstructId(ConstructId _id) {
		return 	_id!=null &&
				ProgrammingLanguage.JAVA.equals(_id.getLang()) &&
				( ConstructType.METH.equals(_id.getType()) || ConstructType.CONS.equals(_id.getType()));
	}	

	/**
	 * {@inheritDoc}
	 *
	 * Creates the construct signature on the basis of the source code provided by {@link Construct#getContent()}.
	 */
	@Override
	public Signature createSignature(Construct _c) {
		if(_c==null) return null;

		ASTConstructBodySignature ast_signature = null;
		try {
			ast_signature = new ASTConstructBodySignature(_c);
			final String construct_name = ((JavaId)_c.getId()).getSimpleName(); //this.extractConstructName(_c.getId());
			if(construct_name!=null && !construct_name.equals("")) //!=" ")
				ast_signature.convertConstructBody(construct_name);
		}
		catch(Exception e){
			JavaSignatureFactory.log.error(e.getMessage().toString());
		}
		return ast_signature;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Creates the construct signature on the basis of a given Java source file.
	 * 
	 * @see JavaSignatureFactory#createFromSourceCache(ConstructId)
	 */
	@Override
	public Signature createSignature(ConstructId _cid, File _java_file) {
		Signature signature = null;

		if(_java_file.getName().endsWith(".java")) {
			// Is the construct body cached?
			signature = this.createFromSourceCache(_cid);

			// No, it is not cached
			if(signature==null) {

				try {
					// Parse the Java file in order to identify all its constructs
					final FileAnalyzer fa = FileAnalyzerFactory.buildFileAnalyzer(_java_file);

					// Get the construct we're interested at
					final Construct c = fa.getConstruct(JavaId.toCoreType(_cid));

					// Create the signature
					if(c!=null) {
						// Fill cache
						this.sourceCache.put(_cid,  c);
						signature = this.createSignature(c);
					}
					else
						JavaSignatureFactory.log.error("Construct [" + _cid + "] not found in Java source file [" + _java_file + "]");
				} catch (IllegalArgumentException e) {
					JavaSignatureFactory.log.error(e.getMessage());
				} catch (FileAnalysisException e) {
					JavaSignatureFactory.log.error(e.getMessage());
				}
			}
		}
		else if(_java_file.getName().endsWith(".class")) {
			// Is the construct body cached?
			signature = this.createFromCompiledCache(_cid);

			// No, it is not cached
			if(signature==null) {

				try {
					final String filename_without_extension = FilenameUtils.removeExtension(_java_file.getName());
					File java_source_file = new File(_java_file.getParent(), filename_without_extension + ".java");

					//Check if the java File already exists before decompiling
					if(!java_source_file.exists())
						java_source_file = decompiler.decompileClassFile(_java_file);

					// Now the same as for java files

					// Parse the Java file in order to identify all its constructs
					final FileAnalyzer fa = FileAnalyzerFactory.buildFileAnalyzer(java_source_file);

					// Get the construct we're interested at
					final Construct c = fa.getConstruct(JavaId.toCoreType(_cid));

					// Create the signature
					if(c!=null) {
						// Fill cache
						this.compiledCache.put(_cid,  c);
						signature = this.createSignature(c);
					}
					else
						JavaSignatureFactory.log.error("Construct [" + _cid + "] not found in Java source file [" + _java_file + "]");
				} catch (IllegalArgumentException e) {
					JavaSignatureFactory.log.error(e.getMessage());
				} catch (FileAnalysisException e) {
					JavaSignatureFactory.log.error(e.getMessage());
				}
			}
		}
		else {
			JavaSignatureFactory.log.error("File extension of [" + _java_file.getName() + "] not supported");
		}

		return signature;
	}

	/**
	 * Returns true if the signature creation is supported for the given {@link ConstructId}. This depends
	 * on whether the ID's definition context can be obtained, and whether the latter is a nested class.
	 * @param _cid
	 */
	static final boolean isSupported(ConstructId _cid, boolean _throw_exception) throws IllegalArgumentException {
		boolean supported = true;

		// Get and check the definition context of the construct whose signature we're about to create
		final JavaClassId class_id = (JavaClassId)JavaId.toCoreType(_cid).getDefinitionContext();

		// Cannot get the def context
		if(class_id==null) {
			supported = false;
			if(_throw_exception)
				throw new IllegalArgumentException("No definition context for construct [" + _cid.getQname() + "]");
		}
		// Nested class
		else if(class_id.isNestedClass()) {
			supported = false;
			JavaSignatureFactory.log.error("Nested classes are not yet supported, cannot create signature for [" + _cid.getQname() + "]");
			if(_throw_exception)
				throw new IllegalArgumentException("Nested classes are not yet supported, cannot create signature for [" + _cid.getQname() + "]");
		}

		return supported;
	}

	/**
	 * Reads the construct from cache and creates the signature.
	 */
	private Signature createFromSourceCache(ConstructId _cid) {
		Signature signature = null;
		if(this.sourceCache.containsKey(_cid))
			signature = this.createSignature(this.sourceCache.get(_cid));
		return signature;
	}

	/**
	 * Reads the construct from cache and creates the signature.
	 */
	private Signature createFromCompiledCache(ConstructId _cid) {
		Signature signature = null;
		if(this.compiledCache.containsKey(_cid))
			signature = this.createSignature(this.compiledCache.get(_cid));
		return signature;
	}

	/**
	 * Reads the bytecode of the given {@link CtClass} and writes it to a temporary file.
	 * @param _cid
	 * @param _class
	 * @return the temporary file to which the byte code has been written
	 * @throws IOException
	 */
	private Path writeBytesToTmpFile(JavaClassId _cid, CtClass _class) throws IOException {
		final Path class_file = Files.createTempFile(VulasConfiguration.getGlobal().getTmpDir(), _cid.getQualifiedName(), ".class", new FileAttribute[] {});
		FileUtil.writeToFile(class_file.toFile(), this.readBytes(_class));
		return class_file;
	}	

	/**
	 * Reads the byte code of the given {@link CtClass}.
	 * @param _class
	 * @return
	 * @throws IOException
	 */
	private byte[] readBytes(CtClass _class) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final DataOutputStream dos = new DataOutputStream(bos);
		final ClassFile cf = _class.getClassFile();
		cf.write(dos);
		dos.flush();
		return bos.toByteArray();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Computes a so-called signature change, i.e., changes required to transform the signature of the first given {@link Construct} into the signature of the second.
	 */
	@Override
	public SignatureChange computeChange(Construct _from, Construct _to) {
		ASTSignatureChange change = null;
		if(_from!=null && _to!=null) {
			final ASTConstructBodySignature from_sign = (ASTConstructBodySignature)this.createSignature(_from);
			final ASTConstructBodySignature to_sign   = (ASTConstructBodySignature)this.createSignature(_to);

			// Note: The call in class ASTSignatureChange.getModifications() { mDistiller.extractClassifiedSourceCodeChanges(defSignatureNode, fixSignatureNode);}
			// changes the from version into the to version, i.e., afterwards both will look the same
			if(from_sign!=null && to_sign!=null) {
				change = new ASTSignatureChange(from_sign, to_sign);
				change.getModifications();
			}
		}
		return change;
	}
}