package gov.nasa.jsc.mdrules.ux;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.MainFrame;

import gov.nasa.jsc.mdrules.action.RunRuleEngine;
import gov.nasa.jsc.mdrules.defs.RunPropertyDefinitions;
import gov.nasa.jsc.mdrules.lessons.Attribute;
import gov.nasa.jsc.mdrules.lessons.Computation;
import gov.nasa.jsc.mdrules.lessons.Constraint;
import gov.nasa.jsc.mdrules.lessons.ConstraintOverride;
import gov.nasa.jsc.mdrules.lessons.Rationale;
import gov.nasa.jsc.mdrules.lessons.Replacement;
import gov.nasa.jsc.mdrules.lessons.Violation;
import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import gov.nasa.jsc.mdrules.repository.ReadSesame;
import gov.nasa.jsc.mdrules.repository.WriteSesame;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;

/**
 * Handles the user interface of the orion power distribution demo
 * @author Sidney Bailin
 *
 */
public class UserExperience {
	
	static Statement actionStmt; // controls the state of the rule engine
	
	static JFrame dialogFrame = new JFrame();
	static public JFrame getDialogFrame() {
		return dialogFrame;
	}
	
	static JFrame logFrame = new JFrame();
	static public JFrame getLogFrame() {
		return logFrame;
	}
	
	static JTextArea logTextArea = new JTextArea();
	static public JTextArea getLogTextArea() {
		return logTextArea;
	}
	
	public UserExperience() {
		
		// create window for running log of what happens
		String showLog = Run.getRun().getParamValue(RunPropertyDefinitions.SHOW_LOG_WINDOW);
		if (showLog != null) {
			showLog = showLog.trim().toLowerCase();
		}
		else {
			showLog = "false";
		}
		if (showLog.equals("true") || showLog.equals("yes")) {
			
			logFrame.setAlwaysOnTop(true);
			logFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			logFrame.getContentPane().removeAll();
			JPanel logPanel = new JPanel();
			logTextArea.setColumns(200);
			logTextArea.setRows(200);
			logTextArea.setEditable(false);
			logPanel.add(logTextArea);
			JScrollPane logScrollPane = new JScrollPane(logPanel);
			logScrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			logScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			Dimension logSize = new Dimension(480, 300);
			logScrollPane.setPreferredSize(logSize);

			logFrame.getContentPane().add(logScrollPane);
			logFrame.setTitle("Log");
			logFrame.pack();

			// position it on the right-hand side of screen
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
			Rectangle rect = defaultScreen.getDefaultConfiguration()
					.getBounds();
			int x = (int) rect.getMaxX() - (logFrame.getWidth() + 20);
			int y = (int) ((rect.getMaxY() - rect.getMinY()) / 2)
					- (logFrame.getHeight() / 2);
			logFrame.setLocation(x, y);
			logFrame.setResizable(true);
			String minimizeLog = Run.getRun().getParamValue(RunPropertyDefinitions.MINIMIZE_LOG_WINDOW).trim().toLowerCase();
			if (minimizeLog.equals("true") || minimizeLog.equals("yes")) {
				logFrame.setState(JFrame.ICONIFIED);
			}
			logFrame.setVisible(true);
		}
		
	}
	
