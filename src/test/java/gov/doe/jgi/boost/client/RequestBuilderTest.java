package gov.doe.jgi.boost.client;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.json.JSONObject;
import org.junit.*;
import com.fasterxml.jackson.databind.*;
import gov.doe.jgi.boost.exception.BOOSTClientException;

public class RequestBuilderTest {

	private RequestBuilder mRequestBuilder;
	private JSONObject expected;

	@Before
	public void setUp() throws Exception {
		mRequestBuilder = new RequestBuilder();
		expected = new JSONObject();
		expected.put("username", "dummyUserName");
		expected.put("password", "dummyPassword");
	}

	@Test
	public void testBuildLogin() throws BOOSTClientException, IOException {
		JSONObject actual = mRequestBuilder.buildLogin("dummyUserName", "dummyPassword");
		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode expectedNode = mapper.readTree(expected.toString());
		final JsonNode actualNode = mapper.readTree(actual.toString());

		assertEquals(expectedNode, actualNode);
	}

	@After
	public void tearDown() throws Exception {
		mRequestBuilder = null;
		expected = null;
	}
}
