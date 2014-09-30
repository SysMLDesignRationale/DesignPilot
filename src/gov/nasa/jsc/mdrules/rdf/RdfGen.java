package gov.nasa.jsc.mdrules.rdf;

import gov.nasa.jsc.mdrules.defs.RunPropertyDefinitions;
import gov.nasa.jsc.mdrules.repository.SesameRepository;
import gov.nasa.jsc.mdrules.repository.WriteSesame;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.PathConnector;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
//import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Generalization;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
//import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ValueSpecification;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

public class RdfGen {
	
	List<Statement> stmts = new ArrayList<Statement>();
	Map<String, PresentationElement> id2PresElem = new HashMap<String, PresentationElement>();
	Map<PresentationElement, String> presElem2Id = new HashMap<PresentationElement, String>();
	Map<String, String> id2ParentId = new HashMap<String, String>();
	
	Set<FlowPort> flowPorts = new HashSet<FlowPort>();
	Map<String, Connector> id2Connector = new HashMap<String, Connector>();
	Set<Block> blocks = new HashSet<Block>();

	Map<String, Block> grandparentId2Block = new HashMap<String, Block>();
	Map<FlowPort, Block> flowPort2Block = new HashMap<FlowPort, Block>();
	
//	FileWriter fw;
	public void genRdf() {
		
		// clear what's currently in the repository
		Run runInstance = Run.getRun();
		SesameRepository repo = runInstance.getRepository();
		RepositoryConnection repConn = repo.getRepositoryConn();
		repo.clear();

		// startup parameter - schema files
		String loadSchemas = runInstance.getParamValue(
				RunPropertyDefinitions.LOAD_SCHEMAS_INTO_DB).toLowerCase();
		if (loadSchemas.equals("true") || loadSchemas.equals("yes")) {
			String schemaFileList = runInstance.getParamValue(RunPropertyDefinitions.SCHEMA_FILES);
			String[] schemaFiles = schemaFileList.split(";");
			for (String fileName : schemaFiles) {
				repo.loadTurtleFile(fileName);
			}
		}
		
		// formulas (qudt)
		stmts.addAll(runInstance.getFormulas().toRdf());

		// debug:
//		try {
//		fw = new FileWriter(new File("/Users/sidneybailin/Documents/aWorkingOntologist/elements.txt"));
//		}
//		catch (Exception e) {
//			Util.logException(e, getClass());
//		}

		Project proj = Application.getInstance().getProject();
		if (proj == null) {
			return;
		}
		Model root = proj.getModel();
		if (root == null) {
			return;
		}
		Run.getRun().setRoot(root);
		
		// generate RDF for model elements
		Collection<Element> elems = root.getOwnedElement();
		for (Element elem : elems) {
			genRecursive(elem, root);
		}
		
		Collection<DiagramPresentationElement> diagPresElems = proj.getDiagrams();
				
		// generate connectivity information for flows by accessing the diagram level
		for (DiagramPresentationElement diagPres : diagPresElems) {
			if (!(diagPres.isLoaded())) {
				diagPres.ensureLoaded();
			}
			Collection<PresentationElement> presElems = diagPres.getPresentationElements();
//			System.out.println("Diagram: " + diagPres.getHumanName());
//			System.out.println("Num elements: " + presElems.size());
			for (PresentationElement elem : presElems) {
				genPresRecursive(diagPres, elem, diagPres);
			}
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////
		// a diagram flowport has a parent that is the grandparent of the block that owns the port,
		// so we now compile this information
		//////////////////////////////////////////////////////////////////////////////////////////
		
		// first, map each block to its grandparent
		for (Block block : blocks) {
			String blockId = block.getId();
			String blockName = block.getName();
			String diagramName = block.getDiagramName();
			if (!(id2ParentId.containsKey(blockId))) {
				throw new IllegalStateException("Block with no parent ID: " + blockName + ", ID = " + blockId + ", Diagram = " + diagramName);
			}
			String parentId = id2ParentId.get(blockId);
			
			if (!(id2ParentId.containsKey(parentId))) {
				continue; // not a block we're interested in for this purpose
//				throw new IllegalStateException("Block with no grandparent ID: " + blockName + ", ID = " + blockId + ", Parent ID = " + parentId + ", Diagram = " + diagramName);
			}
			String grandparentId = id2ParentId.get(parentId);
			if (grandparentId2Block.containsKey(grandparentId)) {
				continue; // not a block we're interested in for this purpose
//				Block block2 = grandparentId2Block.get(grandparentId);
//				throw new IllegalStateException(grandparentId + " is grandparent of more than one block: " + block.getName() + ", ID = " + block.getId() + ", and " + block2.getName() + ", ID = " + block2.getId() + ", Diagram = " + diagramName);
			}
			grandparentId2Block.put(grandparentId, block);
		}
		
		// now map each flowPort to its owning block
		for (FlowPort fp : flowPorts) {
			String fpParentId = id2ParentId.get(fp.getId());
			Block block = grandparentId2Block.get(fpParentId);
			if (block == null) {
				throw new IllegalStateException("Flowport with no owning block: " + fp.getId());				
			}
			block.getFlowPorts().add(fp);
			fp.setHostId(block.getId());
			fp.setHostName(block.getName());
			
			flowPort2Block.put(fp, block);
		}
		
		// Now generate RDF for the connectivity
		for (Block block : blocks) {
			
//			try {
//				fw.write("\nBlock: " + block.getName() + "\n\n");
//			}
//			catch (Exception e) {
//				Util.logException(e, getClass());
//			}
	
			
			// we have to use the model-level ID for the block 
			// in order to match the information from the model-level RDF!
			URI blockUri = new URIImpl(Util.sysmlIdToModelDbUri(block.getModelId()));
			Statement stmt0 = new StatementImpl(blockUri, RdfConstants.isInDiagramUri, new LiteralImpl(block.getDiagramName()));
			stmts.add(stmt0);
			for (FlowPort fp : block.getFlowPorts()) {
				URI fpUri = new URIImpl(Util.sysmlIdToModelDbUri(fp.getId()));
				URI fpModelUri = new URIImpl(Util.sysmlIdToModelDbUri(fp.getModelId()));
				
				// Block hasFlowPort FlowPortX
				Statement stmt1 = new StatementImpl(blockUri, RdfConstants.hasFlowPortUri, fpUri);
				stmts.add(stmt1);
				
				// FlowPortX a FlowPort
				Statement stmt3 = new StatementImpl(fpUri, RdfConstants.isa, RdfConstants.flowPortUri);
				stmts.add(stmt3);
				
				// FlowPortX rdfs:label Label
				Literal nameLit = new LiteralImpl(fp.getName());
				Statement stmt6 = new StatementImpl(fpUri, RdfConstants.rdfsLabelUri, nameLit);
				stmts.add(stmt6);
				
				// FlowPortX hasModel
				Statement stmt5 = new StatementImpl(fpUri, RdfConstants.hasModelUri, fpModelUri);
				stmts.add(stmt5);
				
//				try {
//					fw.write(stmt3.toString() + "\n\n");
//					fw.write(stmt1.toString() + "\n\n");
//					fw.write(stmt6.toString() + "\n\n");
//					fw.write(stmt5.toString() + "\n\n");
//				}
//				catch (Exception e) {
//					Util.logException(e, getClass());
//				}
				for (Connector conn : fp.getConnectors()) {
					Statement stmt2 = null;
					Statement stmt4 = null;
					URI connUri = new URIImpl(Util.sysmlIdToModelDbUri(conn.getId()));
					if (fp == conn.getSource()) {
						stmt2 = new StatementImpl(connUri, RdfConstants.hasSourceUri, fpUri);
					}
					else if (fp == conn.getTarget()) {
						stmt2 = new StatementImpl(connUri, RdfConstants.hasTargetUri, fpUri);						
					}
					else {
						throw new IllegalStateException("FlowPort " + fp.getId() + " has connector " + conn.getId() + " that doesn't have this flowPort as source or target.");
					}
					
					// TODO make hasEndPort an superproperty of hasSource and hasTarget, 
					// and instead of writing it out, use inference in the Sparql queries
					Statement stmt7 = new StatementImpl(connUri, RdfConstants.hasEndPortUri, fpUri);
					stmts.add(stmt7);
					
					stmt4 = new StatementImpl(connUri, RdfConstants.isa, RdfConstants.connectorUri);
					stmts.add(stmt4);

//					try {
//						fw.write(stmt4.toString() + "\n\n");
//						fw.write(stmt2.toString() + "\n\n");
//					}
//					catch (Exception e) {
//						Util.logException(e, getClass());
//					}
					stmts.add(stmt2);
				}
			}
		}
		
//		try {
//			for (Block block : blocks) {
//				fw.write(block + "\n");
//			}
//			for (FlowPort fp : flowPorts) {
//				fw.write(fp + "\n");
//			}
//			for (Connector conn : id2Connector.values()) {
//				fw.write(conn + "\n");
//			}
//		}
//		catch (Exception e) {
//			Util.logException(e, getClass());
//		}

//		try {
//			fw.write("Adding statements:\n\n");
//			for (Statement stmt : stmts) {
//				fw.write(stmt.toString() + "\n\n");
//			}
//		
//			fw.flush();
//			fw.close();
//		}
//		catch (Exception e) {
//			Util.logException(e, getClass());
//		}
		
		WriteSesame.addToRepository(repConn, stmts, true);
	}
	
	void genRecursive(Element elem, Element parent) {
		
		if (!interesting(elem)) {
			return;
		}

		String id = ((BaseElement)elem).getID();
		String nameStr = elem instanceof NamedElement? ((NamedElement)elem).getName() : elem.getHumanName();
		Literal name = new LiteralImpl(nameStr);
		String type = elem.getHumanType();

		type = type.replaceAll(" ", "_");
		URI elemUri = new URIImpl(Util.sysmlIdToModelDbUri(id));
		URI typeUri = new URIImpl(Util.sysmlIdToModelSchemaUri(type));
		Statement stmt = new StatementImpl(elemUri, RdfConstants.isa, typeUri);
		stmts.add(stmt);
		
		Statement stmt2 = new StatementImpl(elemUri, RdfConstants.rdfsLabelUri, name);
		stmts.add(stmt2);
		
		if (elem instanceof InformationFlow) {
			processInformationFlow((InformationFlow)elem, elemUri);
		}
		
		if (elem instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) {
			processClass((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)elem, elemUri);
		}
		
		if (elem instanceof com.nomagic.magicdraw.properties.Property) {
			processMdProperty((com.nomagic.magicdraw.properties.Property)elem, elemUri);
		}
		
		if (elem instanceof Generalization) {
			processGeneralization(((Generalization)elem), elemUri);
		}

//		if (elem instanceof Association) {
//			processAssociation(((Association)elem), elemUri);
//		}

//		if (elem instanceof AssociationClass) {
//			processAssociationClass((com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass)elem, elemUri);
//		}
		
//		if (elem instanceof DirectedRelationship) {
//		processDirectedRelationship(((DirectedRelationship)elem), elemUri);
//	}

//		if (elem instanceof Connector) {
//			processConnector((Connector)elem, elemUri);
//		}

//		if (elem.getHumanType().equals("Binding Connector")) {
//		}
		
		// recurse
		Collection<Element> children = elem.getOwnedElement();
		for (Element child : children) {
			genRecursive(child, elem);

		}
	}
	
	boolean interesting(Element elem) {
		String type = elem.getHumanType();
		if (
//				(type.equals("Diagram")) ||
				(type.equals("Instance Specification")) ||
//				(type.equals("Subsystem")) ||
				(type.equals("Connector")) ||
				(type.equals("Binding Connector")) ||
//				(type.equals("Domain")) ||
//				(type.equals("Stereotype")) ||
//				(type.equals("Extension")) ||
//				(type.equals("ExtensionEnd")) ||
//				(type.equals("DirectedRelationship")) ||
//				(type.equals("Property")) ||
				(type.equals("Information Flow")) ||
				(type.equals("Package")) ||
				(type.equals("Generalization")) ||
				(type.equals("Association")) ||
				(type.equals("Block")) ||
				(type.equals("Property")) ||
				(type.equals("Value Property")) ||
				(type.equals("Part Property")) ||
				(type.equals("Constraint Property")) ||
				(type.equals("Flow Port")) ||
				(type.equals("Constraint Block")) ||
				(type.equals("Quantity Kind")) ||
				(type.equals("Unit"))
		) {
			return true;
		}
//		else {
//			try {
//				fw.write("Uninteresting: " + type + "\n");
//			}
//			catch (Exception e) {
//				Util.logException(e, getClass());
//			}
//		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	void genPresRecursive(DiagramPresentationElement diagPres, PresentationElement elem, PresentationElement parent) {
		
		// bookkeeping
		String id = ((BaseElement)elem).getID();
		String parentId = ((BaseElement)parent).getID();
		Element modelElem = elem.getElement();
		presElem2Id.put(elem, id);
		id2PresElem.put(id, elem);
		id2ParentId.put(id, parentId);
		
		String type = elem.getHumanType();
		if (type.equals("Block")) {
			Block block = new Block();
			block.setId(id);
			block.setModelId(((BaseElement)modelElem).getID());
			String name = elem instanceof NamedElement? ((NamedElement)elem).getName() : elem.getHumanName();
			block.setName(name);
			block.setDiagramName(diagPres.getName());
			blocks.add(block);
		}

		// record a flow port and its connector(s)
		if ((elem instanceof PathConnector) && type.equals("Flow Port")) {
			FlowPort flowPort = new FlowPort();
			flowPort.setId(id);
			String nameStr = elem instanceof NamedElement? ((NamedElement)elem).getName() : elem.getHumanName();
			flowPort.setName(nameStr);
			flowPort.setModelId(modelElem.getID());
			
			String direction = null;
			Collection<Element> coll = new ArrayList<Element>();
			coll.add(modelElem);
			for (Stereotype stereo : StereotypesHelper.getAllAssignedStereotypes(coll)) {
//				System.out.println("Stereotype:" + stereo.getHumanName());
				for (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property prop : stereo.getOwnedAttribute()) {
//					System.out.println("XProp: " + prop.getHumanName());
					if (prop.getHumanName().equals("Property direction")) {
						List value = StereotypesHelper.getStereotypePropertyValue(
								modelElem, stereo, prop.getName());
						EnumerationLiteral valLit = (EnumerationLiteral)value.get(0);
						direction = valLit.getHumanName();
					}
				}
			}
			if (direction == null) {
				return;
			}
			flowPort.setDirection(direction);
						
			List<PathElement> connectorElems = ((PathConnector)elem).getConnectedPathElements();
			if (connectorElems.size() == 0) {
				return;
			}
			for (PathElement connectorElem : connectorElems) {
				String connectorId = ((BaseElement)connectorElem).getID();
				Connector connector = null;
				if (id2Connector.containsKey(connectorId)) {
					connector = id2Connector.get(connectorId);
				}
				else {
					connector = new Connector();
					connector.setId(connectorId);
					id2Connector.put(id, connector);
				}
				if (direction.equals("Enumeration Literal in")) {
					connector.setTarget(flowPort);
				} 
				else if (direction.equals("Enumeration Literal out")) {
					connector.setSource(flowPort);
				} 
				else {
					throw new IllegalStateException(
							"Flow port with unknown direction " + direction
									+ ": " + elem.getHumanName() + ", ID = "
									+ ((BaseElement) elem).getID());
				}
				flowPort.getConnectors().add(connector);
			}
			flowPort.setDirection(direction);
			flowPorts.add(flowPort);			
		}
		
		for (PresentationElement child : elem.getPresentationElements()) {
			genPresRecursive(diagPres, child, elem);
		}
		
	}
	
	void processInformationFlow(InformationFlow flow, URI flowUri) {
		for (NamedElement source : flow.getInformationSource()) {
			URI predUri = RdfConstants.hasSourceUri;
//			String sourceName = uEncode(source.getName());
			String sourceId = ((BaseElement)source).getID();
			URI sourceUri = new URIImpl(Util.sysmlIdToModelDbUri(sourceId));
			Statement stmt = new StatementImpl(flowUri, predUri, sourceUri);
			stmts.add(stmt);
		}
		for (NamedElement target : flow.getInformationTarget()) {
			URI predUri = RdfConstants.hasTargetUri;
//			String targetName = uEncode(target.getName());
			String targetId = ((BaseElement)target).getID();
			URI targetUri = new URIImpl(Util.sysmlIdToModelDbUri(targetId));
			Statement stmt = new StatementImpl(flowUri, predUri, targetUri);
			stmts.add(stmt);
		}
	}

//	void processAssociation(Association assoc, URI assocUri) {
//		for (NamedElement elem : assoc.getEndType()) {
//			p.append("EndType: " + elem.getName() + "\n");
//		}
//		Element supplier = com.nomagic.uml2.ext.jmi.helpers.ModelHelper.getSupplierElement(assoc);
//		Element client = com.nomagic.uml2.ext.jmi.helpers.ModelHelper.getClientElement(assoc);
//		Property prop1 = com.nomagic.uml2.ext.jmi.helpers.ModelHelper.getFirstMemberEnd(assoc);
//		Property prop2 = com.nomagic.uml2.ext.jmi.helpers.ModelHelper.getSecondMemberEnd(assoc);
//		p.append("Supplier: " + supplier.getHumanName() + "\n");
//		p.append("Client: " + client.getHumanName() + "\n");
//		p.append("First member agg kind: " + prop1.getAggregation() + "\n");
//		p.append("Second member agg kind: " + prop2.getAggregation() + "\n");


//		int i=0;
//		for (Property prop : assoc.getMemberEnd()) {
//			AggregationKind aggKind = prop.getAggregation();
//			if ((aggKind != null) && !(aggKind.toString().trim().equals("none"))) {
//				p.append("Kind: " + aggKind + "\n");
//				p.append("Owner index = " + i + "\n");
//			}
//			++i;
//		}
//
//	}
	
//	void processAssociationClass(com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass assoc, URI assocUri) {
//		String tab = indent + "   ";
//		for (NamedElement elem : assoc.getRole()) {
//			p.append("Member: " + elem.getName() + "\n");
//		}
//	}

//	void processConnector(Connector conn, URI connUri) {
//		System.out.println("Connector ID: " + conn.getID() + "\n");
//		System.out.println("Connector Kind: " + conn.getKind() + "\n");
//		for (ConnectorEnd end : conn.getEnd()) {
//			System.out.println("Connector End: " + end.getRole().getName() + "\n");
//		}
//	}

	void processGeneralization(Generalization gen, URI genUri) {
		Classifier general = gen.getGeneral();
		Classifier specific = gen.getSpecific();
		URI predUri = RdfConstants.hasSpecializationUri;
//		String generalName = uEncode(general.getName());
		String generalId = ((BaseElement)general).getID();
		URI generalUri = new URIImpl(Util.sysmlIdToModelDbUri(generalId));
//		String specificName = uEncode(specific.getName());
		String specificId = ((BaseElement)specific).getID();
		URI specificUri = new URIImpl(Util.sysmlIdToModelDbUri(specificId));
		Statement stmt = new StatementImpl(generalUri, predUri, specificUri);
		stmts.add(stmt);
		
	}
	
	void processClass(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class cls, URI clsUri) {

		for (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property prop : cls.getOwnedAttribute()) {
			URI hasAttrUri = RdfConstants.hasAttributeUri;
			String propName = prop.getName();
			String propId = ((BaseElement)prop).getID();
			URI propUri = new URIImpl(Util.sysmlIdToModelDbUri(propId));
			Statement hasAttrStmt = new StatementImpl(clsUri, hasAttrUri, propUri);
			stmts.add(hasAttrStmt);
			
			Statement isAttrStmt = new StatementImpl(propUri, RdfConstants.isa, RdfConstants.attrUri);
			stmts.add(isAttrStmt);
			
			Literal labelLit = new LiteralImpl(propName);
			Statement hasLabelStmt = new StatementImpl(propUri, RdfConstants.rdfsLabelUri, labelLit);
			stmts.add(hasLabelStmt);
			
			URI hasTypeUri = RdfConstants.hasTypeUri;
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type typ = prop.getType();
			if (typ != null) {
				Literal typeLiteral = new LiteralImpl(prop.getType().getName());
				Statement hasTypeStmt = new StatementImpl(propUri, hasTypeUri,
						typeLiteral);
				stmts.add(hasTypeStmt);
			}
			
			URI hasVisibilityUri = RdfConstants.hasVisibilityUri;
			Literal vizLiteral = new LiteralImpl(prop.getVisibility().toString());
			Statement hasVizStmt = new StatementImpl(propUri, hasVisibilityUri, vizLiteral);
			stmts.add(hasVizStmt);
			
			ValueSpecification defaultValue = prop.getDefaultValue();
			String defaultValueStr = null;
			if (defaultValue != null) {
				if (defaultValue instanceof LiteralString) {
					defaultValueStr = ((LiteralString)defaultValue).getValue();
					if (defaultValueStr.length() > 0) {
						URI hasDefaultValueUri = RdfConstants.hasDefaultValueUri;
						Literal defValLiteral = new LiteralImpl(defaultValueStr);
						Statement hasDefValStmt = new StatementImpl(propUri, hasDefaultValueUri, defValLiteral);
						stmts.add(hasDefValStmt);
					}
				}
			}
		}
	}

	void processMdProperty(com.nomagic.magicdraw.properties.Property prop, URI propUri) {
		
		URI hasTypeUri = RdfConstants.hasTypeUri;
		Literal typeLiteral = new LiteralImpl(prop.getClassType());
		Statement hasTypeStmt = new StatementImpl(propUri, hasTypeUri, typeLiteral);
		stmts.add(hasTypeStmt);
		
		URI hasDescrUri = RdfConstants.hasDescriptionUri;
		Literal descrLiteral = new LiteralImpl(prop.getDescription());
		Statement hasDescrStmt = new StatementImpl(propUri, hasDescrUri, descrLiteral);
		stmts.add(hasDescrStmt);
		
		URI hasValueUri = RdfConstants.hasValueUri;
		Literal valueLiteral = new LiteralImpl(prop.getValue().toString());
		Statement hasValueStmt = new StatementImpl(propUri, hasValueUri, valueLiteral);
		stmts.add(hasValueStmt);
		
	}
	
//	/**
//	 * URL-encodes a string
//	 */
//	String uEncode(String in) {
//		String out = null;
//		try {
//			out = URLEncoder.encode(in, "utf8");
//		} 
//		catch (UnsupportedEncodingException e) {
//			Util.logException(e, getClass());
//		}
//		return out;
//	}
	
	/**
	 * Representation of a diagram-level connector between flow ports
	 * @author sidneybailin
	 *
	 */
	class Connector {
		
		String id;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		
		FlowPort source;
		public FlowPort getSource() {
			return source;
		}
		public void setSource(FlowPort source) {
			this.source = source;
		}
		
		FlowPort target;
		public FlowPort getTarget() {
			return target;
		}
		public void setTarget(FlowPort target) {
			this.target = target;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Connector: " + getId() + "\n");
			if (source != null) {
				sb.append("\tSource: " + source.getId());
			}
			if (target != null) {
				sb.append("\tTarget: " + target.getId());
			}
			return sb.toString();
		}

	}

	/**
	 * Representation of a diagram-level flow port
	 * @author sidneybailin
	 *
	 */
	class FlowPort {
		String id;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		
		String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		String modelId; // ID of the corresponding model element
		public String getModelId() {
			return modelId;
		}
		public void setModelId(String modelId) {
			this.modelId = modelId;
		}
		
		String hostId;
		public String getHostId() {
			return hostId;
		}
		public void setHostId(String hostId) {
			this.hostId = hostId;
		}
		
		String hostName;
		public String getHostName() {
			return hostName;
		}
		public void setHostName(String hostName) {
			this.hostName = hostName;
		}
		
		String direction;
		public String getDirection() {
			return direction;
		}
		public void setDirection(String direction) {
			this.direction = direction;
		}
		
		String typ;
		public String getType() {
			return typ;
		}
		public void setType(String typ) {
			this.typ = typ;
		}
		
		List<Connector> connectors = new ArrayList<Connector>();
		public List<Connector> getConnectors() {
			return connectors;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Flow Port: " + getName() + "\n");
			sb.append("\tID: " + getId() + "\n");
			sb.append("\tDirection: " + getDirection() + "\n");
			sb.append("\tHost: " + getHostName() + "\n");
			for (Connector conn : getConnectors()) {
				sb.append("\tConnector: " + conn.getId() + "\n");
			}
			return sb.toString();
		}
		
	}
	
	/**
	 * Representation of a diagram-level block
	 * @author sidneybailin
	 *
	 */
	class Block {
		String id;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		
		String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		String modelId; // ID of the corresponding model element
		public String getModelId() {
			return modelId;
		}
		public void setModelId(String modelId) {
			this.modelId = modelId;
		}
		
		public String diagramName;
		public String getDiagramName() {
			return diagramName;
		}
		public void setDiagramName(String diagramName) {
			this.diagramName = diagramName;
		}
		
		Set<FlowPort> flowPorts = new HashSet<FlowPort>();
		public Set<FlowPort> getFlowPorts() {
			return flowPorts;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Block: " + getName() + "\n");
			sb.append("\tID: " + getId() + "\n");
			for (FlowPort fp : getFlowPorts()) {
				sb.append("\tFlowPort: name = " + fp.getName() + ", ID = "+ fp.getId());
			}
			return sb.toString();
		}
		
	}
}
