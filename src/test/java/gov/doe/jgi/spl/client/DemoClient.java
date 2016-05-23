package gov.doe.jgi.spl.client;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class DemoClient {

	public static Properties loadUsernameAndPassword() 
			throws Exception {
		
		Properties prop = new Properties();

		try (
			InputStream in = 
				Files.newInputStream(Paths.get("spl.properties"));
			) {
			
			prop.load(in);
		}
		
		return prop;
	}
	
	public static void main(String[] args) 
			throws Exception {
		
		// instantiate the SPL client
		SPLClient client = new SPLClient();

		/*
		 * login
		 */
		Properties prop = loadUsernameAndPassword();

		client.login(
				prop.getProperty("username").trim(), 
				prop.getProperty("password").trim());
		
		/*
		 * reverse translate
		 */
		
		/*
		 * codon juggle
		 */

		/*
		 * verify
		 */

		/*
		 * polish (verify + modify)
		 */
		
		/*
		 * partition
		 */
	}
}
