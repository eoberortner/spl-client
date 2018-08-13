package gov.doe.jgi.boost.client.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class CodonJugglerResponserParser {

	public static String parseCodonJuggleResponse(JSONObject jobResponse){
		JSONObject responseObject = jobResponse;
		JSONArray responseArray = responseObject.getJSONArray("response");
		
		JSONObject json_data = null;
		if(responseArray.length() >= 0) {
		json_data = responseArray.getJSONObject(0);	
		}
		String modifiedSequence = json_data.getString("modified-sequences-text");
		
		return modifiedSequence;
	}
	
	public static void parseDNAVerificationResponse(JSONObject jobResponse) {
		JSONObject responseObject = jobResponse;
		if(responseObject.has("provenenance") && responseObject.has("output-document")) {
			String outputSequence = responseObject.getJSONObject("provenance").getString("output-document");	
		}
		JSONArray responseArray = responseObject.getJSONArray("response");
		JSONArray violationArray = responseArray.getJSONObject(0).getJSONArray("violations");
		
		JSONObject violationObject = null;
		if(violationArray.length() >= 0 && violationObject.has("sequence-violations")) {
			violationObject = violationArray.getJSONObject(0).getJSONObject("sequence-violations");
		}else {
			violationObject = null;
		}
		
		ArrayList<String> measureNameList = new ArrayList<String>();
		ArrayList<String> measureValueList = new ArrayList<String>();
		ArrayList<ArrayList<String>> staticsArrayList = new ArrayList<ArrayList<String>>();
		JSONArray staticsArray = jobResponse.getJSONArray("statistics");
		if(null != staticsArray) {
			for(int index=0; index < staticsArray.length(); index++) {
				JSONObject staticsObject = staticsArray.getJSONObject(index);
				String measurementName = staticsObject.getString("measurement-name");
				measureNameList.add(measurementName);
				String measurementValue = staticsObject.getString("measurement-value");
				measureValueList.add(measurementValue);
			}
			staticsArrayList.add(measureNameList);
			staticsArrayList.add(measureValueList);
		}
	}
}
