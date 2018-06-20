package gov.doe.jgi.boost.client.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileUtils {
	
	public static String selectedFilePath;
	private static final String NEWLINE = System.lineSeparator();
	
	public static String readFile(final String filename) 
			throws IOException {
		BufferedReader reader = Files.newBufferedReader(
				Paths.get(filename), Charset.defaultCharset() );
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ( (line = reader.readLine()) != null ) {
			sb.append(line).append(NEWLINE);
		}
		return sb.toString();
	}
	
	public static String SelectedFilePath(String fileType) {
		JFileChooser chooser = new JFileChooser();
		
		switch (fileType) {
		case "sequenceFile":
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("SBOL file", "xml", "rdf", "sbol"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("GenBank", "gb", "gbk"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("FASTA", "fasta"));
			break;

		case "sequencePatterns":
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("FASTA", "fasta"));
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV", "csv"));
			break;
		}
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			selectedFilePath = chooser.getSelectedFile().toString();
		} else {
			System.out.println("No Selection ");
		}
		return selectedFilePath;
	}
}
