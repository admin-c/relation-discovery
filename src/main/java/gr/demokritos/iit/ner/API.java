package gr.demokritos.iit.ner;


import gr.demokritos.iit.textforms.TextForm.InvalidFormatException;
import gr.demokritos.iit.textforms.json.JsonForm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static final NERPipeline ner_pipeline = new NERPipeline(false);

	/**
	 * Get the named entities that were found in the supplied text
	 * Single text input
	 * @param text, the text on which to perform named entity extraction on
	 * @param format, enum of api that maps the input string to a type of format
	 * @param useDBpedia, whether to try to link to dbpedia or not
	 * @return NamedEntityList, a list of NamedEntities found in the text
	 */
	public static NamedEntityList NER(String text,
									  gr.demokritos.iit.api.API.FORMAT format,
									  boolean useDBpedia){
		// careful here - don't remove dbpedia constructor even though false
		ner_pipeline.setUseDBpedia(useDBpedia);
		NamedEntityList text_entities = ner_pipeline.process(text);
		return text_entities;
	}

	/**
	 * Get information about named entities in NamedEntityList form
	 * Multiple text input
	 * @param texts, a list of short texts (will not currently split them
	 * to sentences) to perform relation extraction from
	 * @param format, enum of api that maps the input string to a type of format
	 * @param useDBpedia, whether to try to link to dbpedia or not
	 * @return ArrayList of NamedEntityList, one NamedEntityList for each text
	 */
	public static HashMap<String, NamedEntityList> 
								  NER(List<String> texts,
									  gr.demokritos.iit.api.API.FORMAT format,
									  boolean useDBpedia){
		// careful here - don't remove dbpedia constructor even though false
		ner_pipeline.setUseDBpedia(useDBpedia);
		HashMap<String, NamedEntityList> total_entities = new HashMap();
		for(String text: texts){
			NamedEntityList text_entities = ner_pipeline.process(text);
			total_entities.put(text, text_entities);
		}
		return total_entities;
	}

	/**
	 * Get information about named entities in json form.
	 * @param jsoni, a list of json strings representing tweet data
	 * @param isSAG, whether this text is a tweet or a SAG post
	 * @return List of json, named entities information about tweets
	 */
	//public static List<String> NER(List<String> jsoni, boolean isSAG) throws InvalidJSONException{
	public static List<String> NER(List<String> jsoni, boolean isSAG){
		//obviously the plural of json is jsoni, not jsons
		// Populate the replies array
		List<String> replies = new ArrayList();
		int counter = 0;
		long startTime = System.currentTimeMillis();
		for(String json: jsoni){
			try {
				// parse json depending on what instance it corresponds to
				JsonForm text = JsonForm.fromFormat(json, isSAG);
				// text.print(); // debugging
				ArrayList<String> sentences = text.sentenceSplit();
				NamedEntityList entities = ner_pipeline.process(sentences);
				text.jsonAdd(gr.demokritos.iit.api.API.ENTITIES_LABEL,
						     entities.toJSONElement());
				replies.add(text.toFormat());
				// How do we communicate this? If I throw it a whole batch
				// goes down the shoot.
				counter++;
				if(counter%500==0){
					long endTime = System.currentTimeMillis();
					System.out.println("Processing rate = " + 
							1000*(float)counter/(endTime-startTime) +
							" requests per second");
				}
			} catch (InvalidFormatException ex) {
				Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return replies;
	}
	
}
