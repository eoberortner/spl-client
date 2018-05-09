package gov.doe.jgi.boost.client;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.json.JSONObject;

import gov.doe.jgi.boost.client.constants.BOOSTResources;
import gov.doe.jgi.boost.client.constants.JSONKeys;
import gov.doe.jgi.boost.enums.FileFormat;
import gov.doe.jgi.boost.enums.Strategy;
import gov.doe.jgi.boost.exception.BOOSTBackEndException;
import gov.doe.jgi.boost.exception.BOOSTClientException;

/**
 * The BOOSTClient provides methods to invoke the BOOST REST API.
 * 
 * @author Ernst Oberortner
 */
public class BOOSTClient {

	// the JWT
	private String token;

	/**
	 * Instantiation of the BOOST client using the user's JWT.
	 *  
	 * @param jwt ... the user's authentication token
	 */
	public BOOSTClient(final String jwt) {
		this.token = jwt;
	}
	
	/**
	 * Instantiation of the BOOST client using username and password. The 
	 * BOOST client automatically logs in to BOOST in order to receive 
	 * the user's JWT.
	 * 
	 * @param username  ... the username
	 * @param password  ... the password
	 * 
	 * @throws BOOSTClientException ... in case the username or password is invalid
	 */
	public BOOSTClient(final String username, final String password) 
			throws BOOSTClientException {
		
		// try to login the user
		this.token = login(username, password);
	}
	
	/**
	 * The login() method authenticates a BOOST user with username and password.
	 * 
	 * @param username ... the username
	 * @param password ... the password
	 * 
	 * @throws BOOSTClientException
	 */
	private static String login(final String username, final String password) 
			throws BOOSTClientException {

		// represent the username/password combo as JSON object
		JSONObject jsonRequest = RequestBuilder.buildLogin(username, password);

		// use POST to submit the request
		Response response = RESTInvoker.sendPost(
				BOOSTResources.BOOST_REST_URL + BOOSTResources.LOGIN_RESOURCE, 
				jsonRequest);
		
		String token = null;
		
		// handle the response
		if(null != response) {

			switch(response.getStatus()) {
			case 200:	// OK
				// the response must have a token (for an authenticated user)
				token = parseToken(response.readEntity(String.class));
				
				if(null == token) {
					// the response does not have a token
					throw new BOOSTClientException("Invalid username!");
				}
				break;
			default:
				// for every response code other then 200, 
				// we throw an exception
				throw new BOOSTClientException(
						response.getStatus() + ", " + response.getStatusInfo() + ": " + 
						response.readEntity(String.class));
			}
		}
		
		return token;
	}
	
	/**
	 * 
	 * @param response
	 * 
	 * @return
	 */
	private static String parseToken(final String response) {
		JSONObject jsonResponse = new JSONObject(response);
		if(jsonResponse.has(JSONKeys.TOKEN)) {
			return jsonResponse.getString(JSONKeys.TOKEN);
		}
		return null;
	}
	
