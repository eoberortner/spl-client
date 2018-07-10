package gov.doe.jgi.boost.client;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {
	
	public static void equalityOfJSONObject(JSONObject expectedObject, JSONObject actualObject) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode expectedNode = mapper.readTree(expectedObject.toString());
		final JsonNode actualNode = mapper.readTree(actualObject.toString());
		assertEquals(expectedNode, actualNode);
	}
}
