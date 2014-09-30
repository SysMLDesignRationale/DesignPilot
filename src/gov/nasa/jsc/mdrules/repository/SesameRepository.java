package gov.nasa.jsc.mdrules.repository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.QueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.turtle.TurtleParser;

import gov.nasa.jsc.mdrules.repository.SesameRepositoryFactory;
import gov.nasa.jsc.mdrules.rules.Rule;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;

public class SesameRepository {
	
	Run runInstance = Run.getRun();

	Repository repository;
	public Repository getRepository() {
		return repository;
	}
	RepositoryConnection repConn;
	public RepositoryConnection getRepositoryConn() {
		return repConn;
	}
	
//	FileWriter fw; // for debugging only
	
	public SesameRepository(String path) {
		repository = new SesameRepositoryFactory().getRepository(
			SesameRepositoryFactory.RepositoryType.NATIVE, 
			path);
		try {
			repConn = repository.getConnection();
		} 
		catch (RepositoryException e) {
			Util.logException(e, getClass());
		}
		
		// debug
//		try {
//			fw = new FileWriter(new File("/Users/sidneybailin/Documents/aWorkingOntologist/results.txt"));
//		} 
//		catch (IOException e) {
//			Util.logException(e, getClass());
//		}

		
		// startup parameter - schema files
		// we do this now in RdfGen.genRdf()
//		String loadSchemas = runInstance.getParamValue(
//				RunPropertyDefinitions.LOAD_SCHEMAS_INTO_DB).toLowerCase();
//		if (loadSchemas.equals("true") || loadSchemas.equals("yes")) {
//			String schemaFileList = runInstance.getParamValue(RunPropertyDefinitions.SCHEMA_FILES);
//			String[] schemaFiles = schemaFileList.split(";");
//			for (String fileName : schemaFiles) {
//				loadTurtleFile(fileName);
//			}
//			Util.printRepoContents(repConn);
//		}
	}
	
	/*
	 * Shuts down the Sesame repository object
	 */
	public void shutdown() {
		try {
			if (repConn != null) {
				repConn.close();
			}
			if (repository != null) {
				repository.shutDown();
			}
		} 
		catch (RepositoryException e) {
			Util.logException(e,  this.getClass());
		}
	}
	
