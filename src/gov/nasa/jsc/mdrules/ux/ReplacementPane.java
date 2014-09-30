package gov.nasa.jsc.mdrules.ux;

import gov.nasa.jsc.mdrules.lessons.Attribute;
import gov.nasa.jsc.mdrules.lessons.Constraint;
import gov.nasa.jsc.mdrules.lessons.Replacement;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Reusable widget to specify a replacement of one attribute set with another
 * @author sidneybailin
 *
 */
public class ReplacementPane extends JPanel implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	
	Constraint constraint;
	Replacement replacement;
	protected Replacement getReplacement() {
		return replacement;
	}
	Set<Attribute> knownAttributes;

	JTextField replaceThisText = new MyJTextField();
	JTextField replaceWithText = new MyJTextField();
	GridLayout layout1 = new GridLayout(2, 1);
	GridLayout layout2 = new GridLayout(2, 1);
	GridLayout layout3 = new GridLayout(1, 2);
	JPanel replaceThisPane = new JPanel();
	JPanel replaceWithPane = new JPanel();
	
	public ReplacementPane(Constraint constr, Replacement repl, Set<Attribute> knownAttributes) {

		constraint = constr;
		replacement = repl;
		this.knownAttributes = knownAttributes;

		JLabel replaceThisLabel = new JLabel(String.format(UxConstants.REPLACE_THIS, constr.getAttributeId(), constr.getEntityId()));
		JLabel replaceWithLabel = new JLabel(UxConstants.REPLACE_WITH);

		replaceThisPane.setLayout(layout1);
		replaceThisPane.add(replaceThisLabel);
//		replaceThisText.setColumns(40);
		replaceThisText.addMouseListener(this);
		replaceThisPane.add(replaceThisText);
        replaceThisPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        replaceThisPane.addMouseListener(this);

		replaceWithPane.setLayout(layout2);
		replaceWithPane.add(replaceWithLabel);
//		replaceWithText.setColumns(40);
		replaceWithText.addMouseListener(this);
		replaceWithPane.add(replaceWithText);		
        replaceWithPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        replaceWithPane.addMouseListener(this);

		setLayout(layout3);
		add(replaceThisPane);
		add(replaceWithPane);
			
	}
	
	/**
	 * Sets the "replace this" field as the currently selected field
	 */
	protected void setReplaceThisSelected() {
		ReplacementDialog.getDialog().setSelectedTextField(replaceThisText);
	}
	
	/**
	 * Sets the "replace this" field as the currently selected field
	 */
	protected void setReplaceWithSelected() {
		ReplacementDialog.getDialog().setSelectedTextField(replaceWithText);
	}
	
	/**
	 * Gets the "replace this" field value
	 */
	protected String getReplaceThis() {
		return replaceThisText.getText();
	}
	
	/**
	 * Sets the "replace this" field value
	 */
	protected void setReplaceThis(String replaceThisStr) {
		replaceThisText.setText(replaceThisStr);
	}
	
	/**
	 * Makes the "replace this" field read only
	 */
	protected void setReplaceThisReadOnly() {
		replaceThisText.setEditable(false);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Component comp = e.getComponent();
		if (comp instanceof JTextField) {
			ReplacementDialog.getDialog().setSelectedTextField((JTextField)comp);
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
		// show a tool tip explaining how the computation uses (or can use) the listed attributes
		Component comp = e.getComponent();
		JTextField textField = null;
		if (comp instanceof JTextField) {
			textField = (JTextField)comp;
		}
		else return;
		
		String text = textField.getText();
		if ((text == null) || (text.length() == 0)) {
			return;
		}
		
		String toolTipText = textField.getToolTipText();
		if (toolTipText != null) {
			return; // no need to create tool tip
		}
		
//		String[] attrs = text.split(ReplacementDialog.getAttrSeparator());
//		String computeMethod = constraint.getComputeMethod(attrs);
//		textField.setToolTipText("Computed using " + computeMethod);
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	class MyJTextField extends JTextField {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		@Override
		public void setText(String text) {
			super.setText(text);
			String tooltip = tooltip(text);
			setToolTipText(tooltip);
			if (tooltip.equals(DONT_KNOW_HOW_TO_USE_THIS)) {
				setForeground(Color.red);
			}
			else {
				setForeground(Color.black);
			}
		}
		
	}
	
	final static String DONT_KNOW_HOW_TO_USE_THIS = "I don't know what to compute with this information.";
	String tooltip(String text) {
		String[] attrStrs = text.split(ReplacementDialog.getAttrSeparator());
		Set<Attribute> attrs = new HashSet<Attribute>();
		for (String attrStr : attrStrs) {
			// try to find an Attribute that matches this string
			Attribute foundAttr = null;
			for (Attribute attr : knownAttributes) {
				if (attrStr.equals(attr.toString())) {
					foundAttr = attr;
					break;
				}
			}
			if (foundAttr != null) {
				attrs.add(foundAttr);
			}
			else {
				return DONT_KNOW_HOW_TO_USE_THIS;
			}
		}
		
		
		String computeMethod = constraint.getComputeMethod(attrs);
		if ((computeMethod != null) && (computeMethod.length() > 0)) {
			return "Computed using " + computeMethod;
		}
		else {
			return DONT_KNOW_HOW_TO_USE_THIS;
		}
		
	}


}
