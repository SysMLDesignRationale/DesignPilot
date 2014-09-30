package gov.nasa.jsc.mdrules.lessons;

import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import gov.nasa.jsc.mdrules.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

public class Replacement extends LessonComponent {

	Set<Attribute> replaceThis; // set of attribute IDs
	public Set<Attribute> getReplaceThis() {
		return replaceThis;
	}

	Set<Attribute> replaceWith; // set of attribute IDs
	public Set<Attribute> getReplaceWith() {
		return replaceWith;
	}

	public Replacement(Set<Attribute> replaceThis, Set<Attribute> replaceWith) {
		this.replaceThis = replaceThis;
		this.replaceWith = replaceWith;
	}
	
	@Override
	public List<Statement> toRdf() {
		List<Statement> stmts = new ArrayList<Statement>();

		// :Repl a :Replacement
//		String replStr = Util.genUri("Replacement");
//		URI replUri = new URIImpl(replStr);
//		setUri(replUri);
		Statement stmtA = new StatementImpl(getUri(), RdfConstants.isa, RdfConstants.replacementClassUri);
		stmts.add(stmtA);
		
		// :AttrSet1 a :AttributeSet
		String as1Str = Util.genUri("AttributeSet");
		URI as1Uri = new URIImpl(as1Str);
		Statement stmtB = new StatementImpl(as1Uri, RdfConstants.isa, RdfConstants.attrSetClassUri);
		stmts.add(stmtB);
		
		// :AttrSet1 :hasMember :Attr (for each Attr in replaceThis)
		for (Attribute attr : replaceThis) {
			 Statement stmtC = new StatementImpl(as1Uri, RdfConstants.hasMemberUri, attr.getUri());
			 stmts.add(stmtC);
		}
		
		// :AttrSet2 a :AttributeSet
		String as2Str = Util.genUri("AttributeSet");
		URI as2Uri = new URIImpl(as2Str);
		Statement stmtD = new StatementImpl(as2Uri, RdfConstants.isa, RdfConstants.attrSetClassUri);
		stmts.add(stmtD);
		
		// :AttrSet2 :hasMember :Attr (for each Attr in replaceWith)
		for (Attribute attr : replaceWith) {
			 Statement stmtE = new StatementImpl(as2Uri, RdfConstants.hasMemberUri, attr.getUri());
			 stmts.add(stmtE);
		}
				
		return stmts;
	}
	
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("Replace this:\n");
		for (Attribute attr : replaceThis) {
			ret.append("\t" + attr.getEntityLabel() + "." + attr.getLabel());
		}
		ret.append("\nReplace with:\n");
		for (Attribute attr : replaceWith) {
			ret.append("\t" + attr.getEntityLabel() + "." + attr.getLabel());
		}
		ret.append("\n");
		return ret.toString();
	}


}
