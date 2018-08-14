package gov.doe.jgi.boost.resopnseparser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class CDSPolisingResponseParser {
	
	private JSONObject responseObject = null;
	
	public CDSPolisingResponseParser(JSONObject jsonObject) {
		this.responseObject = jsonObject;
	}
	
	public String inputSequence() {
		JSONObject object = responseObject.getJSONObject("provenance");
		String inputSequence = object.getString("input-document");
		return inputSequence;
	}
	
	public String outputSequence() {
		JSONObject object = responseObject.getJSONObject("provenance");
		String outputSequence = object.getString("output-document");
		return outputSequence;
	}
	
	public JSONObject vioBeforePolish() {
		JSONArray responseArray = responseObject.getJSONArray("response");
		JSONObject vioBeforeObj = responseArray.getJSONObject(0);
		JSONArray vioBeforeArray = vioBeforeObj.getJSONArray("violations-before");
		JSONObject obj = vioBeforeArray.getJSONObject(0);
		JSONObject seqVioObject = obj.getJSONObject("sequence-violations");
		
		return seqVioObject;
	}
	
	public ArrayList<String> repeatsBeforePolish(){
		ArrayList<String> repeatsArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioBeforePolish();
		JSONArray repeats = sequenceViolationObj.getJSONArray("Repeat");
		
		for(int i=0; i< repeats.length(); i++) {
			repeatsArrayList.add(i, repeats.getString(i));
		}
		return repeatsArrayList;
	}
	
	public ArrayList<String> seqPattBeforeVio(){
		ArrayList<String> seqPatternsArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioBeforePolish();
		JSONArray seqPattBeforeViolation = sequenceViolationObj.getJSONArray("Sequence Pattern");
		
		for(int i=0; i< seqPattBeforeViolation.length(); i++) {
			seqPatternsArrayList.add(i, seqPattBeforeViolation.getString(0));
		}	
		return seqPatternsArrayList;
	}
	
	public ArrayList<String> gcContentBeforeVio(){
		ArrayList<String> gcContentArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioBeforePolish();
		JSONArray gcBeforeViolation = sequenceViolationObj.getJSONArray("Local %GC");
		
		for(int i=0; i< gcBeforeViolation.length(); i++) {
			gcContentArrayList.add(i, gcBeforeViolation.getString(i));
		}
		return gcContentArrayList;
	}
	
	
	public JSONObject vioAfterPolish() {
		JSONArray vioAfterPolishArray = responseObject.getJSONArray("violations-after");
		JSONObject vioAfterObj = vioAfterPolishArray.getJSONObject(0);
		JSONObject seqVioObject = vioAfterObj.getJSONObject("sequence-violations");
		
		return seqVioObject;
	}
	
	public ArrayList<String> repeatsAfterPolish(){
		ArrayList<String> repeatsArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioAfterPolish();
		JSONArray repeats = sequenceViolationObj.getJSONArray("Repeat");
		
		for(int i=0; i< repeats.length(); i++) {
			repeatsArrayList.add(i, repeats.getString(i));
		}
		return repeatsArrayList;
	}
	
	public ArrayList<String> seqPattAfterVio(){
		ArrayList<String> seqPatternsArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioAfterPolish();
		JSONArray seqPattAfterViolation = sequenceViolationObj.getJSONArray("Sequence Pattern");
		
		for(int i=0; i< seqPattAfterViolation.length(); i++) {
			seqPatternsArrayList.add(i, seqPattAfterViolation.getString(0));
		}	
		return seqPatternsArrayList;
	}
	
	public ArrayList<String> gcContentAfterVio(){
		ArrayList<String> gcContentArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioBeforePolish();
		JSONArray gcAfterViolation = sequenceViolationObj.getJSONArray("Local %GC");
		
		for(int i=0; i< gcAfterViolation.length(); i++) {
			gcContentArrayList.add(i, gcAfterViolation.getString(i));
		}
		return gcContentArrayList;
	}
}
