package gr.demokritos.iit.textforms.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Model for the tweet, comes included with cleaning functions
 *
 * @author Gregory
 */
public class Tweet extends JsonForm{
	// Labels of fields we want in json
	public static final String ID_LABEL = "id_str";
	public static final String TEXT_LABEL = "text";
	public static final String CREATED_LABEL = "created_at";
	public static final String LANG_LABEL = "lang";
	public static final String ENTITIES_LABEL = "entities";
	public static final String HASHTAGS_LABEL = "hashtags";
	public static final String URLS_LABEL = "urls";
	public static final String MENTIONS_LABEL = "user_mentions";
	public static final String NAME_LABEL = "name";
	public static final String EXPURL_LABEL = "expanded_url";

	private ArrayList<String> mentions;
	private ArrayList<String> hashtags;
	private ArrayList<String> urls;
	private String lang;

	public Tweet(String id, String text, String date_created, String lang, JsonObject json) {
		super(id, text, date_created, json);
		this.lang = lang;
		this.mentions = new ArrayList();
		this.hashtags = new ArrayList();
		this.urls = new ArrayList();
	}

	public Tweet(String id, String text, String date_created, String lang,
				 JsonObject json, ArrayList<String> mentions,
				 ArrayList<String> hashtags, ArrayList<String> urls){
		super(id, text, date_created, json);
		this.lang = lang;
		this.mentions = mentions;
		this.hashtags = hashtags;
		this.urls = urls;
	}

	/**
	 * Pretty much self explanatory, print the tweet
	 */
	public void prettyPrint() {
		System.out.println("tweetId: " + id);
		System.out.println("text: " + text);
		System.out.println("created_at: " + date_created);
		System.out.println("lang: " + lang);
		System.out.println("hashtags: " + hashtags.toString());
		System.out.println("mentions: " + mentions.toString());
		System.out.println("urls: " + urls.toString());
	}

	/**
	 * Get a new instance of this class from a json representation of it.
	 * @param json a json string containing the class attributes.
	 * @return a new instance of Tweet.
	 */
	public static Tweet fromFormat(String json) throws InvalidJsonException{
		try{
			JsonElement json_element = new JsonParser().parse(json);
			JsonObject  json_object = json_element.getAsJsonObject();
			String id = json_object.get(ID_LABEL).getAsString();
			String text = json_object.get(TEXT_LABEL).getAsString();
			String date_created = json_object.get(CREATED_LABEL).getAsString();
			String lang = json_object.get(LANG_LABEL).getAsString();
			// populate tags stuff
			JsonObject entities = json_object.get(ENTITIES_LABEL).getAsJsonObject();
			// ---------------Start--------------------
			// added below to parse the change from array to dictionary
			// with indices
			// JsonObject mention_object = entities.get(MENTIONS_LABEL).getAsJsonObject();
			// ArrayList<String> mentions = new ArrayList();
			// int index = 0;
			// while we find indexes in mentions, continue
			// while(true){
				// String index_value = String.valueOf(index);
				// if(mention_object.has(index_value)){
					// JsonObject mention = mention_object.getAsJsonObject(index_value);
					// mentions.add(mention.get(NAME_LABEL).getAsString());
				// }
				// else{
					// break;
				// }
				// index++;
			// }
			// -------------------End----------------------------
			// Maybe below wasn't deprecated after all - replace from -Start- if
			// discovered otherwise
			// deprecated - mentions is no longer a list, it is a dictionary with indices
			ArrayList<String> mentions = extractInner(entities, MENTIONS_LABEL, NAME_LABEL);
			ArrayList<String> hashtags = extractInner(entities, HASHTAGS_LABEL, TEXT_LABEL);
			ArrayList<String> urls = extractInner(entities, URLS_LABEL, EXPURL_LABEL);
			return new Tweet(id, text, date_created, lang, json_object, mentions, hashtags, urls);
		}
		catch(NullPointerException | JsonSyntaxException e){
			e.printStackTrace();
			throw new InvalidJsonException();
		}
	}

