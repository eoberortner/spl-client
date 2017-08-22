package gov.doe.jgi.boost.client;

import javax.ws.rs.core.Response;

import org.json.JSONObject;

import gov.doe.jgi.boost.exception.BOOSTClientException;

public class LoginExample {

//	// LOCAL BOOST 
//	private static final String BOOST_REST_URL = "http://localhost:8080/BOOST/rest";

	// LOCAL DOCKER BOOST 
	private static final String BOOST_REST_URL = "http://localhost:9090/BOOST/rest";
	
//	// JGI/NERSC BOOST
//	private static final String BOOST_REST_URL = "https://boost.jgi.doe.gov/rest";
	
	public static void main(String[] args) 
			throws Exception {

		// represent the username/password combo as JSON object
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("username", "<your-username>");
		jsonRequest.put("password", "<your-password>");

		System.out.println("---------------------------------------");
		System.out.println("Request JSON: " + jsonRequest);
		System.out.println("Resource URL: " + BOOST_REST_URL + "/auth/login");
		System.out.println("---------------------");
		
		Response response = RESTInvoker.sendPost(
				BOOST_REST_URL + "/auth/login", jsonRequest);
		
		String jwt = null;
		// handle the response
		if(null != response) {
			System.out.println("Response Status: " + response.getStatus());
			switch(response.getStatus()) {
			case 200:	// OK
				// the response must have a token (for an authenticated user)
				JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
				
				System.out.println("Response: " + jsonResponse);
				jwt = jsonResponse.getString("boost-jwt");
				break;
			default:
				// for every response code other then 200, 
				// we throw an exception
				throw new BOOSTClientException(response.getStatus() + ": " + response.getStatusInfo());
			}
		}
		System.out.println("---------------------------------------");
		
		// invoke the "get jobs" resource
		System.out.println("---------------------------------------");
		System.out.println("Request JSON: " + jsonRequest);
		System.out.println("Resource URL: " + BOOST_REST_URL + "/jobs");
		response = RESTInvoker.sendGet(
				BOOST_REST_URL + "/jobs", jwt);
		System.out.println("---------------------");
		System.out.println("Response Status: " + response.getStatus());
//		JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
		System.out.println("---------------------------------------");

	}

}
