package gov.doe.jgi.spl.client;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import gov.doe.jgi.spl.client.exception.SPLClientException;
import gov.doe.jgi.spl.client.utils.FileUtils;
import gov.doe.jgi.spl.commons.FileFormat;
import gov.doe.jgi.spl.commons.GeneticCode;
import gov.doe.jgi.spl.commons.SequenceType;
import gov.doe.jgi.spl.commons.Strategy;
import gov.doe.jgi.spl.commons.Vendor;

public class RequestBuilder {

	/**
	 * 
	 * @param filename
	 * @param type
	 * @param bAutoAnnotate
	 * @return
	 * @throws SPLClientException
	 */
	public static JSONObject buildSequenceData(final String filename, SequenceType type, boolean bAutoAnnotate) 
			throws SPLClientException {

		// sequence information
		JSONObject sequenceData = new JSONObject();

		String sequences;
		try {
			sequences = FileUtils.readFile(filename);
		} catch (IOException e1) {
			throw new SPLClientException(e1.getLocalizedMessage());
		}
		sequenceData.put(JSON2InputArgs.TEXT, sequences);
		
		// sequence type
		JSONArray types = new JSONArray();
		types.put(type);
		sequenceData.put(JSON2InputArgs.SEQUENCE_TYPE, types);
		
		// auto-annotate?
		sequenceData.put(JSON2InputArgs.AUTO_ANNOTATE, bAutoAnnotate);

		return sequenceData;
	}
	
	/**
	 * 
	 * @param strategy
	 * @param filename
	 * @return
	 */
	public static JSONObject buildModificationData(final Strategy strategy, final String filename) 
			throws IOException {
		
		JSONObject jsonData = new JSONObject();
		jsonData.put(JSON2InputArgs.STRATEGY_NAME, strategy);
		jsonData.put(JSON2InputArgs.TEXT, FileUtils.readFile(filename));
		jsonData.put(JSON2InputArgs.STRATEGY_GENETIC_CODE, GeneticCode.STANDARD);
		return jsonData;
	}
	
	public static JSONObject buildOutputData(final FileFormat format) {
		JSONObject jsonData = new JSONObject();
		jsonData.put(JSON2InputArgs.OUTPUT_FORMAT, format);
		return jsonData;
	}
	
	/**
	 * 
	 * @param vendor
	 * @return
	 */
	public static JSONObject buildConstraintsData(Vendor vendor) {
		JSONObject constraintsData = new JSONObject();
		constraintsData.put("vendor", vendor);
		
		return constraintsData;
	}

}
