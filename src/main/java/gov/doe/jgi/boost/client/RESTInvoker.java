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

public class RESTInvoker {

	public static Response doPost(final String resource, final JSONObject requestData, final String token) 
			throws BOOSTClientException {
		
		// build the URL of the SPL REST authentication resource
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(BOOSTResources.BOOST_REST_URL).path(resource);
		System.out.println("[invoke] --> " + webTarget);
		
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder.header("authorization", token);

		try {
			return invocationBuilder.post(
					Entity.entity(requestData.toString(), MediaType.APPLICATION_JSON));
		} catch(Exception e) {
			throw new BOOSTClientException(e.getLocalizedMessage());
		}
	}
}
