/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.demos;

import gr.demokritos.iit.api.API;
import gr.demokritos.iit.ner.NamedEntityList;
import gr.demokritos.iit.re.RelationCounter;
import gr.demokritos.iit.re.RelationList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author grv
 */
public class DemoCerth {

	public static void main(String[] args) {
		
		List<String> texts = Arrays.asList(
			"OObama accused Tony Blair of being the British prime minister",
			"Obama accused Tony Blair of being the British prime minister",
			"Obama accused Tony Blair of being the British prime ministerr",
			"Barack Obama accused Tony Blair of being the British prime minister.",
			"Lara Croft denied George Orwell",
			"Lara Croft denied George Plinky",
			"Lara Croft allied with George Plinky",
			"Lara Croft alied with George Plinky",
			"Lara Croft talied with George Plinky",
			"George Orwell killed Lara Croft in the battle of Albaquerque");

		System.out.println("------- Print named entities without DBpedia ----------\n");
		// switched to hashmap to clearly maintain mapping between text and 
		// named entity list - entities found in that text
		HashMap<String, NamedEntityList> entities =
				API.NER(texts, API.FORMAT.TEXT_CERTH_TWEET, false);
		for(NamedEntityList entitylist: entities.values()){
			entitylist.prettyPrint();
		}
		System.out.println("------- Print named entities with DBpedia ----------\n");
		entities = API.NER(texts, API.FORMAT.TEXT_CERTH_TWEET, true);
		for(NamedEntityList entitylist: entities.values()){
			entitylist.prettyPrint();
		}
		System.out.println("------- Print Relation Counter ----------\n");
		// use new api - pass hashmap mapping of texts to entity lists
		// so that we don't run ner again
		RelationCounter counter = API.RE(entities, API.FORMAT.TEXT_CERTH_TWEET);
		counter.prettyPrint();
		Map <String, RelationList> counts =  counter.getGroups();
		for (RelationList relations : counts.values()) {
			System.out.println(relations.size());
		}
		// Give example on how we can see all the relations used in the counter
		// counter contains groups - hashmap from relation label to relation list
		// System.out.println("Relation list traversal");
		// Set <String> labels = counter.getLabels();
		// for (String label : labels){
			// RelationList rl = counter.getGroup(label);
			// rl.prettyPrint();
		// }

		// get labels that have 3 or more counts
		Set<String> labels = counter.getTopLabels(3);
		for (String label: labels){
			System.out.println(label);
			System.out.println(counter.getCount(label));
		}

		// Single text processing pipeline example
		// System.out.println("-\n\n------ Single text processing ----------\n");
		// for (String text : texts) {
			// System.out.println('\n' + text);
			// NamedEntityList found_entities = API.NER(text,
												     // API.FORMAT.TEXT_CERTH_TWEET,
													 // true);
			// found_entities.prettyPrint();
			// RelationCounter counts = API.RE(text,
									        // found_entities,
											// API.FORMAT.TEXT_CERTH_TWEET);
			// counts.prettyPrint();
		// }
	}
}
