package gov.nasa.jsc.mdrules.rdf;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.defs.RunPropertyDefinitions;

/**
 * Constants defining schema classes and predicates.
 * 
 * TODO Rather than hard code these definitions -- which essentially duplicate
 * the contents of the RDF schema files -- we could generate this class, with
 * more or less the identical content, from the RDF files, and then dynamically
 * add it to the class loader.
 * 
 * @author sidneybailin
 *
 */
public class RdfConstants {
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// RDF/RDFS/OWL definitions
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final String rdfsLabelStr = "http://www.w3.org/2000/01/rdf-schema#label";
	public static final URI rdfsLabelUri = new URIImpl(rdfsLabelStr);

	public static final String rdfTypeStr = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static final URI isa = new URIImpl(rdfTypeStr);

	public static final String rdfSubjStr = "http://www.w3.org/1999/02/22-rdf-syntax-ns#subject";
	public static final URI rdfSubjUri = new URIImpl(rdfSubjStr);

	public static final String rdfPredStr = "http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate";
	public static final URI rdfPredUri = new URIImpl(rdfPredStr);

	public static final String rdfObjStr = "http://www.w3.org/1999/02/22-rdf-syntax-ns#object";
	public static final URI rdfObjUri = new URIImpl(rdfObjStr);
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Lesson definitions
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final String hasEntityStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasEntity";
	public static final URI hasEntityUri = new URIImpl(hasEntityStr);

	public static final String hasStmtStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasStatement";
	public static final URI hasStmtUri = new URIImpl(hasStmtStr);

	public static final String constraintClassStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#Constraint";
	public static final URI constrClassUri = new URIImpl(constraintClassStr);

	public static final String violationClassStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#ConstraintViolation";
	public static final URI violationClassUri = new URIImpl(violationClassStr);

	public static final String overrideClassStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#ConstraintOverride";
	public static final URI overrideClassUri = new URIImpl(overrideClassStr);

	public static final String rationaleClassStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#Rationale";
	public static final URI rationaleClassUri = new URIImpl(rationaleClassStr);

	public static final String computationClassStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#Computation";
	public static final URI computationClassUri = new URIImpl(computationClassStr);

	public static final String actualValuesWillDifferRationaleTypeStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#RationaleType1";
	public static final URI actualValuesWilLDifferRationaleTypeUri = new URIImpl(actualValuesWillDifferRationaleTypeStr);

	public static final String replacementClassStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#Replacement";
	public static final URI replacementClassUri = new URIImpl(replacementClassStr);

	public static final String attrSetClassStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#AttributeSet";
	public static final URI attrSetClassUri = new URIImpl(attrSetClassStr);

	public static final String hasPriorityStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasPriority";
	public static final URI hasPriorityUri = new URIImpl(hasPriorityStr);

	public static final String hasComputationStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasComputation";
	public static final URI hasComputationUri = new URIImpl(hasComputationStr);

	public static final String hasMinValueStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasMinValue";
	public static final URI hasMinValueUri = new URIImpl(hasMinValueStr);
	
	public static final String hasMaxValueStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasMaxValue";
	public static final URI hasMaxValueUri = new URIImpl(hasMaxValueStr);

	public static final String violatesConstraintStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#violatesConstraint";
	public static final URI violatesConstraintUri = new URIImpl(violatesConstraintStr);

	public static final String overridesStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#overrides";
	public static final URI overridesUri = new URIImpl(overridesStr);

	public static final String hasRationaleStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasRationale";
	public static final URI hasRationaleUri = new URIImpl(hasRationaleStr);

	public static final String hasRationaleTypeStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasRationaleType";
	public static final URI hasRationaleTypeUri = new URIImpl(hasRationaleTypeStr);

	public static final String replacesThisStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#replacesThis";
	public static final URI replacesThisUri = new URIImpl(replacesThisStr);

	public static final String replacesWithStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#replacesWith";
	public static final URI replacesWithUri = new URIImpl(replacesWithStr);

	public static final String hasMemberStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasMember";
	public static final URI hasMemberUri = new URIImpl(hasMemberStr);

	public static final String hasReplacementStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasReplacement";
	public static final URI hasReplacementUri = new URIImpl(hasReplacementStr);

	public static final String hasComputedAttributeStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasComputedAttribute";
	public static final URI hasComputedAttributeUri = new URIImpl(hasComputedAttributeStr);

	public static final String usesAttributeSetStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#usesAttributeSet";
	public static final URI usesAttributeSetUri = new URIImpl(usesAttributeSetStr);

	public static final String executeThisStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#executeThis";
	public static final URI executeThisUri = new URIImpl(executeThisStr);

	public static final String executeWithStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#executeWith";
	public static final URI executeWithUri = new URIImpl(executeWithStr);

	public static final String userActionStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#userAction";
	public static final URI userActionUri = new URIImpl(userActionStr);

	public static final String userActionOverrideStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#UserActionOverride";
	public static final URI userActionOverrideUri = new URIImpl(userActionOverrideStr);

	public static final String userActionRationaleProvidedStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#UserActionRationaleProvided";
	public static final URI userActionRationaleProvidedUri = new URIImpl(userActionRationaleProvidedStr);

	public static final String userActionReplacementsProvidedStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#UserActionReplacementsProvided";
	public static final URI userActionReplacementsProvidedUri = new URIImpl(userActionReplacementsProvidedStr);
	
