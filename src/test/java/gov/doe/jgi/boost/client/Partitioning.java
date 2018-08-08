package gov.doe.jgi.boost.client;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidationException;

import gov.doe.jgi.boost.client.constants.BOOSTClientConfigs;
import gov.doe.jgi.boost.client.constants.LoginCredentials;
import gov.doe.jgi.boost.enums.FileFormat;
import gov.doe.jgi.boost.enums.Strategy;
import gov.doe.jgi.boost.enums.Vendor;
import gov.doe.jgi.boost.exception.BOOSTBackEndException;
import gov.doe.jgi.boost.exception.BOOSTClientException;

public class Partitioning {

	public static final String boostNamespace = "https://boost.jgi.doe.gov/";

	/**
	 * 
	 * @param filename
	 * 
	 * @return
	 * 
	 * @throws BOOSTClientException
	 * @throws BOOSTBackEndException
	 * @throws IOException
	 * @throws SBOLValidationException
	 * @throws SBOLConversionException
	 */
	public static OutputFiles doPartitioning(final String filename) 
			throws BOOSTClientException, BOOSTBackEndException, IOException, SBOLValidationException, SBOLConversionException {
		
		OutputFiles outputFiles = (OutputFiles)null;
		
		BOOSTClient client = new BOOSTClient(LoginCredentials.mJWT);
		
		// set the target namespace
		BOOSTClientConfigs.SBOL_TARGET_NAMESPACE = "https://boost.jgi.doe.gov/";
		
		// we store all submitted jobs in a hash-set
		Set<String> jobUUIDs = new HashSet<String>();

		String fivePrimeVectorOverlap = "ACGTACGTACGT";
		String threePrimeVectorOverlap = "ACGTACGTACGT";
		String minLengthBB = "300";
		String maxLengthBB = "1800";
		String minOverlapGC = "40";
		String maxOverlapGC = "70";
		String optOverlapGC = "62";
		String minOverlapLength = "20";
		String optOverlapLength = "20";
		String maxOverlapLength = "30";
		String minPrimerLength = "7";
		String maxPrimerLength = "7";
		String maxPrimerTm = "60";
		
		// polish the given DNA
		String partitionJobUUID = client.partition(
				filename,           						// input sequence
				BOOSTClientConfigs.SBOL_TARGET_NAMESPACE,	// the target namespace
				fivePrimeVectorOverlap, 
				threePrimeVectorOverlap, 
				minLengthBB, maxLengthBB, 
				minOverlapGC, optOverlapGC, maxOverlapGC, 
				minOverlapLength, optOverlapLength, maxOverlapLength, 
				minPrimerLength, maxPrimerLength, maxPrimerTm);
		if (null != partitionJobUUID) {
			jobUUIDs.add(partitionJobUUID);
			System.out.println("Job-UUID for Partition: " + partitionJobUUID);
		}

		// for all jobs, we check their status
		for(String jobUUID : jobUUIDs) {
			
			JSONObject jobReport = null;
			while(null == (jobReport = client.getJobReport(jobUUID))) {
				try {
					Thread.sleep(2000);
				} catch(Exception e) {}
				System.out.println("Job-UUID for Partition: " + partitionJobUUID);
			}

			outputFiles = client.saveJobReport(jobUUID, jobReport);
		}
		
		return outputFiles;
	}

	/**
	 * 
	 * @param directory
	 * 
	 * @throws SBOLValidationException
	 * @throws IOException
	 * @throws SBOLConversionException
	 * @throws BOOSTClientException
	 * @throws BOOSTBackEndException
	 */
	public static void design(final String directory) 
			throws SBOLValidationException, IOException, SBOLConversionException, BOOSTClientException, BOOSTBackEndException {
		
		// the name of the file that contains the SBOLDocument, which is the output of SBOLDesigner
		String sbolDesignerDocumentFilename = directory + "/01-sbol-designer-output.sbol.xml";

		// call BOOST's API to do codon-juggling
		OutputFiles outputFiles = doPartitioning(sbolDesignerDocumentFilename);
		
//		// copy the file from the BOOST client's job-directory
//		// to the test-directory
//		FileUtils.copyFile(Paths.get(outputFilename).toFile(), Paths.get(directory, "02-boost-output.sbol.xml").toFile());
//		
		// for partitioning, we need to read to Provenance file since BOOST always 
		// generates a FASTA file for the building blocks
		// read the SBOLDocument that is being returned by BOOST
		SBOLDocument document = SBOLReader.read(outputFiles.provenanceFile.toFile());

		//------------------------------------------------------------------------------------
		System.out.println("------------- ROOT COMPONENTDEFINITIONS ------------");
		Set<URI> wasDerivedFromURIs = null;
        for (org.sbolstandard.core2.ComponentDefinition componentDefinition : 
        			document.getRootComponentDefinitions()) {
        	System.out.println("root CD: " + componentDefinition.getIdentity());
        	wasDerivedFromURIs = componentDefinition.getWasDerivedFroms();
        	System.out.println("    wasDerivedFroms: " + wasDerivedFromURIs);
        }
		System.out.println("----------------------------------------------------");
		//------------------------------------------------------------------------------------

	}


	/**
	 * 
	 * @param args
	 * 
	 * @throws SBOLValidationException
	 * @throws IOException
	 * @throws SBOLConversionException
	 * @throws BOOSTClientException
	 * @throws BOOSTBackEndException
	 */
	public static void main(String[] args) 
			throws SBOLValidationException, IOException, SBOLConversionException, BOOSTClientException, BOOSTBackEndException {
		design("./data/test/partitioning/01-single-cds/");
	}
	
	
	public static File[] listSubDirectories(final String directory) {
		return new File(directory).listFiles(
				new FileFilter() {
		    @Override
		    public boolean accept(File file) {
		        return file.isDirectory();
		    }
		});
	}

}