	/**
	 * Loads an RDF file in turtle format into a collection of statements
	 * @param fileName
	 */
	public void loadTurtleFile(String fileName) {
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
		} 
		catch (FileNotFoundException e) {
			System.err.print(e.getMessage());
			e.printStackTrace();
		}
		TurtleParser parser = new TurtleParser();
		StatementCollector collector = new StatementCollector();
		parser.setRDFHandler(collector);
		try {
			parser.parse(fis, "http://example.org/orionPowerDistribution");
		}
		catch (Exception e) {
			System.err.print(e.getMessage());
			e.printStackTrace();
		}
		Collection<Statement> stmts = collector.getStatements();
		for (Statement stmt : stmts) {
			try {
				repConn.add(stmt);
			} 
			catch (RepositoryException e) {
				Util.logException(e, getClass());
			}
		}
	}
	
	/**
	 * Executes a SPARQL CONSTRUCT query and adds the resulting statements to the repository
	 * @param queryString
	 */
	public boolean executeSparqlRule(Rule rule) {
		String queryString = rule.getSparql();
		boolean ret = false; // assume no results, i.e., rule doesn't actually
								// fire
		QueryResult<Statement> result = null;
		try {
			GraphQuery query = repConn.prepareGraphQuery(
					org.openrdf.query.QueryLanguage.SPARQL, queryString);

			result = query.evaluate();
			List<Statement> stmts = new ArrayList<Statement>();
			while (result.hasNext()) {
				Statement stmt = result.next();
				stmts.add(stmt);
			}
			if (stmts.size() > 0) {

				List<Statement> uniqueStmts = removeDuplicates(stmts);
				stmts = uniqueStmts;

				// /////////////////////////////////////////////////////////////
				// DEBUG
//				try {
//					fw.append("\n\n*****\n");
//					fw.append("***** " + rule.getName() + " *****\n");
//					fw.append("*****\n\n");
//					for (Statement stmt : stmts) {
//						String subj = Util.prettifyUri(stmt.getSubject()
//								.stringValue());
//						String pred = Util.prettifyUri(stmt.getPredicate()
//								.stringValue());
//						Value objVal = stmt.getObject();
//						String obj = null;
//						if (objVal instanceof Literal) {
//							obj = objVal.stringValue();
//						} else {
//							obj = Util.prettifyUri(stmt.getObject()
//									.stringValue());
//						}
//						fw.append(subj + "\n" + pred + "\n" + obj + "\n\n");
//					}
//					fw.flush();
//				} 
//				catch (Exception e) {
//					Util.logException(e, getClass());
//				}
				// /////////////////////////////////////////////////////////////

				ret = true;
				WriteSesame.addToRepository(repository, stmts, true);
			}
		} 
		catch (Exception e) {
			Util.logException(e, this.getClass());
		}
		return ret;

	}
	
	List<Statement> removeDuplicates(List<Statement> stmts) {
		List<Statement> ret = new ArrayList<Statement>();
		HashSet<String> stringReps = new HashSet<String>();
		for (Statement stmt : stmts) {
			String stringRep = stmt.toString();
			if (stringReps.contains(stringRep))	{
				continue; // it's a duplicate
			}
			stringReps.add(stringRep);
			ret.add(stmt);
		}

		return ret;
	}
	
	/**
	 * Empties the repository
	 */
	public void clear() {
		Statement stmtToRemove = new StatementImpl(null, null, null);
		RepositoryConnection repConn = null;
		try {
			repConn = repository.getConnection();
			repConn.remove(stmtToRemove);
			repConn.commit();
		} 
		catch (RepositoryException e) {
			Util.logException(e,  this.getClass());
		}
	}
	
	/**
	 * Dumps the repository contents as a stringbuilder in Turtle syntax
	 * @return
	 */
	public StringBuilder dumpRepository() {
		StringBuilder ret = new StringBuilder();
		
		// namespace prefixes
		Map<String, String> ns2prefix = Run.getRun().getNsPrefixes().getNs2Prefix();
		Map<String, String> prefix2Ns = Run.getRun().getNsPrefixes().getPrefix2Ns();
		for (Map.Entry<String, String> entry : ns2prefix.entrySet()) {
			String ns = entry.getKey();
			String prefix = entry.getValue();
			ret.append("@prefix " + prefix + " <" + ns + "> .\n");
		}
		
		// Need a default namespace to handle anonymous nodes (representing restrictions)
		ret.append("@prefix : <" + prefix2Ns.get("lessonDb:") + "> . \n");
				
		List<Statement>  stmts = ReadSesame.getFromRepository(repConn, null, null,
				null, false); // get all triples
		for (Statement stmt : stmts) {
			String subj = replaceAllNsWithPrefix(stmt.getSubject().stringValue());
			
			String pred = replaceAllNsWithPrefix(stmt.getPredicate().stringValue());
			Value obj = stmt.getObject();
			String objStr = replaceAllNsWithPrefix(obj.stringValue());
			if (obj instanceof Literal) {
				while (objStr.contains("\"")) { // homemade replaceAll
					objStr = objStr.replace("\"", "\'");
				}
				objStr = "\"" + objStr + "\"";
			}	
			else {
				objStr = replaceAllNsWithPrefix(objStr);
			}
			
			// hack to handle items that don't have a namespace prefix
			if (subj.startsWith("http://")) {
				subj = "<" + subj + ">";
			}
			if (pred.startsWith("http://")) {
				pred = "<" + pred + ">";
			}
			if (objStr.startsWith("http://")) {
				objStr = "<" + objStr + ">";
			}
			if (subj.startsWith("node")) {
				subj = ":" + subj;
			}
			if (objStr.startsWith("node")) {
				objStr = ":" + objStr;
			}

			ret.append(subj + " " + pred + " " + objStr + " . \n");
		}
		return ret;

	}

	String replaceAllNsWithPrefix(String in) {
		
		String out = in;
		Map<String, String> ns2prefix = Run.getRun().getNsPrefixes().getNs2Prefix();
		for (Map.Entry<String, String> entry : ns2prefix.entrySet()) {
			String ns = entry.getKey();
			String prefix = entry.getValue();
			out = out.replaceAll(ns, prefix);
		}
		return out;
	}


}
