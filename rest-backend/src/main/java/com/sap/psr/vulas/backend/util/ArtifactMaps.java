package com.sap.psr.vulas.backend.util;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sap.psr.vulas.backend.model.LibraryId;
import com.sap.psr.vulas.shared.json.JacksonUtil;
import com.sap.psr.vulas.shared.util.VulasConfiguration;

/**
 * <p>ArtifactMaps class.</p>
 *
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties
public class ArtifactMaps {
	
	/** Constant <code>ARTIFACT_MAPS="vulas.backend.artifactMaps"</code> */
	public static final String ARTIFACT_MAPS = "vulas.backend.artifactMaps";
	
	private List<ArtifactMap> maps = null;
	
	/**
	 * <p>Getter for the field <code>maps</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<ArtifactMap> getMaps() { return maps; }
	
	/**
	 * <p>Setter for the field <code>maps</code>.</p>
	 *
	 * @param maps a {@link java.util.List} object.
	 */
	public void setMaps(List<ArtifactMap> maps) { this.maps = maps; }
	
	/**
	 * <p>getGreaterArtifacts.</p>
	 *
	 * @param _group a {@link java.lang.String} object.
	 * @param _artifact a {@link java.lang.String} object.
	 * @return a {@link java.util.List} object.
	 */
	@JsonIgnore
	public List<LibraryId> getGreaterArtifacts(@NotNull String _group, @NotNull String _artifact) {
		if(_group==null || _group.equals("") || _artifact==null || _artifact.equals(""))
			throw new IllegalArgumentException("Both group and artifact aragument must be specified");
		final LibraryId l = new LibraryId();
		l.setMvnGroup(_group);
		l.setArtifact(_artifact);
		return this.getGreaterArtifacts(l);
	}
	
	/**
	 * <p>getGreaterArtifacts.</p>
	 *
	 * @param _a a {@link com.sap.psr.vulas.backend.model.LibraryId} object.
	 * @return a {@link java.util.List} object.
	 */
	@JsonIgnore
	public List<LibraryId> getGreaterArtifacts(LibraryId _a) {
		final List<LibraryId> later = new ArrayList<LibraryId>();
		if(this.maps!=null) {
			for(ArtifactMap map: this.maps) {
				if(map.containsArtifact(_a)) {
					later.addAll(map.getGreaterArtifacts(_a));
					return later;
				}
			}
		}
		return later;
	}
	
	/**
	 * Reads the configuration setting vulas.backend.artifactMaps.
	 *
	 * @return a {@link com.sap.psr.vulas.backend.util.ArtifactMaps} object.
	 */
	public static ArtifactMaps buildMaps() {
		String m = VulasConfiguration.getGlobal().getConfiguration().getString(ARTIFACT_MAPS, null);
		if(m!=null)
			m = m.replace(';', ',');
		return ArtifactMaps.buildMaps(m);
	}
	
	/**
	 * Reads the configuration setting vulas.backend.artifactMaps.
	 * @return
	 */
	private static ArtifactMaps buildMaps(String _json) {
		ArtifactMaps maps = new ArtifactMaps();
		if(_json!=null) {
			maps = (ArtifactMaps)JacksonUtil.asObject(_json, ArtifactMaps.class);
		}
		return maps;
	}
	
	@JsonInclude(JsonInclude.Include.ALWAYS)
	@JsonIgnoreProperties
	private static class ArtifactMap {
		
		private List<LibraryId> map = null;
		
		public List<LibraryId> getMap() { return map; }
		
		public void setMap(List<LibraryId> map) { this.map = map; }
		
		@JsonIgnore
		public boolean containsArtifact(LibraryId _a) {
			if(this.map!=null) {
				for(LibraryId l: this.map) {
					if(l.equalsButVersion(_a)) {
						return true;
					}
				}
			}
			return false;
		}
		
		@JsonIgnore
		public List<LibraryId> getGreaterArtifacts(LibraryId _a) {
			final List<LibraryId> list = new ArrayList<LibraryId>();
			int gt = -1;
			if(this.map!=null) {
				for(int i=0; i<this.map.size(); i++) {
					if(this.map.get(i).equalsButVersion(_a))
						gt = i;
					if(gt!=-1 && i>gt)
						list.add(this.map.get(i));
				}
			}
			return list;
		}
	}
}
