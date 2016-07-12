package gov.doe.jgi.boost.client;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Ernst Oberortner
 */
public class JSON2InputArgs {

	/*------------------------
	 * KEY NAMES OF REQUESTS
	 *------------------------*/

	public static final String CONSTRAINTS_INFORMATION = "constraints";
	public static final String VENDOR_NAME = "vendor";
	
	public static final String SEQUENCE_INFORMATION = "sequences";
	public static final String SEQUENCE_TYPE = "sequences-types";
	public static final String AUTO_ANNOTATE = "auto-annotate";
	
	// pattern-related keys
	public static final String PATTERN_INFORMATION = "patterns";
	
	// modification-related keys
	public static final String MODIFICATION_INFORMATION = "modifications";
	public static final String STRATEGY_NAME = "strategy";
	public static final String STRATEGY_CODON_USAGE_TABLE = "codon-usage-table";
	public static final String STRATEGY_GENETIC_CODE = "genetic-code";

	// output-related keys
	public static final String OUTPUT_INFORMATION = "output";
	public static final String OUTPUT_FORMAT = "format";
	public static final String OUTPUT_FILNAME = "filename";
	public static final String OUTPUT_LOG_FILENAME = "log-filename";
	
	// file-upload vs. copy-paste entry of information
	public static final String FILE = "file";
	public static final String TEXT = "text";
	public static final String SAVE_AS = "save-as";

	// response
	public static final String STATUS = "STATUS";
	public static final String STATUS_ERROR = "ERROR";
	public static final String STATUS_OK = "OK";
	
	
	/*------------------------
	 * KEY NAMES FOR RESPONSE
	 *------------------------*/
	public static final String INVALID_SEQUENCE_FORMAT = "invalid";

	/*-------------------------------------
	 * UTILITY DATA STRUCTURES AND METHODS
	 *-------------------------------------*/
	
	public static Map<String, String> polisherArgs;
	
	// initialize the polisherArgs
	{
		polisherArgs = new HashMap<String, String>();
		
		// auto annotations
		polisherArgs.put("auto-annotate", "--auto-annotate");
		
	}
}
