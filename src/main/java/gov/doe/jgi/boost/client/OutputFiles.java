package gov.doe.jgi.boost.client;

import java.nio.file.*;

/**
 * 
 * @author eoberortner
 */
public class OutputFiles {

	public Path inputFile;
	public Path outputFile;
	public Path provenanceFile;
	
	public Path boostRequest;
	public Path boostResponse;
	
	public OutputFiles() {
		this.inputFile = null;
		this.outputFile = null;
		this.provenanceFile = null;
	}
}
