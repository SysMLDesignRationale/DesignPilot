package gov.nasa.jsc.mdrules.ux;

import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;

/**
 * Dialog widget to specify replacement of attribute sets with other attribute sets.
 * Adapted from http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/ListDialogRunnerProject/src/components/ListDialog.java
 * @author sidneybailin
 *
 */
public class ConfirmSaveDialog extends JFrame
                        implements ActionListener {
	
	Method actionCallbackMethod;
	java.util.List<Object> actionCallbackArgs;
	
	Method clickSelectedTextCallbackMethod;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ConfirmSaveDialog dialog;
	public static ConfirmSaveDialog getDialog() {
		return dialog;
	}
	
	private JPanel confirmPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private JCheckBox checkBox = new JCheckBox();
		
    /**
     * Set up and show the dialog.  The first Component argument
     * determines which frame the dialog depends on; it should be
     * a component in the dialog's controlling frame. The second
     * Component argument should be null if you want the dialog
     * to come up with its left corner in the center of the screen;
     * otherwise, it should be the component on top of which the
     * dialog should appear.
     */
    public static void showDialog(Component frameComp,
                                     String messageText1,
                                     String messageText2,
                                     Color textColor,
                                    String title,
                                    String confirmLabel,
                                    Method actionCallbackMethod,
                                    java.util.List<Object> actionCallbackArgs) {
    	
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new ConfirmSaveDialog(frame,
                                messageText1,
                                messageText2,
                                textColor,
                                title,
                                confirmLabel,
                                actionCallbackMethod,
                                actionCallbackArgs
        		);
        dialog.setVisible(true);
        return;
    }
 
   public ConfirmSaveDialog(Frame frame,
           			   String messageText1,
           			   String messageText2,
                       Color textColor,
                       String title,
                       String confirmLabel,
                       Method actionCallbackMethod,
                       java.util.List<Object> actionCallbackArgs) {
	   
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setAlwaysOnTop(true);
        setResizable(true);
        dialog = this;
        setTitle(title);
        this.actionCallbackMethod = actionCallbackMethod;
        this.actionCallbackArgs = actionCallbackArgs;
        
        Container contentPane = getContentPane();
        LayoutManager contentLayout = new BorderLayout();
        contentPane.setLayout(contentLayout);
        JPanel messagesPane = new JPanel(new GridLayout(1,2));
                
        // Create the first message area         
        JTextPane messageArea1 = new JTextPane();
        messageArea1.setContentType("text/html");
        messageArea1.setText(messageText1);
        messageArea1.setEditable(false);
        messageArea1.setCaretPosition(0);
        messageArea1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));		
    	messagesPane.add(messageArea1);
         
        // Create the second message area         
        JTextPane messageArea2 = new JTextPane();
        messageArea2.setContentType("text/html");
        messageArea2.setText(messageText2);
        messageArea2.setEditable(false);
        messageArea2.setCaretPosition(0);
        messageArea2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));		
    	messagesPane.add(messageArea2);
    	
    	contentPane.add(messagesPane, BorderLayout.NORTH);
          
        // Create the confirm widget
		JLabel checkLabel = new JLabel(confirmLabel);
        checkLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));		
    	FlowLayout confirmPanelLayout = new FlowLayout();
    	confirmPanel.setLayout(confirmPanelLayout);
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        confirmPanel.add(checkBox);
        confirmPanel.add(checkLabel);
         
        //Create and initialize the buttons.
        FlowLayout buttonPanelLayout = new FlowLayout();
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        final JButton okayButton = new JButton(UxConstants.OKAY);
        okayButton.setActionCommand(UxConstants.OKAY);
        okayButton.addActionListener(this);
        getRootPane().setDefaultButton(okayButton);
 
        buttonPanel.add(okayButton);
 
        contentPane.add(confirmPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
 
        pack();
        
		// position it in the center of screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) ((rect.getMaxX() - rect.getMinX())/2) - (getWidth()/2);
        int y = (int) ((rect.getMaxY() - rect.getMinY())/2) - (getHeight()/2);
        setLocation(x, y);
        setResizable(true);

    }
 
    //Handle clicks 
	public void actionPerformed(ActionEvent e) {
    	
    	String action = e.getActionCommand();
    	if (action.equals(UxConstants.OKAY)) {
   			dialog.setVisible(false);
			actionCallbackArgs.add(new Boolean(checkBox.isSelected()));
			try {
				actionCallbackMethod.invoke(Run.getRun().getUx(), actionCallbackArgs);
			} 
			catch (Exception e1) {
				Util.logException(e1, getClass());
			} 
   		}
    	else if (action.equals(UxConstants.CANCEL)) {
    		// TODO
//          dialog.setVisible(false);
    	}
    }

    boolean firstClick = true;
    String selectedText;


}
