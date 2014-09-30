package gov.nasa.jsc.mdrules.lessons;

import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.DecimalLiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 * Represents a violation of some constraint
 * @author sidneybailin
 *
 */
public class Violation extends LessonComponent {

	String entityId;
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	String attributeId;
	public String getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	
	Constraint constraint;
	public Constraint getConstraint() {
		return constraint;
	}
	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}

	BigDecimal value;
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
	@Override
	public List<Statement> toRdf() {
		List<Statement> stmts = new ArrayList<Statement>();

		// :Vio a :ConstraintViolation
//		String vioStr = Util.genUri("ConstraintViolation");
//		URI vioUri = new URIImpl(vioStr);
//		setUri(vioUri);
		Statement stmtA = new StatementImpl(getUri(), RdfConstants.isa, RdfConstants.violationClassUri);
		stmts.add(stmtA);
		
		// :Vio :violatesConstraint :Constr
		URI constrUri = constraint.getUri();
		Statement stmtD = new StatementImpl(getUri(), RdfConstants.violatesConstraintUri, constrUri);
		stmts.add(stmtD);
		
		// Reify ":Entity :hasAttribute :Attr"
		URI stmtBUri = new URIImpl(Util.genUri("Statement"));
		String entityUriStr = Util.sysmlIdToLessonDbUri(entityId);
		URI entityUri = new URIImpl(entityUriStr);
		String attrUriStr = Util.sysmlIdToLessonDbUri(attributeId);
		URI attrUri = new URIImpl(attrUriStr);
		Statement stmtSubjB = new StatementImpl(stmtBUri, RdfConstants.rdfSubjUri, entityUri);
		Statement stmtPredB = new StatementImpl(stmtBUri, RdfConstants.rdfPredUri, RdfConstants.hasAttributeUri);;
		Statement stmtObjB = new StatementImpl(stmtBUri, RdfConstants.rdfObjUri, attrUri);
		stmts.add(stmtSubjB);
		stmts.add(stmtPredB);
		stmts.add(stmtObjB);
				
		// :Violation :hasStatement :StmtB
		Statement stmtB2 = new StatementImpl(getUri(), RdfConstants.hasStmtUri, stmtBUri);
		stmts.add(stmtB2);
		
		// Reify ":Attr :hasValue :Value"
		URI stmtCUri = new URIImpl(Util.genUri("Statement"));
		Statement stmtSubjC = new StatementImpl(stmtCUri,
				RdfConstants.rdfSubjUri, attrUri);
		Statement stmtPredC = new StatementImpl(stmtCUri,
				RdfConstants.rdfPredUri, RdfConstants.hasValueUri);
		;
		Value valueVal = new DecimalLiteralImpl(value);
		Statement stmtObjC = new StatementImpl(stmtCUri,
				RdfConstants.rdfObjUri, valueVal);
		stmts.add(stmtSubjC);
		stmts.add(stmtPredC);
		stmts.add(stmtObjC);

		// :Violation :hasStatement :StmtC
		Statement stmtC2 = new StatementImpl(getUri(), RdfConstants.hasStmtUri,
				stmtCUri);
		stmts.add(stmtC2);

		return stmts;
	}
	
	public String toString(String indent) {
		StringBuilder ret = new StringBuilder();
		ret.append(indent + "Entity: " + entityId);
		ret.append("\n     " + indent + "Attribute: " + attributeId);
		ret.append("\n     " + indent + "Value:" + value.doubleValue());
		ret.append("\n     " + indent + "Constraint:\n" + constraint.toString(indent + "     "));
		return ret.toString();

	}
	
	public String toVerboseString(String indent) {
		StringBuilder ret = new StringBuilder();
		getEntityLabel();
		getAttributeLabel();
		ret.append("<html><head><style>a {color:#aa00aa;}</style></head><body style='color:red'>");
//		ret.append(indent + "The model <b>" + constraint.getModelId() + "</b> was analyzed,<br />and a constraint in the model was found to be violated.");
		ret.append(indent + "The model was analyzed, and a constraint in the model was found to be violated.");
		ret.append("<br /><br />" + indent + "This constraint applies to the <b>" + attributeLabel + "</b> attribute of the <a href='#'>" + entityLabel + "</a> entity .");
		ret.append("<br /><br />" + indent + "The constraint specifies a ");
		BigDecimal max = constraint.getMax();
		BigDecimal min = constraint.getMin();
		
		// HACK FOR DEMO!!!!
		if (min.equals(new BigDecimal(0))) {
			min = null; // just doesn't look good, and I forced it to be zero in the rules
		}
		
		if (max != null) {
			ret.append("maximum value of <b>" + max.doubleValue() + "</b>");
			if (min == null) {
				ret.append(".");
			}
			else {
				ret.append("<br /> and a ");
			}
		}
		if (min != null) {
			ret.append("minimum value of <b>" + min.doubleValue() + "</b>");
			
		}
		ret.append(" for the <b>" + attributeLabel + "</b> attribute of <a href='#'>" + entityLabel + "</a>.");
		ret.append("<br /><br />" + indent + "But the computed value of this attribute is <b>" + value.doubleValue() + "</b>");
		ret.append("</body></html>");
		return ret.toString();

	}
	
	String entityLabel;
	String getEntityLabel() {
		if (entityLabel == null) {
			RepositoryConnection repConn = Run.getRun().getRepository()
					.getRepositoryConn();
			RepositoryResult<Statement> stmts = null;
			try {
				stmts = repConn.getStatements(
						new URIImpl(Util.sysmlIdToModelDbUri(entityId)),
						RdfConstants.rdfsLabelUri, null, false);
				if (stmts.hasNext()) {
					Statement stmt = stmts.next();
					entityLabel = stmt.getObject().stringValue();
				}
			} 
			catch (RepositoryException e) {
				Util.logException(e, getClass());
			}
		}
		return entityLabel;		
	}

	String attributeLabel;
	String getAttributeLabel() {
		if (attributeLabel == null) {
			RepositoryConnection repConn = Run.getRun().getRepository()
					.getRepositoryConn();
			RepositoryResult<Statement> stmts = null;
			try {
				stmts = repConn.getStatements(
						new URIImpl(Util.sysmlIdToModelDbUri(attributeId)),
						RdfConstants.rdfsLabelUri, null, false);
				if (stmts.hasNext()) {
					Statement stmt = stmts.next();
					attributeLabel = stmt.getObject().stringValue();
				}
			} 
			catch (RepositoryException e) {
				Util.logException(e, getClass());
			}
		}
		return attributeLabel;
		
	}


}