	/**
	 * @return the user's token
	 */
	public String getToken() {
		return this.token;
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
	public String reverseTranslate(
			final String filenameSequences,
			Strategy strategy, final String filenameCodonUsageTable,
			final FileFormat outputFormat)
					throws BOOSTClientException, BOOSTBackEndException, IOException {
		
		// construct the request's JSON object 
		JSONObject requestData = RequestBuilder.buildReverseTranslate(
				filenameSequences, strategy, filenameCodonUsageTable, outputFormat);

		System.out.println(requestData.toString(4));
		
		return this.submitJob(requestData);
		
//		// send the request
//		Response response = RESTInvoker.sendPost(
//				BOOSTResources.BOOST_REST_URL + BOOSTResources.REVERSE_TRANSLATE_RESOURCE, 
//				requestData, 
//				this.token);
//
//		// process the response
//		switch(response.getStatus()) {
//		case 200:
//			JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
//			
//			if(jsonResponse.has(JSONKeys.TEXT)) {
//				return jsonResponse.getString(JSONKeys.TEXT);
//			}
//			
//			throw new BOOSTClientException("The server returned an unknown response!");
//		}
//		
//		throw new BOOSTBackEndException(
//				response.getStatus(),
//				response.readEntity(String.class));
	}
	
	/**
	 * The codonJuggle method invokes BOOST's codon-juggling functionality.
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
	public String codonJuggle(
			final String filenameSequences, boolean bAutoAnnotate, 
			Strategy strategy, final String filenameCodonUsageTable,
			final FileFormat outputFormat)
				throws BOOSTClientException, BOOSTBackEndException, IOException {
		
		// construct the request's JSON object 
		JSONObject requestData = RequestBuilder.buildCodonJuggle(
				filenameSequences, bAutoAnnotate, strategy, filenameCodonUsageTable, outputFormat);
		
		
		return this.submitJob(requestData);
		
//		// send the request
//		Response response = RESTInvoker.sendPost(
//				BOOSTResources.BOOST_REST_URL + BOOSTResources.REVERSE_TRANSLATE_RESOURCE, 
//				requestData, 
//				this.token);
//
//		switch(response.getStatus()) {
//		case 200:
//			JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
//			
//			if(jsonResponse.has(JSONKeys.TEXT)) {
//				return jsonResponse.getString(JSONKeys.TEXT);
//			}
//			
//			throw new BOOSTClientException("The server returned an unknown response!");
//		}
//		
//		throw new BOOSTBackEndException(
//				response.getStatus(),
//				response.readEntity(String.class));
	}
	
	/**
	 * The verify method submits a job to BOOST that verifies 
	 * sequences against DNA synthesis constraints.
	 * 
	 * @param filenameSequences ... the name of the file that contains the sequences
	 * @param constraintsFilename ... the name of the file that contains the DNA synthesis constraints
	 * @param sequencePatternsFilename ... the name of the file that contains sequence patterns
	 * 
	 * @return the UUID of the submitted job
	 * 
	 * @throws BOOSTClientException
	 */
	public String dnaVarification(
			final String filenameSequences, 
			final String constraintsFilename, 
			final String sequencePatternsFilename)
				throws BOOSTClientException, BOOSTBackEndException, IOException {

		// represent the request data in JSON and
		// submit it to BOOST's Job Queue Management System (JQMS)
		return submitJob(RequestBuilder.buildVerify(
				filenameSequences, constraintsFilename, sequencePatternsFilename));
	}

	/**
	 * The submitJob method submits a job to the BOOST back-end and returns 
	 * the UUID (as String) of the submitted job. 
	 * 
	 * @param jsonRequest ... the JSON object to be sent to the BOOST back-end
	 * 
	 * @return the UUID of the submitted job
	 * 
	 * @throws BOOSTClientException
	 */
	public String submitJob(final JSONObject requestData) 
			throws BOOSTClientException, BOOSTBackEndException {
		
		// send the request
		Response response = RESTInvoker.sendPost(
				BOOSTResources.BOOST_REST_URL + BOOSTResources.SUBMIT_JOB_RESOURCE, 
				requestData, 
				this.token);

		switch(response.getStatus()) {
		case 200:
			JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
			
			if(jsonResponse.has(JSONKeys.JOB_UUID)) {
				return jsonResponse.getString(JSONKeys.JOB_UUID);
			}
			
			throw new BOOSTClientException("The server returned an unknown response!");
		}
		
		throw new BOOSTBackEndException(
				response.getStatus(),
				response.readEntity(String.class));
	}
	
	/**
	 * sends a GET request to the BOOST REST API in order 
	 * to retrieve all predefined hosts (i.e., codon usage tables)
	 * 
	 * @return
	 * @throws BOOSTClientException 
	 */
	public JSONObject getPredefinedHosts() 
			throws BOOSTClientException {
		
		// get the status of the job
		Response response = RESTInvoker.sendGet(
				BOOSTResources.BOOST_REST_URL + BOOSTResources.GET_PREDEFINED_HOSTS_RESOURCE,  
				this.token);
		
		switch(response.getStatus()) {
		case 200:
			JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
			return jsonResponse;
		}
		
		return (JSONObject)null;
	}
	
	/**
	 * 
	 * @param jobUUID
	 * @return
	 * @throws BOOSTClientException
	 * @throws BOOSTBackEndException
	 */
	public JSONObject getJobReport(final String jobUUID) 
			throws BOOSTClientException, BOOSTBackEndException {
	
		// get the status of the job
		Response response = RESTInvoker.sendGet(
				BOOSTResources.BOOST_REST_URL + BOOSTResources.GET_JOB_RESOURCE + "/" + jobUUID,  
				this.token);
		
		switch(response.getStatus()) {
		case 200:
			JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));

			System.out.println(jsonResponse);
			if(jsonResponse.has(JSONKeys.JOB_INFORMATION)) {
				
				JSONObject jsonJob = jsonResponse.getJSONObject(JSONKeys.JOB_INFORMATION);
				
				if(jsonJob.has(JSONKeys.JOB_REPORT)) {
					return jsonJob.getJSONObject(JSONKeys.JOB_REPORT);
				}
			}
		}
		
		return (JSONObject)null;
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
