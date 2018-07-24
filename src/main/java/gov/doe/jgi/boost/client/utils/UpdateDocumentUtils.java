package gov.doe.jgi.boost.client.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.sbolstandard.core2.Component;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.Sequence;

/**
 * 
 * @author Chris Myers
 */
public class UpdateDocumentUtils {


	/**
	 * inspects the given document if there are any root component-definitions that 
	 * have wasDerivedFrom properties.
	 * 
	 * @param document
	 * @return
	 */
	public static Map<URI, URI> getMapOfUpdatedURIs(final SBOLDocument document) {
		
		// key ... the URI of the original CD
		// value ... the URI of the updated CD (i.e., the CD that has a non-empty set of wasDerivedFroms)
		Map<URI, URI> updatedURIs = new HashMap<>();
		
		for(ComponentDefinition cd : document.getRootComponentDefinitions()) {
			if(null != cd.getWasDerivedFroms() && !cd.getWasDerivedFroms().isEmpty()) {
				for(URI wasDerivedFromURI : cd.getWasDerivedFroms()) {
					updatedURIs.put(cd.getIdentity(), wasDerivedFromURI);
				}
			}
		}
		return updatedURIs;
	}
	
	/**
	 * updates ...
	 * 
	 * @param doc
	 * @param map
	 * @param targetNamespace
	 * 
	 * @throws SBOLValidationException
	 */
	public static void updateDocument(final SBOLDocument doc, final Map<URI,URI> map, final String targetNamespace) 
			throws SBOLValidationException { 
		for (ComponentDefinition cd : doc.getRootComponentDefinitions()) {
			updateDocumentRecurse(doc, cd, map, targetNamespace);
		}
	}

	
	/**
	 * 
	 * @param doc
	 * @param cd
	 * @param map
	 * @param targetNamespace
	 * 
	 * @throws SBOLValidationException
	 */
	private static void updateDocumentRecurse(
			final SBOLDocument doc, final ComponentDefinition cd, final Map<URI,URI> map, final String targetNamespace) 
			throws SBOLValidationException {
		
		ComponentDefinition copyCD = null;
		
		// do nothing if the ComponentDefinition does not have any sub-components
		if(null == cd || null == cd.getComponents()) { return; }
		
		for (Component comp : cd.getComponents()) {
			
			if (map.get(comp.getDefinitionURI())!=null) {
				if (copyCD == null) {
					copyCD = (ComponentDefinition)doc.createCopy(cd, targetNamespace, null, null);

					// Add wasDerivedFrom / wasGeneratedBy links from copyCD to cd
					copyCD.addWasDerivedFrom(cd.getIdentity());
				}
				
				Component copyComp = copyCD.getComponent(comp.getDisplayId());
				copyComp.setDefinition(map.get(comp.getDefinitionURI()));
				
				map.put(cd.getIdentity(),copyCD.getIdentity());
				
			} else {
				
				updateDocumentRecurse(doc, comp.getDefinition(), map, targetNamespace);
				
				if (map.get(comp.getDefinitionURI())!=null) {
					if (copyCD == null) {
						copyCD = (ComponentDefinition)doc.createCopy(cd, targetNamespace, null, null);

						// Add wasDerivedFrom / wasGeneratedBy links from copyCD to cd
						copyCD.addWasDerivedFrom(cd.getIdentity());
					}
					
					Component copyComp = copyCD.getComponent(comp.getDisplayId());
					copyComp.setDefinition(map.get(comp.getDefinitionURI()));
					
					map.put(cd.getIdentity(), copyCD.getIdentity());
				} 
			}
		}
		
		if (copyCD != null) {
			
			String newSeq = copyCD.getImpliedNucleicAcidSequence();
			Sequence seq = doc.createSequence(targetNamespace, copyCD.getDisplayId() + "_sequence", "2", 
					newSeq, Sequence.IUPAC_DNA);
			
			copyCD.clearSequences();
			copyCD.addSequence(seq);
		}
	}

}
