package gov.doe.jgi.boost.client.constants;

public class BOOSTResources {

	// local
	public static String BOOST_REST_URL = "http://localhost:8080/boost-web/rest";

	// the BOOST resources
	// (depending on the URL of the BOOST REST API)
	public static final String LOGIN_RESOURCE = BOOST_REST_URL + "/auth/login";
	public static final String REVERSE_TRANSLATE_RESOURCE = BOOST_REST_URL + "/juggler/juggle";
	public static final String CODON_JUGGLE_RESOURCE = BOOST_REST_URL + "/juggler/juggle";
	
	public static final String SUBMIT_JOB_RESOURCE = BOOST_REST_URL + "/jobs/submit";
	public static final String GET_JOB_RESOURCE = BOOST_REST_URL + "/jobs";
}
