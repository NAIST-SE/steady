package com.sap.psr.vulas.java;

import com.sap.psr.vulas.ConstructId;


/**
 * Identifies a Java enum.
 */
public class JavaEnumId extends JavaId {
	
	private JavaId declarationContext = null;
	private String enumName = null;
	
	/**
	 * Constructor for creating the identifier of an enum.
	 *
	 * @param _declaration_ctx a {@link com.sap.psr.vulas.java.JavaId} object.
	 * @param _simple_name a {@link java.lang.String} object.
	 */
	public JavaEnumId(JavaId _declaration_ctx, String _simple_name) {
		super(JavaId.Type.ENUM);
		this.declarationContext = _declaration_ctx;
		this.enumName = _simple_name;
	}
	
	/**
	 * Returns true if the enum is not directly declared within a package, but within another construct like a class or interface.
	 *
	 * @return a boolean.
	 */
	public boolean isNested() {
		return this.declarationContext!=null && !this.declarationContext.getType().equals(JavaId.Type.PACKAGE);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * Returns the fully qualified class name, i.e., including the name of the package in which the class is defined.
	 */
	@Override
	public String getQualifiedName() {
		if(this.declarationContext!=null) {
			if(!this.isNested())
				return this.declarationContext.getQualifiedName() + "." + this.enumName;
			else
				return this.declarationContext.getQualifiedName() + "$" + this.enumName;
		} else {
			return this.enumName;
		}
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * Returns a class name that is unique within the package in which the class is defined.
	 * In case of nested classes, the names of parent classes will be included (e.g., OuterClass$InnerClass).
	 * @return the class name including the names of parent classes (if any)
	 */
	@Override
	public String getName() {
		if(this.declarationContext!=null) {
			if(!this.isNested())
				return this.enumName;
			else
				return this.declarationContext.getName() + "$" + this.enumName;
		} else {
			return this.enumName;
		}
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * Returns the class name without considering any context.
	 * @return the simple class name w/o context information
	 */
	@Override
	public String getSimpleName() { return this.enumName; }
	
	/**
	 * {@inheritDoc}
	 *
	 * Returns the name of the Java package in which the class or nested class is defined. Returns null if a class is defined outside of a package.
	 * @return a Java package name
	 */
	@Override
	public ConstructId getDefinitionContext() { return this.getJavaPackageId(); }
	
	/**
	 * {@inheritDoc}
	 *
	 * Returns the Java package in the context of which the construct is defined.
	 */
	@Override
	public JavaPackageId getJavaPackageId() {
		if(this.isNested())
			return this.declarationContext.getJavaPackageId();
		else
			return (JavaPackageId)this.declarationContext;
	}
}
