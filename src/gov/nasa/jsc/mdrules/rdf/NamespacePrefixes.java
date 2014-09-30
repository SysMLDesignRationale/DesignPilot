package gov.nasa.jsc.mdrules.rdf;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jsc.mdrules.defs.RunPropertyDefinitions;
import gov.nasa.jsc.mdrules.run.Run;

public class NamespacePrefixes {

	Map<String, String> ns2prefix = new HashMap<String, String>();
	public Map<String, String >getNs2Prefix() {
		return ns2prefix;
	}
	
	Map<String, String> prefix2ns = new HashMap<String, String>();
	public Map<String, String> getPrefix2Ns() {
		return prefix2ns;
	}
	
	public NamespacePrefixes() {
		
		String lessonDbNs = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_DB_NAMESPACE);
		String lessonSchemaNs = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE);
		String modelDbNs = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_DB_NAMESPACE);
		String modelSchemaNs = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE);
		String simDbNs = Run.getRun().getParamValue(RunPropertyDefinitions.SIM_DB_NAMESPACE);
		String simSchemaNs = Run.getRun().getParamValue(RunPropertyDefinitions.SIM_SCHEMA_NAMESPACE);
		String qudvSchemaNs = Run.getRun().getParamValue(RunPropertyDefinitions.QUDV_SCHEMA_NAMESPACE);

		String owlNs = "http://www.w3.org/2002/07/owl#";
		String rdfNs = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		String rdfsNs = "http://www.w3.org/2000/01/rdf-schema#";
		
		if (lessonDbNs != null) {	
			lessonDbNs += "#";
			ns2prefix.put(lessonDbNs, "lessonDb:");
			prefix2ns.put("lessonDb:", lessonDbNs);
		}
		if (lessonSchemaNs != null) {	
			lessonSchemaNs += "#";
			ns2prefix.put(lessonSchemaNs, "lessonSchema:");
			prefix2ns.put("lessonSchema:", lessonSchemaNs);
		}
		if (modelDbNs != null) {	
			modelDbNs += "#";
			ns2prefix.put(modelDbNs, "modelDb:");
			prefix2ns.put("modelDb:", modelDbNs);
		}
		if (modelSchemaNs != null) {	
			modelSchemaNs += "#";
			ns2prefix.put(modelSchemaNs, "modelSchema:");
			prefix2ns.put("modelSchema:", modelSchemaNs);
		}
		if (simDbNs != null) {	
			simDbNs += "#";
			ns2prefix.put(simDbNs, "simDb:");
			prefix2ns.put("simDb:", simDbNs);
		}
		if (simSchemaNs != null) {	
			simSchemaNs += "#";
			ns2prefix.put(simSchemaNs, "simSchema:");
			prefix2ns.put("simSchema:", simSchemaNs);
		}
		if (qudvSchemaNs != null) {	
			qudvSchemaNs += "#";
			ns2prefix.put(qudvSchemaNs, "qudvSchema:");
			prefix2ns.put("qudvSchema:", qudvSchemaNs);
		}
		
		ns2prefix.put(owlNs, "owl:");
		prefix2ns.put("owl:", owlNs);
		
		ns2prefix.put(rdfNs, "rdf:");
		prefix2ns.put("rdf:", rdfNs);
		
		ns2prefix.put(rdfsNs, "rdfs:");
		prefix2ns.put("rdfs:", rdfsNs);

	}
	
	
	
}
