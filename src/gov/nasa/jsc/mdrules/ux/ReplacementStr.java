package gov.nasa.jsc.mdrules.ux;

public class ReplacementStr {

	String replaceThis;	
	public String getReplaceThis() {
		return replaceThis;
	}
	public void setReplaceThis(String replaceThis) {
		this.replaceThis = replaceThis;
	}

	String replaceWith;
	public String getReplaceWith() {
		return replaceWith;
	}
	public void setReplaceWith(String replaceWith) {
		this.replaceWith = replaceWith;
	}
	
	public String toString() {
		return "\nReplace this: " + replaceThis + "\n\twith: " + replaceWith;
	}
}
