package gov.nasa.jsc.mdrules.rules;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JTextArea;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import gov.nasa.jsc.mdrules.repository.SesameRepository;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;
import gov.nasa.jsc.mdrules.ux.UserExperience;
import gov.nasa.jsc.mdrules.defs.RunPropertyDefinitions;

/**
 * Executes the SPARQL rules either periodically or on demand. If a rule constructs an
 * "executeThis" statement, it then calls the method specified in that statement,
 * with the arguments specified in any "executeWith" statements.
 * 
 * @author sidneybailin
 *
 */
public class AssertionBasedExecution extends Thread {

	boolean stop = false;
	public void setStop() {
		stop = true;
	}
	
	long sleepInterval;
	public void setSleepInterval(long sleepInterval) {
		this.sleepInterval = sleepInterval;
	}
	
	Run runInstance = Run.getRun();
	
	static final String hasSubjectPredQname = "rdf:hasSubject";
	static final String hasPredicatePredQname = "rdf:hasPredicate";
	static final String hasObjectPredQname = "rdf:hasObject";
	
	SesameRepository repo = runInstance.getRepository();
	RepositoryConnection repConn = repo.getRepositoryConn();
	String schemaNamespace = runInstance.getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#";
	String dbNamespace = runInstance.getParamValue(RunPropertyDefinitions.LESSON_DB_NAMESPACE) + "#";
	URI execThisPred = RdfConstants.executeThisUri; 
	URI hasSubjectPred = new URIImpl(hasSubjectPredQname);
	URI hasPredicatePred = new URIImpl(hasPredicatePredQname);
	URI hasObjectPred = new URIImpl(hasObjectPredQname);
	
	Rules rules;
	public Rules getRules() {
		return rules;
	}

	public AssertionBasedExecution() {
		super();
		String sleepIntervalStr = runInstance.getParamValue(RunPropertyDefinitions.ASSERTION_BASED_EXEC_INTERVAL);
		if (sleepIntervalStr != null) {
			sleepInterval = new Long(sleepIntervalStr);
		}
		else {
			sleepInterval = -1;
		}
		rules = new Rules();
	}
		
	public void run() {
		
		for (;;) {			
			try {
				for (;;) {
					if (stop) {
						return;
					}
					
					// re-get the sleep interval because it may have changed
					String sleepIntervalStr = runInstance.getParamValue(RunPropertyDefinitions.ASSERTION_BASED_EXEC_INTERVAL);
					if (sleepIntervalStr != null) {
						sleepInterval = new Long(sleepIntervalStr);
					}
					else {
						sleepInterval = -1;
					}
					if (sleepInterval < 0) {
						return;
					}
					executeOneRule();
					

					try {
						sleep(sleepInterval);
					} 
					catch (InterruptedException e) {
						continue;
					}
				}
			}
			catch (Exception e) {
				Util.logException(e, this.getClass());
				continue;
			}
		}

	}
	
	
	public void executeOneRule() {
		
		// ////////////////////////////////////////////////////////////////////////////////
		// Remove all executeThis and executeWith statements
		// ////////////////////////////////////////////////////////////////////////////////

		try {
			repConn.remove((Resource)null, execThisPred, null);
			for (int i=1; ; ++i) {
				URI execWithPred = new URIImpl(RdfConstants.executeWithStr + "_" + i);
				RepositoryResult<Statement> execWithStmts = repConn
						.getStatements(null, execWithPred, null, false);
				if (!(execWithStmts.hasNext())) {
					break; // no more arguments
				}
				repConn.remove((Resource)null, execWithPred, null);
			}
		} 
		catch (RepositoryException e) {
			Util.logException(e, getClass());
		}
//		Util.printRepoContents(repConn); // debug
		
		// ////////////////////////////////////////////////////////////////////////////////
		// Execute the sparql rules until one returns TRUE indicating it did
		// something
		// ////////////////////////////////////////////////////////////////////////////////
		
		boolean fired = false;
		for (Rule rule : rules.getSortedRules()) {
//			System.out.println("Rule:\n\n" + rule.getName() + "\n");
			fired = Run.getRun().getRepository().executeSparqlRule(rule);
			if (fired) {
				String prompt = "\nFired Rule:\n\n" + rule.getName() + "\n";
//				System.out.println(prompt);
				
				//////////////////////////////////////////////////////////////////
				// for demo purposes only!
				JTextArea logTextArea = UserExperience.getLogTextArea();
				String oldText = logTextArea.getText();
				String newText = oldText + prompt;
				logTextArea.setText(newText);
//				JTextArea textArea = new JTextArea(prompt);
//				textArea.setEditable(false);
//				JOptionPane.showMessageDialog(
//		                UserExperience.getDialogFrame(),
//		                textArea,
//		                "Rule Engine",
//		                JOptionPane.PLAIN_MESSAGE,
//		                null);
				//////////////////////////////////////////////////////////////////

				break;
			}
		}

		if (fired) {
			
			// check for a java method to be executed, and if so, do it
			checkForExecuteThis();
		}
	}
	