	/**
	 * Callback from rule engine.
	 * Handles a constraint violation discovered through analysis of a SysML model
	 * by the rule engine.
	 * @param constraintViolationUri - OWL instance of class ConstraintViolation
	 * @param constraintUri - OWL instance of class Constraint
	 * @param entityUri - OWL instance of class Block
	 * @param attrUri - OWL instance of class Attribute
	 * @param min - minimum allowed value of the attribute
	 * @param max - maximum allowed value of the attribute
	 * @param val - actual value of the attribute
	 * @param computationUri - OWL instance of class Computation
	 */
	static public void constraintViolated(List<Value> args) {
		
		MainFrame mainFrame = Application.getInstance().getMainFrame();
		RunRuleEngine.setCursor(mainFrame, Cursor.getDefaultCursor());
		RepositoryConnection repConn = Run.getRun().getRepository().getRepositoryConn();

		URI constraintUri = (URI)args.get(1);
		URI entityUri = (URI)args.get(2);
		URI attrUri = (URI)args.get(3);
		Literal min = (Literal)args.get(4);
		Literal max = (Literal)args.get(5);
		Literal val = (Literal)args.get(6);
		URI compUri = (URI)args.get(7);
		
		// create attribute object
		Attribute attrObj = new Attribute();
		attrObj.setUri(attrUri);
				
		// create constraint object
		Constraint constr = new Constraint();
		constr.setUri(constraintUri);
		String entityUriStr = entityUri.stringValue();
		int ind1 = entityUriStr.indexOf("#");
		String entityId = entityUriStr.substring(ind1+1);
		constr.setEntityId(entityId);
		
		// get constraint priority
		List<Statement> priorityStmts = ReadSesame.getFromRepository(repConn, constraintUri, RdfConstants.hasPriorityUri, null, false);
		if (priorityStmts.size() > 0) {
			Literal priority = (Literal)priorityStmts.get(0).getObject();
			constr.setPriority(priority.intValue());
		}

		String attrUriStr = attrUri.stringValue();
		int ind2 = attrUriStr.indexOf("#");
		String attrId = attrUriStr.substring(ind2+1);
		constr.setAttributeId(attrId);
		
		if (min != null)  {
			constr.setMin(min.decimalValue());
		}
		if (max != null)  {
			constr.setMax(max.decimalValue());
		}
		
		// Create a Computation object and hang it off the Constraint
		// TODO - remove limit of one computation (as passed by Rules)
		Computation comp = new Computation();
		comp.setUri(compUri);
		
		// borrow these from the constraint, although they should hang off the computation URI in the RDF too
		comp.setEntityId(entityId);
		comp.setComputedAttributeId(attrId);
		
		List<List<Attribute>> attrsToUse = comp.getUsesAttributes();
		constr.getComputationsToUse().add(comp);
		List<Statement> attrSetStmts = ReadSesame.getFromRepository(repConn, compUri, RdfConstants.usesAttributeSetUri, null, false);
		for (Statement attrSetStmt : attrSetStmts) {
			List<Attribute> attrSet = new ArrayList<Attribute>();
			URI attrSetUri = (URI)attrSetStmt.getObject();
			List<Statement> attrStmts = ReadSesame.getFromRepository(repConn, attrSetUri, RdfConstants.hasMemberUri, null, false);
			for (Statement attrStmt : attrStmts) {
				URI attrToUseUri = (URI)attrStmt.getObject();
				Attribute attrToUse = new Attribute();
				attrToUse.setUri(attrToUseUri);
				attrSet.add(attrToUse);
			}
			attrsToUse.add(attrSet);
		}
		
		// Create a Violation object
		Violation violation = new Violation();
		violation.setConstraint(constr);
		violation.setEntityId(entityId);
		violation.setAttributeId(attrId);
		violation.setValue(val.decimalValue());
		
		// create the violation in the repository
		List<Statement> vioStmts = violation.toRdf();	
				
		// TODO - we probably want to put these transient statements in a separate graph
//		System.out.println("Adding to repository:\n\n");
//		for (Statement stmt : vioStmts) {
//			System.out.println("\t" + stmt);
//		}
//		System.out.println("\n");
		WriteSesame.addToRepository(repConn, vioStmts, true);
	
		Run.getRun().getRuleEngine().executeOneRule();

	}
	

	
//	/**
//	 * Handles a constraint violation discovered through analysis of a SysML model.
//	 * The assumption is that this will be called by the analyzer, either directly
//	 * or indirectly.
//	 * 
//	 * @param modelId
//	 * @param constraint
//	 * @param violation
//	 */
//	public void constraintViolatedOld(String modelId, Constraint constraint, Violation violation) {
//		
//		// create contraint in rdf
//		// TODO - check if the constraint already exists in the repository
//		List<Statement> constrStmts = constraint.toRdf();
//		
//		// create the violation in the repository
//		List<Statement> vioStmts = violation.toRdf();		
//		
//		List<Statement> allStmts = new ArrayList<Statement>();
//		allStmts.addAll(constrStmts);
//		allStmts.addAll(vioStmts);
////		Util.printStatements(allStmts); // debug
//		
//		// TODO - we probably want to put these transient statements in a separate graph
//		System.out.println("Adding to repository:\n\n");
//		for (Statement stmt : allStmts) {
//			System.out.println("\t" + stmt);
//		}
//		System.out.println("\n");
//		WriteSesame.addToRepository(Run.getRun().getRepository().getRepositoryConn(), allStmts, true);
//		Run.getRun().getRuleEngine().executeOneRule();
//
//	}
	
