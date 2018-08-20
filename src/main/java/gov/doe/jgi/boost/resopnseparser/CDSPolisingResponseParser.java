package gov.doe.jgi.boost.resopnseparser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class CDSPolisingResponseParser {
	
	private JSONObject responseObject = null;
	private String PROVENANCE = "provenance";
	private String INPUT_DOCUMENT = "input-document";
	private String OUTPUT_DOCUMENT = "output-document";
	private String RESPONSE = "response";
	private String SEQUENCE_VIOLATIONS = "sequence-violations";
	private String VIOLATION_BEFORE = "violations-before";
	private String VIOLATION_AFTER = "violations-after";
	private String REPEAT = "Repeat";
	private String SEQUENCE_PATTERNS = "Sequence Pattern";
	private String GC_CONTENT = "Local %GC";
	private String STATISTICS = "statistics";

	
	public CDSPolisingResponseParser(JSONObject jsonObject) {
		this.responseObject = jsonObject;
	}
	
	public String inputSequence() {
		JSONObject object = responseObject.getJSONObject(PROVENANCE);
		String inputSequence = object.getString(INPUT_DOCUMENT);
		return inputSequence;
	}
	
	public String outputSequence() {
		JSONObject object = responseObject.getJSONObject(PROVENANCE);
		String outputSequence = object.getString(OUTPUT_DOCUMENT);
		return outputSequence;
	}
	
	public JSONObject vioBeforePolish() {
		JSONArray responseArray = responseObject.getJSONArray(RESPONSE);
		JSONObject vioBeforeObj = responseArray.getJSONObject(0);
		JSONArray vioBeforeArray = vioBeforeObj.getJSONArray(VIOLATION_BEFORE);
		JSONObject obj = vioBeforeArray.getJSONObject(0);
		JSONObject seqVioObject = obj.getJSONObject(SEQUENCE_VIOLATIONS);
		
		return seqVioObject;
	}
	
	public ArrayList<String> repeatsBeforePolish(){
		ArrayList<String> repeatsArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioBeforePolish();
		JSONArray repeats = sequenceViolationObj.getJSONArray(REPEAT);
		
		for(int i=0; i< repeats.length(); i++) {
			repeatsArrayList.add(i, repeats.getJSONArray(i).getString(0));
		}
		return repeatsArrayList;
	}
	
	public ArrayList<String> seqPattBeforeVio(){
		ArrayList<String> seqPatternsArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioBeforePolish();
		JSONArray seqPattBeforeViolation = sequenceViolationObj.getJSONArray(SEQUENCE_PATTERNS);
		
		for(int i=0; i< seqPattBeforeViolation.length(); i++) {
			seqPatternsArrayList.add(i, seqPattBeforeViolation.getJSONArray(i).getString(0));
		}	
		return seqPatternsArrayList;
	}
	
	public ArrayList<String> gcContentBeforeVio(){
		ArrayList<String> gcContentArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioBeforePolish();
		JSONArray gcBeforeViolation = sequenceViolationObj.getJSONArray(GC_CONTENT);
		
		for(int i=0; i< gcBeforeViolation.length(); i++) {
			gcContentArrayList.add(i, gcBeforeViolation.getJSONArray(i).getString(0));
		}
		return gcContentArrayList;
	}
	
	
	public JSONObject vioAfterPolish() {
		JSONArray vioAfterPolishArray = responseObject.getJSONArray(VIOLATION_AFTER);
		JSONObject vioAfterObj = vioAfterPolishArray.getJSONObject(0);
		JSONObject seqVioObject = vioAfterObj.getJSONObject(SEQUENCE_VIOLATIONS);
		
		return seqVioObject;
	}
	
	public ArrayList<String> repeatsAfterPolish(){
		ArrayList<String> repeatsArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioAfterPolish();
		JSONArray repeats = sequenceViolationObj.getJSONArray(REPEAT);
		
		for(int i=0; i< repeats.length(); i++) {
			repeatsArrayList.add(i, repeats.getJSONArray(i).getString(0));
		}
		return repeatsArrayList;
	}
	
	public ArrayList<String> seqPattAfterVio(){
		ArrayList<String> seqPatternsArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioAfterPolish();
		JSONArray seqPattAfterViolation = sequenceViolationObj.getJSONArray(SEQUENCE_PATTERNS);
		
		for(int i=0; i< seqPattAfterViolation.length(); i++) {
			seqPatternsArrayList.add(i, seqPattAfterViolation.getJSONArray(i).getString(0));
		}	
		return seqPatternsArrayList;
	}
	
	public ArrayList<String> gcContentAfterVio(){
		ArrayList<String> gcContentArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = vioBeforePolish();
		JSONArray gcAfterViolation = sequenceViolationObj.getJSONArray(GC_CONTENT);
		
		for(int i=0; i< gcAfterViolation.length(); i++) {
			gcContentArrayList.add(i, gcAfterViolation.getJSONArray(i).getString(0));
		}
		return gcContentArrayList;
	}
}
