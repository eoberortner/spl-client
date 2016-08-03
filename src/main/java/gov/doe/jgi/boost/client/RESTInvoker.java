package gov.doe.jgi.boost.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import gov.doe.jgi.boost.client.constants.BOOSTResources;
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
	 * The sendPost method sends a POST request to a given resource.
	 * 
	 * @param resource ... the resource
	 * @param requestData ... the data of the request
	 * @param token ... the authorization token
	 * 
	 * @return the response of the resource
	 * 
	 * @throws BOOSTClientException ... if something went wrong during the 
	 * API invocation
	 */
	public static Response sendPost(
			final String resource, final JSONObject requestData, final String token) 
			throws BOOSTClientException {
		
		// build the URL of the SPL REST authentication resource
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(BOOSTResources.BOOST_REST_URL).path(resource);

		// build up the message of the invocation
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder.header("authorization", token);

		try {
			// send the POST request
			return invocationBuilder.post(
					Entity.entity(requestData.toString(), MediaType.APPLICATION_JSON));
		} catch(Exception e) {
			throw new BOOSTClientException(e.getLocalizedMessage());
		}
	}
}
