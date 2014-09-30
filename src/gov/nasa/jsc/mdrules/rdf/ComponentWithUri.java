package gov.nasa.jsc.mdrules.rdf;

import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import gov.nasa.jsc.mdrules.util.Util;

public abstract class ComponentWithUri {

	public abstract List<Statement> toRdf();
	
	URI uri;
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
		Util.putComponentWithUri(uri, this);
	}
	
	public ComponentWithUri() {
		setUri(new URIImpl(Util.genUri(getClass().getSimpleName())));
	}
	
}
