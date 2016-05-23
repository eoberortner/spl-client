package gov.doe.jgi.spl.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import gov.doe.jgi.spl.client.exception.SPLClientException;

/**
 * 
 * @author Ernst Oberortner
 *
 */
public class SPLClient {

	private Client client;
	private String token;
	
	// local dev
//	private static final String SPL_REST_URL = "http://localhost:8080/spl-web/rest";
	
	private static final String SPL_REST_URL = "https://spl.jgi.doe.gov/rest";
	
	/**
	 * default no-args constructor 
	 */
	public SPLClient() {
		this.client = ClientBuilder.newClient();
		this.token = null;
	}

	/**
	 * The login() method reads the username and password 
	 * from the command line and authenticates the user
	 * 
	 * @throws SPLClientException ... if the authentication was not successful
	 */
	/**
	 * The login() method authenticates a SPL user with username and password.
	 * 
	 * @param username ... the username
	 * @param password ... the password
	 * 
	 * @throws SPLClientException
	 */
	public void login(final String username, final String password) 
			throws SPLClientException {

		// build the URL of the SPL REST authentication resource
		WebTarget webTarget = client.target(SPL_REST_URL).path("auth").path("login");
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		
		// build the request data
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("username", username);
		jsonRequest.put("password", password);

		// use POST to submit the request
		Response response = null;
		try {
			response = invocationBuilder.post(
					Entity.entity(jsonRequest.toString(), MediaType.APPLICATION_JSON));
		} catch(Exception e) {
			throw new SPLClientException(e.getLocalizedMessage());
		}

		// handle the response
		if(null != response) {

			switch(response.getStatus()) {
			case 200:	// OK
				// the response must have a token (for an authenticated user)
				this.token = this.parseToken(response.readEntity(String.class));
				
				if(null == token) {
					// the response does not have a token
					throw new SPLClientException("Invalid username/password!");
				} 
				
				return;
			default:
				// for every response code other then 200, 
				// we throw an exception
				throw new SPLClientException(response.getStatus() + ": " + response.getStatusInfo());
			}
		}

	}
	
	private String parseToken(final String response) {
		JSONObject jsonResponse = new JSONObject(response);
		if(jsonResponse.has("token")) {
			return jsonResponse.getString("token");
		}
		return null;
	}
	
	/**
	 * The verify() method verifies the sequences of a given file with the 
	 * gene synthesis constraints of a given vendor.
	 * 
	 * @param sequencesFilename ... the name of the file that contains the sequences
	 * @param type ... the type of sequences, i.e. DNA, RNA, Protein
	 * @param vendor ... the vendor
	 * 
	 * @return a map, where each key represents a file and its corresponding 
	 * value is a map, where each key represents a sequence id and its corresponding
	 * value is a list of violations represented as String objects
	 * 
	 * @throws SPLClientException
	 */
	public Map<String, Map<String, List<String>>> verify(
			final String sequencesFilename, SequenceType type, final Vendor vendor) 
			throws SPLClientException {
		
		// check if the user did a login previously
		if(null == token) {
			throw new SPLClientException("You must authenticate first!");
		}
		
		/*
		 * build the request
		 */
		JSONObject jsonRequestData = new JSONObject();

		// sequence information
		jsonRequestData.put(JSON2InputArgs.SEQUENCE_INFORMATION, 
				RequestBuilder.buildSequenceData(sequencesFilename, type, false));
		
		// constraints information
		jsonRequestData.put(JSON2InputArgs.CONSTRAINTS_INFORMATION, 
				RequestBuilder.buildConstraintsData(vendor));
		
		try {
			/*
			 * invoke the verify resource
			 */
			Response response = this.invoke("polisher/verify", jsonRequestData);
		
			switch(response.getStatus()) {
			case 200:	// OK
				/*
				 *  TODO: parse the response
				 */  
				return new HashMap<String, Map<String, List<String>>>();
			default:
				throw new SPLClientException(response.getEntity().toString());
			}
		} catch(Exception e) {
			throw new SPLClientException(e.getLocalizedMessage());
		}
	}
	
	
	/**
	 * The polish() method verifies the sequences in a given file against the 
	 * gene synthesis constraints of a commercial synthesis vendor. 
	 * In case of violations, the polish() method modifies the coding regions 
	 * of the sequence using the specified codon replacement strategy.
	 *  
	 * @param sequencesFilename ... the name of the file that contains the sequences
	 * @param type ... the type of the sequences, i.e. DNA, RNA, Protein
	 * @param bCodingSequences ... if the sequences are encoded in a format that does not 
	 * support sequence feature annotations and if bCoding sequences is set to true, 
	 * then are all sequences are treated as coding sequences. If the sequences are 
	 * encoded in a format that does support sequence feature annotations, then the 
	 * bCodingSequences flag is ignored. 
	 * @param vendor ... the name of commercial synthesis provider
	 * @param strategy ... the codon replacement strategy
	 * @param codonUsageTableFilename ... the name of the file that contains the codon 
	 * usage table
	 * 
	 * @throws SPLClientException
	 */
	public void polish(final String sequencesFilename, SequenceType type, boolean bCodingSequences,
			Vendor vendor, Strategy strategy, final String codonUsageTableFilename) 
				throws SPLClientException {
		
		// check if the user did a login previously
		if(null == token) {
			throw new SPLClientException("You must authenticate first!");
		}
		
		/*
		 * build the request
		 */
		JSONObject jsonRequestData = new JSONObject();

		// sequence information
		jsonRequestData.put(JSON2InputArgs.SEQUENCE_INFORMATION, 
				RequestBuilder.buildSequenceData(sequencesFilename, type, bCodingSequences));
		
		// constraints information
		jsonRequestData.put(JSON2InputArgs.CONSTRAINTS_INFORMATION, 
				RequestBuilder.buildConstraintsData(vendor));

		/*
		 * TODO: -- modification information
		 */ 
//		jsonRequest.put(JSON2InputArgs.MODIFICATION_INFORMATION, 
//				RequestBuilder.buildModificationData(strategy, codonUsageTableFilename);
		
		/*
		 * invoke the resource
		 */
		try {
			/*
			 * invoke the verify resource
			 */
			Response response = this.invoke("polisher/verify", jsonRequestData);
		
			switch(response.getStatus()) {
			case 200:	// OK
				/*
				 *  TODO: parse the response
				 */  
				return;
			default:
				throw new SPLClientException(response.getEntity().toString());
			}
		} catch(Exception e) {
			throw new SPLClientException(e.getLocalizedMessage());
		}
	}
	
	/**
	 * 
	 * @param resource
	 * @param jsonRequestData
	 * @return
	 * @throws SPLClientException
	 */
	public Response invoke(final String resource, final JSONObject jsonRequestData) 
			throws SPLClientException {
		
		WebTarget webTarget = client.target(SPL_REST_URL).path(resource);
		
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder.header("authorization", this.token);
		
		try {
			Response response = invocationBuilder.post(
					Entity.entity(jsonRequestData.toString(), MediaType.APPLICATION_JSON));
			
			switch(response.getStatus()) {
			case 200:	// OK
				return response;
			default:
				throw new SPLClientException(response.getEntity().toString());
			}
		} catch(Exception e) {
			throw new SPLClientException(e.getLocalizedMessage());
		}
	}
}
