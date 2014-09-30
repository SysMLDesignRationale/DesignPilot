package gov.nasa.jsc.mdrules.action;

import java.util.List;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.actions.MDAction;

public class RunRulesConfigurator implements AMConfigurator {
	
	MDAction action;
	
	public RunRulesConfigurator(MDAction action) {
		this.action = action;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void configure(ActionsManager mngr) {
		// searching for action after which insert should be done.
//		NMAction found = mngr.getActionFor(ActionsID.NEW_PROJECT);
		NMAction found = mngr.getActionFor(ActionsID.VALIDATION);
		// action found, inserting
		if (found != null) {
			// find category of "New Project" action.
			ActionsCategory category = (ActionsCategory) mngr
					.getActionParent(found);
			// get all actions from this category (menu).
			List actionsInCategory = category.getActions();
			// add action after "New Project" action.
			int indexOfFound = actionsInCategory.indexOf(found);
			actionsInCategory.add(indexOfFound + 1, action);
			// set all actions.
			category.setActions(actionsInCategory);
		}
	}

	public int getPriority() {
		return AMConfigurator.MEDIUM_PRIORITY;
	}

}
