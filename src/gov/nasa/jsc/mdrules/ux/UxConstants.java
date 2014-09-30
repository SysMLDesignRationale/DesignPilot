package gov.nasa.jsc.mdrules.ux;

public class UxConstants {
	
	static public final String SELECT_ACTION = "Select Action";
	static public final String OKAY = "Okay";
	static public final String CANCEL = "Cancel";
	
	static public final String CONSTRAINT_VIOLATION = "Constraint Violation";
	static public final String FIX = "Fix the constraint violation";
	static public final String OVERRIDE = "Override the constraint violation";
	
	static public final String GO_FIX = "After you have edited the design to fix the problem, you should re-run the analyzer.";
	static public final String GO_FIX_TITLE = "Fix and Re-Run";
	
	static public final String PROVIDE_RATIONALE = "Provide Rationale";
	static public final String WHAT_IS_RATIONALE_FOR = "<body style=\"color:green\">What is the <b>rationale</b> for overriding the constraint?<br /><br />This rationale will be saved and applied as a <b>lesson learned</b> in future design analyses.</body>";
	static public final String ACTUAL_VALUES_DIFFER = "The actual value of the attribute will be different";
	static public final String CONSTRAINT_NOT_RELEVANT = "The constraint is not relevant to my design goals";
	static public final String DEFER_TO_LATER = "I want to defer resolution of this issue to later";
	
	static public final String PROVIDE_REPLACEMENTS = "<body style=\"color:green\">How should the analyzer compute the <b>%s</b> attribute of <b>%s</b>?<br /><br />This information will be placed in a new constraint that takes precedence over the previously violated constraint.</body>";
	static public final String REPLACE_THIS = "The analyzer used the following values:\n";
	static public final String REPLACE_WITH = "Instead, the analyzer should use the following values:\n";
	static public final String ADD_REPL = "New Replacement";
	
	static public final String SELECT_ATTRS = "Select Attribute:";
	static public final String ADD_ATTR = "Add Attribute";
	
	static public final String CREATED_NEW_CONSTRAINT = "A new constraint has been created:";
	static public final String HERE_IS_OLD_CONSTRAINT = "Here is the old constraint:";
	static public final String NEW_CONSTRAINT = "New Constraint";
	
	static public final String INVALID_ATTRIBUTES = "Invalid Attributes";
	static public final String INVALID_ATTRIBUTES_FOLLOWING = "The following attributes are not valid:";

	static public final String DO_YOU_WANT_TO_SAVE_THIS = "Check here to save the %s to the design knowledge base";
	static public final String SAVED_NEW_CONSTRAINT = "The new constraint has been saved in the knowledge base\nand is available for future analyses.";
	static public final String SAVED_CONSTRAINT_TITLE = "Saved Constraint";
}
