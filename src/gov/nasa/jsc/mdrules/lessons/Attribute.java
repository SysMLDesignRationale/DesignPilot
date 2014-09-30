package gov.nasa.jsc.mdrules.lessons;

import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

public class Attribute extends LessonComponent implements Comparable<Attribute> {

	String label;
	public String getLabel() {
		if (label == null) {
			RepositoryConnection repConn = Run.getRun().getRepository()
					.getRepositoryConn();
			RepositoryResult<Statement> stmts = null;
			try {
				stmts = repConn.getStatements(
						getUri(),
						RdfConstants.rdfsLabelUri, null, false);
				if (stmts.hasNext()) {
					Statement stmt = stmts.next();
					label = stmt.getObject().stringValue();
				}
			} 
			catch (RepositoryException e) {
				Util.logException(e, getClass());
			}
		}
		return label;
	}
	
	String type;
	public String getType() {
		if (type == null) {
			RepositoryConnection repConn = Run.getRun().getRepository()
					.getRepositoryConn();
			RepositoryResult<Statement> stmts = null;
			try {
				stmts = repConn.getStatements(
						getUri(),
						RdfConstants.hasTypeUri, null, false);
				if (stmts.hasNext()) {
					Statement stmt = stmts.next();
					type = stmt.getObject().stringValue();
				}
			} 
			catch (RepositoryException e) {
				Util.logException(e, getClass());
			}
		}
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	URI entityUri;
	public URI getEntityUri() {
		if (entityUri == null) {
			RepositoryConnection repConn = Run.getRun().getRepository().getRepositoryConn();
			RepositoryResult<Statement> stmts = null;
			try {
				stmts = repConn.getStatements(null, RdfConstants.hasAttributeUri, getUri(), false);
				if (stmts.hasNext()) {
					Statement stmt = stmts.next();
					entityUri = (URI)stmt.getSubject();
				}
			} 
			catch (RepositoryException e) {
				Util.logException(e, getClass());
			}
		}
		return entityUri;		
	}
	
	public String getEntityLabel() {
		String entityLabel = null;
		RepositoryConnection repConn = Run.getRun().getRepository().getRepositoryConn();
		RepositoryResult<Statement> stmts = null;
		try {
			stmts = repConn.getStatements(getEntityUri(), RdfConstants.rdfsLabelUri, null, false);
			if (stmts.hasNext()) {
				Statement stmt = stmts.next();
				entityLabel = stmt.getObject().stringValue();
			}
		} 
		catch (RepositoryException e) {
			Util.logException(e, getClass());
		}
		return entityLabel;
	}
	
	public List<Statement> toRdf() {
		// TODO
		return new ArrayList<Statement>();
	}
	
	public String toString() {
		return(getEntityLabel() + "." + getLabel());
	}
	@Override
	public int compareTo(Attribute o) {
		return toString().compareTo(o.toString());
	}


}
