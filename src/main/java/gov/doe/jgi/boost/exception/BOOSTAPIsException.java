package gov.doe.jgi.boost.exception;

/**
 * @author Prem
 * 
 * This class defines custom error which arises during
 * invocation of BOOST APIs, if resource is not valid
 *
 */

public class BOOSTAPIsException 
               extends Exception{
	
	private static final long serialVersionUID = 7552788970070216235L;
	
	private String mBOOSTResource;

	public BOOSTAPIsException(String resBOOST) {
		super("BOOST Resource: " + resBOOST + " is not a valid Url");

		this.mBOOSTResource = resBOOST;
	}

	public String getString() {
		return this.mBOOSTResource;
	}
}
