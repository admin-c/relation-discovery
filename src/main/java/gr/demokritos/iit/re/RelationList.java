/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.re;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author grv
 */
public class RelationList extends ArrayList<Relation>{

	private static final List<String> filter_list = 
			Arrays.asList("PERSON", "LOCATION", "ORGANIZATION");

	 
	/**
	 * Get a filtered edition of this RelationList which only contains
	 * relations whose subject or arguments contain a named entity
	 * of types included in filter_list
	 * @return  a filtered instance of RelationList
	 */
	public RelationList filter(){
		RelationList copy = new RelationList();
		copy.addAll(this);
		for(Relation each: this){
			if(!each.containsTypes(RelationList.filter_list)){
				copy.remove(each);
			}
		}
		return copy;
	}


	/**
	 * Create JSON representation of this class.
	 * @return String representing this class in JSON.
	 */
	public JsonElement toJSONElement() {
		return new Gson().toJsonTree(this, RelationList.class);
	}

	/**
	 * Get this class representation as JSON.
	 * @return String this class representation.
	 */
	public String toJSON() {
		return new Gson().toJson(this, RelationList.class);
	}

	/**
	 * Create a new RelationList from a JSON string.
	 * @param json
	 * @return RelationList object that the JSON string represented.
	 */
	public static RelationList fromJSON(String json) {
		return new Gson().fromJson(json, RelationList.class);
	}

	/**
	 * Get a pretty representation of this class. 
	 */
	public void prettyPrint() {
		System.out.println("---- Relations ------");
		for (Relation each : this) {
				System.out.println();
				each.prettyPrint();
				System.out.println();
		}
		System.out.println("---- End Relations ------");
	}

}
