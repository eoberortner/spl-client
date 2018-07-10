package gov.doe.jgi.boost.client.constants;

/**
 * 
 * @author Ernst Oberortner
 */
public class BOOSTResources {

//	// BOOST prod
//    public static String BOOST_REST_URL = "https://boost.jgi.doe.gov/rest";
    
//	// BOOST dev
    public static String BOOST_REST_URL = "https://boost.jgi.doe.gov/Dev/rest";

    // local
//	public static String BOOST_REST_URL = "http://localhost:8080/BOOST/rest";

	// the BOOST resources
	// (depending on the URL of the BOOST REST API)
	public static final String LOGIN_RESOURCE = "/auth/login";
	public static final String REVERSE_TRANSLATE_RESOURCE = "/juggler/juggle";
	public static final String CODON_JUGGLE_RESOURCE = "/juggler/juggle";
	
	public static final String SUBMIT_JOB_RESOURCE = "/jobs/submit";

	// resources that support GET requests
	public static final String GET_PREDEFINED_HOSTS_RESOURCE = "/files/predefined_hosts";
	
	public static final String GET_JOB_RESOURCE = "/jobs";
}
