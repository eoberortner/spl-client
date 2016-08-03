package gov.doe.jgi.boost.client;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.json.JSONObject;

import gov.doe.jgi.boost.client.constants.BOOSTResources;
import gov.doe.jgi.boost.client.constants.JSONKeys;
import gov.doe.jgi.boost.enums.FileFormat;
import gov.doe.jgi.boost.enums.Strategy;
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
	 * The login() method authenticates a BOOST user with username and password.
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
		Response response = RESTInvoker.sendPost(
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
		if(jsonResponse.has(JSONKeys.TOKEN)) {
			return jsonResponse.getString(JSONKeys.TOKEN);
		}
		return null;
	}
	
	/**
	 * The reverseTranslate method invokes BOOT's reverse-translate functionality.
	 *  
	 * @param filenameSequences  ... the name of the file that contains the input sequences
	 * @param strategy ... the codon selection strategy 
	 * @param filenameCodonUsageTable ... the name of the file that contains the codon usage table
	 * @param outputFormat ... the desired output format
	 * 
	 * @throws BOOSTClientException 
	 * @throws IOException
	 */
	public void reverseTranslate(
			final String filenameSequences,
			Strategy strategy, final String filenameCodonUsageTable,
			final FileFormat outputFormat)
				throws BOOSTClientException, IOException {
		
		// construct the request's JSON object 
		JSONObject requestData = RequestBuilder.buildReverseTranslate(
				filenameSequences, strategy, filenameCodonUsageTable, outputFormat);
		
		// send the request
		Response response = RESTInvoker.sendPost(
				BOOSTResources.REVERSE_TRANSLATE_RESOURCE, requestData, this.token);

		// process the response
		System.out.println(response);
	}
	
	/**
	 * The codonJuggle method invokes BOOT's codon-juggling functionality.
	 *  
	 * @param filenameSequences  ... the name of the file that contains the input sequences
	 * @param bAutoAnnotate ... true ... all sequences exclusively 5'-3' protein coding sequences (is only considered 
	 * when the sequences don't have feature annotations, e.g. FASTA or CSV)
	 * @param strategy ... the codon replacement strategy 
	 * @param filenameCodonUsageTable ... the name of the file that contains the codon usage table
	 * @param outputFormat ... the desired output format
	 * 
	 * @throws BOOSTClientException 
	 * @throws IOException
	 */
	public void codonJuggle(
			final String filenameSequences, boolean bAutoAnnotate, 
			Strategy strategy, final String filenameCodonUsageTable,
			final FileFormat outputFormat)
				throws BOOSTClientException {
		
		// construct the request's JSON object 
		JSONObject requestData = RequestBuilder.buildCodonJuggle(
				filenameSequences, bAutoAnnotate, strategy, filenameCodonUsageTable, outputFormat);
		
		// send the request
		Response response = RESTInvoker.sendPost(
				BOOSTResources.REVERSE_TRANSLATE_RESOURCE, requestData, this.token);

		// process the response
		System.out.println(response);
	}
	

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

}
