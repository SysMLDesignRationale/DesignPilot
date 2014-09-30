package gov.nasa.jsc.mdrules.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;

import gov.nasa.jsc.mdrules.defs.Msgs;
import gov.nasa.jsc.mdrules.rdf.ComponentWithUri;
import gov.nasa.jsc.mdrules.rdf.NamespacePrefixes;
import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import gov.nasa.jsc.mdrules.repository.ReadSesame;
import gov.nasa.jsc.mdrules.repository.SesameRepository;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.defs.RunPropertyDefinitions;

public class Util {

	/**
	 * Logs a message 
	 */
	@SuppressWarnings("rawtypes")
	static public void log(Class c, Level level, String code, Object... args) {
		String message = null;
		if (Msgs.getMsgs().containsKey(code)) {
			message = String.format(Msgs.getMsgs().get(code), args);
			
		}
		else {
			message = code;
		}
		Logger.getLogger(c).log(level, message);
		
	}
	
	/**
	 * Logs an exception by printing the exception message and stack trace
	 */
	static public void logException(Exception e, String className) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.append(e.getMessage() + "\n");
		e.printStackTrace(pw);
		String msg = sw.getBuffer().toString();
		Logger.getLogger(className).log(Level.ERROR, msg);
		
	}
	
	/**
	 * Logs an exception by printing the exception message and stack trace
	 */
	@SuppressWarnings("rawtypes")
	static public void logException(Exception e, Class c) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.append(e.getMessage() + "\n");
		e.printStackTrace(pw);
		String msg = sw.getBuffer().toString();
		Logger.getLogger(c).log(Level.ERROR, msg);
	}
	
	/**
	 * Generates a unique string to use as a suffix
	 */
	static public String uniqueSuffix() {
		StringBuilder ret = new StringBuilder();
		Date date = new Date();
		String uniquifier = "_" + date.toString() + "_" + date.getTime();
		uniquifier = uniquifier.replaceAll(" ", "-");
		uniquifier = uniquifier.replaceAll(":", "-");
		ret.append(uniquifier);
		return ret.toString();
	}
	
	/**
	 * Generates a unique new URI
	 */
	static public String genUri(String type) {
		StringBuilder ret = new StringBuilder();
		ret.append(Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_DB_NAMESPACE));
		ret.append("#" + type + "_");
		try {
			Thread.sleep(1); // wait a millisecond to ensure unique timestamp
		} 
		catch (InterruptedException e) {
		} 
		Date date = new Date();
		String uniquifier = date.toString() + "_" + date.getTime();
		uniquifier = uniquifier.replaceAll(" ", "-");
		uniquifier = uniquifier.replaceAll(":", "-");
		ret.append(uniquifier);
		return ret.toString();
	}
	
	/**
	 * Converts an ID from SysML into a URI. For now, just prepend with namespace URI
	 */
	static public String sysmlIdToLessonDbUri(String sysmlId) {
		return Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_DB_NAMESPACE) + "#" + sysmlId;
	}
	
	/**
	 * Converts an ID from SysML into a URI. For now, just prepend with namespace URI
	 */
	static public String sysmlIdToModelDbUri(String sysmlId) {
		return Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_DB_NAMESPACE) + "#" + sysmlId;
	}
	
	/**
	 * Converts an ID from SysML into a URI. For now, just prepend with namespace URI
	 */
	static public String sysmlIdToModelSchemaUri(String sysmlId) {
		return Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#" + sysmlId;
	}
	
	static Map<URI, ComponentWithUri> componentsWithUri = new HashMap<URI, ComponentWithUri>();
	static public void putComponentWithUri(URI uri, ComponentWithUri comp) {
		componentsWithUri.put(uri, comp);
	}
	static public ComponentWithUri getComponentWithUri(URI uri) {
		return componentsWithUri.get(uri);
	}
	
	/**
	 * Prints a set of RDF statements
	 * @param stmts
	 */
	static public void printStatements(List<Statement> stmts) {
		String schemaNamespace = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#";
		String dbNamespace = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_DB_NAMESPACE) + "#";
		for (Statement stmt : stmts) {
			String stmtStr = stmt.toString();
			while (stmtStr.indexOf(schemaNamespace) >= 0) {
				stmtStr = stmtStr.replace(schemaNamespace, "lessonSchema:");
			}
			while (stmtStr.indexOf(dbNamespace) >= 0) {
				stmtStr = stmtStr.replace(dbNamespace, "lessonDb:");
			}
			while (stmtStr.indexOf(RdfConstants.rdfTypeStr) >= 0) {
				stmtStr = stmtStr.replace(RdfConstants.rdfTypeStr, "a");
			}
			System.out.println(stmtStr);
		}
	}
	
	/**
	 * Prints all statements in a specified repository
	 */
	static public void printRepoContents(RepositoryConnection repConn) {
		List<Statement> stmts = ReadSesame.getFromRepository(repConn, (Resource)null, (URI)null, (Value)null, false);
		printStatements(stmts);
	}
	
	/**
	 * Converts a URI into a label if possible, and if not, replaces namespace with prefix if possible
	 */
	static public String prettifyUri(String uriStr) {
		
		Run runInstance = Run.getRun();
		SesameRepository repo = runInstance.getRepository();

		// try to map namespace into prefix
		String qname = null;
		int index = uriStr.indexOf("#");
		if (index >= 0) {

			String namespace = uriStr.substring(0, index+1);
			String elem = uriStr.substring(index + 1);
			NamespacePrefixes nsp = runInstance.getNsPrefixes();
			Map<String, String> ns2prefix = nsp.getNs2Prefix();
			if (ns2prefix.containsKey(namespace)) {
				String prefix = ns2prefix.get(namespace);
				qname = prefix + elem;
			}
		}
		String id = qname == null ? uriStr : qname;
	
		// try to get a label
		URI uri = new URIImpl(uriStr);
		List<Statement> stmts = ReadSesame.getFromRepository(repo.getRepositoryConn(), uri, RdfConstants.rdfsLabelUri, null, false);
		if (stmts.size() > 0) {
			Statement stmt = stmts.get(0);
			String label = stmt.getObject().stringValue();
			return id + " = " + label;
		}
		else {
			return id; 
		}
	}
	
	static public String dumpBlocksAndAttributesFromDatabase() {
		StringBuilder ret = new StringBuilder();

		Run runInstance = Run.getRun();
		SesameRepository repo = runInstance.getRepository();
		RepositoryConnection repConn = repo.getRepositoryConn();
		List<Statement> blockStmts =  ReadSesame.getFromRepository(repConn, null, RdfConstants.isa, RdfConstants.blockUri, false);
		for (Statement stmt : blockStmts) {
			ret.append("\n" + prettifyUri(stmt.getSubject().stringValue()) + "\n");
			List<Statement> attrStmts = ReadSesame.getFromRepository(repConn, stmt.getSubject(), RdfConstants.hasAttributeUri, null, false);
			for (Statement stmt1 : attrStmts) {
				Value attr = stmt1.getObject();
				ret.append("\n\t" + prettifyUri(attr.stringValue()) + "\n");
				if (attr instanceof URI) {
					List<Statement> labelStmts = ReadSesame.getFromRepository(
							repConn, (URI) attr, RdfConstants.rdfsLabelUri,
							null, false);
					for (Statement stmt2 : labelStmts) {
						Value label = stmt2.getObject();
						ret.append("\n\t\tLabel: " + label.stringValue() + "\n");
					}
					List<Statement> typeStmts = ReadSesame.getFromRepository(
							repConn, (URI) attr, RdfConstants.hasTypeUri, null,
							false);
					for (Statement stmt3 : typeStmts) {
						Value type = stmt3.getObject();
						ret.append("\n\t\tType: " + type.stringValue() + "\n");
					}
					List<Statement> defValueStmts = ReadSesame.getFromRepository(
							repConn, (URI) attr, RdfConstants.hasDefaultValueUri,
							null, false);
					for (Statement stmt5 : defValueStmts) {
						Value val = stmt5.getObject();
						ret.append("\n\t\tDefault Value: "
								+ (val instanceof Literal ? val.stringValue()
										: prettifyUri(val.stringValue()))
								+ "\n");
					}
					List<Statement> compValueStmts = ReadSesame.getFromRepository(
							repConn, (URI) attr, RdfConstants.hasComputedValueUri,
							null, false);
					for (Statement stmt6 : compValueStmts) {
						Value val = stmt6.getObject();
						ret.append("\n\t\tComputed Value: "
								+ (val instanceof Literal ? val.stringValue()
										: prettifyUri(val.stringValue()))
								+ "\n");
					}
					List<Statement> valueStmts = ReadSesame.getFromRepository(
							repConn, (URI) attr, RdfConstants.hasValueUri,
							null, false);
					for (Statement stmt4 : valueStmts) {
						Value val = stmt4.getObject();
						ret.append("\n\t\tValue: "
								+ (val instanceof Literal ? val.stringValue()
										: prettifyUri(val.stringValue()))
								+ "\n");
					}
				}
			}	
		}
		return ret.toString();
	}
	

}
