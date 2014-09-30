package gov.nasa.jsc.mdrules.qudt;

import gov.nasa.jsc.mdrules.lessons.Attribute;
import gov.nasa.jsc.mdrules.rdf.RdfConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;

public class Formula extends QudtComponent {

	List<Quantity> operands = new ArrayList<Quantity>();
	public List<Quantity> getOperands() {
		return operands;
	}
	
	Quantity result;
	public Quantity getResult() {
		return result;
	}
	public void setResult(Quantity result) {
		this.result = result;
	}
	
	String operator;
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getOperandNames() {
		List<String> ret = new ArrayList<String>();
		for (Quantity operand : operands) {
			ret.add(operand.getName());
		}
		return ret;
	}
	
	/**
	 * Returns true iff the types of the input attributes can be assigned 1-1 to
	 * the formula's operands, and vice versa
	 * @return
	 */
	public boolean worksWith(Set<Attribute> attrs) {
		
		// check that every attr corresponds to an operand
		List<String> operandTypes = new ArrayList<String>();
		for (Quantity operand : operands) {
			operandTypes.add(operand.getType());
		}
		for (Attribute attr : attrs) {
			String attrType = attr.getType();
			if (!(operandTypes.contains(attrType))) {
				return false;
			}
			operandTypes.remove(attrType);
		}
		
		// check that every operand corresponds to an attr
		List<String> attrTypes = new ArrayList<String>();
		for (Attribute attr : attrs) {
			attrTypes.add(attr.getType());
		}
		for (Quantity operand : operands) {
			String operandType = operand.getType();
			if (!(attrTypes.contains(operandType))) {
				return false;
			}
			attrTypes.remove(operandType);
		}
				
		return true;
	}

	
	public List<Statement> toRdf() {
		List<Statement> stmts = new ArrayList<Statement>();
		
		Statement stmt1 = new StatementImpl(getUri(), RdfConstants.isa, RdfConstants.formulaClassUri);
		stmts.add(stmt1);
		
		Literal nameLit = new LiteralImpl(name);
		Statement stmt5 = new StatementImpl(getUri(), RdfConstants.rdfsLabelUri, nameLit);
		stmts.add(stmt5);
		
		Literal operatorLit = new LiteralImpl(operator);
		Statement stmt2 = new StatementImpl(getUri(), RdfConstants.hasOperatorUri, operatorLit);
		stmts.add(stmt2);
		
		// TODO generalize this to an arbitrary number of operands.
		// The issue is the predicate definitions in RdfConstants.
		Quantity operand1 = operands.get(0);
		stmts.addAll(operand1.toRdf());
		Statement stmt3a = new StatementImpl(getUri(), RdfConstants.hasOperand1Uri, operand1.getUri());
		stmts.add(stmt3a);
		
		Quantity operand2 = operands.get(1);
		stmts.addAll(operand2.toRdf());
		Statement stmt3b = new StatementImpl(getUri(), RdfConstants.hasOperand2Uri, operand2.getUri());
		stmts.add(stmt3b);
		
		stmts.addAll(result.toRdf());
		Statement stmt4 = new StatementImpl(getUri(), RdfConstants.hasResultUri, result.getUri());
		stmts.add(stmt4);
		
		// debug
//		for (Statement stmt : stmts) System.out.println(stmt + "\n");
		
		return stmts;
	}
	

	
}
