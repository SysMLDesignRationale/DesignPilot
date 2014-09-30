package gov.nasa.jsc.mdrules.ux;

import gov.nasa.jsc.mdrules.lessons.Attribute;
import gov.nasa.jsc.mdrules.lessons.Constraint;
import gov.nasa.jsc.mdrules.lessons.Replacement;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Dialog widget to specify replacement of attribute sets with other attribute sets.
 * Adapted from http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/ListDialogRunnerProject/src/components/ListDialog.java
 * @author sidneybailin
 *
 */
public class ReplacementDialog extends JFrame
                        implements ActionListener, MouseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ReplacementDialog dialog;
	public static ReplacementDialog getDialog() {
		return dialog;
	}
	
	static final String attrSeparator = " & ";
	public static String getAttrSeparator() {
		return attrSeparator;
	}
	
	static final String attrSeparatorRegex = " \\& ";
	public static String getAttrSeparatorRegex() {
		return attrSeparatorRegex;
	}
	
	Method callbackMethod;
	java.util.List<Object> callbackArgs;
	
	private JPanel leftPanel = new JPanel();
	private JPanel rightPanel = new JPanel();
	
//	private JPanel promptPanel = new JPanel();
	private JTextPane promptArea = new JTextPane();
	
	private JPanel replPanel = new JPanel();
	private JScrollPane replScroll = new JScrollPane(replPanel);
	
