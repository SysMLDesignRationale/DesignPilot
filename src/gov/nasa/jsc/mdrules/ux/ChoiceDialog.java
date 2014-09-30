package gov.nasa.jsc.mdrules.ux;

import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;

/**
 * Dialog widget to specify replacement of attribute sets with other attribute sets.
 * Adapted from http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/ListDialogRunnerProject/src/components/ListDialog.java
 * @author sidneybailin
 *
 */
public class ChoiceDialog extends JFrame
                        implements ActionListener, MouseListener, HyperlinkListener {
	
	Method actionCallbackMethod;
	java.util.List<Object> actionCallbackArgs;
	
	Method clickSelectedTextCallbackMethod;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ChoiceDialog dialog;
	public static ChoiceDialog getDialog() {
		return dialog;
	}
	
	JComboBox<String> choiceWidget;
	
	private JPanel messagePanel = new JPanel();
	private JPanel choicePanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
		
    private String selectedChoice;
    protected String getSelectedChoice() {
    	return selectedChoice;
    }
    
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
                                     String messageText,
                                     Color textColor,
                                    String title,
                                    String[] choices,
                                    Method actionCallbackMethod,
                                    java.util.List<Object> actionCallbackArgs,
                                    Method clickSelectedTextCallbackMethod) {
    	
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new ChoiceDialog(frame,
                                messageText,
                                textColor,
                                title,
                                choices,
                                actionCallbackMethod,
                                actionCallbackArgs,
                                clickSelectedTextCallbackMethod
        		);
        dialog.setVisible(true);
        return;
    }
 
   public ChoiceDialog(Frame frame,
                       String messageText,
                       Color textColor,
                       String title,
                       String[] choices,
                       Method actionCallbackMethod,
                       java.util.List<Object> actionCallbackArgs,
                       Method clickSelectedTextCallbackMethod) {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setAlwaysOnTop(true);
        setResizable(true);
        dialog = this;
        setTitle(title);
        this.actionCallbackMethod = actionCallbackMethod;
        this.actionCallbackArgs = actionCallbackArgs;
        this.clickSelectedTextCallbackMethod = clickSelectedTextCallbackMethod;
        selectedChoice = choices[0];//        setModal(false); // need to be able to manipulate the log window, etc.
               
        Container contentPane = getContentPane();
        LayoutManager contentLayout = new BorderLayout();
        contentPane.setLayout(contentLayout);
        
        GridLayout messagePanelLayout = new GridLayout(0,1);
        messagePanel.setLayout(messagePanelLayout);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextPane messageArea = new JTextPane();
        messageArea.setContentType("text/html");
        messageArea.addMouseListener(this);
        messageArea.addHyperlinkListener(this);
        messageArea.setText(messageText);
        messageArea.setEditable(false);
        messageArea.setForeground(textColor);
        messageArea.setCaretPosition(0);
        messagePanel.add(messageArea);
        messageArea.setMaximumSize(messageArea.getSize());
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane messageScroll = new JScrollPane(messagePanel);
    	messageScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	messageScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	contentPane.add(messageScroll, BorderLayout.NORTH);
    	pack();
    	messageScroll.setPreferredSize(messageScroll.getSize());
          
        // Create the choice widget
//        GridLayout choicePanelLayout = new GridLayout(0,1);
    	FlowLayout choicePanelLayout = new FlowLayout();
        JLabel choiceLabel = new JLabel("Select one of the following:");
        choicePanel.setLayout(choicePanelLayout);
        choicePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        choiceWidget = new JComboBox<String>(choices);
        choiceWidget.setSelectedIndex(0);
        choiceWidget.addActionListener(this);
        choicePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        choicePanel.add(choiceLabel);
        choicePanel.add(choiceWidget);
         
        //Create and initialize the buttons.
        FlowLayout buttonPanelLayout = new FlowLayout();
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        JButton cancelButton = new JButton(UxConstants.CANCEL);
        cancelButton.setActionCommand(UxConstants.CANCEL);
        cancelButton.addActionListener(this);
        
        final JButton okayButton = new JButton(UxConstants.OKAY);
        okayButton.setActionCommand(UxConstants.OKAY);
        okayButton.addActionListener(this);
        getRootPane().setDefaultButton(okayButton);
 
        buttonPanel.add(okayButton);
        buttonPanel.add(cancelButton);
 
        contentPane.add(choicePanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
 
        pack();
//        setLocationRelativeTo(locationComp);
        
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
    @SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
    	
    	String action = e.getActionCommand();
    	if (action.equals(UxConstants.OKAY)) {
   			dialog.setVisible(false);
			actionCallbackArgs.add(selectedChoice);
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
    	else {
    		Object source = e.getSource();
    		if (source == choiceWidget) {
    			selectedChoice = (String)(((JComboBox<String>)source).getSelectedItem());
    		}
    	}
    }

//    boolean firstClick = true;
    String selectedText;
    int selectionStart;
    int selectionEnd;
	@Override
	public void mouseClicked(MouseEvent e) {
				
//		int numClicks = e.getClickCount();
//		if (numClicks == 2) {	
//			// double clicking is changing the selection, so let's accept single click
//			Component comp = e.getComponent();
//			if (comp instanceof JTextPane) {
//				if (selectedText != null) {
//					try {
//						clickSelectedTextCallbackMethod.invoke(Run.getRun()
//								.getUx(), selectedText);
//					} 
//					catch (Exception e1) {
//						Util.logException(e1, getClass());
//					}
//				}
//			}
//		}
//		if (numClicks == 1) {	
//			// a single click changes the selection, so set it back to what it should be
//			Component comp = e.getComponent();
//			if (comp instanceof JTextPane) {
//				JTextPane textArea = (JTextPane)comp;
//				if (selectedText != null) {
//					textArea.setSelectionStart(selectionStart);
//					textArea.setSelectionEnd(selectionEnd);
					
					
					
					
					
					
//					try {
//						clickSelectedTextCallbackMethod.invoke(Run.getRun()
//								.getUx(), selectedText);
//					} 
//					catch (Exception e1) {
//						Util.logException(e1, getClass());
//					}
//				}
				
//				System.out.println("Selected text: " + textArea.getSelectedText());
//				
//				if (firstClick) {
//					String text = removeTags(textArea.getText());
//					int pos = textArea.getCaretPosition();
//
//					// find the spaces before and after the click position
//					int startPos = pos;
//					while ((startPos >= 0) && (!separatorChar(text.charAt(startPos)))) {
//						--startPos;
//					}
//					startPos++;
//					int endPos = pos;
//					while ((endPos < text.length())
//							&& (!separatorChar(text.charAt(endPos)))) {
//						++endPos;
//					}
//					selectedText = text.substring(startPos, endPos)
//							.trim();
//					textArea.setSelectionStart(startPos);
//					textArea.setSelectionEnd(endPos);
//					firstClick = false;
//				}
//				else {
//					firstClick = true;
//					try {
//						clickSelectedTextCallbackMethod.invoke(Run.getRun()
//								.getUx(), selectedText);
//					} 
//					catch (Exception e1) {
//						Util.logException(e1, getClass());
//					}
//				}
//
//			}
//			
//		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Component comp = e.getComponent();
		if (comp instanceof JTextPane) {
			JTextPane textArea = (JTextPane)comp;
			String selection = textArea.getSelectedText();
			if (selection != null) {
				selectedText = selection;
				
				// We need the following to reset the selection after a single click
				selectionStart = textArea.getSelectionStart();
				selectionEnd = textArea.getSelectionEnd();
								
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	static protected void minimize() {
		dialog.setState(JFrame.ICONIFIED);
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			javax.swing.text.Element elem = e.getSourceElement();
			int start = elem.getStartOffset();
			int end = elem.getEndOffset();
			JTextPane textPane = (JTextPane)(e.getSource());
			try {
				String linkText = textPane.getText(start, end-start);
				clickSelectedTextCallbackMethod.invoke(Run.getRun().getUx(), linkText);
			} 
			catch (Exception e1) {
				Util.logException(e1, getClass());
			}
			
		}
		
	}
	
//	boolean separatorChar(char c) {
//		
//		if (c == ' ') {
//			return true;
//		}
//		if (c == '<') {
//			return true;
//		}
//		if (c == '>') {
//			return true;
//		}
//		if (c == '\'') {
//			return true;
//		}
//		if (c == '\"') {
//			return true;
//		}
//		if (c == '.') {
//			return true;
//		}
//		if (c == ',') {
//			return true;
//		}
//		if (c == ';') {
//			return true;
//		}
//		if (c == '\n') {
//			return true;
//		}
//		if (c == '\r') {
//			return true;
//		}
//		if (c == '\t') {
//			return true;
//		}
//		
//		return false;
//	}
//	
//	String removeTags(String in) {
//		String out = "";
//		boolean inTag = false;
//		boolean inQuote = false;
//		for (int i=0; i<in.length(); ++i)	{
//			char c = in.charAt(i);
//			if (c == '\n') {
//				continue;
//			}
//			if (!inTag) {
//				if ((c == '\"') || (c == '\'')) {
//					inQuote = !inQuote;
//				}
//			}
//			if (inQuote) {
//				inTag = false;
//				out += c;
//				continue;
//			}
//			
//			// not in a quote
//			if (!inTag) {
//				if (c == '<') {
//					inTag = true;
//					continue;
//				}
//			}
//			if (inTag) {
//				if (c == '>') {
//					inTag = false;
//				}
//				continue;
//			}
//			out += c;
//		}
//		
//		// Remove leading spaces
//		out = out.trim();
//		
//		// Remove double spaces
//		while (out.contains("  ")) {
//			out = out.replace("  ", " ");
//		}
//		
//		
//		return out;
//	}
	

}
