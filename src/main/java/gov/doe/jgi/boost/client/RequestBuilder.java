package gov.doe.jgi.boost.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLWriter;

import gov.doe.jgi.boost.client.constants.BOOSTClientConfigs;
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
	 * @param designSequences ... a SBOLDocument that contains the protein sequences
	 * @param strategy ... the desired strategy for codon selection
	 * @param codonUsageTable ... either the name of a predefined host or a 
	 *                            filename that contains the codon usage of the target host
	 * @param outputFormat ... the desired format of the reverse-translated sequences
	 * 
	 * @return a JSONObject that represents the input values
	 * 
	 * @throws BOOSTClientException ... if any given value is NULL or any given String value is empty
	 * @throws SBOLConversionException 
	 * @throws UnsupportedEncodingException 
	 * @throws JSONException 
	 * @throws IOException ... if an given filename points to a non-existing file
	 */
	public static JSONObject buildReverseTranslate(
			final SBOLDocument designSequences, 
			Strategy strategy, final String codonUsageTable, 
			final FileFormat outputFormat) 
					throws BOOSTClientException, JSONException, UnsupportedEncodingException, SBOLConversionException {
		
		// verify the values
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
				RequestBuilder.buildSequenceData(designSequences, SequenceType.PROTEIN, true));
		
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
	 * @param designSequences
	 * @param strategy
	 * @param codonUsageTable ... either predefined-host or a filename
	 * @param outputFormat
	 * @return
	 * @throws BOOSTClientException
	 * @throws SBOLConversionException 
	 * @throws UnsupportedEncodingException 
	 * @throws JSONException 
	 */
	public static JSONObject buildCodonJuggle(
			final SBOLDocument designSequences, boolean bAutoAnnotate, 
			Strategy strategy, final String codonUsageTable, 
			final FileFormat outputFormat) 
					throws BOOSTClientException, JSONException, UnsupportedEncodingException, SBOLConversionException {
		
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
				RequestBuilder.buildSequenceData(designSequences, SequenceType.DNA, bAutoAnnotate));
		
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
	 * @param designSequences
	 * @param constraintsFilename ... the 
	 * @param sequencePatternsFilename ... the name of the file that contains sequence patterns (optionally)
	 * @return
	 * @throws BOOSTClientException
	 * @throws IOException
	 * @throws SBOLConversionException 
	 * @throws JSONException 
	 */
	public static JSONObject buildVerify(
			final SBOLDocument designSequences, 
			Vendor vendor,
			final String sequencePatternsFilename)
				throws BOOSTClientException, IOException, JSONException, SBOLConversionException {
		
		//---------------------------------
		// verify the given values
		ParameterValueVerifier.verifyNull(BOOSTConstants.VENDOR, vendor);
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
				RequestBuilder.buildSequenceData(designSequences, SequenceType.DNA, false));
		//---------------------------------

		//---------------------------------
		// SEQUENCE PATTERNS
		requestData.put(JSONKeys.PATTERN_INFORMATION,
				RequestBuilder.buildSequencePatterns(sequencePatternsFilename));
		//---------------------------------

		//---------------------------------
		// CONSTRAINTS
		requestData.put(JSONKeys.VENDOR_NAME, vendor);
		//---------------------------------

		
		return requestData;
	}
	
	
	/**
	 *  
	 * @param designSequences ... a SBOLDocument that contains the sequences
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
	 * @throws BOOSTClientException
	 * @throws SBOLConversionException 
	 * @throws UnsupportedEncodingException 
	 * @throws JSONException 
	 */
	public static JSONObject buildPolish(
			final SBOLDocument designSequences, 
			boolean bCodingSequences,
			Vendor vendor, 
			Strategy strategy, 
			final FileFormat outputFormat,
			final String codonUsageTable) 
				throws BOOSTClientException, JSONException, UnsupportedEncodingException, SBOLConversionException {
		
		//-------------------------------------
		// verify the given values
		ParameterValueVerifier.verifyNull(BOOSTConstants.VENDOR, vendor);
		try {
			ParameterValueVerifier.verifyFilename(BOOSTConstants.CODON_USAGE_TABLE, codonUsageTable);
		} catch(Exception e) {}
		ParameterValueVerifier.verifyNull(BOOSTConstants.OUTPUT_FORMAT, outputFormat);
		ParameterValueVerifier.verifyNull(BOOSTConstants.STRATEGY, strategy);
		//----------------------------------------
		
		JSONObject modifiedData = new JSONObject();
		
		//-----------------------------------------
		//JOB INFORMATION
		modifiedData.put(JSONKeys.JOB_INFORMATION,
				RequestBuilder.buildJobInformation(BOOSTFunctions.POLISH));
		//-----------------------------------------
		
		// sequence information
		modifiedData.put(JSONKeys.SEQUENCE_INFORMATION,
				RequestBuilder.buildSequenceData(designSequences, SequenceType.DNA, bCodingSequences ));
		//-----------------------------------------
		
		// constraints
		modifiedData.put(JSONKeys.VENDOR_NAME, vendor);
		//------------------------------------------
		
		// modification information
		modifiedData.put(JSONKeys.MODIFICATION_INFORMATION,
				RequestBuilder.buildModificationData(strategy, codonUsageTable));
		//-------------------------------------------
		
		// output information
		modifiedData.put(JSONKeys.OUTPUT_INFORMATION, 
				RequestBuilder.buildOutputData(outputFormat));
		//-------------------------------------------------
		
		
		return modifiedData;
	}
	
	/**
	 * The buildPartation wraps all required information for  
	 * BOOST's dna partition functionality into a JSON representation  
	 * 
	 * @return a JSONObject that represents the input values
	 * 
	 * @throws BOOSTClientException ... if any given value is NULL or any given String value is empty
	 * @throws SBOLConversionException 
	 * @throws UnsupportedEncodingException 
	 * @throws JSONException 
	 * */
	
	public static JSONObject buildPartition(
			final SBOLDocument designSequences,
			final String fivePrimeVectorOverlap,
			final String threePrimeVectorOverlap,
			final String minLengthBB, final String maxLengthBB,
			final String minOverlapGC, final String optOverlapGC, final String maxOverlapGC,
			final String minOverlapLength, final String optOverlapLength, final String maxOverlapLength,
			final String minPrimerLength, final String maxPrimerLength, final String maxPrimerTm)
					throws BOOSTClientException, JSONException, UnsupportedEncodingException, SBOLConversionException{
					
		//verify the values
		//ParameterValueVerifier.verifyFilename(BOOSTConstants.INPUT_FILENAME, designSequences);
		ParameterValueVerifier.verifyValue(BOOSTConstants.FIVE_PRIME_VECTOR_OVERLAP, fivePrimeVectorOverlap);
		ParameterValueVerifier.verifyValue(BOOSTConstants.THREE_PRIME_VECTOR_OVERLAP, threePrimeVectorOverlap);
		ParameterValueVerifier.verifyValue(BOOSTConstants.MIN_BB_LENGTH, minLengthBB);
		ParameterValueVerifier.verifyValue(BOOSTConstants.MAX_BB_LENGTH, maxLengthBB);
		ParameterValueVerifier.verifyLengths(minLengthBB, maxLengthBB);
		
		ParameterValueVerifier.verifyValue(BOOSTConstants.MIN_OVERLAP_GC, minOverlapGC);
		ParameterValueVerifier.verifyValue(BOOSTConstants.OPT_OVERLAP_GC, optOverlapGC);
		ParameterValueVerifier.verifyValue(BOOSTConstants.MAX_OVERLAP_GC, maxOverlapGC);
		ParameterValueVerifier.partitionGCOverlap(minOverlapGC, optOverlapGC, maxOverlapGC);
		
		ParameterValueVerifier.verifyValue(BOOSTConstants.MIN_OVERLAP_LENGTH, minOverlapLength);
		ParameterValueVerifier.verifyValue(BOOSTConstants.OPT_OVERLAP_LENGTH, optOverlapLength);
		ParameterValueVerifier.verifyValue(BOOSTConstants.MAX_OVERLAP_LENGTH, maxOverlapLength);
		ParameterValueVerifier.partationOverlapLen(minOverlapLength, optOverlapLength, maxOverlapLength);
		
		ParameterValueVerifier.verifyValue(BOOSTConstants.MIN_PRIMER_LENGTH, minPrimerLength);
		ParameterValueVerifier.verifyValue(BOOSTConstants.MAX_PRIMER_LENGTH, maxPrimerLength);
		ParameterValueVerifier.verifyPartitionPrimerLength(minPrimerLength, maxPrimerLength);

		ParameterValueVerifier.verifyValue(BOOSTConstants.MAX_PRIMER_TM, maxPrimerTm);

		//----------------------------------------------
		
		// build the JSON representation of the input values
		JSONObject partationData = new JSONObject();
				
		//----------------------------------------------
		
		
		// JOB INFORMATION
		partationData.put(JSONKeys.JOB_INFORMATION, 
				RequestBuilder.buildJobInformation(BOOSTFunctions.PARTITION));
		//----------------------------------------------
		

		// sequence information
		partationData.put(JSONKeys.SEQUENCE_INFORMATION,  
				RequestBuilder.buildSequenceData(designSequences, SequenceType.DNA, false));
		
		// partition information
		JSONObject textParameters = new JSONObject();
		textParameters.put(JSONKeys.TEXT, RequestBuilder.buildPartitionData(designSequences, fivePrimeVectorOverlap,
						threePrimeVectorOverlap, minLengthBB, maxLengthBB, minOverlapGC, optOverlapGC, 
						maxOverlapGC, minOverlapLength, optOverlapLength, maxOverlapLength, 
						minPrimerLength, maxPrimerLength, maxPrimerTm));
		partationData.put(JSONKeys.PARTITIONING_INFORMATION, textParameters);
	    
		
		return partationData;	
		
	}

	/**
	 * @param designSequences
	 * @param fivePrimeVectorOverlap
	 * @param threePrimeVectorOverlap
	 * @param minLengthBB
	 * @param maxLengthBB
	 * @param minOverlapGC
	 * @param optOverlapGC
	 * @param maxOverlapGC
	 * @param minOverlapLength
	 * @param optOverlapLength
	 * @param maxOverlapLength
	 * @param minPrimerLength
	 * @param maxPrimerLength
	 * @param maxPrimerTm
	 * @return
	 */
	private static JSONObject buildPartitionData(final SBOLDocument designSequences,
			final String fivePrimeVectorOverlap, final String threePrimeVectorOverlap,
			final String minLengthBB, final String maxLengthBB, 
			final String minOverlapGC, final String optOverlapGC, final String maxOverlapGC, 
			final String minOverlapLength, final String optOverlapLength, final String maxOverlapLength,
			final String minPrimerLength, final String maxPrimerLength, final String maxPrimerTm) {
		
		JSONObject partitionData = new JSONObject();
		
		//JSONObject subPartationData = new JSONObject();
		partitionData.put(JSONKeys.FIVE_PRIME_VECTOR_OVERLAP, fivePrimeVectorOverlap);
		partitionData.put(JSONKeys.THREE_PRIME_VECTOR_OVERLAP, threePrimeVectorOverlap);
		partitionData.put(JSONKeys.MAX_BB_LENGTH, maxLengthBB);
		partitionData.put(JSONKeys.MIN_BB_LENGTH, minLengthBB);
		partitionData.put(JSONKeys.MAX_OVERLAP_GC, maxOverlapGC);
		partitionData.put(JSONKeys.BATCH, "");
		partitionData.put(JSONKeys.MIN_OVERLAP_GC, minOverlapGC);
		partitionData.put(JSONKeys.MAX_OVERLAP_LENGTH, maxOverlapLength);
		partitionData.put(JSONKeys.OPT_OVERLAP_GC, optOverlapGC);
		partitionData.put(JSONKeys.OPT_OVERLAP_LENGTH, optOverlapLength);
		partitionData.put(JSONKeys.MIN_OVERLAP_LENGTH, minOverlapLength);

		partitionData.put(JSONKeys.MIN_PRIMER_LENGTH, minPrimerLength);
		partitionData.put(JSONKeys.MAX_PRIMER_LENGTH, maxPrimerLength);
		partitionData.put(JSONKeys.MAX_PRIMER_TM, maxPrimerTm);

		JSONObject partationParameters = new JSONObject();
		partationParameters.put(JSONKeys.PARTITIONING_INFORMATION, partitionData);
		
		return partitionData;
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
	 * @throws SBOLConversionException 
	 * @throws UnsupportedEncodingException 
	 * @throws IOException 
	 */
	public static JSONObject buildSequenceData(final SBOLDocument designSequences, SequenceType type, boolean bAutoAnnotate) 
			throws BOOSTClientException, SBOLConversionException, UnsupportedEncodingException {

		// sequence information
		JSONObject sequenceData = new JSONObject();
		
		// reading from SBOL document
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		SBOLWriter.write(designSequences,  outputStream);
		String designDoc = outputStream.toString("UTF-8");
		
		// put its content into the JSON object
		if(designDoc != null && !designDoc.isEmpty()) {
			sequenceData.put(JSONKeys.TEXT, designSequences);
		}
		
		
		// sequence type
		JSONArray types = new JSONArray();
		types.put(type);
		sequenceData.put(JSONKeys.SEQUENCE_TYPE, types);
		
		// auto-annotate?
		sequenceData.put(JSONKeys.AUTO_ANNOTATE, bAutoAnnotate);
		
		// target namespace
		if(null != BOOSTClientConfigs.SBOL_TARGET_NAMESPACE && 
				!BOOSTClientConfigs.SBOL_TARGET_NAMESPACE.trim().isEmpty()) {
			sequenceData.put(JSONKeys.TARGET_NAMESPACE, BOOSTClientConfigs.SBOL_TARGET_NAMESPACE);
		}

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
