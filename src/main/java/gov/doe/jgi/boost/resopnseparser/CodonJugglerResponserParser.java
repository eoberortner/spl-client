package gov.doe.jgi.boost.resopnseparser;

import org.json.JSONArray;
import org.json.JSONObject;

public class CodonJugglerResponserParser {
    private static String RESPONSE = "response";
    private static String MODIFIED_SEQUENCE_TEXT = "modified-sequences-text";
    
	public static String parseCodonJuggleResponse(JSONObject jobResponse){
		JSONObject responseObject = jobResponse;
		JSONArray responseArray = responseObject.getJSONArray(RESPONSE);
		
		JSONObject json_data = null;
		if(responseArray.length() >= 0) {
		json_data = responseArray.getJSONObject(0);	
		}
		String modifiedSequence = json_data.getString(MODIFIED_SEQUENCE_TEXT);
		
		return modifiedSequence;
	}
}
