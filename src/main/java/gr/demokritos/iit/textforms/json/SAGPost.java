/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.textforms.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author grv
 */
public class SAGPost extends JsonForm {
	// Labels of fields we want in json
	public static final String ID_LABEL = "uid";
	public static final String TEXT_LABEL = "body_value";
	public static final String CREATED_LABEL = "post_created";

	public SAGPost(String id, String text, String date_created, JsonObject json) {
		super(id, text, date_created, json);
	}

	@Override
	public ArrayList<String> sentenceSplit() {
		removeHTML();
		String[] tokens = tokenizeSentence();
		return rebuildSentences(tokens);
	}

	@Override
	public void prettyPrint() {
		System.out.println("post id: " + id);
		System.out.println("post text: " + text);
		System.out.println("date created " + date_created);
	}

	/**
	 * Get a new instance of this class from a json representation of it.
	 * @param json a json string containing the class attributes.
	 * @return a new instance of Tweet.
	 * @throws gr.demokritos.iit.textforms.json.JsonForm.InvalidJsonException
	 */
	public static SAGPost fromFormat(String json) throws InvalidJsonException{
		try{
			JsonElement json_element = new JsonParser().parse(json);
			JsonObject  json_object = json_element.getAsJsonObject();
			String id = json_object.get(ID_LABEL).getAsString();
			String text = json_object.get(TEXT_LABEL).getAsString();
			String date_created = json_object.get(CREATED_LABEL).getAsString();
			return new SAGPost(id, text, date_created, json_object);
		}
		catch(NullPointerException ex){
			Logger.getLogger(SAGPost.class.getName()).log(Level.SEVERE, null, ex);
			throw new InvalidJsonException();
		}
	}

	
}