	/**
	 * Callback from rule engine.
	 * Presents a constraint violation to user and prompts for response
	 * @param violation - OWL instance of class ConstraintViolation
	 */
	static public void presentToUserConstraintViolation(List<Value> args) {
		clearActionStatement();
		URI violation = (URI)args.get(0);
		Violation vioObj = (Violation)Util.getComponentWithUri(violation);
		
		String prompt = vioObj.toVerboseString("") + "\n" ;
		JTextArea promptArea = new JTextArea(prompt);
		promptArea.setEditable(false);
		promptArea.setTabSize(2);
		promptArea.setMinimumSize(new Dimension(600, 600));
		String[] possibilities = {UxConstants.FIX, UxConstants.OVERRIDE};
		
		List<Object> callbackArgs = new ArrayList<Object>();
		callbackArgs.add(violation);
		
		try {
			ChoiceDialog.showDialog(
				dialogFrame,
				prompt,
				Color.red,
				UxConstants.CONSTRAINT_VIOLATION,
				possibilities,
				UserExperience.class.getMethod("processUserResponseToConstraintViolation", List.class),
				callbackArgs,
				UserExperience.class.getMethod("highlightNodeFromSelectedText", String.class)
			);
		} 
		catch (Exception e) {
			Util.logException(e, UserExperience.class);
		} 
	}
	
	/**
	 * Callback from UI: user responded to constraint violation notice
	 * @param violation
	 * @param response
	 */
	@SuppressWarnings("rawtypes")
	static public void processUserResponseToConstraintViolation(List args) {
		
		URI violationUri = (URI)args.get(0);
		String response = (String)args.get(1);
		
		if (response.equals(UxConstants.FIX)) {
			JOptionPane.showMessageDialog(dialogFrame, UxConstants.GO_FIX, UxConstants.GO_FIX_TITLE, JOptionPane.PLAIN_MESSAGE);
		}
		
		if (response.equals(UxConstants.OVERRIDE)) {
			Violation vio = (Violation)Util.getComponentWithUri(violationUri);
			Constraint constr = vio.getConstraint();
			ConstraintOverride ovr = new ConstraintOverride(constr);
			List<Statement> stmts = ovr.toRdf();
			actionStmt = new StatementImpl(violationUri, RdfConstants.userActionUri, RdfConstants.userActionOverrideUri);
			stmts.add(actionStmt);
			
			// TODO - we probably want to put these transient statements in a separate graph
			System.out.println("Adding to repository:\n\n");
			for (Statement stmt : stmts) {
				System.out.println("\t" + stmt);
			}
			System.out.println("\n");
			WriteSesame.addToRepository(Run.getRun().getRepository().getRepositoryConn(), stmts, true);
			Run.getRun().getRuleEngine().executeOneRule();			
		}
	}
		
	/**
	 * Callback from rule engine.
	 * Prompts user to provide a rationale for overriding a constraint
	 * @param violation - OWL instance of class ConstraintViolation
	 * @param override - OWL instance of class ConstraintOverride
	 * @return
	 */
	static public void prompUserForOverrideRationale(List<Value> args) {
		
		clearActionStatement();
		clearExecStatements();
		
		URI override = (URI)args.get(0);
		URI constraint = (URI)args.get(1);
		URI violation = (URI)args.get(2);
		
		String prompt = UxConstants.WHAT_IS_RATIONALE_FOR;
		JTextArea promptArea = new JTextArea(prompt);
		promptArea.setEditable(false);
		promptArea.setTabSize(2);
		String[] possibilities = {UxConstants.CONSTRAINT_NOT_RELEVANT, UxConstants.DEFER_TO_LATER, UxConstants.ACTUAL_VALUES_DIFFER};

		List<Object> callbackArgs = new ArrayList<Object>();
		callbackArgs.add(override);
		callbackArgs.add(constraint);
		callbackArgs.add(violation);
		
		try {
			ChoiceDialog.showDialog(
				dialogFrame,
				prompt,
				Color.green.darker().darker(),
				UxConstants.PROVIDE_RATIONALE,
				possibilities,
				UserExperience.class.getMethod("obtainFromUserRationaleForOverride", List.class),
				callbackArgs,
				UserExperience.class.getMethod("highlightNodeFromSelectedText", String.class)
			);
		} 
		catch (Exception e) {
			Util.logException(e, UserExperience.class);
		} 

	}
	
