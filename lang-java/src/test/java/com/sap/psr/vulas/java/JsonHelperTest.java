package com.sap.psr.vulas.java;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.sap.psr.vulas.FileAnalysisException;
import com.sap.psr.vulas.shared.json.JacksonUtil;
import com.sap.psr.vulas.shared.util.FileUtil;

public class JsonHelperTest {

	private static final Log log = LogFactory.getLog(JsonHelperTest.class);

	/**
	 * Analyzes a given JAR twice and checks whether the produced JSON is equal.
	 */
	@Test
	public void jarToJsonEqualityTest() throws FileAnalysisException {
		try {
			// Create JSON for the same JAR
			final JarAnalyzer ja1 = new JarAnalyzer();
			ja1.analyze(new File("./src/test/resources/poi-ooxml-schemas-3.11-beta1.jar"));
			final String json1 = JacksonUtil.asJsonString(ja1.getLibrary());
			final JarAnalyzer ja2 = new JarAnalyzer();
			ja2.analyze(new File("./src/test/resources/poi-ooxml-schemas-3.11-beta1.jar"));
			final String json2 = JacksonUtil.asJsonString(ja1.getLibrary());

			// The JSON should be equal. If not, write it to files so that it can be compared
			if(!json1.equals(json2)) {
				final Path p1 = FileUtil.writeToTmpFile(null, "json", json1);				
				final Path p2 = FileUtil.writeToTmpFile(null, "json", json2);
				JsonHelperTest.log.info("JSON written to [" + p1.toAbsolutePath()+ "] and [" + p2.toAbsolutePath() + "]");
			}

			assertEquals(json1, json2);
		} catch (IllegalStateException ise) {
			// TODO Auto-generated catch block
			ise.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
	}

	/**
	 * Analyzes a JAR that contains nested classes (having a $ in their name), but not the surrounding class.
	 * In fact, a $ is a permitted character in class names. It is only a convention to not use it.
	 */
	@Test
	public void nestedClassesTest() throws FileAnalysisException {
		try {
			final JarAnalyzer ja3 = new JarAnalyzer();
			ja3.analyze(new File("./src/test/resources/diverse.jar"));
			final String pretty_json3 = JacksonUtil.asJsonString(ja3.getLibrary());
			final Path p3 = FileUtil.writeToTmpFile(null, "json",pretty_json3);
			JsonHelperTest.log.info("JSON written to [" + p3.toAbsolutePath() + "]");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
