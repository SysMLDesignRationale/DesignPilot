package gov.nasa.jsc.mdrules.rules;

public class Rule implements Comparable<Rule> {

	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	String sparql;
	public String getSparql() {
		return sparql;
	}
	public void setSparql(String sparql) {
		this.sparql = sparql;
	}
	
	boolean removeAfterExecution;
	public boolean getRemoveAfterExecution() {
		return removeAfterExecution;
	}
	public void setRemoveAfterExecution(boolean removeAfterExecution) {
		this.removeAfterExecution = removeAfterExecution;
	}

	public Rule(String name, String sparql, int priority) {
		this.name = name;
		this.sparql = sparql;
		this.priority = priority;
		removeAfterExecution = false;
	}
	
	public Rule(String name, String sparql, int priority, boolean removeAfterExecution) {
		this.name = name;
		this.sparql = sparql;
		this.priority = priority;
		this.removeAfterExecution = removeAfterExecution;
	}
	
	int priority;
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
		
	@Override
	public int compareTo(Rule r) {
		return new String("" + priority + name).compareTo("" + r.getPriority() + r.getName());
	}

}
