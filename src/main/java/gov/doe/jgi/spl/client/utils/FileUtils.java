package gov.doe.jgi.spl.client.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
	
	public static String readFile(final String filename) 
			throws IOException {
		BufferedReader reader = Files.newBufferedReader(
				Paths.get(filename), Charset.defaultCharset() );
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ( (line = reader.readLine()) != null ) {
			sb.append(line);
		}
		return sb.toString();
	}

}
