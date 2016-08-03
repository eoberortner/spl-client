package gov.doe.jgi.boost.client;

import java.nio.file.Files;
import java.nio.file.Paths;

import gov.doe.jgi.boost.exception.BOOSTClientException;

/**
 * The ParameterValueVerifier provides a set of static methods to verify 
 * parameters values against, for example, NULL or empty.
 * 
 * @author Ernst Oberortner
 *
 */
public class ParameterValueVerifier {
	/**
	 * The verifyNull method verifies if a given value is null.
	 * 
	 * @param name  ... the name of the value (for improved error reporting)
	 * @param value ... the value
	 * 
	 * @throws BOOSTClientException ... if the value is null
	 */
	public static void verifyNull(final String name, final Object value)  
			throws BOOSTClientException {
		
		// is the given String value NULL?
		if(value == null) {
			throw new BOOSTClientException(name + " cannot be null.");
		}
		
	}
	
	/**
	 * The verifyNull method verifies if a given String value is empty.
	 * 
	 * @param name  ... the name of the value (for improved error reporting)
	 * @param value ... the value
	 * 
	 * @throws BOOSTClientException ... if the value is null
	 */
	public static void verifyEmpty(final String name, final String value)  
			throws BOOSTClientException {
		
		// is the given String value empty? 
		if(value.isEmpty()) {
			throw new BOOSTClientException(name + " cannot be empty.");
		}
		
	}
	
	/**
	 * The verifyValue method checks if a given String value 
	 * is NULL or empty. If so, then the method throws an exception.
	 * 
	 * @param name ... the name of the parameter (for better error reporting)
	 * @param value ... the String value which should be verified
	 * 
	 * @throws BOOSTClientException ... if the given String value is NULL or empty
	 */
	public static void verifyValue(final String name, final String value) 
			throws BOOSTClientException {
		verifyNull(name, value);
		verifyEmpty(name, value);
	}
	
	/**
	 * The verifyFilename method checks if a given filename exists, is readable, 
	 * and is a regular file.
	 * 
	 * @param name ... the name of the filename parameter (for improved error reporting)
	 * @param filename
	 * @throws BOOSTClientException
	 */
	public static void verifyFilename(final String name, final String filename) 
			throws BOOSTClientException {

		verifyValue(name, filename);
		
		java.nio.file.Path path = Paths.get(filename);
		if(!Files.exists(path)) {
			throw new BOOSTClientException("The " + name + " " + filename + " does not exist.");
		} else if(Files.isDirectory(path)) {
			throw new BOOSTClientException("The " + name + " " + filename + " cannot be a directory.");
		} else if(!Files.isReadable(path)) {
			throw new BOOSTClientException("The " + name + " " + filename + " is not readable.");
		}

	}
}