	/**
	 * Callback from UI: user provides rationale for constraint override
	 * @param violation
	 * @param override
	 */
	@SuppressWarnings("rawtypes")
	static public void obtainFromUserRationaleForOverride(List args) {

		URI override = (URI)args.get(0);
//		URI constraint = (URI)args.get(1);
		URI violation = (URI)args.get(2);
		String response = (String)args.get(3);
		if (response.equals(UxConstants.ACTUAL_VALUES_DIFFER)) {

			// create rdf instance of Rationale with its RationaleType 
			Rationale rat = new Rationale();
			rat.setRationaleType(RdfConstants.actualValuesWillDifferRationaleTypeStr);
			rat.setRationaleFor((ConstraintOverride)Util.getComponentWithUri(override));
			List<Statement> stmts = rat.toRdf();
			
			actionStmt = new StatementImpl(override, RdfConstants.userActionUri, RdfConstants.userActionRationaleProvidedUri);
			stmts.add(actionStmt);
			
			// remove the violation from the database
			Statement removeThis = new StatementImpl(violation, null, null);
			System.out.println("Removing from repository:\n\n\t" + removeThis);
			try {
				Run.getRun().getRepository().getRepositoryConn().remove(removeThis);
			} 
			catch (RepositoryException e) {
				Util.logException(e, UserExperience.class);
			}
			
			System.out.println("Adding to repository:\n\n");
			for (Statement stmt : stmts) {
				System.out.println("\t" + stmt);
			}
			System.out.println("\n");
			WriteSesame.addToRepository(Run.getRun().getRepository().getRepositoryConn(), stmts, true);
			Run.getRun().getRuleEngine().executeOneRule();
		}
	}
	
