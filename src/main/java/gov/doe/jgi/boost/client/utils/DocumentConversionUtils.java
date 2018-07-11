package gov.doe.jgi.boost.client.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidationException;

public class DocumentConversionUtils {

	// method to convert String document into SBOLDocument
	public static SBOLDocument stringToSBOLDocument(String content)
			throws SBOLValidationException, IOException, SBOLConversionException {
		
		// convert String into InputStream
		InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		// extracting the returned SBOL string into an SBOLDocument
		SBOLDocument sbolDocument = SBOLReader.read(stream);

		return sbolDocument;
	}
}
