package gov.doe.jgi.boost.client;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import gov.doe.jgi.boost.client.constants.BOOSTConstants;
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
	 * @param filenameCodonUsageTable ... a filename that contains the codon usage of the target host
	 * @param outputFormat ... the desired format of the reverse-translated sequences
	 * 
	 * @return a JSONObject that represents the input values
	 * 
	 * @throws BOOSTClientException ... if any given value is NULL or any given String value is empty
	 * @throws IOException ... if an given filename points to a non-existing file
	 */
	public static JSONObject buildReverseTranslate(
			final String filenameSequences, 
			Strategy strategy, final String filenameCodonUsageTable, 
			final FileFormat outputFormat) 
					throws BOOSTClientException {
		
		// verify the values
		ParameterValueVerifier.verifyFilename(BOOSTConstants.INPUT_FILENAME, filenameSequences);
		ParameterValueVerifier.verifyNull(BOOSTConstants.CODON_STRATEGY, strategy);
		ParameterValueVerifier.verifyFilename(BOOSTConstants.CODON_USAGE_TABLE, filenameCodonUsageTable);
		ParameterValueVerifier.verifyNull(BOOSTConstants.OUTPUT_FORMAT, outputFormat);

		// build the JSON representation of the input values
		JSONObject reverseTranslateData = new JSONObject();
		
		// sequence information
		reverseTranslateData.put(JSONKeys.SEQUENCE_INFORMATION,  
				RequestBuilder.buildSequenceData(filenameSequences, SequenceType.PROTEIN, true));
		
		// modification information
		reverseTranslateData.put(JSONKeys.MODIFICATION_INFORMATION,
				RequestBuilder.buildModificationData(strategy, filenameCodonUsageTable));
		
		// output information
		reverseTranslateData.put(JSONKeys.OUTPUT_INFORMATION, 
				RequestBuilder.buildOutputData(outputFormat));
		
		return reverseTranslateData;
	}
	
	
	/**
	 * 
	 * @param filenameSequences
	 * @param strategy
	 * @param filenameCodonUsageTable
	 * @param outputFormat
	 * @return
	 * @throws BOOSTClientException
	 */
	public static JSONObject buildCodonJuggle(
			final String filenameSequences, boolean bAutoAnnotate, 
			Strategy strategy, final String filenameCodonUsageTable, 
			final FileFormat outputFormat) 
					throws BOOSTClientException {
		
		// verify the values
		ParameterValueVerifier.verifyFilename(BOOSTConstants.INPUT_FILENAME, filenameSequences);
		ParameterValueVerifier.verifyNull(BOOSTConstants.CODON_STRATEGY, strategy);
		ParameterValueVerifier.verifyFilename(BOOSTConstants.CODON_USAGE_TABLE, filenameCodonUsageTable);
		ParameterValueVerifier.verifyNull(BOOSTConstants.OUTPUT_FORMAT, outputFormat);
		
		// build the JSON representation of the input values
		JSONObject reverseTranslateData = new JSONObject();
		
		// sequence information
		reverseTranslateData.put(JSONKeys.SEQUENCE_INFORMATION,  
				RequestBuilder.buildSequenceData(filenameSequences, SequenceType.DNA, bAutoAnnotate));
		
		// modification information
		reverseTranslateData.put(JSONKeys.MODIFICATION_INFORMATION,
				RequestBuilder.buildModificationData(strategy, filenameCodonUsageTable));
		
		// output information
		reverseTranslateData.put(JSONKeys.OUTPUT_INFORMATION, 
				RequestBuilder.buildOutputData(outputFormat));
		
		return reverseTranslateData;
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
			throw new BOOSTClientException(ioe.getLocalizedMessage());
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