	/**
	 * Callback from rule engine.
	 * Prompts user to provide replacements substantiating an Actual Values will Differ
	 * override rationale
	 * @param violation - OWL instance of class ConstraintViolation
	 * @param rat - OWL instance of class Rationale
	 * @return
	 */
	static public void promptUserForComputationReplacements(List<Value> args) {
		dialogFrame.getContentPane().removeAll();
		clearActionStatement();
		clearExecStatements();
		URI override = (URI)args.get(0);
		URI constraint = (URI)args.get(1);
		URI rationale = (URI)args.get(2);
		Constraint constraintObj = (Constraint)Util.getComponentWithUri(constraint);
		String prompt = String.format(UxConstants.PROVIDE_REPLACEMENTS, constraintObj.getAttributeLabel(), constraintObj.getEntityLabel());

		RepositoryConnection repConn = Run.getRun().getRepository().getRepositoryConn();
		
		// Get name of diagram that constraint refers to - to filter set of relevant attributes
		String diagramName = null;
		String entityUriStr = Util.sysmlIdToModelDbUri(constraintObj.getEntityId());
		URI entityUri = new URIImpl(entityUriStr);
		List<Statement> diagStmts = ReadSesame.getFromRepository(repConn, entityUri, RdfConstants.isInDiagramUri, null, false);
		if (diagStmts.size() > 0) {
			diagramName = diagStmts.get(0).getObject().stringValue();
		}
		// Create a list of acceptable attribute types: those used in some formula
		Set<String> knownTypes = Run.getRun().getFormulas().getKnownTypes();

		// Collect all attributes, sorted
		Set<Attribute> attributes = new TreeSet<Attribute>();
		List<Statement> stmts = null;
		stmts = ReadSesame.getFromRepository(repConn, null, RdfConstants.isa, RdfConstants.attrUri, false);
		for (Statement stmt : stmts) {
			Attribute attr = new Attribute();
			attr.setUri((URI)stmt.getSubject());
			
			// check that the attribute's type is mentioned in some formula
			if (!(knownTypes.contains(attr.getType()))) {
				continue;
			}
			
			// check that the attribute belongs to the right diagram
			String attrDiagramName = null;
			URI attrEntityUri = attr.getEntityUri();
			List<Statement> attrDiagStmts = ReadSesame.getFromRepository(repConn, attrEntityUri, RdfConstants.isInDiagramUri, null, false);
			if (attrDiagStmts.size() > 0) {
				attrDiagramName = attrDiagStmts.get(0).getObject().stringValue();
				if ((attrDiagramName == null) || !(diagramName.equals(attrDiagramName))) {
					continue;
				}
			}
			else {
				continue;
			}
			attributes.add(attr);
		}


		// Initialize replacement panes with any relevant existing computation in the constraint
		List<ReplacementPane> replacementPaneList = new ArrayList<ReplacementPane>();
		Computation relevantComputation = null;
		for (Computation comp : constraintObj.getComputationsToUse()) {
			if (comp.getEntityId().equals(constraintObj.getEntityId()) && comp.getComputedAttributeId().equals(constraintObj.getAttributeId())) {
				relevantComputation = comp;
				break;
			}
		}
		if (relevantComputation != null) {
			for (List<Attribute> attrSet : relevantComputation.getUsesAttributes()) {
				Set<Attribute> replaceThis = new HashSet<Attribute>();
				replaceThis.addAll(attrSet);
				Set<Attribute> replaceWith = new HashSet<Attribute>();
				Replacement repl = new Replacement(replaceThis, replaceWith);
				ReplacementPane replPane = new ReplacementPane(constraintObj, repl, attributes);
				String replThis = new String();
				for (Attribute attr : attrSet) {
					if (replThis.length() > 0) {
						replThis += ReplacementDialog.getAttrSeparator();
					}
					
					// get the owning entity's label
					List<Statement> entityLabelStmts = ReadSesame.getFromRepository(repConn, attr.getEntityUri(), RdfConstants.rdfsLabelUri, null, false);
					if (entityLabelStmts.size() == 0) {
						throw new IllegalStateException("Computation uses attribute " + attr.getUri() + " whose owning entity has no label: " + attr.getEntityUri());
					}
					String entityLabel = entityLabelStmts.get(0).getObject().stringValue();
					
					replThis += entityLabel + "." + attr.getLabel();
				}
				replPane.setReplaceThis(replThis);
				replPane.setReplaceThisReadOnly();
				replacementPaneList.add(replPane);
			}
		}
		
        // Initializing with 3 elements is a hack to keep the ReplacementPane the right size, but 
		// in ReplacementDialog we make the 2nd and 3rd invisible until they are needed.
		int replacementArraySize = 3; // minimum size for proper sizing of window
		int replacementListSize = replacementPaneList.size();
		if (replacementArraySize < replacementListSize) {
			replacementArraySize = replacementListSize; // ensure we can include the existing replace-this elements
		}
		ReplacementPane replacements[] = new ReplacementPane[replacementArraySize];
		int i=0;
		for (ReplacementPane replPane : replacementPaneList) {
			replacements[i] = replPane;
			++i;
		}
		for (; i<replacementArraySize; ++i) {
			Set<Attribute> replaceThis = new HashSet<Attribute>();
			Set<Attribute> replaceWith = new HashSet<Attribute>();
			Replacement repl = new Replacement(replaceThis, replaceWith);
			replacements[i] = new ReplacementPane(constraintObj, repl, attributes);
		}
		
		// temporarily hard coded:
//		String[] attributes = {
//				"S3_RPC1.Current",
//				"S3_Load1.Voltage",
//				"S3_Load1.Resistance",
//				"S3_RPC2.Current",
//				"S3_Load2.Voltage",
//				"S3_Load2.Resistance",
//				"S3_RPC3.Current",
//				"S3_Load3.Voltage",
//				"S3_Load3.Resistance",
//				"S3_RPC4.Current",
//				"S3_Load4.Voltage",
//				"S3_Load4.Resistance"
//		};
		
		List<Object> callbackArgs = new ArrayList<Object>();
		callbackArgs.add(override);
		callbackArgs.add(constraint);
		callbackArgs.add(rationale);

		try {
			ReplacementDialog.showDialog(
					constraintObj,
					dialogFrame,
					null,
					prompt,
					"Specify New Computation",
					replacements,
					attributes,
					UserExperience.class.getMethod("obtainFromUserComputationReplacements", List.class),
					callbackArgs
					);
		} 
		catch (Exception e) {
			Util.logException(e, UserExperience.class);
		} 
//		List<ReplacementStr> replStrs = ReplacementDialog.getDialog().getReplacements();
//		obtainFromUserComputationReplacements(override, constraint, rationale, replStrs);
		
	}
	
