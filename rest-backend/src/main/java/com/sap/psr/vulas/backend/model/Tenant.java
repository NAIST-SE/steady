package com.sap.psr.vulas.backend.model;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.sap.psr.vulas.backend.model.view.Views;

/**
 * <p>Tenant class.</p>
 *
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown=true, value = { "createdAt", "lastModified" }, allowGetters=true) // On allowGetters: https://github.com/FasterXML/jackson-databind/issues/95
@Entity
@Table( name="Tenant", uniqueConstraints=@UniqueConstraint( columnNames = { "tenantToken" } ) )
public class Tenant {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	private Long id;

	@Column(nullable = false, length = 64)
	private String tenantToken = null;
	
	@Column(nullable = false, length = 1024)
	private String tenantName = null;
	
	@Column
	private boolean isDefault = false;
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone="GMT")
	private java.util.Calendar createdAt;
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone="GMT")
	private java.util.Calendar lastModified;

	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.EAGER, mappedBy = "tenant", orphanRemoval=true)
	@JsonManagedReference
	@JsonView(Views.Never.class)
	private Collection<Space> spaces;
	
	/**
	 * <p>Getter for the field <code>tenantToken</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTenantToken() { return this.tenantToken; }
	/**
	 * <p>Setter for the field <code>tenantToken</code>.</p>
	 *
	 * @param tenantToken a {@link java.lang.String} object.
	 */
	public void setTenantToken(String tenantToken) { this.tenantToken = tenantToken; }
	
	/**
	 * <p>Getter for the field <code>tenantName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTenantName() { return tenantName; }
	/**
	 * <p>Setter for the field <code>tenantName</code>.</p>
	 *
	 * @param tenantName a {@link java.lang.String} object.
	 */
	public void setTenantName(String tenantName) { this.tenantName = tenantName; }
	/**
	 * <p>hasTenantName.</p>
	 *
	 * @return a boolean.
	 */
	public boolean hasTenantName() { return this.tenantName!=null && !this.tenantName.equals(""); }
	
	/**
	 * <p>Getter for the field <code>createdAt</code>.</p>
	 *
	 * @return a {@link java.util.Calendar} object.
	 */
	public java.util.Calendar getCreatedAt() { return createdAt; }
	/**
	 * <p>Setter for the field <code>createdAt</code>.</p>
	 *
	 * @param createdAt a {@link java.util.Calendar} object.
	 */
	public void setCreatedAt(java.util.Calendar createdAt) { this.createdAt = createdAt; }

	/**
	 * <p>Getter for the field <code>lastModified</code>.</p>
	 *
	 * @return a {@link java.util.Calendar} object.
	 */
	public java.util.Calendar getLastModified() { return lastModified; }
	/**
	 * <p>Setter for the field <code>lastModified</code>.</p>
	 *
	 * @param lastModified a {@link java.util.Calendar} object.
	 */
	public void setLastModified(java.util.Calendar lastModified) { this.lastModified = lastModified; }

	/**
	 * <p>Getter for the field <code>spaces</code>.</p>
	 *
	 * @return a {@link java.util.Collection} object.
	 */
	public Collection<Space> getSpaces() { return spaces; }
	/**
	 * <p>Setter for the field <code>spaces</code>.</p>
	 *
	 * @param spaces a {@link java.util.Collection} object.
	 */
	public void setSpaces(Collection<Space> spaces) { this.spaces = spaces; }
	/**
	 * <p>addSpace.</p>
	 *
	 * @param _space a {@link com.sap.psr.vulas.backend.model.Space} object.
	 */
	public void addSpace(Space _space) {
		if(this.getSpaces()==null)
			this.spaces = new HashSet<Space>();
		this.spaces.add(_space);
	}
	
	/**
	 * <p>prePersist.</p>
	 */
	@PrePersist
	public void prePersist() {
		if(this.getCreatedAt()==null)
			this.setCreatedAt(Calendar.getInstance());
		this.setLastModified(Calendar.getInstance());
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tenantToken == null) ? 0 : tenantToken.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tenant other = (Tenant) obj;
		if (tenantToken == null) {
			if (other.tenantToken != null)
				return false;
		} else if (!tenantToken.equals(other.tenantToken))
			return false;
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "tenant [token=" + tenantToken + ", name=" + tenantName + "]";
	}
	
	/**
	 * <p>isTransient.</p>
	 *
	 * @return a boolean.
	 */
	@JsonIgnore
	public boolean isTransient() {
		return this.id==null;
	}
	
	/**
	 * <p>isDefault.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isDefault() { return isDefault; }
	/**
	 * <p>setDefault.</p>
	 *
	 * @param isDefault a boolean.
	 */
	public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

}
