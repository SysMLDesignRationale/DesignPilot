package gov.nasa.jsc.mdrules.action;

import gov.nasa.jsc.mdrules.rdf.RdfGen;
import gov.nasa.jsc.mdrules.run.Run;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.MainFrame;

public class RunRuleEngine extends MDAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RunRuleEngine(String id, String name) {
		super(id, name, null, null); 
		configureAction();
	}

	void configureAction() {

		RunRulesConfigurator conf = new RunRulesConfigurator(this);
		ActionsConfiguratorsManager acm = ActionsConfiguratorsManager.getInstance();
		acm.addMainMenuConfigurator(conf);
		
	}
	
	static boolean alreadyExecuting = false;
	static public void setAlreadyExecuting(boolean a) {
		alreadyExecuting = a;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		MainFrame mainFrame = Application.getInstance().getMainFrame();
		Run runInstance = Run.getRun();
		
		synchronized(runInstance) {
			if (alreadyExecuting) {
				return;
			}
			alreadyExecuting = true;
		}
				
		setCursor(mainFrame, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      
		// generate RDF from the open model and write it to the repository
		RdfGen rdfGen = new RdfGen();
		rdfGen.genRdf();
			
		runInstance.getRuleEngine().getRules().sortRules(); // this reinstates any rules removed during a previous run!
		runInstance.getRuleEngine().executeRulesUntilNoMore();
//		System.out.println("Done");
	}
	
	/**
	 * Recursively sets a window component's cursor and that of all its children
	 * @param component
	 * @param cursor
	 */
	static public void setCursor(Component component, Cursor cursor) {
		component.setCursor(cursor);
		if (component instanceof Container) {
			for (Component c : ((Container)component).getComponents()) {
				setCursor(c, cursor);
			}
		}
	}


}