	/**
	 * Callback from UI: user provides computation replacements supporting Actual Values will Differ
	 * rationale
	 * @param violation
	 * @param rat
	 */
	@SuppressWarnings("rawtypes")
	static public void obtainFromUserComputationReplacements(List args) {
		
		URI override = (URI)args.get(0);
//		URI constraint = (URI)args.get(1);
		URI rationale = (URI)args.get(2);
		List<Replacement> repls = ReplacementDialog.getDialog().getReplacements();
		
		// create replacement instances in rdf
		List<Statement> allStmts = new ArrayList<Statement>();
		for (Replacement repl : repls) {
			allStmts.addAll(repl.toRdf());
			Rationale ratObj = (Rationale)Util.getComponentWithUri(rationale);
			List<Statement> stmts = ratObj.addReplacement(repl);
			allStmts.addAll(stmts);
		}
		actionStmt = new StatementImpl(override, RdfConstants.userActionUri, RdfConstants.userActionReplacementsProvidedUri);
		allStmts.add(actionStmt);
		
		System.out.println("Adding to repository:\n\n");
		for (Statement stmt : allStmts) {
			System.out.println("\t" + stmt);
		}
		System.out.println("\n");
		WriteSesame.addToRepository(Run.getRun().getRepository().getRepositoryConn(), allStmts, true);
		Run.getRun().getRuleEngine().executeOneRule();

	}
	
	/**
	 * Callback from rule engine.
	 * Generates a new constraint using a computation based on the replacements
	 * specified as part of the Actual Values will Differ override-rationale
	 */
	@SuppressWarnings("unused")
	static public void generateConstraintFromReplacements(List<Value> args) {
		
		clearActionStatement();
		clearExecStatements();
		URI overrideUri = (URI)args.get(0);
		URI constraintUri = (URI)args.get(1);
		URI rationaleUri = (URI)args.get(2);
		Constraint constr = (Constraint)Util.getComponentWithUri(constraintUri);
		List<Replacement> replacements = new ArrayList<Replacement>();
		for (int i=3; i<args.size(); ++i) {
			Replacement repl = (Replacement)Util.getComponentWithUri((URI)args.get(i));
			replacements.add(repl);
		}

		// create an instance of the OWL Computation class, with computed attribute
		// the attribute of the constraint
		Computation comp = new Computation();
		String computedAttrId = constr.getAttributeId();
		String entityId = constr.getEntityId();
		comp.setEntityId(entityId);
		comp.setComputedAttributeId(computedAttrId);
		
		// give it the "uses" attribute sets specified in the replacement as "replaceWith"
		List<List<Attribute>> usesAttrs = comp.getUsesAttributes();
		for (Replacement repl : replacements) {
			List<Attribute> attrSet = new ArrayList<Attribute>();
			attrSet.addAll(repl.getReplaceWith());
			usesAttrs.add(attrSet);
		}
		
		// create a constraint like the original one but give it a higher priority,
		// and attach the new computation to it
		Constraint newConstr = constr.clone();
		newConstr.setPriority(constr.getPriority()+1);
		List<Computation> newConstrComputations = newConstr.getComputationsToUse();
		
		// Before attaching the new computation, remove any computations already in
		// the constraint that compute the same attribute
		List<Computation> removeThese = new ArrayList<Computation>();
		for (Computation oldComp : newConstrComputations) {
			if (entityId.equals(oldComp.getEntityId()) && 
					computedAttrId.equals(oldComp.getComputedAttributeId())) {
				removeThese.add(oldComp);
			}
		}
		newConstrComputations.removeAll(removeThese);
		
		// now add the new computation
		newConstrComputations.add(comp);
		
		// Notify user of new constraint
		String prompt1 = "<body style=\"color:green\">" + UxConstants.CREATED_NEW_CONSTRAINT + ":<br /><br />" + newConstr.toString("") + "</body>";	
		String prompt2 = "<body style=\"color:red\">" + UxConstants.HERE_IS_OLD_CONSTRAINT + ":<br /><br />" + constr.toString("");	
		List<Object> callbackArgs = new ArrayList<Object>();
		callbackArgs.add(newConstr);
		callbackArgs.add(comp);
		
		try {
			ConfirmSaveDialog.showDialog(
				dialogFrame,
				prompt1,
				prompt2,
				Color.green.darker().darker(),
				UxConstants.NEW_CONSTRAINT,
				String.format(UxConstants.DO_YOU_WANT_TO_SAVE_THIS, "new constraint"),
				UserExperience.class.getMethod("processUserResponseToNewConstraint", List.class),
				callbackArgs
			);
		} 
		catch (Exception e) {
			Util.logException(e, UserExperience.class);
		} 
	}
	
