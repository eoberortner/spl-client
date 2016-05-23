package gov.doe.jgi.spl.client;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import gov.doe.jgi.spl.client.exception.SPLClientException;
import gov.doe.jgi.spl.client.utils.FileUtils;

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
		sequenceData.put("text", sequences);
		
		// sequence typ
		JSONArray types = new JSONArray();
		types.put(type);
		sequenceData.put("types", types);
		
		// auto-annotate?
		sequenceData.put("auto-annotate", "true");
		
		return sequenceData;
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
