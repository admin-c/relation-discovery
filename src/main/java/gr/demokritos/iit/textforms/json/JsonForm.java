/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.textforms.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gr.demokritos.iit.textforms.TextForm;

/**
 *
 * @author grv
 */
public abstract class JsonForm extends TextForm {
	protected JsonObject json;
	// we keep the above json object so that we can 
	// eventually add fields to it and access it when needed
	// TODO check that this doesn't use too much memory

	public JsonForm(String id, String text, String date_created, JsonObject json) {
		super(id, text, date_created);
		this.json = json;
	}

	public static JsonForm fromFormat(String json, boolean isSAG)
			throws InvalidFormatException{
		if(isSAG){
			return SAGPost.fromFormat(json);
		}
		else{
			return Tweet.fromFormat(json);
		}
	}

	@Override
	public String toFormat(){
		return this.json.toString();
	}

	public void jsonAdd(String label, JsonElement add){
		this.json.add(label, add);
	}

	public String jsonRead(String label){
		try{
			return this.json.get(label).toString();
		}
		catch(NullPointerException e){
			return null;
		}
	}

	public static class InvalidJsonException extends InvalidFormatException{
		private static final String message = "Couldn't parse JSON, either it is"
				+ " malformed, or its fields do not correspond"
				+ " to one of the classes we were expecting";

		public InvalidJsonException() {
			super(message);
		}
	}
}