	/**
	 * Executes sparql rules until there are no more that will fire
	 */
	public void executeRulesUntilNoMore() {
		
		// ////////////////////////////////////////////////////////////////////////////////
		// Remove all executeThis and executeWith statements
		// ////////////////////////////////////////////////////////////////////////////////

		try {
			repConn.remove((Resource)null, execThisPred, null);
			for (int i=1; ; ++i) {
				URI execWithPred = new URIImpl(RdfConstants.executeWithStr + "_" + i);
				RepositoryResult<Statement> execWithStmts = repConn
						.getStatements(null, execWithPred, null, false);
				if (!(execWithStmts.hasNext())) {
					break; // no more arguments
				}
				repConn.remove((Resource)null, execWithPred, null);
			}
		} 
		catch (RepositoryException e) {
			Util.logException(e, getClass());
		}
//		Util.printRepoContents(repConn); // debug
		
		// ////////////////////////////////////////////////////////////////////////////////
		// Execute the sparql rules until one returns TRUE indicating it did
		// something
		// ////////////////////////////////////////////////////////////////////////////////
		
		////////////////////
		// Debug
//		FileWriter fw = null;
//		try {
//			fw = new FileWriter(new File("/Users/sidneybailin/Documents/aWorkingOntologist/dump.txt"));
//		} 
//		catch (IOException e) {
//			Util.logException(e, getClass());
//		}
		////////////////////

		boolean somethingFired = true;
		boolean fired = false;
		Set<Rule> sortedRules = rules.getSortedRules();
		while (somethingFired) {
			somethingFired = false; // reset
			
			for (Rule rule : sortedRules) {
//				System.out.println("Rule:\n\n" + rule.getName() + "\n");
				fired = Run.getRun().getRepository().executeSparqlRule(rule);
				if (fired) {
					
					if (rule.getRemoveAfterExecution()) {
//						System.out.println("Removing " + rule.getName());
						sortedRules.remove(rule);
					}
					somethingFired = true;
					String prompt = "\nFired Rule:\n\n" + rule.getName() + "\n";
					Date date = new Date();
					prompt += date.toString() + "\n";
					System.out.println(prompt);
//					try {
//						fw.append(prompt);
//						fw.append(Util.dumpBlocksAndAttributesFromDatabase());
//					} 
//					catch (IOException e) {
//						Util.logException(e, getClass());
//					}

					// ////////////////////////////////////////////////////////////////
					// for demo purposes only!
					JTextArea logTextArea = UserExperience.getLogTextArea();
					String oldText = logTextArea.getText();
					String newText = oldText + prompt;
					logTextArea.setText(newText);
					// JTextArea textArea = new JTextArea(prompt);
					// textArea.setEditable(false);
					// JOptionPane.showMessageDialog(
					// UserExperience.getDialogFrame(),
					// textArea,
					// "Rule Engine",
					// JOptionPane.PLAIN_MESSAGE,
					// null);
					// ////////////////////////////////////////////////////////////////
					
					if (checkForExecuteThis()) {
						return;
					}
					
					break; // after a rule executes, start again trying from the highest priority rule
				}
			}
		}
		
		//////////////////////////////////////////////////////////
		// debug
//		Set<String> strs = new TreeSet<String>(); // sort output so we can comprehend it
//		Run runInstance = Run.getRun();
//		SesameRepository repo = runInstance.getRepository();
//		List<Statement> stmts = ReadSesame.getFromRepository(repo.getRepositoryConn(), null, RdfConstants.hasAttributeUri, null, false);
//		for (Statement stmt : stmts) {
//			String subj = Util.prettifyUri(stmt.getSubject().stringValue());
//			String pred = Util.prettifyUri(stmt.getPredicate().stringValue());
//			Value objVal = stmt.getObject();
//			String obj = null;
//			if (objVal instanceof Literal) {
//				obj = objVal.stringValue();
//			}
//			else {
//				obj = Util.prettifyUri(stmt.getObject().stringValue());
//			}
//			strs.add(subj + "\n" + pred + "\n" + obj + "\n\n");
//		}
//		for (String s : strs) {
//			System.out.println(s);
//		}
		///////////////////////////////////////////////////////////
		
	}
	
