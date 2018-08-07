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

public class Bug_CDSPolishing_JobRunning {

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
	public static String doPolishing(final String filename) 
			throws BOOSTClientException, BOOSTBackEndException, IOException, SBOLValidationException, SBOLConversionException {
		
		String outputFilename = (String)null;
		
		BOOSTClient client = new BOOSTClient(LoginCredentials.mJWT);
		// set the target namespace
		BOOSTClientConfigs.SBOL_TARGET_NAMESPACE = "https://boost.jgi.doe.gov/";
		
		// we store all submitted jobs in a hash-set
		Set<String> jobUUIDs = new HashSet<String>();

		// polish the given DNA
		String polishDNAJobUUID = client.polish(
				filename	,           				// input sequence
				BOOSTClientConfigs.SBOL_TARGET_NAMESPACE,	// the target namespace
				true,                         	// encoding sequences support sequence feature annotations
				Vendor.LIFE_TECHNOLOGIES,                   	// vendor
				Strategy.Balanced2Random,     	// codon selection strategy
				FileFormat.SBOL,              	// output format
				"Saccharomyces cerevisiae");  	// predefined host
		if (null != polishDNAJobUUID) {
			jobUUIDs.add(polishDNAJobUUID);
			System.out.println("Data for DNA Polish :" + polishDNAJobUUID);
		}

		// for all jobs, we check their status
		for(String jobUUID : jobUUIDs) {
			
			JSONObject jobReport = null;
			while(null == (jobReport = client.getJobReport(jobUUID))) {
				try {
					Thread.sleep(2000);
				} catch(Exception e) {}
				System.out.println("Data for DNA Polish :" + polishDNAJobUUID);
			}
			
			
			outputFilename = client.saveJobReport(
					jobUUID, jobReport);

		}
		
		return outputFilename;
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
		String outputFilename = doPolishing(sbolDesignerDocumentFilename);
		
		// copy the file from the BOOST client's job-directory
		// to the test-directory
		FileUtils.copyFile(Paths.get(outputFilename).toFile(), Paths.get(directory, "02-boost-output.sbol.xml").toFile());
		
		// read the SBOLDocument that is being returned by BOOST
		SBOLDocument document = SBOLReader.read(outputFilename);

		//------------------------------------------------------------------------------------
		// BUG!!!
		Set<URI> rootUri = null;
        for (org.sbolstandard.core2.ComponentDefinition componentDefinition : 
        			document.getRootComponentDefinitions()) {
        		System.out.println(componentDefinition);
        		rootUri = componentDefinition.getWasDerivedFroms();
        		System.out.println(rootUri);
        }
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
		design("./data/test/cds-polishing/01-single-cds/");
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
