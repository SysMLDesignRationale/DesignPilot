package gov.nasa.jsc.mdrules.lessons;

import gov.nasa.jsc.mdrules.qudt.Formula;
import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;
import gov.nasa.jsc.mdrules.defs.RunPropertyDefinitions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

public class Computation extends LessonComponent {

	String entityId;
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	String computedAttributeId;	
	public String getComputedAttributeId() {
		return computedAttributeId;
	}
	public void setComputedAttributeId(String computedAttributeId) {
		this.computedAttributeId = computedAttributeId;
	}

	List<List<Attribute>> usesAttributes = new ArrayList<List<Attribute>>();
	public List<List<Attribute>> getUsesAttributes() {
		return usesAttributes;
	}
	public void setUsesAttributesIds(List<List<Attribute>> usesAttributes) {
		this.usesAttributes = usesAttributes;
	}
	
	@Override
	public List<Statement> toRdf() {
		List<Statement> stmts = new ArrayList<Statement>();

		// :Comp a :Computation
//		String compStr = Util.genUri("Computation");
//		URI compUri = new URIImpl(compStr);
//		setUri(compUri);
		Statement stmtA = new StatementImpl(getUri(), RdfConstants.isa, RdfConstants.computationClassUri);
		stmts.add(stmtA);
		
		// :Comp :hasComputedAttr :Attr
		String computedAttributeStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_DB_NAMESPACE) + "#" + computedAttributeId;
		URI computedAttributeUri = new URIImpl(computedAttributeStr);
		Statement stmtB = new StatementImpl(getUri(), RdfConstants.hasComputedAttributeUri, computedAttributeUri);
		stmts.add(stmtB);

		// :Comp :hasEntity :Entity
		String entityStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_DB_NAMESPACE) + "#" + entityId;
		URI entityUri = new URIImpl(entityStr);
		Statement stmtE = new StatementImpl(getUri(), RdfConstants.hasEntityUri, entityUri);
		stmts.add(stmtE);

		for (List<Attribute> attrSet : usesAttributes) {
			
			// :Comp :usesAttributeSet :AttrSet
			String attrSetId = Util.genUri("AttributeSet");
			URI attrSetUri = new URIImpl(attrSetId);
			Statement stmtC = new StatementImpl(getUri(), RdfConstants.usesAttributeSetUri, attrSetUri);
			stmts.add(stmtC);
			
			for (Attribute attr : attrSet) {
				
				// :AttrSet :hasMember :Attr
				Statement stmtD = new StatementImpl(attrSetUri, RdfConstants.hasMemberUri, attr.getUri());
				stmts.add(stmtD);
			}			
		}
		return stmts;
	}
	
	public String toString(String indent) {
		StringBuilder ret = new StringBuilder();
		ret.append(" for attribute <b>" + getComputedAttributeLabel() + "</b>");
		ret.append(" of entity  <b>" + getEntityLabel() + "</b>");
		for (List<Attribute> attrSet : usesAttributes) {
			ret.append("<br />" + indent + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Uses attributes:" + attrSet);
		}
		ret.append("\n");
		return ret.toString();
	}
	
	/**
	 * Returns the name of a computation method using the specified attributes as part
	 */
	public String getComputeMethod(Set<Attribute> attrs) {
		
		StringBuilder ret = new StringBuilder();
		List<Formula> formulas = Run.getRun().getFormulas().getFormulas();
		for (Formula formula : formulas) {
			if (formula.worksWith(attrs)) {
				if (ret.length() > 0) {
					ret.append(", or ");
				}
				ret.append(formula.getName());
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

	String computedAttributeLabel;
	public String getComputedAttributeLabel() {
		if (computedAttributeLabel == null) {
			RepositoryConnection repConn = Run.getRun().getRepository()
					.getRepositoryConn();
			RepositoryResult<Statement> stmts = null;
			try {
				stmts = repConn.getStatements(
						new URIImpl(Util.sysmlIdToModelDbUri(computedAttributeId)),
						RdfConstants.rdfsLabelUri, null, false);
				if (stmts.hasNext()) {
					Statement stmt = stmts.next();
					computedAttributeLabel = stmt.getObject().stringValue();
				}
			} 
			catch (RepositoryException e) {
				Util.logException(e, getClass());
			}
		}
		return computedAttributeLabel;
		
	}


}
