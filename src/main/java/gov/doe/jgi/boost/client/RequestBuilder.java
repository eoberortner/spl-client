package gov.doe.jgi.boost.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import gov.doe.jgi.boost.client.constants.BOOSTConstants;
import gov.doe.jgi.boost.client.constants.BOOSTFunctions;
import gov.doe.jgi.boost.client.constants.JSONKeys;
import gov.doe.jgi.boost.client.utils.FileUtils;
import gov.doe.jgi.boost.enums.FileFormat;
import gov.doe.jgi.boost.enums.GeneticCode;
import gov.doe.jgi.boost.enums.SequenceType;
import gov.doe.jgi.boost.enums.Strategy;
import gov.doe.jgi.boost.enums.Vendor;
import gov.doe.jgi.boost.exception.BOOSTClientException;

/**
 * 
 * @author Ernst Oberortner
 *
 */
public class RequestBuilder {

	/**
	 * The buildLogin method wraps a given username/password combination into 
	 * a JSON representation that can be submitted to the BOOST REST API
	 * 
	 * @param username ... the username
	 * @param password ... the password
	 * 
	 * @return a JSONObject that contains the username/password combo
	 * 
	 * @throws BOOSTClientException ... if any given String value is NULL or empty
	 */
	public static JSONObject buildLogin(final String username, final String password) 
			throws BOOSTClientException {

		ParameterValueVerifier.verifyValue(BOOSTConstants.USERNAME, username);
		ParameterValueVerifier.verifyValue(BOOSTConstants.PASSWORD, password);

		JSONObject loginData = new JSONObject();
		
		loginData.put("username", username);
		loginData.put("password", password);

		return loginData;
	}
	
	/**
	 * The buildReverseTranslate wraps all required information for  
	 * BOOST's reverse-translate functionality into a JSON representation  
	 * 
	 * @param filenameSequences ... a filename that contains the protein sequences
	 * @param strategy ... the desired strategy for codon selection
	 * @param codonUsageTable ... either the name of a predefined host or a 
	 *                            filename that contains the codon usage of the target host
	 * @param outputFormat ... the desired format of the reverse-translated sequences
	 * 
	 * @return a JSONObject that represents the input values
	 * 
	 * @throws BOOSTClientException ... if any given value is NULL or any given String value is empty
	 * @throws IOException ... if an given filename points to a non-existing file
	 */
	public static JSONObject buildReverseTranslate(
			final String filenameSequences, 
			Strategy strategy, final String codonUsageTable, 
			final FileFormat outputFormat) 
					throws BOOSTClientException {
		
		// verify the values
		ParameterValueVerifier.verifyFilename(BOOSTConstants.INPUT_FILENAME, filenameSequences);
		ParameterValueVerifier.verifyNull(BOOSTConstants.CODON_STRATEGY, strategy);
		try {
			ParameterValueVerifier.verifyFilename(BOOSTConstants.CODON_USAGE_TABLE, codonUsageTable);
		} catch(Exception e) {
//			e.printStackTrace();
		}
		ParameterValueVerifier.verifyNull(BOOSTConstants.OUTPUT_FORMAT, outputFormat);

		// build the JSON representation of the input values
		JSONObject reverseTranslateData = new JSONObject();

		//---------------------------------
		// JOB INFORMATION
		reverseTranslateData.put(JSONKeys.JOB_INFORMATION, 
				RequestBuilder.buildJobInformation(BOOSTFunctions.REVERSE_TRANSLATE));
		//---------------------------------


		// sequence information
		reverseTranslateData.put(JSONKeys.SEQUENCE_INFORMATION,  
				RequestBuilder.buildSequenceData(filenameSequences, SequenceType.PROTEIN, true));
		
		// modification information
		reverseTranslateData.put(JSONKeys.MODIFICATION_INFORMATION,
				RequestBuilder.buildModificationData(strategy, codonUsageTable));
		
		// output information
		reverseTranslateData.put(JSONKeys.OUTPUT_INFORMATION, 
				RequestBuilder.buildOutputData(outputFormat));
		
		return reverseTranslateData;
	}
	
	
	/**
	 * 
	 * @param filenameSequences
	 * @param strategy
	 * @param codonUsageTable ... either predefined-host or a filename
	 * @param outputFormat
	 * @return
	 * @throws BOOSTClientException
	 */
	public static JSONObject buildCodonJuggle(
			final String filenameSequences, boolean bAutoAnnotate, 
			Strategy strategy, final String codonUsageTable, 
			final FileFormat outputFormat) 
					throws BOOSTClientException {
		
		// verify the values
		ParameterValueVerifier.verifyFilename(BOOSTConstants.INPUT_FILENAME, filenameSequences);
		ParameterValueVerifier.verifyNull(BOOSTConstants.CODON_STRATEGY, strategy);
		try {
			ParameterValueVerifier.verifyFilename(BOOSTConstants.CODON_USAGE_TABLE, codonUsageTable);
		} catch(Exception e) {}
		ParameterValueVerifier.verifyNull(BOOSTConstants.OUTPUT_FORMAT, outputFormat);
		
		// build the JSON representation of the input values
		JSONObject reverseTranslateData = new JSONObject();

		// JOB INFORMATION
		reverseTranslateData.put(JSONKeys.JOB_INFORMATION, 
				RequestBuilder.buildJobInformation(BOOSTFunctions.CODON_JUGGLE));

		// sequence information
		reverseTranslateData.put(JSONKeys.SEQUENCE_INFORMATION,  
				RequestBuilder.buildSequenceData(filenameSequences, SequenceType.DNA, bAutoAnnotate));
		
		// modification information
		reverseTranslateData.put(JSONKeys.MODIFICATION_INFORMATION,
				RequestBuilder.buildModificationData(strategy, codonUsageTable));
		
		// output information
		reverseTranslateData.put(JSONKeys.OUTPUT_INFORMATION, 
				RequestBuilder.buildOutputData(outputFormat));
		
		return reverseTranslateData;
	}

