package com.sap.psr.vulas.java;

import com.sap.psr.vulas.ConstructId;

/**
 * Identifies a Java package by its fully qualified name.
 */
public class JavaPackageId extends JavaId {
	
	/**
	 * The default package has an empty string as qualified name. It is used
	 * when working with classes, interfaces or enums that do not belong to
	 * a package.
	 */
	public final static JavaPackageId DEFAULT_PACKAGE = new JavaPackageId(null);

	/** Fully qualified package identifier. */
	protected String packageId = null;
	/**
	 * <p>Constructor for JavaPackageId.</p>
	 *
	 * @param _qualified_name a {@link java.lang.String} object.
	 */
	public JavaPackageId(String _qualified_name) {
		super(JavaId.Type.PACKAGE);
		this.packageId = (_qualified_name==null ? "" : _qualified_name);
	}
	
	/**
	 * Returns the complete package name, including parent packages.
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getQualifiedName() { return this.packageId; }
	
	/**
	 * For packages, the method returns the qualified name.
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() { return this.getQualifiedName(); }
	
	/**
	 * For packages, the method returns the qualified name.
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSimpleName() { return this.getQualifiedName(); }
	
	/**
	 * For packages, this method always returns null.
	 *
	 * @return a {@link com.sap.psr.vulas.ConstructId} object.
	 */
	public ConstructId getDefinitionContext() { return null; }
	
	/**
	 * For packages, this method always returns null.
	 *
	 * @return a {@link com.sap.psr.vulas.java.JavaPackageId} object.
	 */
	public JavaPackageId getJavaPackageId() { return null; }
	
	/**
	 * Returns true if the given {@link JavaPackageId} corresponds to the {@link JavaPackageId#DEFAULT_PACKAGE}, false otherwise.
	 *
	 * @param _pid a {@link com.sap.psr.vulas.java.JavaPackageId} object.
	 * @return a boolean.
	 */
	public static final boolean isDefaultPackage(JavaPackageId _pid) { return DEFAULT_PACKAGE.equals(_pid); }
}
