package gov.doe.jgi.boost.resopnseparser;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class DNAVerificationResponseParser {
	
	private String PROVENANCE = "provenance";
	private String INPUT_DOCUMENT = "input-document";
	private String OUTPUT_DOCUMENT = "output-document";
	private String RESPONSE = "response";
	private String VIOLATIONS = "violations";
	private String SEQUENCE_VIOLATIONS = "sequence-violations";
	private String REPEAT = "Repeat";
	private String SEQUENCE_PATTERNS = "Sequence Pattern";
	private String GC_CONTENT = "Local %GC";
	private String MEASUREMENT_NAME = "measurement-name";
	private String MEASUREMENT_VALUE = "measurement-value";
	private String STATISTICS = "statistics";

	private JSONObject responseObject = null;

	public DNAVerificationResponseParser(JSONObject jsonResponse) {
		this.responseObject = jsonResponse;
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

	public JSONObject constraintsViolations() {
		JSONArray responseArray = responseObject.getJSONArray(RESPONSE);
		JSONArray violationArray = responseArray.getJSONObject(0).getJSONArray(VIOLATIONS);

		JSONObject violationObject = null;
		if (violationArray.length() >= 0 && violationObject.has(SEQUENCE_VIOLATIONS)) {
			violationObject = violationArray.getJSONObject(0).getJSONObject(SEQUENCE_VIOLATIONS);
		} else {
			violationObject = null;
		}
		return violationObject;
	}
	
	public ArrayList<String> repeats(){
		ArrayList<String> repeats = new ArrayList<String>();
		JSONObject violationObject = constraintsViolations();
		JSONArray repeatArray = violationObject.getJSONArray(REPEAT);
		
		for(int i=0; i< repeatArray.length(); i++) {
			String element = repeatArray.getJSONArray(i).getString(0);
			repeats.add(i, element);
		}
		return repeats;
	}
	
	public ArrayList<String> seqPatterns(){
		ArrayList<String> seqPatternsArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = constraintsViolations();
		JSONArray seqPattAfterViolation = sequenceViolationObj.getJSONArray(SEQUENCE_PATTERNS);
		
		for(int i=0; i< seqPattAfterViolation.length(); i++) {
			seqPatternsArrayList.add(i, seqPattAfterViolation.getJSONArray(i).getString(0));
		}	
		return seqPatternsArrayList;
	}
	
	public ArrayList<String> gcContent(){
		ArrayList<String> gcContentArrayList = new ArrayList<String>();
		JSONObject sequenceViolationObj = constraintsViolations();
		JSONArray gcViolation = sequenceViolationObj.getJSONArray(GC_CONTENT);
		
		for(int i=0; i< gcViolation.length(); i++) {
			gcContentArrayList.add(i, gcViolation.getJSONArray(i).getString(0));
		}
		return gcContentArrayList;
	}

	public HashMap<String, String> verificationStatistics() {

		HashMap<String, String> veificationStatists = new HashMap<>();

		JSONArray staticsArray = null;
		if (responseObject.has("statistics")) {
			staticsArray = responseObject.getJSONArray("statistics");
		}

		if (null != staticsArray) {
			for (int index = 0; index < staticsArray.length(); index++) {
				JSONObject staticsObject = staticsArray.getJSONObject(index);
				String measurementName = staticsObject.getString(MEASUREMENT_NAME);
				String measurementValue = staticsObject.getString(MEASUREMENT_VALUE);
				veificationStatists.put(measurementName, measurementValue);
			}
		}
		return veificationStatists;
	}
}
