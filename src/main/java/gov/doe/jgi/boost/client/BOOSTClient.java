package gov.doe.jgi.boost.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidationException;

import gov.doe.jgi.boost.client.constants.BOOSTConstants;
import gov.doe.jgi.boost.client.constants.BOOSTResources;
import gov.doe.jgi.boost.client.constants.JSONKeys;
import gov.doe.jgi.boost.enums.FileFormat;
import gov.doe.jgi.boost.enums.Strategy;
import gov.doe.jgi.boost.enums.Vendor;
import gov.doe.jgi.boost.exception.BOOSTAPIsException;
import gov.doe.jgi.boost.exception.BOOSTBackEndException;
import gov.doe.jgi.boost.exception.BOOSTClientException;

/**
 * The BOOSTClient provides methods to invoke the BOOST REST API.
 * 
 * @author Ernst Oberortner
 */
public class BOOSTClient {
	
	private String token;
	
	/**
	 * 
	 */
	public BOOSTClient() {}

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
	 * @throws BOOSTAPIsException  ... in case of BOOST resources are not valid
	 */
	public BOOSTClient(final String username, final String password) 
			throws BOOSTClientException, BOOSTAPIsException{
		
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
	 * @throws BOOSTAPIsException 
	 */
	private String login(final String username, final String password) 
			throws BOOSTClientException, BOOSTAPIsException, BOOSTAPIsException{


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
		}else {
			// URL might be invalid
			throw new BOOSTAPIsException("The resource: " + BOOSTResources.BOOST_REST_URL + 
					BOOSTResources.LOGIN_RESOURCE + " is Invalid");
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
	 * The reverseTranslate method invokes BOOST's reverse-translate functionality.
	 *  
	 * @param sequencesFilename  ... the name of the file that contains the input sequences
	 * @param targetNamespace ... namespace in that the new elements (ComponentDefinition, 
	 *        Sequence etc) should be placed into
	 * @param strategy ... the codon selection strategy 
	 * @param filenameCodonUsageTable ... the name of the file that contains the codon usage table
	 * @param outputFormat ... the desired output format
	 * 
	 * @throws BOOSTClientException 
	 * @throws IOException
	 * @throws SBOLConversionException 
	 * @throws SBOLValidationException 
	 * @throws BOOSTAPIsException 
	 */
	public String reverseTranslate(
			final String sequencesFilename,
			final String targetNamespace,
			Strategy strategy, final String filenameCodonUsageTable,
			final FileFormat outputFormat)
					throws BOOSTClientException, BOOSTBackEndException, IOException, 
					SBOLValidationException, SBOLConversionException {
		
		// verify for filename 
		ParameterValueVerifier.verifyFilename(BOOSTConstants.INPUT_FILENAME, sequencesFilename);

		// read the file as SBOLDocument
		SBOLDocument document = SBOLReader.read(sequencesFilename);
		return this.reverseTranslate(document, targetNamespace, strategy, filenameCodonUsageTable, outputFormat);
	}
	
	/**
	 * The reverseTranslate method invokes BOOST's reverse-translate functionality.
	 *  
	 * @param designSequences  ... sbol document that contains the input sequences
	 * @param targetNamespace ... namespace in that the new elements (ComponentDefinition, 
	 *        Sequence etc) should be placed into
	 * @param strategy ... the codon selection strategy 
	 * @param filenameCodonUsageTable ... the name of the file that contains the codon usage table
	 * @param outputFormat ... the desired output format
	 * 
	 * @throws BOOSTClientException 
	 * @throws IOException
	 * @throws JSONException 
	 * @throws SBOLConversionException 
	 * @throws BOOSTAPIsException 
	 */
	public String reverseTranslate(
			final SBOLDocument designSequences,
			final String targetNamespace,
			Strategy strategy, final String filenameCodonUsageTable,
			final FileFormat outputFormat)
					throws BOOSTClientException, BOOSTBackEndException, IOException, 
					JSONException, SBOLConversionException {
		
		// construct the request's JSON object 
		JSONObject requestData = RequestBuilder.buildReverseTranslate(designSequences, 
				targetNamespace, strategy, filenameCodonUsageTable, outputFormat);
		
		return this.submitJob(requestData);
	}
	
	/**
	 * The codonJuggle method invokes BOOST's codon-juggling functionality.
	 *  
	 * @param sequencesFilename  ... the name of the file that contains the input sequences
	 * @param targetNamespace ... namespace in that the new elements (ComponentDefinition, 
	 *        Sequence etc) should be placed into
	 * @param bAutoAnnotate ... true ... all sequences exclusively 5'-3' protein coding sequences (is only considered 
	 * when the sequences don't have feature annotations, e.g. FASTA or CSV)
	 * @param strategy ... the codon replacement strategy 
	 * @param filenameCodonUsageTable ... the name of the file that contains the codon usage table
	 * @param outputFormat ... the desired output format
	 * 
	 * @throws BOOSTClientException 
	 * @throws IOException
	 * @throws SBOLConversionException 
	 * @throws SBOLValidationException 
	 * @throws BOOSTAPIsException 
	 */
	public String codonJuggle(
			final String sequencesFilename, 
			final String targetNamespace, boolean bAutoAnnotate, 
			Strategy strategy, final String filenameCodonUsageTable,
			final FileFormat outputFormat)
				throws BOOSTClientException, BOOSTBackEndException, IOException, 
				SBOLValidationException, SBOLConversionException {
		
		// verify for filename 
		ParameterValueVerifier.verifyFilename(BOOSTConstants.INPUT_FILENAME, sequencesFilename);

		// read the file as SBOLDocument
		SBOLDocument document = SBOLReader.read(sequencesFilename);
		
		return this.codonJuggle(document, targetNamespace, bAutoAnnotate, strategy, filenameCodonUsageTable, outputFormat);
	}
	
	
	/**
	 * The codonJuggle method invokes BOOST's codon-juggling functionality.
	 *  
	 * @param designSequences  ... the sbol document that contains the input sequences
	 * @param targetNamespace ... namespace in that the new elements (ComponentDefinition, 
	 *        Sequence etc) should be placed into
	 * @param bAutoAnnotate ... true ... all sequences exclusively 5'-3' protein coding sequences (is only considered 
	 * when the sequences don't have feature annotations, e.g. FASTA or CSV)
	 * @param strategy ... the codon replacement strategy 
	 * @param filenameCodonUsageTable ... the name of the file that contains the codon usage table
	 * @param outputFormat ... the desired output format
	 * 
	 * @throws BOOSTClientException 
	 * @throws IOException
	 * @throws SBOLConversionException 
	 * @throws JSONException 
	 * @throws BOOSTAPIsException 
	 */
	public String codonJuggle(
			final SBOLDocument designSequences, 
			final String targetNamespace, boolean bAutoAnnotate, 
			Strategy strategy, final String filenameCodonUsageTable,
			final FileFormat outputFormat)
				throws BOOSTClientException, BOOSTBackEndException, IOException, 
				JSONException, SBOLConversionException {
		
		// construct the request's JSON object 
		JSONObject requestData = RequestBuilder.buildCodonJuggle(designSequences, 
				targetNamespace,  bAutoAnnotate, strategy, filenameCodonUsageTable, outputFormat);

		return this.submitJob(requestData);
	}
	
	/**
	 * The verify method submits a job to BOOST that verifies 
	 * sequences against DNA synthesis constraints.
	 * 
	 * @param sequencesFileName ... the name of the file that contains the sequences
	 * @param targetNamespace ... namespace in that the new elements (ComponentDefinition, 
	 *        Sequence etc) should be placed into
	 * @param constraintsFilename ... the name of the file that contains the DNA synthesis constraints
	 * @param sequencePatternsFilename ... the name of the file that contains sequence patterns
	 * 
	 * @return the UUID of the submitted job
	 * 
	 * @throws BOOSTClientException
	 * @throws SBOLConversionException 
	 * @throws SBOLValidationException 
	 * @throws BOOSTAPIsException 
	 * 
	 */
	public String dnaVarification(
			final String sequencesFileName, 
			final String targetNamespace,
			Vendor vendor, 
			final String sequencePatternsFilename)
				throws BOOSTClientException, BOOSTBackEndException, 
				IOException, SBOLValidationException, SBOLConversionException {

		// verify for filename 
		ParameterValueVerifier.verifyFilename(BOOSTConstants.INPUT_FILENAME, sequencesFileName);

		// read the file as SBOLDocument
		SBOLDocument document = SBOLReader.read(sequencesFileName);
		
		return this.dnaVerification(document, targetNamespace, vendor, sequencePatternsFilename);
		
	}
	
	/**
	 * The verify method submits a job to BOOST that verifies 
	 * sequences against DNA synthesis constraints.
	 * 
	 * @param codingSequence ... the sbol document that contains the sequences
	 * @param targetNamespace ... namespace in that the new elements (ComponentDefinition, 
	 *        Sequence etc) should be placed into
	 * @param constraintsFilename ... the name of the file that contains the DNA synthesis constraints
	 * @param sequencePatternsFilename ... the name of the file that contains sequence patterns
	 * 
	 * @return the UUID of the submitted job
	 * 
	 * @throws BOOSTClientException
	 * @throws SBOLConversionException 
	 * @throws JSONException 
	 * @throws BOOSTAPIsException 
	 * 
	 */
	public String dnaVerification(
			final SBOLDocument codingSequence, 
			final String targetNamespace,
			Vendor vendor, 
			final String sequencePatternsFilename)
				throws BOOSTClientException, BOOSTBackEndException, IOException, 
				JSONException, SBOLConversionException {

		// represent the request data in JSON and
		// submit it to BOOST's Job Queue Management System (JQMS)
		return submitJob(RequestBuilder.buildVerify(
				codingSequence, targetNamespace, vendor, sequencePatternsFilename));
	}

	/**
	 * The partition() method Partitioning of large DNA sequences into synthesizable
	 * building blocks with partial overlaps for an efficient assembly.
	 * 
	 * @param codingSequence ... the name of the file that contains input sequences 
	 * @param targetNamespace ... namespace in that the new elements (ComponentDefinition, 
	 *        Sequence etc) should be placed into
	 * @param fivePrimeVectorOverlap ... 5-prime-vector overlap
	 * @param threePrimeVectorOverlap ... 3-prime-vector overlap
	 * @param minLengthBB ... min length of sequence
	 * @param maxLengthBB ... max length of sequence
	 * @param minOverlapGC ... minimum %GC content of overlap sequence
	 * @param optOverlapGC ... optimum %GC content of overlap sequence
	 * @param maxOverlapGC ... maximum %GC content of overlap sequence
	 * @param minOverlapLength ... minimum length of overlap sequences
	 * @param optOverlapLength ... optimum length of overlap sequences
	 * @param maxOverlapLength ... maximum length of overlap sequences
	 * @param minPrimerLength ... minimum length of primer
	 * @param maxPrimerLength ... maximum length of primer
	 * @param maxPrimerTm
	 *  
	 * @throws BOOSTClientException
	 * @throws BOOSTBackEndException 
	 * @throws SBOLConversionException 
	 * @throws IOException 
	 * @throws SBOLValidationException 
	 * @throws BOOSTAPIsException 
	 */

	public String partition(
			final String sequencesFilename, 
			final String targetNamespace,
			String fivePrimeVectorOverlap, 
			String threePrimeVectorOverlap,
			String minLengthBB, 
			String maxLengthBB, 
			String minOverlapGC, 
			String optOverlapGC, 
			String maxOverlapGC, 
			String minOverlapLength, 
			String optOverlapLength,
			String maxOverlapLength,
			String minPrimerLength,
			String maxPrimerLength, 
			String maxPrimerTm) 
					throws BOOSTClientException, BOOSTBackEndException, 
					SBOLValidationException, IOException, SBOLConversionException {
		
		// verify for filename 
		ParameterValueVerifier.verifyFilename(BOOSTConstants.INPUT_FILENAME, sequencesFilename);

		// read the file as SBOLDocument
		SBOLDocument document = SBOLReader.read(sequencesFilename);
		
		return this.partition(document, targetNamespace, fivePrimeVectorOverlap, threePrimeVectorOverlap, 
				minLengthBB, maxLengthBB, minOverlapGC, optOverlapGC, maxOverlapGC, minOverlapLength, 
				optOverlapLength, maxOverlapLength, minPrimerLength, maxPrimerLength, maxPrimerTm);		
	}
	
	
	/**
	 * The partition() method Partitioning of large DNA sequences into synthesizable
	 * building blocks with partial overlaps for an efficient assembly.
	 * 
	 * @param codingSequence ... the sbol document that contains the input sequences
	 * @param targetNamespace ... namespace in that the new elements (ComponentDefinition, 
	 *        Sequence etc) should be placed into
     * @param fivePrimeVectorOverlap ... 5-prime-vector overlap
	 * @param threePrimeVectorOverlap ... 3-prime-vector overlap
	 * @param minLengthBB ... min length of sequence
	 * @param maxLengthBB ... max length of sequence
	 * @param minOverlapGC ... minimum %GC content of overlap sequence
	 * @param optOverlapGC ... optimum %GC content of overlap sequence
	 * @param maxOverlapGC ... maximum %GC content of overlap sequence
	 * @param minOverlapLength ... minimum length of overlap sequences
	 * @param optOverlapLength ... optimum length of overlap sequences
	 * @param maxOverlapLength ... maximum length of overlap sequences
	 * @param minPrimerLength ... minimum length of primer
	 * @param maxPrimerLength ... maximum length of primer
	 * @param maxPrimerTm
	 * 
	 * @return
	 * 
	 * @throws BOOSTClientException
	 * @throws BOOSTBackEndException
	 * @throws JSONException
	 * @throws UnsupportedEncodingException
	 * @throws SBOLConversionException
	 */
	public String partition(
			SBOLDocument codingSequence,
			final String targetNamespace,
			String fivePrimeVectorOverlap, 
			String threePrimeVectorOverlap,
			String minLengthBB, 
			String maxLengthBB, 
			String minOverlapGC, 
			String optOverlapGC, 
			String maxOverlapGC, 
			String minOverlapLength, 
			String optOverlapLength,
			String maxOverlapLength,
			String minPrimerLength,
			String maxPrimerLength, 
			String maxPrimerTm) 
					throws BOOSTClientException, BOOSTBackEndException, JSONException, 
					UnsupportedEncodingException, SBOLConversionException {
		
		// construct the request's JSON object
		JSONObject requestData = RequestBuilder.buildPartition(codingSequence, targetNamespace,
				fivePrimeVectorOverlap, threePrimeVectorOverlap, minLengthBB, maxLengthBB, minOverlapGC,
				optOverlapGC, maxOverlapGC, minOverlapLength, optOverlapLength, maxOverlapLength, 
				minPrimerLength, maxPrimerLength, maxPrimerTm);

		return this.submitJob(requestData);
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
	 * @throws BOOSTAPIsException 
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
	 * @throws BOOSTAPIsException 
	 */
	public JSONObject getPredefinedHosts() throws BOOSTClientException {

			// get the status of the job
			Response response = RESTInvoker
					.sendGet(BOOSTResources.BOOST_REST_URL + BOOSTResources.GET_PREDEFINED_HOSTS_RESOURCE, this.token);

			switch (response.getStatus()) {
			case 200:
				JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
				return jsonResponse;
			}
		return (JSONObject) null;
	}
	
	/**
	 * 
	 * @param jobUUID
	 * @return
	 * @throws BOOSTClientException
	 * @throws BOOSTBackEndException
	 * @throws BOOSTAPIsException 
	 */
	public JSONObject getJobReport(final String jobUUID)
			throws BOOSTClientException, BOOSTBackEndException {

			// get the status of the job
			Response response = RESTInvoker.sendGet(
					BOOSTResources.BOOST_REST_URL + BOOSTResources.GET_JOB_RESOURCE + "/" + jobUUID, this.token);

			switch (response.getStatus()) {
			case 200:
				JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));

				if (jsonResponse.has(JSONKeys.JOB_INFORMATION)) {

					JSONObject jsonJob = jsonResponse.getJSONObject(JSONKeys.JOB_INFORMATION);

					if (jsonJob.has(JSONKeys.JOB_REPORT)) {
						return jsonJob.getJSONObject(JSONKeys.JOB_REPORT);
					}
				}
			}
		return (JSONObject) null;
	}
	
	
	/**
	 * The polish() method verifies the sequences in a given file against the 
	 * gene synthesis constraints of a commercial synthesis vendor. 
	 * In case of violations, the polish() method modifies the coding regions 
	 * of the sequence using the specified codon replacement strategy.
	 *  
	 * @param codingSequence ... the name of the file that contains the sequences
	 * @param targetNamespace ... namespace in that the new elements (ComponentDefinition, 
	 *        Sequence etc) should be placed into
	 * @param type ... the type of the sequences, i.e. DNA, RNA, Protein
	 * @param bCodingSequences ... if the sequences are encoded in a format that does not 
	 *        support sequence feature annotations and if bCoding sequences is set to true, 
	 *        then are all sequences are treated as coding sequences. If the sequences are 
	 *        encoded in a format that does support sequence feature annotations, then the 
	 *        bCodingSequences flag is ignored. 
	 * @param vendor ... the name of commercial synthesis provider
	 * @param strategy ... the codon replacement strategy
	 * @param codonUsageTableFilename ... the name of the file that contains the codon 
	 *        usage table
	 * 
	 * @throws BOOSTClientException
	 * @throws BOOSTBackEndException 
	 * @throws SBOLConversionException 
	 * @throws IOException 
	 * @throws SBOLValidationException 
	 * @throws BOOSTAPIsException 
	 */
	public String polish(
			final String sequencesFilename, 
			final String targetNamespace,
			boolean bCodingSequences,
			Vendor vendor, 
			Strategy strategy, 
			final FileFormat outputFormat,
			final String codonUsageTable) 
				throws BOOSTClientException, BOOSTBackEndException, 
				SBOLValidationException, IOException, SBOLConversionException {
	
		// verify for filename 
		ParameterValueVerifier.verifyFilename(BOOSTConstants.INPUT_FILENAME, sequencesFilename);

		// read the file as SBOLDocument
		SBOLDocument document = SBOLReader.read(sequencesFilename);
		
		return this.polish(document, targetNamespace, bCodingSequences, vendor, strategy, 
				outputFormat, codonUsageTable);
	}
	
	
	/**
	 * The polish() method verifies the sequences in a given file against the 
	 * gene synthesis constraints of a commercial synthesis vendor. 
	 * In case of violations, the polish() method modifies the coding regions 
	 * of the sequence using the specified codon replacement strategy.
	 *  
	 * @param codingSequence ... the sbol file that contains the sequences
	 * @param targetNamespace ... namespace in that the new elements (ComponentDefinition, 
	 *        Sequence etc) should be placed into
	 * @param type ... the type of the sequences, i.e. DNA, RNA, Protein
	 * @param bCodingSequences ... if the sequences are encoded in a format that does not 
	 *        support sequence feature annotations and if bCoding sequences is set to true, 
	 *        then are all sequences are treated as coding sequences. If the sequences are 
	 *        encoded in a format that does support sequence feature annotations, then the 
	 *        bCodingSequences flag is ignored. 
	 * @param vendor ... the name of commercial synthesis provider
	 * @param strategy ... the codon replacement strategy
	 * @param codonUsageTableFilename ... the name of the file that contains the codon 
	 * usage table
	 * 
	 * @throws BOOSTClientException
	 * @throws BOOSTBackEndException 
	 * @throws SBOLConversionException 
	 * @throws UnsupportedEncodingException 
	 * @throws JSONException 
	 * @throws BOOSTAPIsException 
	 */
	public String polish(
			final SBOLDocument codingSequence, 
			final String targetNamespace,
			boolean bCodingSequences,
			Vendor vendor, 
			Strategy strategy, 
			final FileFormat outputFormat,
			final String codonUsageTable) 
				throws BOOSTClientException, BOOSTBackEndException, JSONException, UnsupportedEncodingException, SBOLConversionException {
		
		// construct the request's JSON object
		JSONObject requestData = RequestBuilder.buildPolish( codingSequence, targetNamespace, 
			bCodingSequences, vendor, strategy, outputFormat, codonUsageTable);
				
		 return submitJob(requestData);	
	}
}
