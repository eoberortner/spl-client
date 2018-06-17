package gov.doe.jgi.boost.client.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class ParseJsonResponse {

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
}