	/**
	 * 
	 * @param filenameSequences
	 * @param constraintsFilename ... the 
	 * @param sequencePatternsFilename ... the name of the file that contains sequence patterns (optionally)
	 * @return
	 * @throws BOOSTClientException
	 * @throws IOException
	 */
	public static JSONObject buildVerify(
			final String filenameSequences, 
			final String constraintsFilename,
			final String sequencePatternsFilename)
				throws BOOSTClientException, IOException {
		
		//---------------------------------
		// verify the given values
		ParameterValueVerifier.verifyFilename(JSONKeys.SEQUENCE_INFORMATION, filenameSequences);
		ParameterValueVerifier.verifyFilename(JSONKeys.CONSTRAINTS_INFORMATION, constraintsFilename);
		// the sequence patterns filename is optional
		if(null != sequencePatternsFilename && !sequencePatternsFilename.trim().isEmpty()) {
			ParameterValueVerifier.verifyFilename(JSONKeys.PATTERN_INFORMATION, sequencePatternsFilename);
		}
		//---------------------------------

		JSONObject requestData = new JSONObject();
		
		//---------------------------------
		// JOB INFORMATION
		requestData.put(JSONKeys.JOB_INFORMATION, 
				RequestBuilder.buildJobInformation(BOOSTFunctions.VERIFY));
		//---------------------------------
		

		//---------------------------------
		// SEQUENCES
		requestData.put(JSONKeys.SEQUENCE_INFORMATION,  
				RequestBuilder.buildSequenceData(filenameSequences, SequenceType.DNA, false));
		//---------------------------------

		//---------------------------------
		// SEQUENCE PATTERNS
		requestData.put(JSONKeys.PATTERN_INFORMATION,
				RequestBuilder.buildSequencePatterns(sequencePatternsFilename));
		//---------------------------------

		//---------------------------------
		// CONSTRAINTS
		requestData.put(JSONKeys.CONSTRAINTS_INFORMATION,
				RequestBuilder.buildConstraints(constraintsFilename));
		//---------------------------------

		
		return requestData;
	}
	
	/**
	 * 
	 * @param function
	 * @return
	 * @throws BOOSTClientException
	 */
	public static JSONObject buildJobInformation(BOOSTFunctions function) 
			throws BOOSTClientException {
		
		return RequestBuilder.buildJobInformation(
				UUID.randomUUID().toString(), function);
	}
	
