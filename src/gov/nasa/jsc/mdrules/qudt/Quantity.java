package gov.nasa.jsc.mdrules.qudt;

import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;

public class Quantity extends QudtComponent{

	String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Quantity(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public List<Statement> toRdf() {
		List<Statement> stmts = new ArrayList<Statement>();
		
		Statement stmt1 = new StatementImpl(getUri(), RdfConstants.isa, RdfConstants.quantityClassUri);
		stmts.add(stmt1);
		
		Literal typeLit = new LiteralImpl(type);
		Statement stmt2 = new StatementImpl(getUri(), RdfConstants.hasTypeUri, typeLit);
		stmts.add(stmt2);
		
		Literal nameLit = new LiteralImpl(name);
		Statement stmt3 = new StatementImpl(getUri(), RdfConstants.rdfsLabelUri, nameLit);
		stmts.add(stmt3);
		
		return stmts;
	}
}
