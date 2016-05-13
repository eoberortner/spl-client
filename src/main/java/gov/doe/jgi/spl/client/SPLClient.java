package gov.doe.jgi.spl.client;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.sbolstandard.core2.Sequence;

import gov.doe.jgi.spl.client.exception.SPLClientException;

/**
 * 
 * @author Ernst Oberortner
 *
 */
public class SPLClient {

	private Client client;
	private String token;
	
	/**
	 * default no-args constructor 
	 */
	public SPLClient() {
		this.client = ClientBuilder.newClient();
		this.token = null;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws SPLClientException
	 */
	public boolean login(final String username, final String password) 
			throws SPLClientException {
		
		WebTarget webTarget = client.target("http://localhost:8080/spl-web/rest")
	        	.path("auth").path("login");
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("username", username);
		jsonRequest.put("password", password);

		Response response = null;
		try {
			response = invocationBuilder.post(
					Entity.entity(jsonRequest.toString(), MediaType.APPLICATION_JSON));
		} catch(Exception e) {
			throw new SPLClientException(e.getLocalizedMessage());
		}

		String strResponse = response.readEntity(String.class);
		JSONObject jsonResponse = new JSONObject(strResponse);
		if(jsonResponse.has("token")) {
			this.token = jsonResponse.getString("token");
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param sequences
	 * @param constraints
	 * @return
	 */
	public List<Sequence> verify(final List<Sequence> sequences, final String constraints) {
		return null;
	}
}
