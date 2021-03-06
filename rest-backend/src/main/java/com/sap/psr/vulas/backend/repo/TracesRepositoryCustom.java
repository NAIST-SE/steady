package com.sap.psr.vulas.backend.repo;

import java.util.List;

import com.sap.psr.vulas.backend.model.Application;
import com.sap.psr.vulas.backend.model.Trace;

/**
 * Specifies additional methods of the {@link TracesRepository}.
 */
public interface TracesRepositoryCustom {

	/**
	 * <p>customSave.</p>
	 *
	 * @param _app a {@link com.sap.psr.vulas.backend.model.Application} object.
	 * @param _traces an array of {@link com.sap.psr.vulas.backend.model.Trace} objects.
	 * @return a {@link java.util.List} object.
	 */
	public List<Trace> customSave(Application _app, Trace[] _traces);
}