//	private JScrollPane selectionListScrollPane = new JScrollPane();
	private JPanel selectionListPanel = new JPanel();
	private JLabel selectListLabel = new JLabel(UxConstants.SELECT_ATTRS);
    private JList<Attribute> selectionList;
    private JPanel selectButtonPanel = new JPanel();
    private JButton selectButton = new JButton(UxConstants.ADD_ATTR);
    
    private JTextField selectedTextField;
    protected void setSelectedTextField(JTextField field) {
    	selectedTextField = field;
    }
    
    // this is the data entered by the user:
    private java.util.List<Replacement> replacements = new ArrayList<Replacement>();
    public java.util.List<Replacement> getReplacements() {
    	return replacements;
    }
    
    Dimension defaultScrollSize;  
    Constraint constraint;
    
    DefaultListModel<Attribute> validAttributeModel = new DefaultListModel<Attribute>();
    Set<Attribute> validAttributes;
    
    /**
     * Set up and show the dialog.  The first Component argument
     * determines which frame the dialog depends on; it should be
     * a component in the dialog's controlling frame. The second
     * Component argument should be null if you want the dialog
     * to come up with its left corner in the center of the screen;
     * otherwise, it should be the component on top of which the
     * dialog should appear.
     */
    public static void showDialog(Constraint constraint,
    								Component frameComp,
                                    Component locationComp,
                                    String prompt,
                                    String title,
                                    ReplacementPane[] data,
                                    Set<Attribute> choices,
                                    Method callbackMethod,
                                    java.util.List<Object> callbackArgs) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new ReplacementDialog(constraint,
        						frame,
                                locationComp,
                                prompt,
                                title,
                                data,
                                choices,
                                callbackMethod,
                                callbackArgs);
        dialog.setVisible(true);
        return;
    }
 
   public ReplacementDialog(Constraint constraint,
		   				Frame frame,
		   				Component locationComp,
		   				String prompt,
		   				String title,
		   				ReplacementPane[] data,
		   				Set<Attribute> attrs,
		   				Method callbackMethod,
		   				java.util.List<Object> callbackArgs) {
	   	this.constraint = constraint;
        this.setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog = this;
        setTitle(title);
        replScroll.setPreferredSize(defaultScrollSize);
        this.callbackMethod = callbackMethod;
        this.callbackArgs = callbackArgs;
        
        // Save list of valid attributes so we can check validity of
        // edited replacement fields
        this.validAttributes = attrs;
        for (Attribute attr : attrs) {
        	validAttributeModel.addElement(attr);
        }
        selectionList = new JList<Attribute>(validAttributeModel);
        selectionList.addMouseListener(this);
        
        BorderLayout selectionListPanelLayout = new BorderLayout();
        selectionListPanel.setLayout(selectionListPanelLayout);
        selectionListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        selectListLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        selectionList.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        selectionListPanel.add(selectListLabel, BorderLayout.NORTH);
        selectionListPanel.add(selectionList, BorderLayout.SOUTH);
//        selectionListScrollPane.add(selectionListPanel);
//        pack();
//        Dimension defaultSelectionListScrollSize = selectionListScrollPane.getSize();
//        selectionListScrollPane.setPreferredSize(defaultSelectionListScrollSize);
        
        selectButtonPanel.setLayout(new BoxLayout(selectButtonPanel, BoxLayout.LINE_AXIS));
        selectButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        selectButton.addActionListener(this);
        selectButtonPanel.add(selectButton);
               
        BorderLayout leftPanelLayout = new BorderLayout();
        leftPanel.setLayout(leftPanelLayout);
        leftPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        BorderLayout rightPanelLayout = new BorderLayout();
        rightPanel.setLayout(rightPanelLayout);
        rightPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        rightPanel.add(selectionListPanel, BorderLayout.NORTH);
        rightPanel.add(selectButtonPanel,BorderLayout.SOUTH);
        
        GridLayout replPanelLayout = new GridLayout(0,1);
        replPanel.setLayout(replPanelLayout);
        replPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // this is a hack in which we initialize with 3 replacements in order
        // to keep the ReplacementPane the right size, but we make the 2nd
        // and 3rd invisible until they are needed.
        int i=0;
        for (ReplacementPane r : data) {
        	replPanel.add(r);
        	String replaceThisStr = r.getReplaceThis();
        	if (i == 0) {
          		if ((replaceThisStr == null) || replaceThisStr.length() == 0) {   		
          			r.setReplaceThisSelected();
          		}
          		else {
          			r.setReplaceWithSelected();
          		}
        	}
        	else {
        		if ((replaceThisStr == null) || replaceThisStr.length() == 0) {
        			r.setVisible(false);
        		}
        	}
        	++i;
        }
 
        //Create and initialize the buttons.
        JButton cancelButton = new JButton(UxConstants.CANCEL);
        cancelButton.setActionCommand(UxConstants.CANCEL);
        cancelButton.addActionListener(this);
        
        final JButton okayButton = new JButton(UxConstants.OKAY);
        okayButton.setActionCommand(UxConstants.OKAY);
        okayButton.addActionListener(this);
        getRootPane().setDefaultButton(okayButton);
 
        final JButton addButton = new JButton(UxConstants.ADD_REPL);
        addButton.setActionCommand(UxConstants.ADD_REPL);
        addButton.addActionListener(this);
 
         //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        BorderLayout buttonLayout2 = new BorderLayout();
        BorderLayout buttonLayout3 = new BorderLayout();
        
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        JPanel addButtonPane = new JPanel();
        addButtonPane.setLayout(buttonLayout2);
        addButtonPane.add(addButton, BorderLayout.WEST);
        
        JPanel endButtonPane = new JPanel();
        endButtonPane.setLayout(buttonLayout3);
        endButtonPane.add(okayButton, BorderLayout.WEST);
        endButtonPane.add(cancelButton, BorderLayout.EAST);

        buttonPane.add(addButtonPane, BorderLayout.WEST);
        buttonPane.add(endButtonPane, BorderLayout.EAST);
 
        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        replScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        replScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        promptArea.setContentType("text/html");
        promptArea.setText(prompt);
        promptArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.add(promptArea, BorderLayout.NORTH);
        leftPanel.add(replScroll, BorderLayout.CENTER);
        leftPanel.add(buttonPane, BorderLayout.SOUTH);
        contentPane.add(leftPanel, BorderLayout.WEST);
        contentPane.add(rightPanel, BorderLayout.EAST);
 
        pack();
        defaultScrollSize = replScroll.getSize();
        replScroll.setPreferredSize(defaultScrollSize);
        setLocationRelativeTo(locationComp);
    }
 
    //Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
    	
    	String action = e.getActionCommand();
    	if (action.equals(UxConstants.ADD_REPL)) {
    		
            // This is a hack in which we initialize with 3 replacements in order
            // to keep the ReplacementPane the right size, but we make the 2nd
            // and 3rd invisible until they are needed.
    		//
    		// So if the 2nd component is invisible, just make it visible here.
    		// If the 2nd component is already visible, but the 3rd is not,
    		// make the 3rd visible here.
    		//
    		// Otherwise, add a component.
    		Component secondComponent = replPanel.getComponent(1);
    		Component thirdComponent = replPanel.getComponent(2);
    		if (!(secondComponent.isVisible())) {
    			secondComponent.setVisible(true);
    			((ReplacementPane)secondComponent).setReplaceThisSelected();
    		}
    		else if (!(thirdComponent.isVisible())) {
        		thirdComponent.setVisible(true);
    			((ReplacementPane)thirdComponent).setReplaceThisSelected();
    		}
    		else {
    			Set<Attribute> set1 = new HashSet<Attribute>();
    			Set<Attribute> set2 = new HashSet<Attribute>();
    			ReplacementPane newReplPane = new ReplacementPane(constraint, new Replacement(set1, set2), validAttributes);
    			replPanel.add(newReplPane);
    			newReplPane.setReplaceThisSelected();
    		}
     		pack();
    	}
    	if (action.equals(UxConstants.ADD_ATTR)) {
    		addAttr();
    	}
    	else if (action.equals(UxConstants.OKAY)) {
    		replacements.clear();
    		java.util.List<String> invalidAttributes = new ArrayList<String>();
    		for (Component comp : replPanel.getComponents()) {
    			if (!(comp instanceof ReplacementPane)) {
    				continue;
    			}
    			if (!(comp.isVisible())) {
    				break;
    			}
    			ReplacementPane repl = (ReplacementPane)comp;
    			Replacement replacement =  repl.getReplacement();
    			replacements.add(replacement);
    			JPanel replThisComp = (JPanel)repl.getComponent(0);
       		    String replThis = ((JTextField)replThisComp.getComponent(1)).getText();
       		    if ((replThis == null) || replThis.length() == 0) {
       		    	continue;
       		    }
//       		    replStr.setReplaceThis(replThis);
       		    
       		    JPanel replWithComp = (JPanel)repl.getComponent(1);
       		    String replWith = ((JTextField)replWithComp.getComponent(1)).getText();
       		    if ((replWith == null) || replWith.length() == 0) {
       		    	continue;
       		    }
//       		    replStr.setReplaceWith(replWith);
       		    
        		// Check that all replacements refer to valid attributes
       		    // TODO Get rid of hard-coded '+'
       		    
       		    String[] replThisStrs = replThis.split(attrSeparatorRegex);
				for (String r : replThisStrs) {
					Attribute foundAttr = null;
					for (Attribute attr : validAttributes) {
						if (r.equals(attr.toString())) {
							foundAttr = attr;
							break;
						}
					}
					if (foundAttr != null) {
						replacement.getReplaceThis().add(foundAttr);
					}
					else {
						invalidAttributes.add(r);
					}
				}
       		    String[] replWithStrs = replWith.split(attrSeparatorRegex);
				for (String r : replWithStrs) {
					Attribute foundAttr = null;
					for (Attribute attr : validAttributes) {
						if (r.equals(attr.toString())) {
							foundAttr = attr;
							break;
						}
					}
					if (foundAttr != null) {
						replacement.getReplaceWith().add(foundAttr);
					}
					else {
						invalidAttributes.add(r);
					}
				}
  		}
//   			System.out.println(replacements);
   		    if (invalidAttributes.size() == 0) {
    			dialog.setVisible(false);
    			try {
    				callbackMethod.invoke(Run.getRun().getUx(), callbackArgs);
    			} 
    			catch (Exception e1) {
    				Util.logException(e1, getClass());
    			} 
   		    }
   		    else {
   		    	// Inform user of invalid (misspelled) attributes
   		    	String prompt = UxConstants.INVALID_ATTRIBUTES_FOLLOWING + "\n\n";
   		    	for (String attr : invalidAttributes) {
   		    		prompt += " " + attr + "\n";
   		    	}
				JTextArea textArea = new JTextArea(prompt);
				textArea.setEditable(false);
				this.setState(JFrame.ICONIFIED);
				JOptionPane.showMessageDialog(
		                UserExperience.getDialogFrame(),
		                textArea,
		                UxConstants.INVALID_ATTRIBUTES,
		                JOptionPane.PLAIN_MESSAGE,
		                null);

   		    }
    	}
    	else if (action.equals(UxConstants.CANCEL)) {
    		// TODO
//            ReplacementDialog.dialog.setVisible(false);
    	}
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		int numClicks = e.getClickCount();
		if (numClicks == 2) {	
			Component comp = e.getComponent();
			if (comp == selectionList) {
	    		addAttr();
			}
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	void addAttr() {
		Attribute attr = selectionList.getSelectedValue();
		if (selectedTextField != null) {
			String text = selectedTextField.getText();
			if (text.length() > 0) {
				text += attrSeparator;
			}
			text+=attr;
			selectedTextField.setText(text);
		}
//		System.out.println("Selected attribute: " + attr);
	}


}
