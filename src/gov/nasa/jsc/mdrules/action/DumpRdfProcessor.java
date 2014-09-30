package gov.nasa.jsc.mdrules.action;

import gov.nasa.jsc.mdrules.repository.SesameRepository;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.core.Application;

public class DumpRdfProcessor extends MDAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DumpRdfProcessor(String id, String name) {
		super(id, name, null, null); 
		configureAction();
	}

	void configureAction() {

		DumpRdfConfigurator conf = new DumpRdfConfigurator(this);
		ActionsConfiguratorsManager acm = ActionsConfiguratorsManager.getInstance();
		acm.addMainMenuConfigurator(conf);
		
	}
	
	public void actionPerformed(ActionEvent e) {
				
		SesameRepository repo = Run.getRun().getRepository();
		StringBuilder sb = repo.dumpRepository();
		
		JFileChooser fc = new JFileChooser() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void approveSelection(){
				File f = getSelectedFile();
				if(f.exists() && getDialogType() == SAVE_DIALOG){
					int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
					switch(result){
	                	case JOptionPane.YES_OPTION:
	                		super.approveSelection();
	                		return;
	                	case JOptionPane.NO_OPTION:
	                		return;
	                	case JOptionPane.CLOSED_OPTION:
	                		return;
	                	case JOptionPane.CANCEL_OPTION:
	                		cancelSelection();
	                		return;
					}
				}
				super.approveSelection();
			}
		};
		int returnVal = fc.showSaveDialog(Application.getInstance().getMainFrame());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				FileWriter fw = new FileWriter(file);
				fw.write(sb.toString());
				fw.flush();
				fw.close();
			} 
			catch (Exception e1) {
				Util.logException(e1, getClass());
			}
		}
		 
	}
	


}
