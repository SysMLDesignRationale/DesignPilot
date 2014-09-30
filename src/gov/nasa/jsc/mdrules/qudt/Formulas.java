package gov.nasa.jsc.mdrules.qudt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Statement;

public class Formulas {

	
	List<Formula> formulas = new ArrayList<Formula>();
	public List<Formula> getFormulas() {
		return formulas;
	}
	
	public Formulas() {
		
		Quantity voltage = new Quantity("Voltage", "V");
		Quantity current = new Quantity("Current", "A");
		Quantity resistance = new Quantity("Resistance", "½");
		Quantity power = new Quantity("Power", "W");
		
		Formula formOhm = new Formula();
		formOhm.setName("Ohm's Law");
		formOhm.getOperands().add(voltage);
		formOhm.getOperands().add(resistance);
		formOhm.setResult(current);
		formOhm.setOperator("/");
		formulas.add(formOhm);
		
		Formula formJoule = new Formula();
		formJoule.setName("Joule's Law");
		formJoule.getOperands().add(voltage);
		formJoule.getOperands().add(current);
		formJoule.setResult(power);
		formJoule.setOperator("*");
		formulas.add(formJoule);
		
	}
	
	public List<Statement> toRdf() {
		List<Statement> stmts = new ArrayList<Statement>();
		for (Formula form : formulas) {
			stmts.addAll(form.toRdf());
		}
		return stmts;
	}
	
	public Set<String> getKnownTypes() {
		// Create a list of acceptable attribute types: those used in some formula
		Set<String> knownTypes = new HashSet<String>();
		for (Formula formula : formulas) {
			Quantity result = formula.getResult();
			knownTypes.add(result.getType());
			List<Quantity> operands = formula.getOperands();
			for (Quantity operand : operands) {
				knownTypes.add(operand.getType());
			}
		}
		return knownTypes;
	}
}
