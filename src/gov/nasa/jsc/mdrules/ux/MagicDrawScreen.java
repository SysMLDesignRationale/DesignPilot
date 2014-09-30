package gov.nasa.jsc.mdrules.ux;

import java.util.Collection;
import java.util.List;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.PartView;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;

public class MagicDrawScreen {
	
	protected static boolean highlightNode(String nodeName) {
		Project proj = Application.getInstance().getProject();
		if (proj == null) {
			return false;
		}
		Collection<DiagramPresentationElement> diagrams = proj.getDiagrams();
		for (DiagramPresentationElement diagram : diagrams) {
			List<PresentationElement> elements = diagram.getPresentationElements();
			for (PresentationElement elem : elements) {
				if (highlightNodeRecursive(diagram, elem, nodeName)) {
					return true;
				}
			}
		}
		return false;
	}


	static boolean highlightNodeRecursive(DiagramPresentationElement diagram,
			PresentationElement elem, String nodeName) {
		boolean ret = false;
		if (elem instanceof ShapeElement) {
			ret = highlightShapeElement(diagram, (ShapeElement) elem, nodeName);
		}
		if (!ret) {
			List<PresentationElement> children = elem.getPresentationElements();
			for (PresentationElement child : children) {
				ret = highlightNodeRecursive(diagram, child, nodeName);
				if (ret) {
					break;
				}
			}
		}
		return ret;
	}


	static boolean highlightShapeElement(DiagramPresentationElement diagram, ShapeElement node, String nodeName) {
		
		String hname = node.getHumanName();
		String[] hnameParts = hname.split(":");
		String hnameLastPart = hnameParts[hnameParts.length-1].trim();
		if (hnameLastPart.equals(nodeName)) {
			diagram.openInActiveTab(false);
			PresentationElement highlightThis = node;
			while ((highlightThis != null) && !(highlightThis instanceof PartView)) {
				highlightThis = highlightThis.getParent();
			}
			if (highlightThis != null) {
				highlightThis.setSelected(true);
				return true;
			}
		}
		return false;
	}
		

}
