package gov.doe.jgi.spl.client;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
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
	
	private static final String SPL_REST_URL = "http://localhost:8080/spl-web/rest";
	
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
		
		WebTarget webTarget = client.target(SPL_REST_URL).path("auth").path("login");
		System.out.println("[login] " + username + ", " + password);
		
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("username", username);
		jsonRequest.put("password", password);

		System.out.println(jsonRequest);
		
		Response response = null;
		try {
			response = invocationBuilder.post(
					Entity.entity(jsonRequest.toString(), MediaType.APPLICATION_JSON));
		} catch(Exception e) {
			throw new SPLClientException(e.getLocalizedMessage());
		}

		if(null != response) {
			switch(response.getStatus()) {
			case 200:
				this.token = this.parseToken(response.readEntity(String.class));
				if(null != token) {
					return true;
				} 
				return false;
			default:
				throw new SPLClientException(response.getStatus() + ": " + response.getStatusInfo());
			}
		}
		
		return false;
	}
	
	private String parseToken(final String response) {
		JSONObject jsonResponse = new JSONObject(response);
		if(jsonResponse.has("token")) {
			return jsonResponse.getString("token");
		}
		return null;
	}
	
	/**
	 * 
	 * @param sequences
	 * @param constraints
	 * @return
	 */
	public List<Sequence> verify(final List<Sequence> sequences, final String constraints) 
			throws SPLClientException {
		
		if(null == token) {
			throw new SPLClientException("You must authenticate first!");
		}
		
		JSONObject jsonRequest = new JSONObject();
		
		JSONArray sequenceData = new JSONArray();
		for(Sequence sequence : sequences) {
			sequenceData.put(sequence.getElements());
		}
		
		jsonRequest.put(JSON2InputArgs.SEQUENCE_INFORMATION, sequenceData);
		
		WebTarget webTarget = client.target(SPL_REST_URL).path("polisher").path("verify");
		
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		Response response = null;
		try {
			response = invocationBuilder.post(
					Entity.entity(jsonRequest.toString(), MediaType.APPLICATION_JSON));
		} catch(Exception e) {
			throw new SPLClientException(e.getLocalizedMessage());
		}

		return null;
	}
}
