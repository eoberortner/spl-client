package gov.doe.jgi.boost.client;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import gov.doe.jgi.boost.client.constants.BOOSTFunctions;
import gov.doe.jgi.boost.client.constants.JSONKeys;
import gov.doe.jgi.boost.client.utils.FileUtils;
import gov.doe.jgi.boost.enums.GeneticCode;
import gov.doe.jgi.boost.enums.SequenceType;
import gov.doe.jgi.boost.enums.Strategy;
import gov.doe.jgi.boost.enums.Vendor;
import gov.doe.jgi.boost.exception.BOOSTClientException;

public class RequestBuilderTest {

	private JSONObject expected;

	@Before
	public void setUp() throws Exception {
		expected = new JSONObject();
		expected.put("username", "dummyUserName");
		expected.put("password", "dummyPassword");
	}

	// testing buildLogin method
	@Test
	public void testBuildLogin() throws BOOSTClientException, IOException {
		JSONObject actual = RequestBuilder.buildLogin("dummyUserName", "dummyPassword");
		TestUtils.equalityOfJSONObject(expected, actual);
	}
	
	// testing buildJobInformation
	@Test
	public void testBuildJobInformation() throws BOOSTClientException, IOException {
		JSONObject expectedObject = new JSONObject();
		expectedObject.put(JSONKeys.JOB_BOOST_FUNCTION, "VERIFY");
		JSONObject actualObject = RequestBuilder.buildJobInformation(BOOSTFunctions.VERIFY);
		TestUtils.equalityOfJSONObject(expectedObject, actualObject);
	}
	
	// testing buildSequence method
	@Test
	public void testBuildSequenceData() throws BOOSTClientException, IOException {
		String sequences = FileUtils.readFile("./data/dna.fasta");
	    JSONArray types = new JSONArray();  // sequence type
		types.put(SequenceType.DNA);
		JSONObject expectedObject = new JSONObject();
		expectedObject.put("text",sequences);
		expectedObject.put("type",types);
		expectedObject.put("auto-annotate", true);
		
		//JSONObject actualObject = RequestBuilder.buildSequenceData("./data/dna.fasta", SequenceType.DNA, true);
		//TestUtils.equalityOfJSONObject(expectedObject, actualObject);
	}

	// testing buildModification method
	@Test
	public void testBuildModificationData() throws BOOSTClientException, IOException {
		JSONObject actualData = RequestBuilder.buildModificationData(Strategy.MostlyUsed, "Bacillus subtilis");
		
		JSONObject expectedData = new JSONObject();
		expectedData.put(JSONKeys.STRATEGY_NAME, Strategy.MostlyUsed);
		expectedData.put("host-name","Bacillus subtilis");
		expectedData.put(JSONKeys.STRATEGY_GENETIC_CODE, GeneticCode.STANDARD);
		
		TestUtils.equalityOfJSONObject(expectedData, actualData);
	}
	
	// testing buildConstraint method with constraint file as a parameter
	@Test
	public void testBuildConstraints() throws BOOSTClientException, IOException {
		String constraints = FileUtils.readFile("./data/constraints.scl");
		JSONObject actualConstraint = RequestBuilder.buildConstraints("./data/constraints.scl");
		JSONObject expectedConstraints = new JSONObject();
		expectedConstraints.put("text", constraints);
		
		TestUtils.equalityOfJSONObject(expectedConstraints, actualConstraint);
		
	}
	
	// testing buildConstraintData
	@Test
	public void testBuildConstraintData() throws IOException {
		JSONObject expectedObject = new JSONObject();
		expectedObject.put("vendor", Vendor.GEN9);
		JSONObject actualObject = RequestBuilder.buildConstraintsData(Vendor.GEN9);
		
		TestUtils.equalityOfJSONObject(expectedObject, actualObject);
	}
		
	
	@After
	public void tearDown() throws Exception {
		expected = null;
	}
}