	// NOTE: because an attribute might have >1 computed values (e.g., if it is additive
	// and receives values from multiple connections), we must make the "computed value"
	// a unique individual in its own right rather than just using the scalar value itself.
	// Otherwise, for example, we could not keep track of adding the multiple computed values of
	// an additive attribute: once added, the added value must be removed, but there may be
	// another computation that resulted in the same scalar value. So we must make the
	// the "computed value" an individual in its own right, and hang the scalar value off it
	// using the "withValue" predicate
	public static final String hasComputedValueStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#hasComputedValue";
	public static final URI hasComputedValueUri = new URIImpl(hasComputedValueStr);

	public static final String withValueStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#withValue";
	public static final URI withValueUri = new URIImpl(withValueStr);

	public static final String isComputedUsingStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#isComputedUsing";
	public static final URI isComputedUsingUri = new URIImpl(isComputedUsingStr);

	public static final String computeWithAttrStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#computeWithAttr";
	public static final URI computeWithAttrUri = new URIImpl(computeWithAttrStr);

	public static final String computeForwardWithAttrStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#computeForwardWithAttr";
	public static final URI computeForwardWithAttrUri = new URIImpl(computeForwardWithAttrStr);

	public static final String computeBackwardWithAttrStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#computeBackwardWithAttr";
	public static final URI computeBackwardWithAttrUri = new URIImpl(computeBackwardWithAttrStr);

	public static final String startComputationWithBlockStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#startComputationWithBlock";
	public static final URI startComputationWithBlockUri = new URIImpl(startComputationWithBlockStr);

	public static final String startComputationWithAttrStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#startComputationWithAttribute";
	public static final URI startComputationWithAttrUri = new URIImpl(startComputationWithAttrStr);

	public static final String violationBeingProcessedStr = Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#violationBeingProcessed";
	public static final URI violationBeingProcessedUri = new URIImpl(violationBeingProcessedStr);


	///////////////////////////////////////////////////////////////////////////////////////////////
	// SysML model definitions
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final String hasSpecializationStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasSpecialization";
	public static final URI hasSpecializationUri = new URIImpl(hasSpecializationStr);

	public static final String hasSourceStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasSource";
	public static final URI hasSourceUri = new URIImpl(hasSourceStr);

	public static final String hasTargetStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasTarget";
	public static final URI hasTargetUri = new URIImpl(hasTargetStr);

	public static final String hasEndPortStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasEndPort";
	public static final URI hasEndPortUri = new URIImpl(hasEndPortStr);

	public static final String hasAttributeStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasAttribute";
	public static final URI hasAttributeUri = new URIImpl(hasAttributeStr);

	public static final String hasTypeStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasType";
	public static final URI hasTypeUri = new URIImpl(hasTypeStr);

	public static final String hasVisibilityStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasVisibility";
	public static final URI hasVisibilityUri = new URIImpl(hasVisibilityStr);

	public static final String hasDefaultValueStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasDefaultValue";
	public static final URI hasDefaultValueUri = new URIImpl(hasDefaultValueStr);

	public static final String hasDescriptionStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasDescription";
	public static final URI hasDescriptionUri = new URIImpl(hasDescriptionStr);

	public static final String hasValueStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasValue";
	public static final URI hasValueUri = new URIImpl(hasValueStr);

	public static final String hasFlowPortStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasFlowPort";
	public static final URI hasFlowPortUri = new URIImpl(hasFlowPortStr);

	public static final String flowPortStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#FlowPort";
	public static final URI flowPortUri = new URIImpl(flowPortStr);

	public static final String connectorStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#Connector";
	public static final URI connectorUri = new URIImpl(connectorStr);

	public static final String blockStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#Block";
	public static final URI blockUri = new URIImpl(blockStr);

	public static final String attrStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#Attribute";
	public static final URI attrUri = new URIImpl(attrStr);

	public static final String additiveAttrStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#AdditiveAttribute";
	public static final URI additiveAttrUri = new URIImpl(additiveAttrStr);

	public static final String hasModelStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#hasModel";
	public static final URI hasModelUri = new URIImpl(hasModelStr);

	public static final String isInDiagramStr = Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_SCHEMA_NAMESPACE) + "#isInDiagram";
	public static final URI isInDiagramUri = new URIImpl(isInDiagramStr);

	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Computation definitions
	//
	// TODO Integrate this with the QUDT ontology
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	// TODO We might want to make Formula a subclass of Computation - would probably require some adjustments
	public static final String formulaClassStr = Run.getRun().getParamValue(RunPropertyDefinitions.QUDV_SCHEMA_NAMESPACE) + "#Formula";
	public static final URI formulaClassUri = new URIImpl(formulaClassStr);

	public static final String hasOperand1Str = Run.getRun().getParamValue(RunPropertyDefinitions.QUDV_SCHEMA_NAMESPACE) + "#hasOperand1";
	public static final URI hasOperand1Uri = new URIImpl(hasOperand1Str);

	public static final String hasOperand2Str = Run.getRun().getParamValue(RunPropertyDefinitions.QUDV_SCHEMA_NAMESPACE) + "#hasOperand2";
	public static final URI hasOperand2Uri = new URIImpl(hasOperand2Str);

	public static final String hasOperatorStr = Run.getRun().getParamValue(RunPropertyDefinitions.QUDV_SCHEMA_NAMESPACE) + "#hasOperator";
	public static final URI hasOperatorUri = new URIImpl(hasOperatorStr);

	public static final String hasResultStr = Run.getRun().getParamValue(RunPropertyDefinitions.QUDV_SCHEMA_NAMESPACE) + "#hasResult";
	public static final URI hasResultUri = new URIImpl(hasResultStr);

	public static final String quantityClassStr = Run.getRun().getParamValue(RunPropertyDefinitions.QUDV_SCHEMA_NAMESPACE) + "#Quantity";
	public static final URI quantityClassUri = new URIImpl(quantityClassStr);

	


	


}