	/**
	 * 
	 * @param userdefinedJobId
	 * @param function
	 * @return
	 * @throws BOOSTClientException
	 */
	public static JSONObject buildJobInformation(
			final String userdefinedJobId, BOOSTFunctions function) 
			throws BOOSTClientException {
		
		JSONObject jobInformation = new JSONObject();
		jobInformation.put(JSONKeys.JOB_BOOST_FUNCTION, function.toString());
		
		return jobInformation;
	}

	/**
	 * 
	 * @param filename
	 * @param type
	 * @param bAutoAnnotate
	 * @return
	 * @throws BOOSTClientException
	 */
	public static JSONObject buildSequenceData(final String filename, SequenceType type, boolean bAutoAnnotate) 
			throws BOOSTClientException {

		// sequence information
		JSONObject sequenceData = new JSONObject();

		String sequences;
		try {
			sequences = FileUtils.readFile(filename);
		} catch (IOException e1) {
			throw new BOOSTClientException(e1.getLocalizedMessage());
		}
		sequenceData.put(JSONKeys.TEXT, sequences);
		
		// sequence type
		JSONArray types = new JSONArray();
		types.put(type);
		sequenceData.put(JSONKeys.SEQUENCE_TYPE, types);
		
		// auto-annotate?
		sequenceData.put(JSONKeys.AUTO_ANNOTATE, bAutoAnnotate);

		return sequenceData;
	}
	
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws BOOSTClientException
	 * @throws IOException
	 */
	public static JSONObject buildConstraints(final String constraints)
			throws BOOSTClientException, IOException {
		
		if(null == constraints || constraints.isEmpty()) {
			throw new BOOSTClientException("Invalid filename.");
		}

		JSONObject jsonConstraints = new JSONObject();

		// check if it's a file
		Path file = Paths.get(constraints);
		if(Files.isDirectory(file)) {
			throw new BOOSTClientException(constraints + " is a directory.");
		}
		if(Files.exists(file)) {
			
			String sConstraints = FileUtils.readFile(constraints);
			jsonConstraints.put(JSONKeys.CONSTRAINTS_TEXT, sConstraints);
		} else {
			// maybe it's a vendor?
			jsonConstraints.put(JSONKeys.VENDOR_NAME, constraints);
		}
		
		return jsonConstraints;
	}
	
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws BOOSTClientException
	 * @throws IOException
	 */
	public static JSONObject buildSequencePatterns(final String filename)
			throws BOOSTClientException, IOException {
		
		if(null == filename || filename.isEmpty()) {
			throw new BOOSTClientException("Invalid filename.");
		}
		
		// check that the file exists and that it is not a directory
		Path file = Paths.get(filename);
		if(!Files.exists(file)) {
			throw new BOOSTClientException("The file " + filename + " does not exist.");
		}
		if(Files.isDirectory(file)) {
			throw new BOOSTClientException(filename + " is a directory.");
		}

		// create the JSON object
		JSONObject sequencePatterns = new JSONObject();
		
		// read the file content and store it in the JSON object
		String fileContent = FileUtils.readFile(filename);
		sequencePatterns.put(JSONKeys.SEQUENCE_PATTERNS_TEXT, fileContent);
		
		return sequencePatterns;
	}
	
	/**
	 * 
	 * @param strategy
	 * @param filename
	 * @return
	 */
	public static JSONObject buildModificationData(final Strategy strategy, final String filename) 
			throws BOOSTClientException {
		
		JSONObject jsonData = new JSONObject();
		
		jsonData.put(JSONKeys.STRATEGY_NAME, strategy);
		
		try {
			jsonData.put(JSONKeys.TEXT, FileUtils.readFile(filename));
		} catch(IOException ioe) {
			jsonData.put("host-name", filename);
//			throw new BOOSTClientException(ioe.getLocalizedMessage());
		}
		
		jsonData.put(JSONKeys.STRATEGY_GENETIC_CODE, GeneticCode.STANDARD);
		
		return jsonData;
	}
	
	public static JSONObject buildOutputData(final FileFormat format) {
		JSONObject jsonData = new JSONObject();
		jsonData.put(JSONKeys.OUTPUT_FORMAT, format);
		return jsonData;
	}
	
	/**
	 * 
	 * @param vendor
	 * @return
	 */
	public static JSONObject buildConstraintsData(Vendor vendor) {
		JSONObject constraintsData = new JSONObject();
		constraintsData.put("vendor", vendor);
		
		return constraintsData;
	}

}
