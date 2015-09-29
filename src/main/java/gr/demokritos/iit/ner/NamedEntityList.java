package gr.demokritos.iit.ner;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
/**
 * List of NamedEntity representation class.
 * @author grv
 */
public class NamedEntityList extends ArrayList<NamedEntity> {

		public NamedEntityList(int initialCapacity) {
			super(initialCapacity);
		}

		public NamedEntityList() {
		}

		public NamedEntityList(Collection<? extends NamedEntity> c) {
			super(c);
		}

        /**
         * Support searching for a named entity by its token and popping
         * it from the list
         * @param token The token this entity is represented by in text
         *  
         * @return NamedEntity - the first named entity found in the list
         *         and is represented by this token - null otherwise
         **/
        public NamedEntity pop(String token) {
			for (NamedEntity entity : this) {
				if(token.equals(entity.getText())){
				   this.remove(entity);
				   return entity;
				} 
			}
			return null;
		}

        /**
         * Support searching for named entities in text and popping them
         * from the list.
         * @param text The text to search for the named entities in
         *  
         * @return NamedEntityList - the first named entities found in the text
         *         Empty list if none were found
         **/
        public NamedEntityList popAllFound(String text) {
			NamedEntityList found = new NamedEntityList();
			for (NamedEntity entity : this) {
				if(text.contains(entity.getText())){
				   found.add(entity);
				} 
			}
			this.removeAll(found);
			return found;
		}

		public String toJSON() {
			return new Gson().toJson(this, NamedEntityList.class);
		}
		
		/**
		 * Create JSON representation of this class.
		 * @return String representing this class in JSON.
		 */
		public JsonElement toJSONElement() {
			return new Gson().toJsonTree(this, NamedEntityList.class);
		}

		/**
		 * Get a pretty representation of this class. 
		 */
		public void prettyPrint() {
				for (NamedEntity each : this) {
						System.out.println();
						each.prettyPrint();
						System.out.println();
				}
		}

		/**
		 * Get a new instance of this class from a json representation of it.
		 * @param json - a json String containing this class attributes.
		 * @return a new instance of NamedEntityList.
		 */
		public static NamedEntityList fromJSON(String json) {
				return new Gson().fromJson(json, NamedEntityList.class);
		}
		
}
