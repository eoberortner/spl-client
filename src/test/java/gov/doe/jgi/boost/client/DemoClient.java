package gov.doe.jgi.boost.client;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import gov.doe.jgi.boost.client.constants.BOOSTResources;
import gov.doe.jgi.boost.enums.FileFormat;
import gov.doe.jgi.boost.enums.Strategy;
import gov.doe.jgi.boost.enums.Vendor;
import gov.doe.jgi.boost.ui.UserAuthentication;

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
				"./data/protein.fasta",		    // input sequences 
				Strategy.MostlyUsed, 		    // codon selection strategy
				"Bacillus subtilis",		    // predefined host
				FileFormat.GENBANK);		    // output format
		if(null != reverseTranslateJobUUID) {
			jobUUIDs.add(reverseTranslateJobUUID);
		}

		// codon juggle
		String codonJuggleJobUUID = client.codonJuggle(
				"./data/dna.fasta",			  // input sequences 
				true,						  // exclusively 5'-3' coding sequences 
				Strategy.MostlyUsed,		  // codon selection strategy
				"Saccharomyces cerevisiae",   // predefined host
				FileFormat.GENBANK);		  // output format
		if(null != codonJuggleJobUUID) {
			jobUUIDs.add(codonJuggleJobUUID);
		}

    	// verify against DNA synthesis constraints and sequence patterns
		String dnaVarificationJobUUID = client.dnaVarification(
				"./data/dna.fasta",           // input sequence
				Vendor.GEN9,                  // vendor
				"./data/patterns.fasta");     // sequence patterns
		if (null != dnaVarificationJobUUID) {
			jobUUIDs.add(dnaVarificationJobUUID);
		}

		// polish the given DNA
		String polishDNAJobUUID = client.polish(
				"./data/dna.fasta",           // input sequence
				true,                         // encoding sequences support sequence feature annotations
				Vendor.JGI,                   // vendor
				Strategy.Balanced2Random,     // codon selection strategy
				FileFormat.SBOL,              // output format
				"Saccharomyces cerevisiae");  // // predefined host
		if (null != polishDNAJobUUID) {
			jobUUIDs.add(polishDNAJobUUID);
		}
		
		/** // partitioning of DNA
		String partitiongDNAJobUUID = client.partition(
				"./data/dna.fasta",           // input sequence
				"aaacccgggttt",               // 5-prime-vector-overlap
				"tttgggcccaaa",               // 3-prime-vector-overlap
				Integer.toString(15),         // min-BB-length
				Integer.toString(3000),       // max-BB-length
				Double.toString(4.0),         // minimum overlap GC
				Double.toString(40.0),        // optimum overlap GC
				Double.toString(62.0),        // maximum overlap GC
				Integer.toString(5),          // minimum overlap length
				Integer.toString(25),         // optimum overlap length
				Integer.toString(30)          // maximum overlap length
				);
		if (null != partitiongDNAJobUUID) {
			jobUUIDs.add(polishDNAJobUUID);
		}
		**/
		
				
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
