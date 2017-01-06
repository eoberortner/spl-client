package gov.doe.jgi.boost.client;

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
		
		/*
		 * login
		 */

		// instantiate the BOOST client
		BOOSTClient client = new BOOSTClient("<your-username>", "<your-password>");

		
		/*
		 * reverse translate
		 */
		client.reverseTranslate(
				"./data/protein.fasta",		// input sequences 
				Strategy.MostlyUsed, 		// codon selection strategy
				"./data/Ecoli.cudb", 		// codon usage table
				FileFormat.GENBANK);		// output format
		
		/*
		 * codon juggle
		 */
		client.codonJuggle(
				"./data/dna.fasta",			// input sequences 
				true,						// exclusively 5'-3' coding sequences 
				Strategy.MostlyUsed,		// codon selection strategy
				"./data/Ecoli.cudb", 		// codon usage table
				FileFormat.GENBANK);		// output format

//		/*
//		 * verify
//		 */
//		client.verify("./data/dna.fasta", SequenceType.DNA, Vendor.LIFE_TECHNOLOGIES);
//		
//		/*
//		 * polish (verify + modify)
//		 */
//		client.polish(
//				"./data/dna.fasta",	// sequences 
//				SequenceType.DNA, 				// sequence type
//				true,							// all 5'-3' coding sequences 
//				Vendor.LIFE_TECHNOLOGIES, 		// vendor
//				Strategy.BALANCED_TO_RANDOM,
//				"./data/Ecoli.cudb");
		
		/*
		 * partition
		 */
	}
}
