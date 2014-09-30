package gov.nasa.jsc.mdrules.lessons;

import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.DecimalLiteralImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

public class Constraint extends LessonComponent implements Cloneable {
	

	int priority;
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	String modelId;
	public String getModelId() {
		return modelId;
	}
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

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

	BigDecimal min;	
	public BigDecimal getMin() {
		return min;
	}
	public void setMin(BigDecimal min) {
		this.min = min;
	}

	BigDecimal max;
	public BigDecimal getMax() {
		return max;
	}
	public void setMax(BigDecimal max) {
		this.max = max;
	}
	
	List<Computation> computationsToUse = new ArrayList<Computation>();
	public List<Computation> getComputationsToUse() {
		return computationsToUse;
	}
	
	@Override
	public List<Statement> toRdf() {
		
		List<Statement> stmts = new ArrayList<Statement>();

		// :Constr a :Constraint
//		String constrStr = Util.genUri("Constraint");
//		URI constrUri = new URIImpl(constrStr);
//		setUri(constrUri);
		Statement stmtA = new StatementImpl(getUri(), RdfConstants.isa, RdfConstants.constrClassUri);
		stmts.add(stmtA);
		
		// :Constr :hasPriority priority
		Literal priorityLit = new LiteralImpl(priority + "^^xsd:int");
		Statement stmtE = new StatementImpl(getUri(), RdfConstants.hasPriorityUri, priorityLit);
		stmts.add(stmtE);

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
				
		// :Constraint :hasStatement :StmtB
		Statement stmtB2 = new StatementImpl(getUri(), RdfConstants.hasStmtUri, stmtBUri);
		stmts.add(stmtB2);
		
		// Reify ":Attr :hasMin :Min"
		if (min != null) {
			URI stmtCUri = new URIImpl(Util.genUri("Statement"));
			Statement stmtSubjC = new StatementImpl(stmtCUri,
					RdfConstants.rdfSubjUri, attrUri);
			Statement stmtPredC = new StatementImpl(stmtCUri,
					RdfConstants.rdfPredUri, RdfConstants.hasMinValueUri);
			;
			Value minValue = new DecimalLiteralImpl(min);
			Statement stmtObjC = new StatementImpl(stmtCUri,
					RdfConstants.rdfObjUri, minValue);
			stmts.add(stmtSubjC);
			stmts.add(stmtPredC);
			stmts.add(stmtObjC);
			
			// :Constraint :hasStatement :StmtC
			Statement stmtC2 = new StatementImpl(getUri(), RdfConstants.hasStmtUri, stmtCUri);
			stmts.add(stmtC2);
		}
				
		// Reify ":Attr :hasMax :Max"
		if (max != null) {
			URI stmtDUri = new URIImpl(Util.genUri("Statement"));
			Statement stmtSubjD = new StatementImpl(stmtDUri,
					RdfConstants.rdfSubjUri, attrUri);
			Statement stmtPredD = new StatementImpl(stmtDUri,
					RdfConstants.rdfPredUri, RdfConstants.hasMinValueUri);
			;
			Value maxValue = new DecimalLiteralImpl(max);
			Statement stmtObjD = new StatementImpl(stmtDUri,
					RdfConstants.rdfObjUri, maxValue);
			stmts.add(stmtSubjD);
			stmts.add(stmtPredD);
			stmts.add(stmtObjD);
			
			// :Constraint :hasStatement :StmtD
			Statement stmtD2 = new StatementImpl(getUri(), RdfConstants.hasStmtUri, stmtDUri);
			stmts.add(stmtD2);
		}
		
		// :Constr :hasComputation :Comp
		for (Computation comp : computationsToUse) {
			
			Statement stmtF = new StatementImpl(getUri(), RdfConstants.hasComputationUri, comp.getUri());
			stmts.add(stmtF);	
			stmts.addAll(comp.toRdf());
		}
		
		return stmts;
	}
	
	public String toString(String indent) {
		StringBuilder ret = new StringBuilder();
		ret.append(indent + "     Weight: " + priority);
		ret.append("<br />" + indent + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Entity: <b>" + getEntityLabel() + "</b>");
		ret.append("<br />" + indent + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Attribute:  <b>" + getAttributeLabel() + "</b>");
		if (min != null) {
			if (!min.equals(new BigDecimal(0)))  // for demo purposes only!
				ret.append("<br />" + indent + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Min:   <b>" + min.doubleValue() + "</b>");
		}
		if (max != null) {
			ret.append("<br />" + indent + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Max:  <b>" + max.doubleValue() + "</b>");
		}
		for (Computation comp : computationsToUse) {
			ret.append("<br />" + indent + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Computation ");
			ret.append(comp.toString(indent + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
		}
//		ret.append("\n");
		return ret.toString();
	}
	
	@Override
	public Constraint clone() {
		Constraint ret = new Constraint();
		ret.setPriority(priority);
		ret.setModelId(modelId);
		ret.setEntityId(entityId);
		ret.setAttributeId(attributeId);
		ret.setMin(min);
		ret.setMax(max);
		ret.getComputationsToUse().addAll(computationsToUse);
		return ret;
	}
	
	/**
	 * Returns the name of a computation method using the specified attributes as part
	 * of the constraint's computations
	 */
	public String getComputeMethod(Set<Attribute> attrs) {
		StringBuilder ret = new StringBuilder();
		for (Computation computation : computationsToUse) {
			if (ret.length() > 0) {
				ret.append(", ");
			}
			String computeMethod = computation.getComputeMethod(attrs);
			if (computeMethod != null) {
				ret.append(computeMethod);
			}
		}
		return ret.toString();
	}
	
	String entityLabel;
	public String getEntityLabel() {
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
	public String getAttributeLabel() {
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
