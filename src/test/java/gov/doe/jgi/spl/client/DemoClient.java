package gov.doe.jgi.spl.client;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import gov.doe.jgi.spl.commons.FileFormat;
import gov.doe.jgi.spl.commons.Strategy;


/**
 * The DemoClient demonstrates the use of the 
 * SPL client's functions.
 * 
 * @author Ernst Oberortner
 *
 */
public class DemoClient {

	/**
	 * The loadUsernameAndPassword() method loads the username 
	 * and password from a local .properties file
	 * 
	 * @param filename ... the name of the .properties file
	 * 
	 * @return a Properties object containing all properties that 
	 * are specified in the .properties file
	 * 
	 * @throws Exception ... if something went wrong while loading 
	 * the properties, such as the file does not exist or the username 
	 * and password keys don't exist
	 */
	public static Properties loadUsernameAndPassword(final String filename) 
			throws Exception {
		
		// load the properties
		Properties prop = new Properties();
		try (
			InputStream in = 
				Files.newInputStream(Paths.get(filename));
			) {
			
			prop.load(in);
		}
		
		// check that username and password exist
		if(!prop.containsKey("username")) {
			throw new Exception("No username specified!");
		}
		if(!prop.containsKey("password")) {
			throw new Exception("No password specified!");
		}
		
		return prop;
	}
	
	/*-------------
	 * MAIN
	 *-------------*/
	public static void main(String[] args) 
			throws Exception {
		
		// instantiate the SPL client
		SPLClient client = new SPLClient();

		/*
		 * login
		 */
		Properties prop = loadUsernameAndPassword("spl.properties");

		client.login(
				prop.getProperty("username").trim(), 
				prop.getProperty("password").trim());
		
		/*
		 * reverse translate
		 */
		
		client.reverseTranslate(
				"./data/protein.fasta", 
				Strategy.MostlyUsed, 
				"./data/Ecoli.cudb", 
				FileFormat.GENBANK);
		
		/*
		 * codon juggle
		 */
		client.codonJuggle(
				"./data/dna.fasta", true,
				Strategy.MostlyUsed,
				"./data/Ecoli.cudb", 
				FileFormat.GENBANK);

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
