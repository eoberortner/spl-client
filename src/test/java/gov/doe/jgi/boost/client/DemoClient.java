package gov.doe.jgi.boost.client;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import gov.doe.jgi.boost.client.constants.BOOSTResources;
import gov.doe.jgi.boost.enums.FileFormat;
import gov.doe.jgi.boost.enums.Strategy;

/**
 * The DemoClient exemplifies how to invoke each functionality 
 * of the BOOST REST API.  
 *  
 * @author Ernst Oberortner
 */
public class DemoClient {
	
	/*-------------
	 * MAIN
	 *-------------*/
	public static void main(String[] args) 
			throws Exception {
		
		BOOSTResources.BOOST_REST_URL = "https://boost.jgi.doe.gov/rest";

		/*
		 * login
		 */
		// instantiate the BOOST client
		// -- alternative 1: provide your BOOST JWT
		BOOSTClient client = new BOOSTClient(LoginCredentials.mJWT);
		// -- alternative 2: provider you BOOST username and password
  	    //BOOSTClient client = new BOOSTClient(LoginCredentials.mUserName, LoginCredentials.mPassword);
		
		// get the predefined hosts
		JSONObject jsonPredefinedHosts = client.getPredefinedHosts();
		System.out.println(jsonPredefinedHosts.toString(4));
		
		// we store all submitted jobs in a hash-set
		Set<String> jobUUIDs = new HashSet<String>();
		
		// reverse translate
		String reverseTranslateJobUUID = client.reverseTranslate(
				"./data/protein.fasta",		// input sequences 
				Strategy.MostlyUsed, 		// codon selection strategy
				"Bacillus subtilis",		// predefined host
				FileFormat.GENBANK);		// output format
		if(null != reverseTranslateJobUUID) {
			jobUUIDs.add(reverseTranslateJobUUID);
		}

		// codon juggle
		String codonJuggleJobUUID = client.codonJuggle(
				"./data/dna.fasta",			// input sequences 
				true,						// exclusively 5'-3' coding sequences 
				Strategy.MostlyUsed,		// codon selection strategy
				"Saccharomyces cerevisiae", // predefined host
				FileFormat.GENBANK);		// output format
		if(null != codonJuggleJobUUID) {
			jobUUIDs.add(codonJuggleJobUUID);
		}

    	// verify against DNA synthesis constraints and sequence patterns
		String dnaVarificationJobUUID = client.verify("./data/dna.fasta", // input sequence
				"./data/constraints.scl", // synthesis constraints
				"./data/patterns.fasta"); // sequence patterns
		if (null != dnaVarificationJobUUID) {
			jobUUIDs.add(dnaVarificationJobUUID);
		}

		// for all jobs, we check their status
		for(String jobUUID : jobUUIDs) {
			
			JSONObject jobReport = null;
			while(null == (jobReport = client.getJobReport(jobUUID))) {
				
				// if the job isn't finished, then we wet some seconds
				// and check again
				System.out.println("Job " + jobUUID + " is not finished yet.");
				
				try {
					Thread.sleep(5000);
				} catch(Exception e) {}
			}
			
			// output of the job report (which is a JSON object)
			System.out.println(jobReport);
		}
	}
}
