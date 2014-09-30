package gov.nasa.jsc.mdrules.rules;

import java.util.TreeSet;

import gov.nasa.jsc.mdrules.rdf.RdfConstants;
import gov.nasa.jsc.mdrules.run.Run;
import gov.nasa.jsc.mdrules.defs.RunPropertyDefinitions;

/**
 * Contains the rules, formalized a SPARQL queries, that drive the system
 * @author sidneybailin
 *
 */
public class Rules {
	
	public Rules() {
	}
	
	public void sortRules() {
		for (Rule rule : rules) {
			sortedRules.add(rule);
		}
		
//		for (Rule rule : rules) {
//			System.out.println("\n***Rule: " + rule.getName() + "\n");
//			System.out.println(rule.getSparql());
//		}
		
	}
	
	static String nsPrefixes = "PREFIX lessonSchema: <" + Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_SCHEMA_NAMESPACE) + "#> \n" +
			"PREFIX lessonDb: <" + Run.getRun().getParamValue(RunPropertyDefinitions.LESSON_DB_NAMESPACE) + "#> \n" +
			"PREFIX modelDb: <" + Run.getRun().getParamValue(RunPropertyDefinitions.MODEL_DB_NAMESPACE) + "#> \n" +
			"PREFIX owl: <http://www.w3.org/2002/07/owl#> \n" +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n";

	TreeSet<Rule> sortedRules = new TreeSet<Rule>();
	public TreeSet<Rule> getSortedRules() {
		return sortedRules;
	}
	
	
	// NOTE: the sort order won't work for priorities greater than 9 (must be single digit)
	// TODO: fix this
	Rule[] rules = {
			new Rule("promptUserForOverrideRationale", promptUserForOverrideRationale, 0, true),
			new Rule("promptUserForComputationReplacements", promptUserForComputationReplacements, 0, true),
			new Rule("generateConstraintFromComputationReplacements", generateConstraintFromComputationReplacements, 0, true),
			new Rule("computeMultiplicativeFormula1", computeMultiplicativeFormula1, 1),
			new Rule("computeMultiplicativeFormula2", computeMultiplicativeFormula2, 1),
			new Rule("constraintFromDefaultValues", constraintFromDefaultValues, 1, true),
			new Rule("constraintComputationFromFormula", constraintComputationFromFormula, 1, true),
			new Rule("startComputationFromConstraint", startComputationFromConstraint, 1, true),
			new Rule("constraintViolationFound", constraintViolationFound, 1, true),
			new Rule("propagateAttributeFromGenToSpec",propagateAttributeFromGenToSpec, 1, true),
			new Rule("getInitialBlocksAndAttrsToComputeWith",getInitialBlocksAndAttrsToComputeWith, 1),
			new Rule("currentIsAdditive", currentIsAdditive, 1),
			new Rule("hasDefaultValueImpliesHasValue", hasDefaultValueImpliesHasValue, 1), // don't remove this after execution!!!!
			new Rule("hasComputedValueImpliesHasValue", hasComputedValueImpliesHasValue, 2),
			new Rule("computeForwardWithAttrImpliesComputeWithAttr", computeForwardWithAttrImpliesComputeWithAttr, 1),
			new Rule("computeBackwardWithAttrImpliesComputeWithAttr", computeBackwardWithAttrImpliesComputeWithAttr, 1),
			new Rule("propagateAttributeValueFromGenToSpec",propagateAttributeValueFromGenToSpec, 3, true),
			new Rule("propagateAttributeValueForwardOverFlow", propagateAttributeValueForwardOverFlow, 1),
			new Rule("propagateAttributeValueBackwardOverFlow", propagateAttributeValueBackwardOverFlow, 1),
			new Rule("inferExistenceOfSiblingAttribute1", inferExistenceOfSiblingAttribute1, 4),
			new Rule("inferExistenceOfSiblingAttribute2", inferExistenceOfSiblingAttribute2, 4),
			new Rule("inferSiblingAttributeValue1", inferSiblingAttributeValue1, 4),
			new Rule("inferSiblingAttributeValue2", inferSiblingAttributeValue2, 4),
			new Rule("addValuesOfAdditiveAttribute", addValuesOfAdditiveAttribute, 6),
			new Rule("computeDivisionFormula1", computeDivisionFormula1, 8),
			new Rule("computeDivisionFormula2", computeDivisionFormula2, 8),
			new Rule("notifyUserOfViolation", notifyUserOfViolation, 9, true),
	};
	
	public Rule[] getRules() {
		return rules;
	}
			
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: notify user of constraint violation
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		a constraint violation
	//		Reify the statements with the constraint as subject and with the violation as subject
	//		Create:
	//		an executeThis statement pointing to UX.presentToUserConstraintViolation
	//		executeWith statements pointing to the constraint and violation statements
	//		
	static String notifyUserOfViolation = nsPrefixes +
	"CONSTRUCT {\n" +
	"\t?vio <" + RdfConstants.executeThisStr + "> \"gov.nasa.jsc.mdrules.ux.UserExperience:presentToUserConstraintViolation\" . \n" +
	"\t?vio <" + RdfConstants.executeWithStr + "_1> ?constr . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?vio a <" + RdfConstants.violationClassStr + "> . \n" +
	"\t?vio <" + RdfConstants.violatesConstraintStr + "> ?constr . \n" +
	"\tFILTER NOT EXISTS {?vio <" + RdfConstants.userActionStr + "> ?action} . \n" +
	"\n}"
	;
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: prompt for rationale of a constraint override
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		a constraint override
	//		Create:
	//		an executeThis statement pointing to UX.obtainFromUserRationaleForOverride
	//
	static String promptUserForOverrideRationale = nsPrefixes +
	"CONSTRUCT {\n" +
	"\t?ovr <" + RdfConstants.executeThisStr + "> \"gov.nasa.jsc.mdrules.ux.UserExperience:prompUserForOverrideRationale\" . \n" +
	"\t?ovr <" + RdfConstants.executeWithStr + "_1> ?constr . \n" +
	"\t?ovr <" + RdfConstants.executeWithStr + "_2> ?vio . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?ovr a <" + RdfConstants.overrideClassStr + "> . \n" +
	"\t?ovr <" + RdfConstants.overridesStr + "> ?constr . \n" +
	"\t?vio <" + RdfConstants.violatesConstraintStr + "> ?constr . \n" +
	"\t?vio <" + RdfConstants.userActionStr + "> <" + RdfConstants.userActionOverrideStr + "> ." +
	"\n}"
	;
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: prompt for replacements substantiating an Actual Values Will Differ rationale
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		a constraint override rationale of type Actual Values Will Differ
	//		Create:
	//		an executeThis statement pointing to UX.obtainFromUserRationaleForOverride
	//
	static String promptUserForComputationReplacements = nsPrefixes +
	"CONSTRUCT {\n" +
	"\t?ovr <" + RdfConstants.executeThisStr + "> \"gov.nasa.jsc.mdrules.ux.UserExperience:promptUserForComputationReplacements\" . \n" +
	"\t?ovr <" + RdfConstants.executeWithStr + "_1> ?constr . \n" +
	"\t?ovr <" + RdfConstants.executeWithStr + "_2> ?rat . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?ovr <" + RdfConstants.overridesStr + "> ?constr . \n" +
	"\t?ovr <" + RdfConstants.userActionStr + "> <" + RdfConstants.userActionRationaleProvidedStr + "> . \n" +
	"\t?ovr <" + RdfConstants.hasRationaleStr + "> ?rat . \n" +
	"\t?rat <" + RdfConstants.hasRationaleTypeStr + "> <" + RdfConstants.actualValuesWillDifferRationaleTypeStr + "> . \n" +
	"\n}"
	;
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: generate new constraint from override rationale 
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		a constraint override rationale of the type "Actual values will differ"
	//		Create:
	//		a new, higher-priority constraint using the rationale's replacements
	//
	static String generateConstraintFromComputationReplacements = nsPrefixes +
	"CONSTRUCT {\n" +
	"\t?ovr <" + RdfConstants.executeThisStr + "> \"gov.nasa.jsc.mdrules.ux.UserExperience:generateConstraintFromReplacements\" . \n" +
	"\t?ovr <" + RdfConstants.executeWithStr + "_1> ?constr . \n" +
	"\t?ovr <" + RdfConstants.executeWithStr + "_2> ?rat . \n" +
	"\t?ovr <" + RdfConstants.executeWithStr + "_3> ?repl . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?ovr <" + RdfConstants.overridesStr + "> ?constr . \n" +
	"\t?ovr <" + RdfConstants.userActionStr + "> <" + RdfConstants.userActionReplacementsProvidedStr + "> . \n" +
	"\t?ovr <" + RdfConstants.hasRationaleStr + "> ?rat . \n" +
	"\t?rat <" + RdfConstants.hasRationaleTypeStr + "> <" + RdfConstants.actualValuesWillDifferRationaleTypeStr + "> . \n" +
	"\t?rat <" + RdfConstants.hasReplacementStr + "> ?repl . \n" +
	"\n}"
	;
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: propagate an attribute over a specialization relationship
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		an entity Y with attribute Ay of type A with label L and default value V
	//		an entity X that is a specialization of Y
	//		   such that X does not have an attribute Ax of type A with label L
	//		Create:
	//		an attribute Ax for X with label L and default value V
	//
	static String propagateAttributeFromGenToSpec = nsPrefixes +
	"CONSTRUCT {\n" +
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?ax . \n" + 
	"\t?ax <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.attrStr + "> . \n" +
	"\t?ax <" + RdfConstants.rdfsLabelStr + "> ?label . \n" +
	"\t?ax <" + RdfConstants.hasTypeStr + "> ?type . \n" +
	"\t?ax <" + RdfConstants.hasDefaultValueStr + "> ?ayValue . \n" +
	"\t?ax <" + RdfConstants.isComputedUsingStr + "> ?ay . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?y a <" + RdfConstants.blockStr + "> . \n" + 
	"\t?y <" + RdfConstants.hasAttributeStr + "> ?ay . \n" + 
	"\t?ay <" + RdfConstants.rdfsLabelStr + "> ?label . \n" +
	"\t?ay <" + RdfConstants.hasTypeStr + "> ?type . \n" +
	"\t?ay <" + RdfConstants.hasDefaultValueStr + "> ?ayValue . \n" +
	"\t?y <" + RdfConstants.hasSpecializationStr + "> ?x . \n" +
	
	"\tBIND (URI(CONCAT(\"http://example.org/orionPowerDistribution/modelDb#\", STRUUID())) as ?ax) . \n" + 
	"\tFILTER (NOT EXISTS {?x <" + RdfConstants.hasAttributeStr + "> ?ax1 . ?ax1 <" + RdfConstants.hasTypeStr + "> ?type . ?ax1 <" + RdfConstants.rdfsLabelStr + "> ?label}) . \n" +
	"\n}"
	;
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: propagate an attribute value over a specialization relationship
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		an entity Y with attribute Ay of type A with label L and default value V
	//		an entity X that is a specialization of Y
	//		   such that X has an attribute Ax of type A with label L but not value
	//		Create:
	//		default value V for Ax
	//
	static String propagateAttributeValueFromGenToSpec = nsPrefixes +
	"CONSTRUCT {\n" +
	"\t?ax <" + RdfConstants.hasDefaultValueStr + "> ?ayValue . \n" +
	"\t?ax <" + RdfConstants.isComputedUsingStr + "> ?ay . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?y a <" + RdfConstants.blockStr + "> . \n" + 
	"\t?y <" + RdfConstants.hasAttributeStr + "> ?ay . \n" + 
	"\t?ay <" + RdfConstants.rdfsLabelStr + "> ?label . \n" +
	"\t?ay <" + RdfConstants.hasTypeStr + "> ?type . \n" +
	"\t?ay <" + RdfConstants.hasDefaultValueStr + "> ?ayValue . \n" +
	"\t?y <" + RdfConstants.hasSpecializationStr + "> ?x . \n" +
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?ax . \n" + 
	"\t?ax <" + RdfConstants.rdfsLabelStr + "> ?label . \n" +
	"\t?ax <" + RdfConstants.hasTypeStr + "> ?type . \n" +
	
	"\tFILTER (NOT EXISTS {?ax <" + RdfConstants.hasDefaultValueStr + "> ?axLabel}) . \n" +
	"\n}"
	;
		
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: propagate an attribute forward over a MagicDraw connection
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		an entity Y with attribute Ay of type A that has default value V
	//		an entity X with attribute Ax of type A
	//		   such that X does not have a default, computed, or regular value for Ax
	//		a connection of type C between flow ports in Y and X 
	//		Create:
	//		a computed value V for Ax
	//		statement that the computation of Ax uses Ay
	//
	static String propagateAttributeValueForwardOverFlow = nsPrefixes +			
	"CONSTRUCT {\n" +
			
	"\t?ax <" + RdfConstants.hasComputedValueStr + "> ?newComputedValue . \n" +
	"\t?newComputedValue <" + RdfConstants.withValueStr + "> ?ayValue . \n" +
	"\t?ax <" + RdfConstants.isComputedUsingStr + "> ?ay . \n" +
	"\t?x <" + RdfConstants.computeForwardWithAttrStr + "> ?ax . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?y <" + RdfConstants.computeWithAttrStr + "> ?ay . \n" +
	
	"\t?x a <" + RdfConstants.blockStr + "> . \n" + 
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?ax . \n" + 
	"\t?x <" + RdfConstants.hasFlowPortStr + "> ?fpx . \n" +
	"\t?fpx <" + RdfConstants.hasModelStr + "> ?fpxm . \n" +
	"\t?fpxm <" + RdfConstants.hasTypeStr + "> ?type . \n" +
	"\t?ax <" + RdfConstants.hasTypeStr + "> ?type . \n" +
	
	"\t?conn <" + RdfConstants.hasTargetStr + "> ?fpx . \n" +
	"\t?conn <" + RdfConstants.hasSourceStr + "> ?fpy . \n" +
	"\t?conn a <" + RdfConstants.connectorStr + "> . \n" + 

	"\t?y <" + RdfConstants.hasFlowPortStr + "> ?fpy . \n" +
	"\t?ay <" + RdfConstants.hasTypeStr + "> ?type . \n" +
	"\t?ay <" + RdfConstants.hasValueStr + "> ?ayValue . \n" +	
	"\tBIND (URI(CONCAT(\"http://example.org/\", StrUUID())) as ?newComputedValue) . \n" + 
	
	"\tFILTER ((?x != ?y) " +
		"\t\t&& (NOT EXISTS {?ax <" + RdfConstants.hasComputedValueStr + "> ?vx1}) \n" +
		"\t\t&& (NOT EXISTS {?ax <" + RdfConstants.hasDefaultValueStr + "> ?vx2}) \n" +
		"\t\t&& (NOT EXISTS {?ax <" + RdfConstants.hasValueStr + "> ?vx3}) \n" +
		"\t\t&& (NOT EXISTS { ?y <" + RdfConstants.computeBackwardWithAttrStr + "> ?ay } )) .\n" + // ?ay must not be "compute backward with this"
	"\n}"
	;
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: propagate an attribute backward over a MagicDraw connection
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		an entity Y with attribute Ay of type A that has default value V
	//		an entity X with attribute Ax of type A
	//		   such that X does not have a default, computed, or regular value for Ax
	//		a connection of type C between flow ports in Y and X 
	//		Create:
	//		a computed value V for Ax
	//		statement that the computation of Ax uses Ay
	//
	static String propagateAttributeValueBackwardOverFlow = nsPrefixes +			
	"CONSTRUCT {\n" +
			
	"\t?ax <" + RdfConstants.hasComputedValueStr + "> ?newComputedValue . \n" +
	"\t?newComputedValue <" + RdfConstants.withValueStr + "> ?ayValue . \n" +
	"\t?ax <" + RdfConstants.isComputedUsingStr + "> ?ay . \n" +
	"\t?x <" + RdfConstants.computeBackwardWithAttrStr + "> ?ax . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?y <" + RdfConstants.computeWithAttrStr + "> ?ay . \n" +
	
	"\t?x a <" + RdfConstants.blockStr + "> . \n" + 
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?ax . \n" + 
	"\t?x <" + RdfConstants.hasFlowPortStr + "> ?fpx . \n" +
	"\t?fpx <" + RdfConstants.hasModelStr + "> ?fpxm . \n" +
	"\t?fpxm <" + RdfConstants.hasTypeStr + "> ?type . \n" +
	"\t?ax <" + RdfConstants.hasTypeStr + "> ?type . \n" +
	
	"\t?conn <" + RdfConstants.hasSourceStr + "> ?fpx . \n" +
	"\t?conn <" + RdfConstants.hasTargetStr + "> ?fpy . \n" +
	"\t?conn a <" + RdfConstants.connectorStr + "> . \n" + 

	"\t?y <" + RdfConstants.hasFlowPortStr + "> ?fpy . \n" +
	"\t?ay <" + RdfConstants.hasTypeStr + "> ?type . \n" +
	"\t?ay <" + RdfConstants.hasValueStr + "> ?ayValue . \n" +	
	"\tBIND (URI(CONCAT(\"http://example.org/\", StrUUID())) as ?newComputedValue) . \n" + 

	"\tFILTER ((?x != ?y) " +
		"\t\t&& (NOT EXISTS {?ax <" + RdfConstants.hasComputedValueStr + "> ?vx1}) \n" +
		"\t\t&& (NOT EXISTS {?ax <" + RdfConstants.hasDefaultValueStr + "> ?vx2}) \n" +
		"\t\t&& (NOT EXISTS {?ax <" + RdfConstants.hasValueStr + "> ?vx3}) \n" +
		"\t\t&& (NOT EXISTS { ?y <" + RdfConstants.computeForwardWithAttrStr + "> ?ay } )) . \n" + // ?ay must not be "compute forward with this"
	"\n}"
	;
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Compute a formula that multiplies two operands
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		a formula with two operands and whose operator is multiplication
	//		a block containing:
	//		  an attribute with a value of the correct type for each operand
	//		  an attribute of the correct type for the formula's result
	//			such that one of the operand attributes has "computeWithAttribute"
	//		Create:
	//		a computed value for the attribute whose type is that of the formula's result
	//
	//		NOTE: we split this into two rules, depending on which operand attribute has 
	//		"computeWithAttribute". If we combined this via a disjunction in one rule, 
	//		we could get two matches for the same set of operands (if they both have 
	//		"computeWithAttribute"), resulting in the operation being performed twice.
	//
	static String computeMultiplicativeFormula1 = nsPrefixes +			
	"CONSTRUCT {\n" +
				
	"\t?a3 <" + RdfConstants.hasComputedValueStr + "> ?newComputedValue . \n" +
	"\t?newComputedValue <" + RdfConstants.withValueStr + "> ?val3 . \n" +
	// TODO may want to change the subject of isComputedUsing to ?newComputedValue
	// but this may involve extensive change in many rules
	"\t?a3 <" + RdfConstants.isComputedUsingStr + "> ?a1 . \n" +
	"\t?a3 <" + RdfConstants.isComputedUsingStr + "> ?a2 . \n" +
	"\t?x <" + RdfConstants.computeWithAttrStr + "> ?a3 . \n" +
	"} WHERE \n" +
	"{\n" +
	
	"\t?f a <" + RdfConstants.formulaClassStr + "> . \n" + 
	"\t?f <" + RdfConstants.hasOperatorStr + "> \"*\" . \n" + 

	"\t?f <" + RdfConstants.hasOperand1Str + "> ?o1 . \n" + 
	"\t?o1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +

	"\t?f <" + RdfConstants.hasOperand2Str + "> ?o2 . \n" + 
	"\t?o2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +

	"\t?f <" + RdfConstants.hasResultStr + "> ?r . \n" + 
	"\t?r <" + RdfConstants.hasTypeStr + "> ?type3 . \n" +

	"\t?x a <" + RdfConstants.blockStr + "> . \n" + 
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a1 . \n" + 
	"\t?a1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
	"\t?x <" + RdfConstants.computeWithAttrStr + "> ?a1 . \n" + 
	"\t?a1 <" + RdfConstants.hasValueStr + "> ?val1 . \n" +	

	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a2 . \n" + 
	"\t?a2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t?a2 <" + RdfConstants.hasValueStr + "> ?val2 . \n" +	

	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a3 . \n" + 
	"\t?a3 <" + RdfConstants.hasTypeStr + "> ?type3 . \n" +

	"\tBIND ((xsd:decimal(?val1) * xsd:decimal(?val2)) as ?val3) . \n" +  
	"\tBIND (URI(CONCAT(\"http://example.org/\", StrUUID())) as ?newComputedValue) . \n" + 
	
	"\tFILTER ((NOT EXISTS { ?a3 <" + RdfConstants.isComputedUsingStr + "> ?a1 . ?a3 <" + RdfConstants.isComputedUsingStr + "> ?a2 }))" +
	"\n}"
	;

	static String computeMultiplicativeFormula2 = nsPrefixes +			
	"CONSTRUCT {\n" +
				
	"\t?a3 <" + RdfConstants.hasComputedValueStr + "> ?newComputedValue . \n" +
	"\t?newComputedValue <" + RdfConstants.withValueStr + "> ?val3 . \n" +
	// TODO may want to change the subject of isComputedUsing to ?newComputedValue
	// but this may involve extensive change in many rules
	"\t?a3 <" + RdfConstants.isComputedUsingStr + "> ?a1 . \n" +
	"\t?a3 <" + RdfConstants.isComputedUsingStr + "> ?a2 . \n" +
	"\t?x <" + RdfConstants.computeWithAttrStr + "> ?a3 . \n" +
	"} WHERE \n" +
	"{\n" +
	
	"\t?f a <" + RdfConstants.formulaClassStr + "> . \n" + 
	"\t?f <" + RdfConstants.hasOperatorStr + "> \"*\" . \n" + 

	"\t?f <" + RdfConstants.hasOperand1Str + "> ?o1 . \n" + 
	"\t?o1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +

	"\t?f <" + RdfConstants.hasOperand2Str + "> ?o2 . \n" + 
	"\t?o2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +

	"\t?f <" + RdfConstants.hasResultStr + "> ?r . \n" + 
	"\t?r <" + RdfConstants.hasTypeStr + "> ?type3 . \n" +

	"\t?x a <" + RdfConstants.blockStr + "> . \n" + 
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a1 . \n" + 
	"\t?a1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
	"\t?a1 <" + RdfConstants.hasValueStr + "> ?val1 . \n" +	

	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a2 . \n" + 
	"\t?a2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t?x <" + RdfConstants.computeWithAttrStr + "> ?a2 . \n" + 
	"\t?a2 <" + RdfConstants.hasValueStr + "> ?val2 . \n" +	

	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a3 . \n" + 
	"\t?a3 <" + RdfConstants.hasTypeStr + "> ?type3 . \n" +

	"\tBIND ((xsd:decimal(?val1) * xsd:decimal(?val2)) as ?val3) . \n" +  
	"\tBIND (URI(CONCAT(\"http://example.org/\", StrUUID())) as ?newComputedValue) . \n" + 
	
	"\tFILTER ((NOT EXISTS { ?a3 <" + RdfConstants.isComputedUsingStr + "> ?a1 . ?a3 <" + RdfConstants.isComputedUsingStr + "> ?a2 }))" +
	"\n}"
	;

	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Compute a formula that divides two operands
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		a formula with two operands and whose operator is division
	//		a block containing:
	//		  an attribute with a value of the correct type for each operand
	//		  an attribute of the correct type for the formula's result
	//			such that one of the operand attributes has "computeWithAttribute"
	//		Create:
	//		a computed value for the attribute whose type is that of the formula's result
	//
	//		NOTE: we split this into two rules, depending on which operand attribute has 
	//		"computeWithAttribute". If we combined this via a disjunction in one rule, 
	//		we could get two matches for the same set of operands (if they both have 
	//		"computeWithAttribute"), resulting in the operation being performed twice.
	//
	static String computeDivisionFormula1 = nsPrefixes +			
			"CONSTRUCT {\n" +
			
			"\t?a3 <" + RdfConstants.hasComputedValueStr + "> ?newComputedValue . \n" +
			"\t?newComputedValue <" + RdfConstants.withValueStr + "> ?val3 . \n" +
			"\t?a3 <" + RdfConstants.isComputedUsingStr + "> ?a1 . \n" +
			"\t?a3 <" + RdfConstants.isComputedUsingStr + "> ?a2 . \n" +
			"\t?x <" + RdfConstants.computeWithAttrStr + "> ?a3 . \n" +
			"} WHERE \n" +
			"{\n" +
			
			"\t?f a <" + RdfConstants.formulaClassStr + "> . \n" + 
			"\t?f <" + RdfConstants.hasOperatorStr + "> \"/\" . \n" + 

			"\t?f <" + RdfConstants.hasOperand1Str + "> ?o1 . \n" + 
			"\t?o1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +

			"\t?f <" + RdfConstants.hasOperand2Str + "> ?o2 . \n" + 
			"\t?o2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +

			"\t?f <" + RdfConstants.hasResultStr + "> ?r . \n" + 
			"\t?r <" + RdfConstants.hasTypeStr + "> ?type3 . \n" +

			"\t?x a <" + RdfConstants.blockStr + "> . \n" + 
			"\t?x <" + RdfConstants.hasAttributeStr + "> ?a1 . \n" + 
			"\t?a1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
			"\t?x <" + RdfConstants.computeWithAttrStr + "> ?a1 . \n" + 
			"\t?a1 <" + RdfConstants.hasValueStr + "> ?val1 . \n" +	

			"\t?x <" + RdfConstants.hasAttributeStr + "> ?a2 . \n" + 
			"\t?a2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
			"\t?a2 <" + RdfConstants.hasValueStr + "> ?val2 . \n" +	

			"\t?x <" + RdfConstants.hasAttributeStr + "> ?a3 . \n" + 
			"\t?a3 <" + RdfConstants.hasTypeStr + "> ?type3 . \n" +

			
			"\tBIND ((xsd:decimal(?val1) / xsd:decimal(?val2)) as ?val3) . \n" +  
			"\tBIND (URI(CONCAT(\"http://example.org/\", StrUUID())) as ?newComputedValue) . \n" + 
			"\tFILTER ((NOT EXISTS { ?a3 <" + RdfConstants.isComputedUsingStr + "> ?a1 }) && (NOT EXISTS { ?a3 <" + RdfConstants.isComputedUsingStr + "> ?a2 }))" +
			"\n}"
			;

	static String computeDivisionFormula2 = nsPrefixes +			
			"CONSTRUCT {\n" +
			
			"\t?a3 <" + RdfConstants.hasComputedValueStr + "> ?newComputedValue . \n" +
			"\t?newComputedValue <" + RdfConstants.withValueStr + "> ?val3 . \n" +
			"\t?a3 <" + RdfConstants.isComputedUsingStr + "> ?a1 . \n" +
			"\t?a3 <" + RdfConstants.isComputedUsingStr + "> ?a2 . \n" +
			"\t?x <" + RdfConstants.computeWithAttrStr + "> ?a3 . \n" +
			"} WHERE \n" +
			"{\n" +
			
			"\t?f a <" + RdfConstants.formulaClassStr + "> . \n" + 
			"\t?f <" + RdfConstants.hasOperatorStr + "> \"/\" . \n" + 

			"\t?f <" + RdfConstants.hasOperand1Str + "> ?o1 . \n" + 
			"\t?o1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +

			"\t?f <" + RdfConstants.hasOperand2Str + "> ?o2 . \n" + 
			"\t?o2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +

			"\t?f <" + RdfConstants.hasResultStr + "> ?r . \n" + 
			"\t?r <" + RdfConstants.hasTypeStr + "> ?type3 . \n" +

			"\t?x a <" + RdfConstants.blockStr + "> . \n" + 
			"\t?x <" + RdfConstants.hasAttributeStr + "> ?a1 . \n" + 
			"\t?a1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
			"\t?a1 <" + RdfConstants.hasValueStr + "> ?val1 . \n" +	

			"\t?x <" + RdfConstants.hasAttributeStr + "> ?a2 . \n" + 
			"\t?a2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
			"\t?x <" + RdfConstants.computeWithAttrStr + "> ?a2 . \n" + 
			"\t?a2 <" + RdfConstants.hasValueStr + "> ?val2 . \n" +	

			"\t?x <" + RdfConstants.hasAttributeStr + "> ?a3 . \n" + 
			"\t?a3 <" + RdfConstants.hasTypeStr + "> ?type3 . \n" +
			
			"\tBIND ((xsd:decimal(?val1) / xsd:decimal(?val2)) as ?val3) . \n" +  
			"\tBIND (URI(CONCAT(\"http://example.org/\", StrUUID())) as ?newComputedValue) . \n" + 
			"\tFILTER ((NOT EXISTS { ?a3 <" + RdfConstants.isComputedUsingStr + "> ?a1 }) && (NOT EXISTS { ?a3 <" + RdfConstants.isComputedUsingStr + "> ?a2 }))" +
			"\n}"
			;

	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Infer hasValue from hasDefaultValue
	///////////////////////////////////////////////////////////////////////////////////////
	
	static String hasDefaultValueImpliesHasValue = nsPrefixes +			
	"CONSTRUCT {\n" +
				
	"\t?a <" + RdfConstants.hasValueStr + "> ?val . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?a <" + RdfConstants.hasDefaultValueStr + "> ?val . \n" + 
	"\tFILTER (NOT EXISTS { ?a <" + RdfConstants.hasValueStr + "> ?val }) " +
	"\n}"
	;

	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Infer hasValue from hasComputedValue for a non-additive attribute
	///////////////////////////////////////////////////////////////////////////////////////
	
	static String hasComputedValueImpliesHasValue = nsPrefixes +			
	"CONSTRUCT {\n" +
				
	"\t?a <" + RdfConstants.hasValueStr + "> ?val . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?a <" + RdfConstants.hasComputedValueStr + "> ?computedValue1 . \n" + 
	"\t?computedValue1 <" + RdfConstants.withValueStr + "> ?val . \n" + 
	"\tFILTER ( (NOT EXISTS { ?a <" + RdfConstants.hasValueStr + "> ?val1 } ) && (NOT EXISTS { ?a <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.additiveAttrStr + "> } )) . \n" +
	"\n}"
	;

	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Infer computeWithAttr from computeForwardWithAttr
	///////////////////////////////////////////////////////////////////////////////////////
	
	static String computeForwardWithAttrImpliesComputeWithAttr = nsPrefixes +			
	"CONSTRUCT {\n" +
				
	"\t?a <" + RdfConstants.computeWithAttrStr + "> ?val . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?a <" + RdfConstants.computeForwardWithAttrStr + "> ?val . \n" + 
	"\tFILTER (NOT EXISTS { ?a <" + RdfConstants.computeWithAttrStr + "> ?val }) " +
	"\n}"
	;

	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Infer computeWithAttr from computeBackwardWithAttr
	///////////////////////////////////////////////////////////////////////////////////////
	
	static String computeBackwardWithAttrImpliesComputeWithAttr = nsPrefixes +			
	"CONSTRUCT {\n" +
				
	"\t?a <" + RdfConstants.computeWithAttrStr + "> ?val . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?a <" + RdfConstants.computeBackwardWithAttrStr + "> ?val . \n" + 
	"\tFILTER (NOT EXISTS { ?a <" + RdfConstants.computeWithAttrStr + "> ?val }) " +
	"\n}"
	;

	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Infer existing of an attribute representing formula operand given other operand
	//
	// But do this only when the to-be-inferred attribute is "needed" by virtue of
	// a "start-computing-with-attr" statement (possibly referencing a different block)
	// where the needed attribute doesn't exist in the block where it is needed.
	//
	// This rule is an admittedly questionable attempt to push the attribute (and a possible
	// value for it) through from a block where it exists.
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		a "start computing with this" statement such that the specified attribute 
	//		   of type T does not exist in the specified block B
	//		a formula with two operands, with types S and T, respectively
	//		a block B2 that has an attribute of type S but not one of type T
	//
	//		optionally, a flow to the attribute of type S, such that
	//			the block B3 on the other end of the flow has an attribute of 
	//			type T with a value
	//
	//		Create:
	//		an attribute of type T in B2
	//		if the optional condition holds, give it the value that
	//			B3 has for the attribute of type T
	//
	//		The optional logic propagates a value over an "implicit"
	//		flow that is inferred from the existing flow and the
	//		sibling relationship between the two attribute types
	//		("sibling" meaning they occur as respective operands of some formula)
	//
	static String inferExistenceOfSiblingAttribute1 = nsPrefixes +			
	"CONSTRUCT {\n" +
				
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a2 . \n" + 
	"\t?a2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t?a2 <" + RdfConstants.rdfsLabelStr + "> ?label . \n" +
	"\t?a2 <" + RdfConstants.hasComputedValueStr + "> ?newComputedValue . \n" +
	"\t?newComputedValue <" + RdfConstants.withValueStr + "> ?a2yValue . \n" +
	"\t?a2 <" + RdfConstants.isComputedUsingStr + "> ?a2y . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?goal <" + RdfConstants.startComputationWithBlockStr + "> ?blockName . \n" + 
	"\t?goal <" + RdfConstants.startComputationWithAttrStr + "> ?attrName . \n" + 
	"\t?block <" + RdfConstants.rdfsLabelStr + "> ?blockName . \n" +

	"\t?f a <" + RdfConstants.formulaClassStr + "> . \n" + 
	"\t?f <" + RdfConstants.hasOperand1Str + "> ?o1 . \n" + 
	"\t?o1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +

	"\t?f <" + RdfConstants.hasOperand2Str + "> ?o2 . \n" + 
	"\t?o2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t?o2 <" + RdfConstants.rdfsLabelStr + "> ?label . \n" +

	"\t?x a <" + RdfConstants.blockStr + "> . \n" + 
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a1 . \n" + 
	"\t?a1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
	"\t?x <" + RdfConstants.rdfsLabelStr + "> ?xLabel . \n" +
	
	// TODO these two optional blocks can now be combined into one (use hasEndPort)
	// optional value that can be inferred over a flow to the sibling attribute
	"\tOPTIONAL { \n" +
		"\t\t?x <" + RdfConstants.hasFlowPortStr + "> ?fpx . \n" +
		"\t\t?fpx <" + RdfConstants.hasModelStr + "> ?fpxm . \n" +
		"\t\t?fpxm <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +

		"\t\t?conn <" + RdfConstants.hasTargetStr + "> ?fpx . \n" +
		"\t\t?conn <" + RdfConstants.hasSourceStr + "> ?fpy . \n" +
		"\t\t?conn a <" + RdfConstants.connectorStr + "> . \n" + 

		"\t\t?y <" + RdfConstants.hasFlowPortStr + "> ?fpy . \n" +
		"\t\t?y a <" + RdfConstants.blockStr + "> . \n" + 
		"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a1y . \n" + 
		"\t\t?a1y <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
		"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a2y . \n" + 
		"\t\t?a2y <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
		"\t\t?a2y <" + RdfConstants.hasValueStr + "> ?a2yValue . \n" +	
	"\t}" +

	"\tOPTIONAL { \n" +
		"\t\t?x <" + RdfConstants.hasFlowPortStr + "> ?fpx . \n" +
		"\t\t?fpx <" + RdfConstants.hasModelStr + "> ?fpxm . \n" +
		"\t\t?fpxm <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +

		"\t\t?conn <" + RdfConstants.hasSourceStr + "> ?fpx . \n" +
		"\t\t?conn <" + RdfConstants.hasTargetStr + "> ?fpy . \n" +
		"\t\t?conn a <" + RdfConstants.connectorStr + "> . \n" + 

		"\t\t?y <" + RdfConstants.hasFlowPortStr + "> ?fpy . \n" +
		"\t\t?y a <" + RdfConstants.blockStr + "> . \n" + 
		"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a1y . \n" + 
		"\t\t?a1y <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
		"\t\t?a1y <" + RdfConstants.hasValueStr + "> ?a1yValue . \n" +	
		"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a2y . \n" + 
		"\t\t?a2y <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t}" +

	// Can't use UUID() because there may be >1 formula from which the sibling is being created,
	// and we need them all to result in the same URI for the new attribute
	"\tBIND (URI(CONCAT(?xLabel, \"_\", ?type2, \"_\", STR(NOW()))) as ?a2) . \n" + 
	"\tBIND (URI(CONCAT(\"http://example.org/\", StrUUID())) as ?newComputedValue) . \n" + 
	"\tFILTER ((NOT EXISTS { ?block <" + RdfConstants.hasAttributeStr + "> ?attr . ?attr <" + RdfConstants.rdfsLabelStr + "> ?attrName } ) && \n" +
		"\t\t (NOT EXISTS { ?x <" + RdfConstants.hasAttributeStr + "> ?a3 . ?a3 <" + RdfConstants.hasTypeStr + "> ?type2  })) \n" +

	"\n}"
	;
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Infer existing of an attribute representing formula operand given other operand
	//
	// But do this only when the to-be-inferred attribute is "needed" by virtue of
	// a "start-computing-with-attr" statement (possibly referencing a different block)
	// where the needed attribute doesn't exist in the block where it is needed.
	//
	// This rule is an admittedly questionable attempt to push the attribute (and a possible
	// value for it) through from a block where it exists.
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		a "compute this" statement with string values (i.e., an initial compute-this
	//			statement) such that the specified attribute of type T does not exist in the 
	//		    specified block B
	//		a formula with two operands, with types T and S, respectively
	//		a block B2 that has an attribute of type S but not one of type T
	//
	//		optionally, a flow to the attribute of type S, such that
	//			the block B3 on the other end of the flow has an attribute of 
	//			type T with a value
	//
	//		Create:
	//		an attribute of type T in B2
	//		if the optional condition holds, give it the value that
	//			B3 has for the attribute of type T
	//
	//		The optional logic propagates a value over an "implicit"
	//		flow that is inferred from the existing flow and the
	//		sibling relationship between the two attribute types
	//		("sibling" meaning they occur as respective operands of some formula)
	//
	static String inferExistenceOfSiblingAttribute2 = nsPrefixes +			
	"CONSTRUCT {\n" +
				
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a1 . \n" + 
	"\t?a1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
	"\t?a1 <" + RdfConstants.rdfsLabelStr + "> ?label . \n" +
	"\t?a1 <" + RdfConstants.hasComputedValueStr + "> ?newComputedValue . \n" +
	"\t?newComputedValue <" + RdfConstants.withValueStr + "> ?a1yValue . \n" +
	"\t?a1 <" + RdfConstants.isComputedUsingStr + "> ?a1y . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?goal <" + RdfConstants.startComputationWithBlockStr + "> ?blockName . \n" + 
	"\t?goal <" + RdfConstants.startComputationWithAttrStr + "> ?attrName . \n" + 
	"\t?block <" + RdfConstants.rdfsLabelStr + "> ?blockName . \n" +

	"\t?f a <" + RdfConstants.formulaClassStr + "> . \n" + 
	"\t?f <" + RdfConstants.hasOperand1Str + "> ?o1 . \n" + 
	"\t?o1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
	"\t?o1 <" + RdfConstants.rdfsLabelStr + "> ?label . \n" +

	"\t?f <" + RdfConstants.hasOperand2Str + "> ?o2 . \n" + 
	"\t?o2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +

	"\t?x a <" + RdfConstants.blockStr + "> . \n" + 
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a2 . \n" + 
	"\t?a2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t?x <" + RdfConstants.rdfsLabelStr + "> ?xLabel . \n" +

	// TODO these two optional blocks can now be combined into one (use hasEndPort)
	// optional value that can be inferred over a flow to the sibling attribute
	"\tOPTIONAL { \n" +
		"\t\t?x <" + RdfConstants.hasFlowPortStr + "> ?fpx . \n" +
		"\t\t?fpx <" + RdfConstants.hasModelStr + "> ?fpxm . \n" +
		"\t\t?fpxm <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +

		"\t\t?conn <" + RdfConstants.hasTargetStr + "> ?fpx . \n" +
		"\t\t?conn <" + RdfConstants.hasSourceStr + "> ?fpy . \n" +
		"\t\t?conn a <" + RdfConstants.connectorStr + "> . \n" + 

		"\t\t?y <" + RdfConstants.hasFlowPortStr + "> ?fpy . \n" +
		"\t\t?y a <" + RdfConstants.blockStr + "> . \n" + 
		"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a1y . \n" + 
		"\t\t?a1y <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
		"\t\t?a1y <" + RdfConstants.hasValueStr + "> ?a1yValue . \n" +	
		"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a2y . \n" + 
		"\t\t?a2y <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t}" +

	"\tOPTIONAL { \n" +
		"\t\t?x <" + RdfConstants.hasFlowPortStr + "> ?fpx . \n" +
		"\t\t?fpx <" + RdfConstants.hasModelStr + "> ?fpxm . \n" +
		"\t\t?fpxm <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +

		"\t\t?conn <" + RdfConstants.hasSourceStr + "> ?fpx . \n" +
		"\t\t?conn <" + RdfConstants.hasTargetStr + "> ?fpy . \n" +
		"\t\t?conn a <" + RdfConstants.connectorStr + "> . \n" + 

		"\t\t?y <" + RdfConstants.hasFlowPortStr + "> ?fpy . \n" +
		"\t\t?y a <" + RdfConstants.blockStr + "> . \n" + 
		"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a1y . \n" + 
		"\t\t?a1y <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
		"\t\t?a1y <" + RdfConstants.hasValueStr + "> ?a1yValue . \n" +	
		"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a2y . \n" + 
		"\t\t?a2y <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t}" +

	// Can't use UUID() because there may be >1 formula from which the sibling is being created,
	// and we need them all to result in the same URI for the new attribute
	"\tBIND (URI(CONCAT(?xLabel, \"_\", ?type1, \"_\", STR(NOW()))) as ?a1) . \n" + 
	"\tBIND (URI(CONCAT(\"http://example.org/\", StrUUID())) as ?newComputedValue) . \n" + 
	"\tFILTER ((NOT EXISTS { ?block <" + RdfConstants.hasAttributeStr + "> ?attr . ?attr <" + RdfConstants.rdfsLabelStr + "> ?attrName } ) && \n" +
	"\t\t (NOT EXISTS { ?x <" + RdfConstants.hasAttributeStr + "> ?a3 . ?a3 <" + RdfConstants.hasTypeStr + "> ?type1  })) \n" +
	"\n}"
	;
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Infer value of an attribute representing formula operand given other operand
	//
	// But do this only when the to-be-inferred attribute is "needed" by virtue of
	// a "start-computing-with-attr" statement (possibly referencing a different block)
	// where the needed attribute doesn't exist in the block where it is needed.
	//
	// This rule is an admittedly questionable attempt to push the attribute 
	// value through from a block where it exists.
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		a "start computing with this" statement such that the specified attribute 
	//		   of type T exists but with no value in the specified block B
	//		a formula with two operands, with types S and T, respectively
	//		a block B2 that has an attribute of type S, and one of type T but with no value
	//		a flow to the attribute of type S, such that
	//			the block B3 on the other end of the flow has an attribute of 
	//			type T with a value
	//
	//		Create:
	//		give the attribute of type T in B2 the value that
	//			B3 has for the attribute of type T
	//
	//		This propagates a value over an "implicit"
	//		flow that is inferred from the existing flow and the
	//		sibling relationship between the two attribute types
	//		("sibling" meaning they occur as respective operands of some formula)
	//
	static String inferSiblingAttributeValue1 = nsPrefixes +			
	"CONSTRUCT {\n" +
				
	"\t?a2x <" + RdfConstants.hasComputedValueStr + "> ?newComputedValue . \n" +
	"\t?newComputedValue <" + RdfConstants.withValueStr + "> ?a2yValue . \n" +
	"\t?a2x <" + RdfConstants.isComputedUsingStr + "> ?a2y . \n" +
	"} WHERE \n" +
	"{\n" +
	"\t?goal <" + RdfConstants.startComputationWithBlockStr + "> ?blockName . \n" + 
	"\t?goal <" + RdfConstants.startComputationWithAttrStr + "> ?attrName . \n" + 
	"\t?block <" + RdfConstants.rdfsLabelStr + "> ?blockName . \n" +
	"\t?block <" + RdfConstants.hasAttributeStr + "> ?attr . \n" +
	"\t?attr <" + RdfConstants.rdfsLabelStr + "> ?attrName . \n" +

	"\t?f a <" + RdfConstants.formulaClassStr + "> . \n" + 
	"\t?f <" + RdfConstants.hasOperand1Str + "> ?o1 . \n" + 
	"\t?o1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +

	"\t?f <" + RdfConstants.hasOperand2Str + "> ?o2 . \n" + 
	"\t?o2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t?o2 <" + RdfConstants.rdfsLabelStr + "> ?label . \n" +

	"\t?x a <" + RdfConstants.blockStr + "> . \n" + 
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a1x . \n" + 
	"\t?a1x <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
	"\t?x <" + RdfConstants.rdfsLabelStr + "> ?xLabel . \n" +
	"\t\t?x <" + RdfConstants.hasAttributeStr + "> ?a2x . \n" + 
	"\t\t?a2x <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	
	"\t\t?x <" + RdfConstants.hasFlowPortStr + "> ?fpx . \n" +
	"\t\t?fpx <" + RdfConstants.hasModelStr + "> ?fpxm . \n" +
	"\t\t?fpxm <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
	"\t\t?conn a <" + RdfConstants.connectorStr + "> . \n" + 
	"\t\t?conn <" + RdfConstants.hasEndPortStr + "> ?fpx . \n" +
	"\t\t?conn <" + RdfConstants.hasEndPortStr + "> ?fpy . \n" +
	"\t\t?y <" + RdfConstants.hasFlowPortStr + "> ?fpy . \n" +
	
	"\t\t?y a <" + RdfConstants.blockStr + "> . \n" + 
	"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a1y . \n" + 
	"\t\t?a1y <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
	"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a2y . \n" + 
	"\t\t?a2y <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t\t?a2y <" + RdfConstants.hasValueStr + "> ?a2yValue . \n" +	
	
	// TODO we can delete these two optional blocks
	// choose direction of the flow
	"\tOPTIONAL { \n" +
		"\t\t?conn <" + RdfConstants.hasTargetStr + "> ?fpx . \n" +
		"\t\t?conn <" + RdfConstants.hasSourceStr + "> ?fpy . \n" +
	"\t}" +

	"\tOPTIONAL { \n" +
		"\t\t?conn <" + RdfConstants.hasSourceStr + "> ?fpx . \n" +
		"\t\t?conn <" + RdfConstants.hasTargetStr + "> ?fpy . \n" +
	"\t}" +

	"\tBIND (URI(CONCAT(\"http://example.org/\", StrUUID())) as ?newComputedValue) . \n" + 
	"\tFILTER ((NOT EXISTS { ?attr <" + RdfConstants.hasValueStr + "> ?neededValue } ) && \n" +
		"\t\t (NOT EXISTS { ?a2x <" + RdfConstants.hasValueStr + "> ?v })) \n" +

	"\n}"
	;
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Infer value of an attribute representing formula operand given other operand
	//
	// But do this only when the to-be-inferred attribute is "needed" by virtue of
	// a "start-computing-with-attr" statement (possibly referencing a different block)
	// where the needed attribute doesn't exist in the block where it is needed.
	//
	// This rule is an admittedly questionable attempt to push the attribute 
	// value through from a block where it exists.
	///////////////////////////////////////////////////////////////////////////////////////

	//		Given:
	//		a "start computing with this" statement such that the specified attribute 
	//		   of type T exists but with no value in the specified block B
	//		a formula with two operands, with types T and S, respectively
	//		a block B2 that has an attribute of type S, and one of type T but with no value
	//		a flow to the attribute of type S, such that
	//			the block B3 on the other end of the flow has an attribute of 
	//			type T with a value
	//
	//		Create:
	//		give the attribute of type T in B2 the value that
	//			B3 has for the attribute of type T
	//
	//		This propagates a value over an "implicit"
	//		flow that is inferred from the existing flow and the
	//		sibling relationship between the two attribute types
	//		("sibling" meaning they occur as respective operands of some formula)
	//
	static String inferSiblingAttributeValue2 = nsPrefixes +			
	"CONSTRUCT {\n" +
				
	"\t?a1x <" + RdfConstants.hasComputedValueStr + "> ?newComputedValue . \n" +
	"\t?newComputedValue <" + RdfConstants.withValueStr + "> ?a1yValue . \n" +
	"\t?a1x <" + RdfConstants.isComputedUsingStr + "> ?a1y . \n" +

	"} WHERE \n" +
	"{\n" +
	"\t?goal <" + RdfConstants.startComputationWithBlockStr + "> ?blockName . \n" + 
	"\t?goal <" + RdfConstants.startComputationWithAttrStr + "> ?attrName . \n" + 
	"\t?block <" + RdfConstants.rdfsLabelStr + "> ?blockName . \n" +
	"\t?block <" + RdfConstants.hasAttributeStr + "> ?attr . \n" +
	"\t?attr <" + RdfConstants.rdfsLabelStr + "> ?attrName . \n" +
	
	"\t?f a <" + RdfConstants.formulaClassStr + "> . \n" + 
	"\t?f <" + RdfConstants.hasOperand1Str + "> ?o1 . \n" + 
	"\t?o1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +

	"\t?f <" + RdfConstants.hasOperand2Str + "> ?o2 . \n" + 
	"\t?o2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t?o2 <" + RdfConstants.rdfsLabelStr + "> ?label . \n" +

	"\t?x a <" + RdfConstants.blockStr + "> . \n" + 
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a1x . \n" + 
	"\t?a1x <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
	"\t?x <" + RdfConstants.rdfsLabelStr + "> ?xLabel . \n" +
	"\t\t?x <" + RdfConstants.hasAttributeStr + "> ?a2x . \n" + 
	"\t\t?a2x <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	
	"\t\t?x <" + RdfConstants.hasFlowPortStr + "> ?fpx . \n" +
	"\t\t?fpx <" + RdfConstants.hasModelStr + "> ?fpxm . \n" +
	"\t\t?fpxm <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	"\t\t?conn a <" + RdfConstants.connectorStr + "> . \n" + 
	"\t\t?conn <" + RdfConstants.hasEndPortStr + "> ?fpx . \n" +
	"\t\t?conn <" + RdfConstants.hasEndPortStr + "> ?fpy . \n" +
	"\t\t?y <" + RdfConstants.hasFlowPortStr + "> ?fpy . \n" +
	
	"\t\t?y a <" + RdfConstants.blockStr + "> . \n" + 
	"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a1y . \n" + 
	"\t\t?a1y <" + RdfConstants.hasTypeStr + "> ?type1 . \n" +
	"\t\t?a1y <" + RdfConstants.hasValueStr + "> ?a1yValue . \n" +	
	"\t\t?y <" + RdfConstants.hasAttributeStr + "> ?a2y . \n" + 
	"\t\t?a2y <" + RdfConstants.hasTypeStr + "> ?type2 . \n" +
	
	// TODO we can delete these two optional blocks
	// choose direction of the flow
	"\tOPTIONAL { \n" +
		"\t\t?conn <" + RdfConstants.hasTargetStr + "> ?fpx . \n" +
		"\t\t?conn <" + RdfConstants.hasSourceStr + "> ?fpy . \n" +
	"\t}" +

	"\tOPTIONAL { \n" +
		"\t\t?conn <" + RdfConstants.hasSourceStr + "> ?fpx . \n" +
		"\t\t?conn <" + RdfConstants.hasTargetStr + "> ?fpy . \n" +
	"\t}" +

	"\tBIND (URI(CONCAT(\"http://example.org/\", StrUUID())) as ?newComputedValue) . \n" + 
	"\tFILTER ((NOT EXISTS { ?attr <" + RdfConstants.hasValueStr + "> ?neededValue } ) && \n" +
		"\t\t (NOT EXISTS { ?a1x <" + RdfConstants.hasValueStr + "> ?v })) \n" +

	"\n}"
	;
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Get URI for blocks and attributes to compute with
	///////////////////////////////////////////////////////////////////////////////////////
	
	static String getInitialBlocksAndAttrsToComputeWith = nsPrefixes +			
	"CONSTRUCT {\n" +
	"\t?block <" + RdfConstants.computeWithAttrStr + "> ?attr . \n" + 
	"} WHERE \n" +
	"{\n" +
	"\t?goal <" + RdfConstants.startComputationWithBlockStr + "> ?blockName . \n" + 
	"\t?goal <" + RdfConstants.startComputationWithAttrStr + "> ?attrName . \n" + 
	"\t?block <" + RdfConstants.rdfsLabelStr + "> ?blockName . \n" +
	"\t?block <" + RdfConstants.hasAttributeStr + "> ?attr . \n" +
	"\t?attr <" + RdfConstants.rdfsLabelStr + "> ?attrName . \n" +
	"\tFILTER (NOT EXISTS { ?block <" + RdfConstants.computeWithAttrStr + "> ?attr }) . \n" +
	"\n}"
	;
	

	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: add the values of an additive attribute
	//
	///////////////////////////////////////////////////////////////////////////////////////
	
	static String addValuesOfAdditiveAttribute = nsPrefixes +		
	"CONSTRUCT {\n" +
	"\t?ax <" + RdfConstants.hasValueStr + "> ?sum . \n" + 
	"} WHERE \n" +
	"{\n" +
	
	"\tFILTER (NOT EXISTS {?ax <" + RdfConstants.hasValueStr + "> ?sum1} ) .\n" +
	"\t{\n" +
	"\t\tSELECT ?ax (SUM(?valNum) as ?sum)  \n" +
	"\t\tWHERE \n" +
	"\t\t{ \n" +	
	
		"\t\t\t?ax <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.additiveAttrStr + "> . \n" + 
		"\t\t\t?ax <" + RdfConstants.hasComputedValueStr + "> ?computedVal . \n" + 
		"\t\t\t?computedVal <" + RdfConstants.withValueStr + "> ?val . \n" + 
		"\t\t\tBIND (xsd:decimal(?val) as ?valNum) . \n" +
	"\t\t} \n" +
	"\t\tGROUP BY ?ax \n" +
	"\t} \n" +
	"\n}"
	;

	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: flag all instances of Current attribute as additive
	//
	// TODO This is a hack, pending better integration with MagicDraw and with QUDT ontology
	///////////////////////////////////////////////////////////////////////////////////////
	
	static String currentIsAdditive = nsPrefixes +		
	"CONSTRUCT {\n" +
	"\t?ax <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.additiveAttrStr + "> . \n" + 
	"} WHERE \n" +
	"{\n" +
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?ax . \n" + 
	"\t?ax <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.attrStr + "> . \n" + 
	"\t?ax <" + RdfConstants.hasTypeStr + "> \"A\" . \n" + 
	"\tFILTER ( NOT EXISTS {?ax <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.additiveAttrStr + "> }) . \n" +
	"\n}"
	;

	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Create a constraint from a set of default values corresponding to the
	// operands and result of a formula.
	///////////////////////////////////////////////////////////////////////////////////////
	
	// Given:
	// a set of attributes with default values
	// a formula such that:
	//		the formula's result has the same type as one of the attributes
	//		the formula's operands respectively have the same type as the other attributes 
	//		the first operand attribute belongs to the same block as the result
	//		the second operand attribute belongs to a block with a flow from or to the result block
	// Create:
	// a computation representing the formula
	// a constraint on the "result" attribute specifying:
	//		the newly created computation
	// 		max equal to the default value for the "result" attribute
	//
	// NOTE: this rule does not create the (input) attribute sets for the computation
	// because that has to be done iteratively, so we use a subsequent rule for that.
	//
	// TODO This is a hack -- especially since the attributes need not belong to the
	// same block -- pending better integration with contraints in MagicDraw.
	//
	// The rule arbitrarily requires that the first operand belong to the same
	// block as the result, but not the second.
	//
	// Also, we use the default value as a max, rather arbitrarily.
	//
	///////////////////////////////////////////////////////////////////////////////////////
	
	static String constraintFromDefaultValues = nsPrefixes +		
	"CONSTRUCT {\n" +
	"\t?constr <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.constraintClassStr + "> . \n" + 
	"\t?constr <" + RdfConstants.rdfsLabelStr + "> ?constrName . \n" + 
	"\t?comput <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.computationClassStr + "> . \n" + 
	"\t?comput <" + RdfConstants.rdfsLabelStr + "> ?computName . \n" + 
	"\t?constr <" + RdfConstants.hasComputationStr + "> ?comput . \n" + 
	"\t?constr <" + RdfConstants.hasPriorityStr + "> \"1\"^^xsd:integer . \n" + 
	
	// TODO This stmt is just to simplify the FILTER, rather than having to include
	// the whole reified version within the FILTER - it too is a hack
	"\t?constr <" + RdfConstants.hasAttributeStr + "> ?a3 . \n" + 
	
	"\t?constr <" + RdfConstants.hasStmtStr + "> ?stmtB . \n" + 
	"\t?stmtB <" + RdfConstants.rdfSubjStr + "> ?x1 . \n" + 
	"\t?stmtB <" + RdfConstants.rdfPredStr + "> <" + RdfConstants.hasAttributeStr + "> . \n" + 
	"\t?stmtB <" + RdfConstants.rdfObjStr + "> ?a3 . \n" + 
	
	"\t?constr <" + RdfConstants.hasStmtStr + "> ?stmtD . \n" + 
	"\t?stmtD <" + RdfConstants.rdfSubjStr + "> ?a3 . \n" + 
	"\t?stmtD <" + RdfConstants.rdfPredStr + "> <" + RdfConstants.hasMaxValueStr + "> . \n" + 
	"\t?stmtD <" + RdfConstants.rdfObjStr + "> ?val3 . \n" + 

	"\t?comput <" + RdfConstants.hasComputedAttributeStr + "> ?a3 . \n" + 
	"\t?comput <" + RdfConstants.hasEntityStr + "> ?x3 . \n" + 
	
	"} WHERE \n" +
	"{\n" +
	"\t?f <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.formulaClassStr + "> . \n" + 
	"\t?f <" + RdfConstants.rdfsLabelStr + "> ?fName . \n" + 
	"\t?f <" + RdfConstants.hasOperand1Str + "> ?operand1 . \n" + 
	"\t?operand1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" + 
	"\t?f <" + RdfConstants.hasOperand2Str + "> ?operand2 . \n" + 
	"\t?operand2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" + 
	"\t?f <" + RdfConstants.hasResultStr + "> ?result . \n" + 
	"\t?result <" + RdfConstants.hasTypeStr + "> ?type3 . \n" + 
	"\t?x1 <" + RdfConstants.hasAttributeStr + "> ?a1 . \n" + 
	"\t?a1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" + 
	"\t?a1 <" + RdfConstants.hasDefaultValueStr + "> ?val1 . \n" + 
	"\t?x2 <" + RdfConstants.hasAttributeStr + "> ?a2 . \n" + 
	"\t?a2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" + 
	"\t?a2 <" + RdfConstants.hasDefaultValueStr + "> ?val2 . \n" + 
	"\t?x1 <" + RdfConstants.hasAttributeStr + "> ?a3 . \n" + 
	"\t?a3 <" + RdfConstants.hasTypeStr + "> ?type3 . \n" + 
	"\t?a3 <" + RdfConstants.rdfsLabelStr + "> ?a3Name . \n" + 
	"\t?a3 <" + RdfConstants.hasDefaultValueStr + "> ?val3 . \n" + 
	"\t?x1 <" + RdfConstants.rdfsLabelStr + "> ?x1Label . \n" + 
	
	// x1 and x2 are connected by a flow
	"\t?conn a <" + RdfConstants.connectorStr + "> . \n" + 
	"\t?x1 <" + RdfConstants.hasFlowPortStr + "> ?fpx1 . \n" +
	"\t?conn <" + RdfConstants.hasEndPortStr + "> ?fpx1 . \n" +
	"\t?x2 <" + RdfConstants.hasFlowPortStr + "> ?fpx2 . \n" +
	"\t?conn <" + RdfConstants.hasEndPortStr + "> ?fpx2 . \n" +
	
	// Names may contain non-RDF-URI acceptable characters, so convert to a hash code
	"\tBIND(STR(SHA256(?a3Name)) as ?a3Hash) . \n" +
	"\tBIND(STR(SHA256(?fName)) as ?fHash) . \n" +
	
	"\tBIND(CONCAT(\"http://example.org/orionPowerDistribution/modelDb#\", \"Constraint_\", ?x1Label, STR(NOW()), \"_\", ?fHash, \"_\", ?a3Hash)  as ?constrName) . \n" +
	"\tBIND(URI(?constrName)  as ?constr) . \n" +

	"\tBIND(CONCAT(\"http://example.org/orionPowerDistribution/modelDb#\", \"Computation_\", ?x1Label, STR(NOW()), \"_\", ?fHash, \"_\", ?a3Hash )  as ?computName) . \n" +
	"\tBIND(URI(?computName)  as ?comput) . \n" +
	
	"\tBIND(URI(CONCAT(\"http://example.org/orionPowerDistribution/modelDb#\", \"StmtB_\", STR(NOW()), \"_\", ?fHash, \"_\", ?a3Hash ))  as ?stmtB) . \n" +
	
	"\tBIND(URI(CONCAT(\"http://example.org/orionPowerDistribution/modelDb#\", \"StmtD_\", STR(NOW()), \"_\", ?fHash, \"_\", ?a3Hash ))  as ?stmtD) . \n" +
	
	// require that the result attribute does not already have an existing constraint (hack to prevent repeated execution)
	"\tFILTER (NOT EXISTS { ?constr1 <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.constraintClassStr + "> . ?constr1 <" + RdfConstants.hasAttributeStr + "> ?a3 } ) . \n" +
	"\n}"
	;

	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Create a constraint from a set of default values corresponding to the
	// operands and result of a formula.
	///////////////////////////////////////////////////////////////////////////////////////
	
	// Given:
	// a set of attributes with default values
	// a formula such that:
	//		the formula's result has the same type as one of the attributes
	//		the formula's operands respectively have the same type as the other attributes 
	//		the first operand attribute belongs to the same block as the result
	//		the second operand attribute belongs to a block with a flow from or to the result block
	// 		a constraint on the "result" attribute specifying:
	// 			a computation representing the formula, but with no input attribute sets yet
	// Create:
	//		the attribute sets for the computation
	//
	// TODO This, along with constraintFromDefaultValues is a hack,
	// pending better integration with contraints in MagicDraw.
	//
	///////////////////////////////////////////////////////////////////////////////////////
	
	static String constraintComputationFromFormula = nsPrefixes +		
	"CONSTRUCT {\n" +
	"\t?comput <" + RdfConstants.usesAttributeSetStr + "> ?attrSet . \n" + 
	"\t?attrSet <" + RdfConstants.hasMemberStr + "> ?a1 . \n" + 
	"\t?attrSet <" + RdfConstants.hasMemberStr + "> ?a2 . \n" + 
	
	"} WHERE \n" +
	"{\n" +
	"\t?f <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.formulaClassStr + "> . \n" + 
	"\t?f <" + RdfConstants.rdfsLabelStr + "> ?fName . \n" + 
	"\t?f <" + RdfConstants.hasOperand1Str + "> ?operand1 . \n" + 
	"\t?operand1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" + 
	"\t?f <" + RdfConstants.hasOperand2Str + "> ?operand2 . \n" + 
	"\t?operand2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" + 
	"\t?f <" + RdfConstants.hasResultStr + "> ?result . \n" + 
	"\t?result <" + RdfConstants.hasTypeStr + "> ?type3 . \n" + 
	"\t?x1 <" + RdfConstants.hasAttributeStr + "> ?a1 . \n" + 
	"\t?a1 <" + RdfConstants.hasTypeStr + "> ?type1 . \n" + 
	"\t?a1 <" + RdfConstants.hasDefaultValueStr + "> ?val1 . \n" + 
	"\t?x2 <" + RdfConstants.hasAttributeStr + "> ?a2 . \n" + 
	"\t?a2 <" + RdfConstants.hasTypeStr + "> ?type2 . \n" + 
	"\t?a2 <" + RdfConstants.hasDefaultValueStr + "> ?val2 . \n" + 
	"\t?x1 <" + RdfConstants.hasAttributeStr + "> ?a3 . \n" + 
	"\t?a3 <" + RdfConstants.hasTypeStr + "> ?type3 . \n" + 
	"\t?a3 <" + RdfConstants.rdfsLabelStr + "> ?a3Name . \n" + 
	"\t?a3 <" + RdfConstants.hasDefaultValueStr + "> ?val3 . \n" + 
	
	// x1 and x2 are connected by a flow
	"\t?conn a <" + RdfConstants.connectorStr + "> . \n" + 
	"\t?x1 <" + RdfConstants.hasFlowPortStr + "> ?fpx1 . \n" +
	"\t?conn <" + RdfConstants.hasEndPortStr + "> ?fpx1 . \n" +
	"\t?x2 <" + RdfConstants.hasFlowPortStr + "> ?fpx2 . \n" +
	"\t?conn <" + RdfConstants.hasEndPortStr + "> ?fpx2 . \n" +
	
	// Constraint exists already:
	"\t?constr <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.constraintClassStr + "> . \n" + 
	"\t?constr <" + RdfConstants.hasComputationStr + "> ?comput . \n" + 
	"\t?comput <" + RdfConstants.hasComputedAttributeStr + "> ?a3 . \n" + 

	"\tBIND(URI(CONCAT(\"http://example.org/orionPowerDistribution/modelDb#\", STRUUID()))  as ?attrSet) . \n" +
	
	// require that the result attribute does not already have an existing constraint (hack to prevent repeated execution)
	"\tFILTER (NOT EXISTS { ?comput <" + RdfConstants.usesAttributeSetUri + "> ?attrSetX } ) . \n" +
	"\n}"
	;
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Given a constraint of highest priority, with a computation that has its
	// input attribute sets defined, generate "startComputationWith" statements for the
	// attributes in the input attribute sets
	///////////////////////////////////////////////////////////////////////////////////////
	
	static String startComputationFromConstraint = nsPrefixes +		
	"CONSTRUCT {\n" +
	"\t?goal <" + RdfConstants.startComputationWithBlockStr + "> ?xLabel . \n" + 
	"\t?goal <" + RdfConstants.startComputationWithAttrStr + "> ?aLabel . \n" + 
	
	"} WHERE \n" +
	"{\n" +
	
	// Constraint with computation
	"\t?constr <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.constraintClassStr + "> . \n" + 
	"\t?constr <" + RdfConstants.hasComputationStr + "> ?comput . \n" + 
	"\t?constr <" + RdfConstants.rdfsLabelStr + "> ?constrName . \n" + 
	
	// Computation with input attribute set
	"\t?comput <" + RdfConstants.usesAttributeSetStr + "> ?attrSet . \n" + 
	"\t?attrSet <" + RdfConstants.hasMemberStr + "> ?a . \n" + 
	"\t?a <" + RdfConstants.rdfsLabelStr + "> ?aLabel . \n" + 
	
	// Attribute with attribute set belongs to block
	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a . \n" + 
	"\t?x <" + RdfConstants.rdfsLabelStr + "> ?xLabel . \n" + 

	"\tBIND(URI(CONCAT(\"http://example.org/GoalForConstraint_\", ?constrName))  as ?goal) . \n" +
	
	// require that the result attribute does not already have an existing constraint (hack to prevent repeated execution)
	"\tFILTER (NOT EXISTS { ?goal1 <" + RdfConstants.startComputationWithBlockStr + "> ?xLabel . ?goal1 <" + RdfConstants.startComputationWithAttrStr + "> ?aLabel } ) . \n" +
	"\n}"
	;
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Rule: Discover a constraint violation
	///////////////////////////////////////////////////////////////////////////////////////
	
	
	static String constraintViolationFound = nsPrefixes +			
	"CONSTRUCT {\n" +
				
	"\t?vio <" + RdfConstants.executeThisStr + "> \"gov.nasa.jsc.mdrules.ux.UserExperience:constraintViolated\" . \n" +
	"\t?vio <" + RdfConstants.executeWithStr + "_1> ?constr . \n" +
	"\t?vio <" + RdfConstants.executeWithStr + "_2> ?x . \n" +
	"\t?vio <" + RdfConstants.executeWithStr + "_3> ?a . \n" +
	"\t?vio <" + RdfConstants.executeWithStr + "_4> \"0\"^^xsd:decimal . \n" +  // hack - min not part of demo
	"\t?vio <" + RdfConstants.executeWithStr + "_5> ?max . \n" +
	"\t?vio <" + RdfConstants.executeWithStr + "_6> ?actualVal . \n" +
	"\t?vio <" + RdfConstants.executeWithStr + "_7> ?comput . \n" +
	"\t?constr <" + RdfConstants.violationBeingProcessedStr + "> ?vio . \n" +
	"} WHERE \n" +
	"{\n" +
	
	"\t?constr <" + RdfConstants.rdfTypeStr + "> <" + RdfConstants.constraintClassStr + "> . \n" + 
	"\t?constr <" + RdfConstants.rdfsLabelStr + "> ?constrName . \n" + 
	"\t?constr <" + RdfConstants.hasComputationStr + "> ?comput . \n" + 
	"\t?comput <" + RdfConstants.hasComputedAttributeStr + "> ?a . \n" + 

	"\t?constr <" + RdfConstants.hasStmtStr + "> ?stmtD . \n" + 
	"\t?stmtD <" + RdfConstants.rdfSubjStr + "> ?a . \n" + 
	"\t?stmtD <" + RdfConstants.rdfPredStr + "> <" + RdfConstants.hasMaxValueStr + "> . \n" + 
	"\t?stmtD <" + RdfConstants.rdfObjStr + "> ?max . \n" + 

	"\t?x <" + RdfConstants.hasAttributeStr + "> ?a . \n" + 
	"\t?x <" + RdfConstants.rdfsLabelStr + "> ?xLabel . \n" + 

	// HACK - for an additive property, the computed value is only one addend,
	// but as we don't have any negative valued attributes right now, and
	// are only testing for max exceeded, we can get away with this.
	//
	"\t?a <" + RdfConstants.hasComputedValueStr + "> ?computedVal . \n" + 
	"\t?computedVal <" + RdfConstants.withValueStr + "> ?actualVal . \n" + 
	"\t?a <" + RdfConstants.rdfsLabelStr + "> ?aLabel . \n" + 
	
	"\tBIND(URI(SHA256(?constraintName)) as ?constrHash) . \n" +
	"\tBIND(URI(CONCAT(\"http://example.org/ViolationOfConstraint_\", ?constrName))  as ?vio) . \n" +
	"\tFILTER ((xsd:decimal(?actualVal) > xsd:decimal(?max)) && NOT EXISTS { ?constr1 <" + RdfConstants.violationBeingProcessedStr + "> ?vio1  } ) . \n" +

	"\n}"
	;



	

}
