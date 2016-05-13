package gov.doe.jgi.spl.client;

public class DemoClient {

	public static void main(String[] args) 
			throws Exception {
		
		SPLClient client = new SPLClient();

		// login
		client.login("<username>", "<password>");
		
		
	}
}
