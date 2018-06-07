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
	
	/**
	 * The verifyLengths method checks for minimum length should be less than 
	 * or equal to maximum length
	 * 
	 * @param minLengthBB... minimum length of sequences that can be synthesized.
	 * @param maxLengthBB... maximum length of sequences that can be synthesized.
	 * 
	 * @throws BOOSTClientException ... if the given minimum length is grater than maximum length
	 */
	public static void verifyLengths(String minLengthBB, String maxLengthBB) throws BOOSTClientException {
		int minlen = Integer.parseInt(minLengthBB);
		int maxlen = Integer.parseInt(maxLengthBB);

		if (minlen >= 0 && maxlen >= 0) {
			if (minlen > maxlen) {
				throw new BOOSTClientException("The minimum length can't be grater than maximum length");
			}
		} else {
			throw new BOOSTClientException("All of the specified Base-pair Length should be non-negative");
		}
	}
	/**
	 * The partationOverlapLen method checks constraint on different overlap length
	 * 
	 * @param minOverlapLength ... minimum length of overlap sequences
	 * @param optOverlapLength ... optimum length of overlap sequences
	 * @param optOverlapLength ... maximum length of overlap sequences
	 * 
	 * @throws BOOSTClientException 
	 */
	public static void partationOverlapLen(String minOverlapLength, String optOverlapLength, String maxOverlapLength)
			throws BOOSTClientException {
		int minOverlapLen = Integer.parseInt(minOverlapLength);
		int optOverlapLen = Integer.parseInt(optOverlapLength);
		int maxOverlapLen = Integer.parseInt(maxOverlapLength);

		if (minOverlapLen >= 0 && optOverlapLen >= 0 && maxOverlapLen >= 0) {
			if ((minOverlapLen > maxOverlapLen) || (optOverlapLen > maxOverlapLen || optOverlapLen < minOverlapLen)) {
				throw new BOOSTClientException("The minimum overlap length can't be grater than maximum "
						+ "overlap length and optimum overlap should be between them ");
			}
		} else {
			throw new BOOSTClientException("All of the specified Overlap Length should be non-negative");
		}
	}
	
	/**
	 * The partationGCOverlap method the constraints between min, opt and max %GC content
	 * - all entered percentage should be lies [0, 100]
	 * 
	 * @param minGCOverlap ... minimum %GC content of overlap sequence
	 * @param optGCOverlap ... optimum %GC content of overlap sequence
	 * @param optGCOverlap ... maximum %GC content of overlap sequence
	 * 
	 * @throws BOOSTClientException
	 */

	public static void partitionGCOverlap(final String minOverlapGC, final String optOverlapGC, final String maxOverlapGC)
			throws BOOSTClientException {
		
		double minGCOverlap = Double.parseDouble(minOverlapGC);
		double optGCOverlap = Double.parseDouble(optOverlapGC);
		double maxGCOverlap = Double.parseDouble(maxOverlapGC);

		if (0 <= minGCOverlap && (minGCOverlap <= optGCOverlap) && (optGCOverlap <= maxGCOverlap)
				&& maxGCOverlap <= 100) {
			if ((minGCOverlap > maxGCOverlap) || (optGCOverlap > maxGCOverlap || optGCOverlap < minGCOverlap)) {
				throw new BOOSTClientException("The minimum %GC overlap can't be grater than maximum "
						+ " %GC overlap and optimum %GC overlap should be between them ");
			}
		} else {
			throw new BOOSTClientException("All of the specified %GC content should be in 0 to 100");
		}
	}

	/**
	 * 
	 * @param minPrimerLength
	 * @param maxPrimerLength
	 * @throws BOOSTClientException
	 */
	public static void verifyPartitionPrimerLength(final String strMinPrimerLength, final String strMaxPrimerLength)
			throws BOOSTClientException {

		int minPrimerLength = Integer.parseInt(strMinPrimerLength);
		int maxPrimerLength = Integer.parseInt(strMaxPrimerLength);

		if (minPrimerLength <= 0) {
			throw new BOOSTClientException("The min. primer length should be >0.");
		} else if (maxPrimerLength <= 0) {
			throw new BOOSTClientException("The max. primer length should be >0.");
		} else if (minPrimerLength > maxPrimerLength) {
			throw new BOOSTClientException("The min. primer length can't be grater than max. primer length ");
		}
	}
}
