package gov.doe.jgi.boost.client;

import java.io.IOException;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import javax.ws.rs.core.Response;

import gov.doe.jgi.boost.client.constants.BOOSTResources;
import gov.doe.jgi.boost.client.constants.JSONKeys;
import gov.doe.jgi.boost.client.constants.LoginCredentials;
import gov.doe.jgi.boost.client.utils.FileUtils;
import gov.doe.jgi.boost.exception.BOOSTAPIsException;
import gov.doe.jgi.boost.exception.BOOSTBackEndException;
import gov.doe.jgi.boost.exception.BOOSTClientException;

public class SendRequest {

	// logger
    protected static final org.slf4j.Logger LOGGER = 
    		LoggerFactory.getLogger("gov.doe.jgi.automation.boost");

    // Main
	public static void main(String[] args) 
			throws IOException, BOOSTClientException, BOOSTBackEndException, BOOSTAPIsException {

		// read the JSON data from a file
		String sJson = FileUtils.readFile("./data/bugs/diva-bug-01.json");
		JSONObject requestData = new JSONObject(sJson);

		// instantiate the BOOST client
		BOOSTClient client = new BOOSTClient(LoginCredentials.mUserName, LoginCredentials.mPassword);
		// get the token from the client 
		// NOTE! I've added the getToken() method to the BOOSTCLient
		String token = client.getToken();
		System.out.println(token);
//		System.exit(1);
		
		// send the request
		Response response = RESTInvoker.sendPost(
				BOOSTResources.BOOST_REST_URL + BOOSTResources.SUBMIT_JOB_RESOURCE, requestData, token);
		// NOTE! the following sendPost call (w/o the token) results in a 500 error
//		Response response = RESTInvoker.sendPost(
//				BOOSTResources.BOOST_REST_URL + BOOSTResources.SUBMIT_JOB_RESOURCE, requestData);

		// process the response
		switch(response.getStatus()) {
		case 200:
			JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
			
			if(jsonResponse.has(JSONKeys.JOB_UUID)) {
				System.out.println(jsonResponse.getString(JSONKeys.JOB_UUID));
				System.exit(1);
			}
			
			throw new BOOSTClientException("The server returned an unknown response!");
		}
		
		throw new BOOSTBackEndException(
				response.getStatus(),
				response.readEntity(String.class));
		
	}

}