	/**
	 Check for a statement indicating a function should be
	 executed:
	 this is a statement of the form
	
	 execId :execThis execWhat
	
	 where execWhat is a string of the form
	 className:methodName and methodName
	 identified a static method of the java class named by
	 className.
	
	 The execId is used to identify the arguments that
	 should be passed
	 to the method. These arguments will be reified RDF
	 statements.
	 The object of each statement
	
	 execId :execUsing reifiedStmtId
	
	 is the URI of a reified statement. We use this URI to
	 pick up
	 the subject, predicate, and object of the reified
	 statement, as
	
	 reifiedStmtId rdf:hasSubject subj
	 reifiedStmtId rdf:hasPredicate pred
	 reifiedStmtId rdf:hasObject obj
	
	 We retrieve these and create from them an instance of
	 Statement, which
	 we then add to the list of arguments to be passed to
	 the method that
	 will be invoked.
	 */
	boolean checkForExecuteThis() {
		try {
			RepositoryResult<Statement> execThisStmts = repConn
					.getStatements(null, execThisPred, null, false);
			// just do one execution at a time
			if (execThisStmts.hasNext()) {
				Statement execThisStmt = execThisStmts.next();
				Resource execThisSubj = execThisStmt.getSubject();
				Value execThisObj = execThisStmt.getObject();

				// Get the class and static method to be executed
				// according to this statement
				String[] classAndMethod = execThisObj.stringValue().split(
						":");
				String className = classAndMethod[0];
				String methodName = classAndMethod[1];
				Class<?> cls = Class.forName(className);
				Method method = cls.getMethod(methodName, List.class);
				List<Value> argsToUse = new ArrayList<Value>();
				argsToUse.add((URI) execThisStmt.getSubject());

				// Get the arguments to be used when executing the method.
				// We pass the subject of the executeThis statement as the
				// first argument (for some reason, constructing a statement
				// with the executeWith subject also as the object results in 
				// the anonymous node ID for that subject being used as the object).
				for (int i = 1;; ++i) { // loop until no more argumentss
					URI execWithPred = new URIImpl(RdfConstants.executeWithStr
							+ "_" + i);
					RepositoryResult<Statement> execWithStmts = repConn
							.getStatements(execThisSubj, execWithPred, null,
									false);
					if (!(execWithStmts.hasNext())) {
						break; // no more arguments
					}
					while (execWithStmts.hasNext()) {
						Statement execWithStmt = execWithStmts.next();

						// the object of each "execute using" statement
						// is a URI or literal
						// to be used as an argument in the desired
						// method execution.
						Value execUsingObj = execWithStmt.getObject();
						
						// This is a bit of a hack - sometimes we want to add
						// multiple arguments from the same construct-slot, and
						// sometimes not, so the criterion we use is whether
						// it is a duplication of an already stowed argument
						if (!(argsToUse.contains(execUsingObj))) {
							argsToUse.add(execUsingObj);
						}
					}
				}

				// execute the method
				method.invoke(cls, argsToUse);

				// just do one execution at a time
				repConn.remove(execThisStmt);
				return true;
			}
		} 
		catch (Exception e) {
			Util.logException(e, getClass());
		}
		return false;
	}
	

}




