	/**
	 * Helper function
	 * @param entities
	 * @param array_label
	 * @param inner_label
	 * @return 
	 */
	private static ArrayList<String> extractInner(JsonObject entities,
			String array_label, String inner_label){
		ArrayList<String> extracted = new ArrayList();
		JsonArray extract_array = entities.get(array_label).getAsJsonArray();
		for(JsonElement el: extract_array){
			JsonObject extract = el.getAsJsonObject();
			extracted.add(extract.get(inner_label).toString());
		}
		return extracted;

	}

	@Override
	// TODO: decouple sentence split and cleaning - this used to be called 
	// cleanSentences.
	public ArrayList<String> sentenceSplit() {
		removeHTML();
		String[] tokens = tokenizeSentence();
		swapSymbols(tokens);
		// remove consecutive tags from the beginning
		cleanHead(tokens);
		// remove consecutive tags from the end
		cleanTail(tokens);
		// change tags in body for better ner
		cleanBody(tokens);
		return rebuildSentences(tokens);
	}

	private void swapSymbols(String[] tokens){
		Iterator ith = this.hashtags.iterator();
		Iterator itm = this.mentions.iterator();

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].startsWith("@") && itm.hasNext()) {
				tokens[i] = "@" + StringUtils.capitalize(itm.next().toString());
			} else if (tokens[i].startsWith("#") && ith.hasNext()) {
				tokens[i] = "#" + StringUtils.capitalize(ith.next().toString());
			}
		}
	}

	/**
	 * Remove consecutive tags at the beginning of the sentence. Stops when no 
	 * tag is found so it doesn't necessarily run for all the text.
	 * @param tokens The "words" of the sentence
	 */
	private void cleanHead(String[] tokens) { //Removing from the start
		for (int i = 0; i < tokens.length - 1; i++) {
			if (tokens[i].startsWith("#")) {
				tokens[i] = "";
			} else if (tokens[i].startsWith("@")) {
				//Don't touch
			} else if (tokens[i].matches("http.*")) {
				tokens[i] = "";
			} else if (tokens[i].matches("(RT)")) {
				tokens[i] = ""; // It is always followed by an @ mention
				if (i + 1 < tokens.length - 1) {
					tokens[i + 1] = "";
					i++;
				}
			} else {
				break;
			}
		}
	}

	/**
	 * Remove consecutive tags from the end of the sentence. Stops when no
	 * tag is found, so it doesn't necessarily run for all the text.
	 * @param tokens The "words" of the sentence
	 */
	private void cleanTail(String[] tokens) {
		for (int i = tokens.length - 1; i > 0; i--) {
			if (tokens[i].startsWith("#")) {
				tokens[i] = "";
			} else if (tokens[i].startsWith("@")) {
				//Don't touch
			} else if (tokens[i].matches("http.*")) {
				tokens[i] = "";
			} else {
				break;
			}
		}
	}

	/**
	 * Replace the tags in the text with a nicer form for
	 * Named Entity Recognition.
	 * @param tokens The "words" of the sentence
	 */
	private void cleanBody(String[] tokens) {
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].startsWith("@")) {
				tokens[i] = removeCamelCase(tokens[i].replace("@", "")).trim();
			} else if (tokens[i].startsWith("#")) {
				tokens[i] = removeCamelCase(tokens[i].replace("#", "")).trim();
			} else if (tokens[i].startsWith("!") || tokens[i].startsWith("?")) {
				tokens[i] = tokens[i].replaceAll("!+", "");
				tokens[i] = tokens[i].replaceAll("\\?+", "");
				if (i - 1 >= 0) {
					tokens[i - 1] = tokens[i - 1] + ".";
				}
			} else if (tokens[i].matches("http.*")) {
				tokens[i] = "";
			}
		}
	}
}
