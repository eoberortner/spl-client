package gov.doe.jgi.boost.client;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import gov.doe.jgi.boost.client.constants.BOOSTClientConfigs;
import gov.doe.jgi.boost.client.constants.LoginCredentials;
import gov.doe.jgi.boost.enums.FileFormat;
import gov.doe.jgi.boost.enums.Strategy;

/**
 * The DemoClient exemplifies how to invoke each functionality 
 * of the BOOST REST API.  
 *  
 * @author Ernst Oberortner
 */
public class CodonJugglePlusUpdate {
	
	/*-------------
	 * MAIN
	 *-------------*/
	public static void main(String[] args) 
			throws Exception {

		// instantiate the BOOST client
  	    BOOSTClient client = new BOOSTClient(LoginCredentials.mJWT);

		// set the target namespace
		BOOSTClientConfigs.SBOL_TARGET_NAMESPACE = "https://boost.jgi.doe.gov/";
		
		// we store all submitted jobs in a hash-set
		Set<String> jobUUIDs = new HashSet<String>();

		
		// get all files from the "./data/example-designs" folder
		List<File> files = Arrays.asList(new java.io.File("./data/example-designs/").listFiles());
		for(File file : files) {
			
			if(!file.toString().contains("07-")) { continue; }
			System.out.println("file: " + file);
			
			// codon juggle the content's of the SBOL file
			String codonJuggleJobUUID1 = client.codonJuggle(
					file.toString(),								// input sequences
					BOOSTClientConfigs.SBOL_TARGET_NAMESPACE,	// the target namespace
					false,					 					// exclusively 5'-3' coding sequences 
					Strategy.Balanced,		  					// codon selection strategy
					"Saccharomyces cerevisiae",   				// predefined host
					FileFormat.SBOL);		  					// output format
			if(null != codonJuggleJobUUID1) {
				jobUUIDs.add(codonJuggleJobUUID1);
				System.out.println("Data for codon Juggling :" + codonJuggleJobUUID1);
			}
		}

		// for all jobs, we check their status
		for(String jobUUID : jobUUIDs) {
			
			JSONObject jobReport = null;
			while(null == (jobReport = client.getJobReport(jobUUID))) {
				
				// if the job isn't finished, then we wet some seconds
				// and check again
				System.out.println("Job " + jobUUID + " is not finished yet.");
				
				try {
					Thread.sleep(2000);
				} catch(Exception e) {}
			}

			// output of the job report (which is a JSON object)
//			System.out.println(jobReport.toString(4));

			java.nio.file.Path jobOutputDir = Paths.get(".", "data", "out", jobUUID);
			if(!Files.exists(jobOutputDir)) {
				Files.createDirectories(jobOutputDir);

				// write the response to a file
				FileUtils.writeStringToFile(
						Paths.get(jobOutputDir.toString(), "response-" + jobUUID + ".json").toFile(), 
						jobReport.toString(4));

				// write the input and output files
				if(jobReport.has("response")) {
					
					JSONArray responseArray = jobReport.getJSONArray("response");
					
					Iterator<Object> it = responseArray.iterator();
					while(it.hasNext()) {
						Object responseObject = it.next();
						if(responseObject instanceof JSONObject) {
							JSONObject jsonResponseObject = (JSONObject)responseObject;
							
							// input sequences
							FileUtils.writeStringToFile(
								Paths.get(jobOutputDir.toString(), jobUUID + "-input-file.sbol.xml").toFile(),
								jsonResponseObject.getString("original-sequences-text"));

							// output sequences
							FileUtils.writeStringToFile(
								Paths.get(jobOutputDir.toString(), jobUUID + "-output-file.sbol.xml").toFile(),
								jsonResponseObject.getString("modified-sequences-text"));
						}
					}
				}
				
				// write just the output SBOL document to a file
				if(jobReport.has("provenance")) {

					// get the SBOL document from the JSON response
					JSONObject jsonProvenance = jobReport.getJSONObject("provenance");

					// write to input SBOLdocument (input to BOOST) to a file
					String boostProvInputDocument = jsonProvenance.getString("input-document");
					FileUtils.writeStringToFile(
							Paths.get(jobOutputDir.toString(), jobUUID + "-input-document.sbol.xml").toFile(), 
							boostProvInputDocument);

					// write to output SBOLdocument (generated by BOOST) to a file
					String boostProvOutputDocument = jsonProvenance.getString("output-document");
					FileUtils.writeStringToFile(
							Paths.get(jobOutputDir.toString(), jobUUID + "-output-document.sbol.xml").toFile(), 
							boostProvOutputDocument);
					
					System.out.println(Paths.get(jobOutputDir.toString(), jobUUID + "-output-document.sbol.xml").toFile());
				}
			
			}
		}
	}
}
