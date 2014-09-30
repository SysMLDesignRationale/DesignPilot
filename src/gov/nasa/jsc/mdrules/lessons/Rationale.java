package gov.nasa.jsc.mdrules.lessons;

import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

public class Rationale extends LessonComponent {

	String rationaleType;
	public String getRationaleType() {
		return rationaleType;
	}
	public void setRationaleType(String rationaleType) {
		this.rationaleType = rationaleType;
	}
	
	ConstraintOverride rationaleFor;
	public ConstraintOverride isRationaleFor() {
		return rationaleFor;
	}
	public void setRationaleFor(ConstraintOverride ovr) {
		rationaleFor = ovr;
	}

	List<Object> supportingObjects = new ArrayList<Object>();
	public List<Object> getSupportingObjects() {
		return supportingObjects;
	}
	
	@Override
	public List<Statement> toRdf() {
		List<Statement> stmts = new ArrayList<Statement>();

		// :Rat a :Rationale
//		String ratStr = Util.genUri("Rationale");
//		URI ratUri = new URIImpl(ratStr);
//		setUri(ratUri);
		Statement stmtA = new StatementImpl(getUri(), RdfConstants.isa, RdfConstants.rationaleClassUri);
		stmts.add(stmtA);
		
		// :Rat :hasType :RationaleType
		URI ratTypeUri = new URIImpl(rationaleType);
		Statement stmtB = new StatementImpl(getUri(), RdfConstants.hasRationaleTypeUri, ratTypeUri);
		stmts.add(stmtB);

		// :Constraint :hasRationale :Rat
		URI constraintUri = rationaleFor.getUri();
		Statement stmtC = new StatementImpl(constraintUri, RdfConstants.hasRationaleUri, getUri());
		stmts.add(stmtC);

		
		return stmts;
	}
		
	List<Replacement> replacements = new ArrayList<Replacement>();
	public List<Replacement> getReplacements() {
		return replacements;
	}
	public List<Statement> addReplacement(Replacement repl) {
		replacements.add(repl);
		Statement stmt = new StatementImpl(getUri(), RdfConstants.hasReplacementUri, repl.getUri());
		List<Statement> stmts = new ArrayList<Statement>();
		stmts.add(stmt);
		return stmts; // we return this rather than writing it, to be consistent with toRdf()

	}

}
