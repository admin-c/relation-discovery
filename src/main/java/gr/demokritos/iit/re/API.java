package gr.demokritos.iit.re;

import gr.demokritos.iit.ner.NERPipeline;
import gr.demokritos.iit.ner.NamedEntityList;
import gr.demokritos.iit.textforms.TextForm;
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

	public static final REPipeline rePipeline = new REPipeline();
	private static NERPipeline nerPipeline ;
	private static NamedEntityList entities;


	/**
	 * Get count of relations that occur in the text
	 * @param data, the data to search for relations in
	 * @param format, enum of api that maps the input string to a type of format
	 * @param entities, the entities that exist in this text as a
	 * NamedEntityList. This format of input can be created by running
	 * NER first.
	 * @return RelationCounter, a counter of the relations
	 */
	public static RelationCounter RE(String data,
								   	 NamedEntityList entities,
									 gr.demokritos.iit.api.API.FORMAT format){
		RelationCounter rel_counter = new RelationCounter();
			TextForm text;
		try {
			text = TextForm.fromFormat(data, gr.demokritos.iit.api.API.mapping.get(format));
			ArrayList <String> sentences = text.sentenceSplit();
			RelationList text_rels = rePipeline.process(sentences, entities);
			rel_counter.add(text_rels);
		} catch (InvalidFormatException ex) {
			Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, ex);
		} catch (StackOverflowError ex) {
			Logger.getLogger(API.class.getName()).log(Level.SEVERE, 
					"DAG graph text : {0}", data);
		}
		return rel_counter;
	}

	public static RelationCounter RE(List<String> texts,
									 gr.demokritos.iit.api.API.FORMAT format){
		if(nerPipeline == null){
			nerPipeline = new NERPipeline();
		}
		RelationCounter rel_counter = new RelationCounter();
		for(String text: texts){
			entities = nerPipeline.process(text);
			// TODO: check this is not very slow - using addAll
			// other solution would be to return RelationList from 
			// single text method
			RelationCounter text_relations = RE(text, entities, format);
			rel_counter.addAll(text_relations);
		}
		return rel_counter;
	}

	/**
	 * Get counts of relations that occur in the texts
	 * @param entityMappings, the mapping of texts to process with the entities
	 * they contain. This format of input can be created by running NER first.
	 * @param format, enum of api that maps the input string to a type of format
	 * @return RelationCounter, a counter of the relations
	 */
	public static RelationCounter RE(HashMap<String, NamedEntityList> entityMappings,
									 gr.demokritos.iit.api.API.FORMAT format){
		RelationCounter rel_counter = new RelationCounter();
		for(String text: entityMappings.keySet()){
			entities = entityMappings.get(text);
			// TODO: check this is not very slow - using addAll
			// other solution would be to return RelationList from 
			// single text method
			RelationCounter text_relations = RE(text, entities, format);
			rel_counter.addAll(text_relations);
		}
		return rel_counter;
	}

	/**
	 * Get information about named entities in json form.
	 * @param jsoni, a list of json strings representing tweet data
	 * @return List of json, named entities information about tweets
	 */
	//public static List<String> RE(List<String> jsoni, boolean isSAG) throws InvalidJSONException{
	public static String RE(List<String> jsoni, boolean isSAG){
		//obviously the plural of json is jsoni, not jsons
		int counter = 0;
		long startTime = System.currentTimeMillis();
		RelationCounter rel_counter = new RelationCounter();
		for(String json: jsoni){
			// parse json depending on what instance it corresponds to
			JsonForm text;
			try {
				text = JsonForm.fromFormat(json, isSAG);
				// text.print(); // debugging
				ArrayList<String> sentences = text.sentenceSplit();
				String json_ents = text.jsonRead(gr.demokritos.iit.api.API.ENTITIES_LABEL);
				if(json_ents == null){
					// if we get a json that doesn't contain the named entity
					// field - we load the named entity module and extract entities
					if(nerPipeline == null){
						nerPipeline = new NERPipeline();
					}
					entities = nerPipeline.process(sentences);
				}
				else{
					entities = NamedEntityList.fromJSON(json_ents);
				}
				RelationList text_rels = rePipeline.process(sentences, entities);
				rel_counter.add(text_rels);
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
		return rel_counter.toJSON();
	}
	
}
