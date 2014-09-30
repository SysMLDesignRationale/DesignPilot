package gov.nasa.jsc.mdrules.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;

import gov.nasa.jsc.mdrules.action.DumpRdfProcessor;
import gov.nasa.jsc.mdrules.action.RunRuleEngine;
import gov.nasa.jsc.mdrules.qudt.Formulas;
import gov.nasa.jsc.mdrules.rdf.NamespacePrefixes;
import gov.nasa.jsc.mdrules.repository.SesameRepository;
import gov.nasa.jsc.mdrules.rules.AssertionBasedExecution;
import gov.nasa.jsc.mdrules.util.Util;
import gov.nasa.jsc.mdrules.ux.UserExperience;
import gov.nasa.jsc.mdrules.defs.Msgs;
import gov.nasa.jsc.mdrules.defs.RunPropertyDefinitions;

import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;

public class Run extends Plugin {

	static Run runInstance;
	static public Run getRun() {
		return runInstance;
	}
	
	UserExperience ux;
	public UserExperience getUx() {
		return ux;
	}
	public void setUx(UserExperience ux) {
		this.ux = ux;
	}
	
	Formulas formulas;
	public Formulas getFormulas() {
		return formulas;
	}
	
	
	RunRuleEngine gen;
	public RunRuleEngine getGen() {
		return gen;
	}
	
	DumpRdfProcessor DumpRdf;
	public DumpRdfProcessor getDumpRdf() {
		return DumpRdf;
	}
	
	NamespacePrefixes nsPrefixes;
	public NamespacePrefixes getNsPrefixes() {
		return nsPrefixes;
	}
	
	Model root;
	public Model getRoot() {
		return root;
	}
	public void setRoot(Model root) {
		this.root = root;
	}
	
	AssertionBasedExecution ruleEngine;
	public AssertionBasedExecution getRuleEngine() {
		return ruleEngine;
	}

	Map<String, String> paramNameToValue = new HashMap<String, String>();
	public String getParamValue(String paramName) {
		return paramNameToValue.get(paramName);
	}

	public void setParamValue(String paramName, String value) {
		paramNameToValue.put(paramName, value);
	}

	SesameRepository repository;
	public SesameRepository getRepository() {
		return repository;
	}


	
	public Run() {
		super();
		runInstance = this;
		
	}
	
	@Override
	public boolean close() {
		// this takes too long: (why?)
//		repository.shutdown();
		return true;
	}

	@Override
	public void init() {
		File pluginDir = getDescriptor().getPluginDirectory();
		System.setProperty(RunPropertyDefinitions.PROPERTIES_FILE, pluginDir.getAbsolutePath() + "/orionPowerDistribDemo.properties");
		startup();		
		
		// Create MagiDraw menu items
		DumpRdf = new DumpRdfProcessor("", "Print RDF");
		gen = new RunRuleEngine("", "Run Rules");

	}

	@Override
	public boolean isSupported() {		// TODO Auto-generated method stub
		return true;
	}
	
	/**
	 * Processes startup parameters, creates sources and destinations, and
	 * initializes pollers.
	 * 
	 * Startup parameters will have been placed in a set by contextInitialized,
	 * when the system is run as a webapp. If an expected parameter is not there
	 * (e.g., if the system is running stand-alone, for testing purposes), we
	 * try to get it as a runtime property.
	 */
	public void startup() {

		// startup parameter: properties file
		String propertiesFileName = paramNameToValue.get(RunPropertyDefinitions.PROPERTIES_FILE);
		if (propertiesFileName == null) {
			propertiesFileName = System.getProperty(RunPropertyDefinitions.PROPERTIES_FILE);
		}
		if (propertiesFileName != null) {
			try {
				InputStream propStream = new FileInputStream(propertiesFileName);
				System.getProperties().load(propStream);
			} 
			catch (Exception e) {
				Util.logException(e, this.getClass());
			}
		}

		// Record all properties to our own table so other parts of the system
		// can access them. If running as a web app and parameters were
		// specified in web.xml, they will be overridden by any values
		// in the properties file.
		for (Map.Entry<Object, Object> entry : System.getProperties()
				.entrySet()) {
			String propertyName = (String) entry.getKey();
			String propertyValue = (String) entry.getValue();
			paramNameToValue.put(propertyName, propertyValue);
		}
		
		// namespace / prefix mapping
		nsPrefixes = new NamespacePrefixes();

		// startup parameter: repository path
		String repositoryPath = runInstance.getParamValue(RunPropertyDefinitions.SESAME_REPOSITORY_PATH);
		
		// create repository
		repository = new SesameRepository(repositoryPath);
//		repository.clear();
		
		// assertion-based execution engine
		ruleEngine = new AssertionBasedExecution();
//		ruleEngine.start();

		// Create placeholder for unit/dimension/physics computation information
		formulas = new Formulas();

		// Create user interface
		setUx(new UserExperience());

		Util.log(getClass(), Level.INFO, Msgs.OPD_DEMO_STARTUP_COMPLETE);
	}



}
