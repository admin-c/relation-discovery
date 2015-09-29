/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.re;

import gr.demokritos.iit.ner.NamedEntity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import gr.demokritos.iit.ner.NamedEntityList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author grv
 */
public class Relation {

	private String label;
	private NamedEntity subject;
	private NamedEntity argument;
	private String relation;
	private List <NamedEntity> additional_arguments;
	
	/**
	 * Create a new relation triple with no additional arguments
	 * @param subject - Named entity class - subject of relation
	 * @param argument - Named entity class - argument of relation
	 * @param relation - String - the word that acts as a relation
	 */
	public Relation(NamedEntity subject, String relation, NamedEntity argument){
		this.subject = subject;
		this.relation = relation;
		this.argument = argument;
		this.additional_arguments = new ArrayList();
		labelCreator();
	}

	/**
	 * Create a new relation triple with additional arguments
	 * Note: additional arguments do not necessarily involve the relation
	 * It is however part of the argument and therefore describes the relation
	 * in some way
	 * @param subject - Named entity class - subject of relation
	 * @param argument - Named entity class - argument of relation
	 * @param relation - String - the word that acts as a relation
	 * @param additional_arguments - NamedEntityList class - additional 
	 *								 named entities found in the argument
	 */
	public Relation(NamedEntity subject, String relation, NamedEntity argument,
					NamedEntityList additional_arguments){
		this.subject = subject;
		this.relation = relation;
		this.argument = argument;
		this.additional_arguments = additional_arguments;
		labelCreator();
	}

	public String getLabel() {
		return label;
	}

	public NamedEntity getSubject() {
		return subject;
	}

	public NamedEntity getArgument() {
		return argument;
	}

	/**
	 * Get text that corresponds to subject named entity
	 *  
	 * @return String
	 **/
	public String getSubjectText() {
		return subject.getText();
	}

	/**
	 * Get text that corresponds to argument named entity
	 *  
	 * @return String
	 **/
	public String getArgumentText() {
		return argument.getText();
	}

	public String getRelationText() {
		return relation;
	}

	/**
	 * Check if this relation has any additional arguments
	 *  
	 * @return boolean - True if relation has additional arguments
	 **/
	public boolean hasAdditionalArguments() {
		return additional_arguments.size() > 0;
	}

	/**
	 * Check whether this relation contains this type of named entity.
	 * we look in subject types and argument types.
	 * @param type
	 * @return true if this type is contained, else false
	 */
	public boolean containsType(String type){
		return (subject.isType(type) || argument.isType(type));
	}
	
	/**
	 * Check whether at least one of the types of named entity exists at least
	 * once in subject types or argument types.
	 * @param types
	 * @return true if at least one of the types is contained, else false
	 */
	public boolean containsTypes(List<String> types){
		for(String type: types){
			if(containsType(type)){
				return true;
			}
		}
		return false;	
	}
	
	/**
	 * Get a pretty printed representation of the class.
	 */
	public void prettyPrint() {
		System.out.println("label: " + label);
		System.out.println("subject: ");
		subject.prettyPrint();
		System.out.println("argument: ");
		argument.prettyPrint();
		System.out.println("relation: " + relation);
		System.out.println("additional arguments:");
		for(NamedEntity entity : additional_arguments){
			entity.prettyPrint();
		}
	}

	/**
	 * Create a label for this relation which mirrors what we consider
	 * to be unique. For now order of entities does not matter - we sort names.
	 */
	private void labelCreator(){
		String[] tokens = {this.subject.refersTo(), this.argument.refersTo()};
		Arrays.sort(tokens);
		this.label = String.join("-", tokens).replace(" ", "_");
	 }

	/**
	 * Create JSON representation of this class.
	 * @return String representing this class in JSON.
	 */
	public JsonElement toJSONElement() {
		return new Gson().toJsonTree(this, Relation.class);
	}

	/**
	 * Create JSON representation of this class.
	 * @return String representing this class in JSON.
	 */
	public String toJSON() {
		return new Gson().toJson(this, Relation.class);
	}
	
	/**
	 * Create an instance of this class from JSON string.
	 * @param json
	 * @return an instance of this class represented in json.
	 */
	public static Relation fromJSON(String json) {
			return new Gson().fromJson(json, Relation.class);
	}
}
