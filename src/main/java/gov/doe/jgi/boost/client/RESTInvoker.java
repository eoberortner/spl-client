package gov.doe.jgi.boost.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import gov.doe.jgi.boost.exception.BOOSTClientException;

/**
 * The RESTInvoker class provides static methods the send GET and POST requests
 * to any given resource
 * 
 * @author Ernst Oberortner
 *
 */
public class RESTInvoker {

	/**
	 * 
	 * @param resourceURL
	 * @param token
	 * @return
	 * @throws BOOSTClientException
	 */
	public static Response sendGet(
			final String resourceURL, final String token) 
			throws BOOSTClientException {
		
		// build the URL of the BOOST REST authentication resource
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(resourceURL);

		// build up the message of the invocation
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		if(null != token && !token.trim().isEmpty()) {
			invocationBuilder.cookie("boost-jwt", token.trim());
		}

		try {
			// send the GET request
			return invocationBuilder.get();
		} catch(Exception e) {
			throw new BOOSTClientException(e.getLocalizedMessage() + " In sendGet");
		}
	}

	/**
	 * The sendPost method sends a POST request to a given resource.
	 * 
	 * @param boostResource ... the BOOST resource that should be invoked
	 * @param requestData ... the data of the request
	 * 
	 * @return the response of the resource
	 * 
	 * @throws BOOSTClientException ... if something went wrong during the 
	 * API invocation
	 */
	public static Response sendPost(
			final String boostResource, final JSONObject requestData) 
			throws BOOSTClientException {
		return sendPost(boostResource, requestData, null);
	}
	
	/**
	 * The sendPost method sends a POST request to a given resource.
	 * 
	 * @param boostResource ... the BOOST resource that should be invoked
	 * @param requestData ... the data of the request
	 * @param token ... the authorization token
	 * 
	 * @return the response of the resource
	 * 
	 * @throws BOOSTClientException ... if something went wrong during the 
	 * API invocation
	 */
	public static Response sendPost(
			final String resourceURL, final JSONObject requestData, final String token) 
					throws BOOSTClientException{
		
		// build the URL of the BOOST REST authentication resource
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(resourceURL);

		// build up the message of the invocation
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		if(null != token && !token.trim().isEmpty()) {
			// set the cookie that contains the boost-jwt in the headers
			invocationBuilder.cookie("boost-jwt", token.trim());
		} else {
			invocationBuilder.cookie("boost-jwt", "");
		}

		try {
			// send the POST request
			return invocationBuilder.post(
			Entity.entity(requestData.toString(), MediaType.APPLICATION_JSON));
		} catch(Exception e) {
			throw new BOOSTClientException(e.getLocalizedMessage() + " In sendPost");
		}
	}
}