	static public void processUserResponseToNewConstraint(List<Object> args) {
		
		Constraint newConstr = (Constraint)args.get(0);
		Computation comp = (Computation)args.get(1);
		boolean checked = ((Boolean)args.get(2)).booleanValue();
		
		if (checked) {
			// Create RDF for the new constraint and computation
			List<Statement> stmts = comp.toRdf();
			stmts.addAll(newConstr.toRdf());

			System.out.println("Adding to repository:\n\n");
			for (Statement stmt : stmts) {
				System.out.println("\t" + stmt);
			}
			System.out.println("\n");
			WriteSesame.addToRepository(Run.getRun().getRepository()
					.getRepositoryConn(), stmts, true);

			JOptionPane.showMessageDialog(dialogFrame,
					UxConstants.SAVED_NEW_CONSTRAINT,
					UxConstants.SAVED_CONSTRAINT_TITLE,
					JOptionPane.PLAIN_MESSAGE);
		}
		RunRuleEngine.setAlreadyExecuting(false);
	}
	
	/**
	 * Removes the most recent action statement from the repository
	 */
	static void clearActionStatement() {
		if (actionStmt != null) {
			System.out.println("Removing from repository:\n\n\t" + actionStmt);
			RepositoryConnection repConn = Run.getRun().getRepository().getRepositoryConn();
			try {
				repConn.remove(actionStmt);
			} 
			catch (RepositoryException e) {
				Util.logException(e, UserExperience.class);
			}
		}
	}
	
	/**
	 * Removes the most recent action statement from the repository
	 */
	static void clearExecStatements() {
		RepositoryConnection repConn = Run.getRun().getRepository().getRepositoryConn();
		try {
			repConn.remove((Resource)null, RdfConstants.executeThisUri, null);
			repConn.remove((Resource)null, RdfConstants.executeWithUri, null);
		} 
		catch (RepositoryException e) {
			Util.logException(e, UserExperience.class);
		}

	}
	
	/**
	 * Places a GUI component in the center of the screen
	 * @param c
	 */
	static void center(Component c ) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		c.setLocation(screenSize.width/2 - c.getWidth()/2,
				screenSize.height/2 - c.getHeight()/2);
	}
	
	/**
	 * Highlights a MagicDraw node corresponding to selected text
	 */
	public static void highlightNodeFromSelectedText(String nodeName) {
//		System.out.println("Highlighting: " + nodeName);
		if (nodeName == null) {
			return;
		}
		if (nodeName.length() == 0) {
			return;
		}
		if (MagicDrawScreen.highlightNode(nodeName)) {
			ChoiceDialog.minimize();
			logFrame.setState(JFrame.ICONIFIED);
		}
	}

	
}
