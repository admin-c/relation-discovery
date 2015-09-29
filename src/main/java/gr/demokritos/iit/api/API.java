package gr.demokritos.iit.api;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import gr.demokritos.iit.ner.NamedEntityList;
import gr.demokritos.iit.re.RelationCounter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author grv
 */
public class API {

	/**
	 * This is the API to the outside world - hello world!
	 * RE and NER modules can be called with a java list of json texts
	 * OR a json array of json texts in string format. Different formats are 
	 * supported with the isSAG flag that specifies if we expect the tweet
	 * json format or the SAG json format.
	 */

	/* consts
	   the name of the field that will be appended to json of the text
	   containing the extracted named entities. This field is created
	   in the named entity pipeline and is expected to be found in the 
	   input json of each text in the relation extraction module
	*/
	public static final String ENTITIES_LABEL = "named_entities";

	// TODO definitely move to FORMAT but don't break api
	public static enum FORMAT {JSON_SAG_POST,
	                           JSON_REVEAL_TWEET,
							   TEXT_CERTH_TWEET};

	// Mapping from formats we can parse to the class that is responsible
	// for parsing this input for us. These classes must override toFormat
	// and fromFormat functions. For a deeper insight on how this works
	// take a look at TextForm - fromFormat.
	public static final EnumMap<FORMAT, Class> mapping = new EnumMap(FORMAT.class);
	static{
		mapping.put(FORMAT.JSON_SAG_POST, gr.demokritos.iit.textforms.json.SAGPost.class);
		mapping.put(FORMAT.JSON_REVEAL_TWEET, gr.demokritos.iit.textforms.json.Tweet.class);
		mapping.put(FORMAT.TEXT_CERTH_TWEET, gr.demokritos.iit.textforms.DefaultForm.class);
	}
	/**
	 * Get relations for a single text
	 * @param text, text to extract relations from (will currently not
	 * perform sentence splitting)
	 * @param entities, list of entities found to be contained in the text
	 * @param format, enum of api that maps the input string to a type of format
	 * in the textforms package. A serializer and deserializer is created.
	 * @return String in JSON format, counts of relations found in tweets
	 * 
	 */
	// TODO: correct documentation
	public static RelationCounter RE(String text,
									 NamedEntityList entities,
								     FORMAT format){
		return gr.demokritos.iit.re.API.RE(text, entities, format);
	}

	/**
	 * Get relations for a list of texts
	 * @param texts, a list of small sized texts (will currently not
	 * perform sentence splitting)
	 * @param format, enum of api that maps the input string to a type of format
	 * in the textforms package. A serializer and deserializer is created.
	 * @return String in JSON format, counts of relations found in tweets
	 */
	public static RelationCounter RE(List<String> texts, FORMAT format){
		return gr.demokritos.iit.re.API.RE(texts, format);
	}

	/**
	 * Get relations for a mapping of texts and entities contained in them.
	 * @param textMappings, a mapping of texts (will currently not
	 * perform sentence splitting) with the named entities that were found
	 * to be contained in the texts, one list per text.
	 * @param format, enum of api that maps the input string to a type of format
	 * in the textforms package. A serializer and deserializer is created.
	 * @return String in JSON format, counts of relations found in tweets
	 * 
	 */
	// TODO: correct documentation
	public static RelationCounter RE(HashMap<String,
								   	 NamedEntityList> textMappings,
								     FORMAT format){
		return gr.demokritos.iit.re.API.RE(textMappings, format);
	}

	/**
	 * Get information about relations in json form.
	 * @param jsoni, a list of json strings representing tweet data
	 * @param isSAG, true if json contains sag posts instead of tweets
	 * @return String in JSON format, counts of relations found in tweets
	 */
	public static String RE(List<String> jsoni, boolean isSAG){
		//obviously the plural of json is jsoni, not jsons
		// Initialize if this is the first run
		return gr.demokritos.iit.re.API.RE(jsoni, isSAG);
	}

	/**
	 * Get information about relations in json form. Overload above method
	 * to accept array of json in a single string instead of an array of json
	 * strings.
	 * @param json, a string of json representing an array of json of tweets
	 * @param isSAG, true if json contains sag posts instead of tweets
	 * @return String in JSON format, counts of relations found in tweets
	 */
	public static String RE(String json, boolean isSAG){
		// obviously the plural of json is jsoni, not jsons
		// Initialize if this is the first run
		JsonArray text_array = new JsonParser().parse(json).getAsJsonArray();
		ArrayList<String> jsoni = new ArrayList();
		for(JsonElement json_text: text_array){
			jsoni.add(json_text.toString());
		}
		return gr.demokritos.iit.re.API.RE(jsoni, isSAG);
	}

	/**
	 * Get named entities occuring in a single text.
	 * @param text, a text (will currently not * perform sentence splitting)
	 * @param format, enum of api that maps the input string to a type of format
	 * in the textforms package. A serializer and deserializer is created.
	 * @param useDBpedia, boolean - whether to search for a link to this entity
	 * on dbpedia or not.
	 * @return NamedEntityList, A list of the entities found in the text.
	 */
	public static NamedEntityList NER(String text,
								   	  FORMAT format,
									  boolean useDBpedia){
		return gr.demokritos.iit.ner.API.NER(text, format, useDBpedia);
	}

	/**
	 * Get named entities occuring in texts. Mapping is kept returned in a 
	 * hashmap.
	 * @param texts, a list of small sized texts (will currently not
	 * perform sentence splitting)
	 * @param format, enum of api that maps the input string to a type of format
	 * in the textforms package. A serializer and deserializer is created.
	 * @param useDBpedia, boolean - whether to search for a link to this entity
	 * on dbpedia or not.
	 * @return A list of our representation of entity lists. One list of entities
	 * per text
	 */
	public static HashMap<String, NamedEntityList> NER(List<String> texts,
													   FORMAT format,
													   boolean useDBpedia){
		return gr.demokritos.iit.ner.API.NER(texts, format, useDBpedia);
	}

	/**
	 * Get information about named entities in json form.
	 * @param jsoni, a list of json strings representing tweet data
	 * @param isSAG, true if json contains sag posts instead of tweets
	 * @return List of json, named entities information about tweets
	 */
	public static List<String> NER(List<String> jsoni, boolean isSAG){
		//obviously the plural of json is jsoni, not jsons
		return gr.demokritos.iit.ner.API.NER(jsoni, isSAG);
	}
	
	/**
	 * Get information about named entities in json form. Overload above method
	 * to accept array of json in a single string instead of an array of json
	 * strings.
	 * @param json, a string of json representing an array of json of tweets
	 * @param isSAG, true if json contains sag posts instead of tweets
	 * @return List of json, named entities information about tweets
	 */
	public static List<String> NER(String json, boolean isSAG){
		//obviously the plural of json is jsoni, not jsons
		// Initialize if this is the first run
		JsonArray text_array = new JsonParser().parse(json).getAsJsonArray();
		ArrayList<String> jsoni = new ArrayList();
		for(JsonElement json_text: text_array){
			jsoni.add(json_text.toString());
		}
		return gr.demokritos.iit.ner.API.NER(jsoni, isSAG);
	}
}
