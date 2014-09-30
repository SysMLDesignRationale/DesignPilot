package gov.nasa.jsc.mdrules.lessons;

import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;

/**
 * Represents a user's override of a constraint
 * @author sidneybailin
 *
 */
public class ConstraintOverride extends LessonComponent {
	
	Constraint constr;
	
	public ConstraintOverride(Constraint constr) {
		this.constr = constr;
	}

	@Override
	public List<Statement> toRdf() {
		List<Statement> stmts = new ArrayList<Statement>();
		
		// :Ovr a :ConstraintOverride
//		String ovrStr = Util.genUri("ConstraintOverride");
//		URI ovrUri = new URIImpl(ovrStr);
//		setUri(ovrUri);
		Statement stmtA = new StatementImpl(getUri(), RdfConstants.isa, RdfConstants.overrideClassUri);
		stmts.add(stmtA);
		
		// :Ovr :overrides :Constr
		URI constrUri = constr.getUri();
		Statement stmtB = new StatementImpl(getUri(), RdfConstants.overridesUri, constrUri);
		stmts.add(stmtB);
				
		return stmts;
	}

}
