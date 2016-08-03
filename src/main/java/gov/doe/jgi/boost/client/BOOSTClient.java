package gov.doe.jgi.boost.client;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.json.JSONObject;

import gov.doe.jgi.boost.commons.FileFormat;
import gov.doe.jgi.boost.commons.Strategy;
import gov.doe.jgi.boost.exception.BOOSTClientException;

/**
 * The BOOSTClient provides methods to invoke the BOOST REST API.
 * 
 * @author Ernst Oberortner
 */
public class BOOSTClient {

	private String token;

	/**
	 * Instantiation of the BOOST client using username and password.
	 * 
	 * @param username  ... the username
	 * @param password  ... the password
	 * 
	 * @throws BOOSTClientException ... in case the username or password is invalid
	 */
	public BOOSTClient(final String username, final String password) 
			throws BOOSTClientException {
		
		// try to login the user
		this.login(username, password);
	}
	
	/**
	 * Instantiation of the BOOST client using a JSON Web Token (JWT)
	 * 
	 * @param token ... the JWT
	 * 
	 * @throws BOOSTClientException ... in case the JWT is invalid
	 */
	public BOOSTClient(final String token)
			throws BOOSTClientException {
		
		this.token = token;
	}

	/**
	 * The login() method authenticates a SPL user with username and password.
	 * 
	 * @param username ... the username
	 * @param password ... the password
	 * 
	 * @throws BOOSTClientException
	 */
	private void login(final String username, final String password) 
			throws BOOSTClientException {

		// represent the username/password combo as JSON object
		JSONObject jsonRequest = RequestBuilder.buildLogin(username, password);

		// use POST to submit the request
		Response response = RESTInvoker.doPost(
				BOOSTResources.LOGIN_RESOURCE, jsonRequest, this.token);
		
		// handle the response
		if(null != response) {

			switch(response.getStatus()) {
			case 200:	// OK
				// the response must have a token (for an authenticated user)
				this.token = this.parseToken(response.readEntity(String.class));
				
				if(null == token) {
					// the response does not have a token
					throw new BOOSTClientException("Invalid username/password!");
				} 
				
				return;
			default:
				// for every response code other then 200, 
				// we throw an exception
				throw new BOOSTClientException(response.getStatus() + ": " + response.getStatusInfo());
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
	 * 
	 * @param filenameSequences
	 * @param type
	 * @param bCodingSequences
	 * @param strategy
	 * @param filenameCodonUsageTable
	 */
	public void reverseTranslate(
			final String filenameSequences, 
			Strategy strategy, final String filenameCodonUsageTable,
			final FileFormat outputFormat)
				throws BOOSTClientException, IOException {
		
		JSONObject requestData = RequestBuilder.buildReverseTranslate(
				filenameSequences, strategy, filenameCodonUsageTable, outputFormat);
		
		Response response = 
				RESTInvoker.doPost(BOOSTResources.REVERSE_TRANSLATE_RESOURCE, requestData, this.token);

		System.out.println(response);
		
		//handleResponse(response);
	}
	
//	/**
//	 * 
//	 * @param filenameSequences
//	 * @param strategy
//	 * @param filenameCodonUsageTable
//	 * @param outputFormat
//	 * @throws BOOSTClientException
//	 * @throws IOException
//	 */
//	public void codonJuggle(
//			final String filenameSequences, boolean bAutoAnnotate, 
//			Strategy strategy, final String filenameCodonUsageTable,
//			final FileFormat outputFormat)
//				throws BOOSTClientException, IOException {
//
//		Response response = 
//				this.invokeJuggler(
//						filenameSequences, SequenceType.DNA, bAutoAnnotate,
//						strategy, filenameCodonUsageTable, 
//						outputFormat);
//		
//		handleResponse(response);
//	}

//	/**
//	 * 
//	 * @param response
//	 * @throws BOOSTClientException
//	 */
//	public void handleResponse(Response response) 
//			throws BOOSTClientException {
//		
//		switch(response.getStatus()) {
//		case 200:	// OK
//			JSONObject jsonResponseData = new JSONObject(response.readEntity(String.class));
//			
//			if(jsonResponseData.has(JSON2InputArgs.TEXT)) {
//				System.out.println(jsonResponseData.get(JSON2InputArgs.TEXT));
//			} else if(jsonResponseData.has(JSON2InputArgs.FILE)) {
//				System.out.println(jsonResponseData.get(JSON2InputArgs.TEXT));
//			}
//			
//			break;
//			
//		default:
//			throw new BOOSTClientException(response.getStatus() + ": " + response.getStatusInfo());
//		}
//	}
//	
//	/**
//	 * The verify() method verifies the sequences of a given file with the 
//	 * gene synthesis constraints of a given vendor.
//	 * 
//	 * @param sequencesFilename ... the name of the file that contains the sequences
//	 * @param type ... the type of sequences, i.e. DNA, RNA, Protein
//	 * @param vendor ... the vendor
//	 * 
//	 * @return a map, where each key represents a file and its corresponding 
//	 * value is a map, where each key represents a sequence id and its corresponding
//	 * value is a list of violations represented as String objects
//	 * 
//	 * @throws BOOSTClientException
//	 */
//	public Map<String, Map<String, List<String>>> verify(
//			final String sequencesFilename, SequenceType type, final Vendor vendor) 
//			throws BOOSTClientException {
//		
//		// check if the user did a login previously
//		if(null == token) {
//			throw new BOOSTClientException("You must authenticate first!");
//		}
//		
//		/*
//		 * build the request
//		 */
//		JSONObject jsonRequestData = new JSONObject();
//
//		// sequence information
//		jsonRequestData.put(JSON2InputArgs.SEQUENCE_INFORMATION, 
//				RequestBuilder.buildSequenceData(sequencesFilename, type, false));
//		
//		// constraints information
//		jsonRequestData.put(JSON2InputArgs.CONSTRAINTS_INFORMATION, 
//				RequestBuilder.buildConstraintsData(vendor));
//		
//		try {
//			/*
//			 * invoke the verify resource
//			 */
//			Response response = this.invoke("polisher/verify", jsonRequestData);
//		
//			switch(response.getStatus()) {
//			case 200:	// OK
//				/*
//				 *  TODO: parse the response
//				 */  
//				return new HashMap<String, Map<String, List<String>>>();
//			default:
//				throw new BOOSTClientException(response.getEntity().toString());
//			}
//		} catch(Exception e) {
//			throw new BOOSTClientException(e.getLocalizedMessage());
//		}
//	}
//	
//	
//	/**
//	 * The polish() method verifies the sequences in a given file against the 
//	 * gene synthesis constraints of a commercial synthesis vendor. 
//	 * In case of violations, the polish() method modifies the coding regions 
//	 * of the sequence using the specified codon replacement strategy.
//	 *  
//	 * @param sequencesFilename ... the name of the file that contains the sequences
//	 * @param type ... the type of the sequences, i.e. DNA, RNA, Protein
//	 * @param bCodingSequences ... if the sequences are encoded in a format that does not 
//	 * support sequence feature annotations and if bCoding sequences is set to true, 
//	 * then are all sequences are treated as coding sequences. If the sequences are 
//	 * encoded in a format that does support sequence feature annotations, then the 
//	 * bCodingSequences flag is ignored. 
//	 * @param vendor ... the name of commercial synthesis provider
//	 * @param strategy ... the codon replacement strategy
//	 * @param codonUsageTableFilename ... the name of the file that contains the codon 
//	 * usage table
//	 * 
//	 * @throws BOOSTClientException
//	 */
//	public void polish(final String sequencesFilename, SequenceType type, boolean bCodingSequences,
//			Vendor vendor, Strategy strategy, final String codonUsageTableFilename) 
//				throws BOOSTClientException {
//		
//		// check if the user did a login previously
//		if(null == token) {
//			throw new BOOSTClientException("You must authenticate first!");
//		}
//		
//		/*
//		 * build the request
//		 */
//		JSONObject jsonRequestData = new JSONObject();
//
//		// sequence information
//		jsonRequestData.put(JSON2InputArgs.SEQUENCE_INFORMATION, 
//				RequestBuilder.buildSequenceData(sequencesFilename, type, bCodingSequences));
//		
//		// constraints information
//		jsonRequestData.put(JSON2InputArgs.CONSTRAINTS_INFORMATION, 
//				RequestBuilder.buildConstraintsData(vendor));
//
//		/*
//		 * TODO: -- modification information
//		 */ 
////		jsonRequest.put(JSON2InputArgs.MODIFICATION_INFORMATION, 
////				RequestBuilder.buildModificationData(strategy, codonUsageTableFilename);
//		
//		/*
//		 * invoke the resource
//		 */
//		try {
//			/*
//			 * invoke the verify resource
//			 */
//			Response response = this.invoke("polisher/verify", jsonRequestData);
//		
//			switch(response.getStatus()) {
//			case 200:	// OK
//				/*
//				 *  TODO: parse the response
//				 */  
//				return;
//			default:
//				throw new BOOSTClientException(response.getEntity().toString());
//			}
//		} catch(Exception e) {
//			throw new BOOSTClientException(e.getLocalizedMessage());
//		}
//	}
//	
////	/**
////	 * 
////	 * @param resource
////	 * @param jsonRequestData
////	 * @return
////	 * @throws BOOSTClientException
////	 */
////	public Response invoke(final String resource, final JSONObject jsonRequestData) 
////			throws BOOSTClientException {
////		
////		WebTarget webTarget = client.target(SPL_REST_URL).path(resource);
////		
////		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
////		invocationBuilder.header("authorization", this.token);
////		
////		try {
////			Response response = invocationBuilder.post(
////					Entity.entity(jsonRequestData.toString(), MediaType.APPLICATION_JSON));
////
////			switch(response.getStatus()) {
////			case 200:	// OK
////				return response;
////			default:
////				throw new BOOSTClientException(response.getEntity().toString());
////			}
////		} catch(Exception e) {
////			e.printStackTrace();
////			throw new BOOSTClientException(e.getLocalizedMessage());
////		}
////	}
}
