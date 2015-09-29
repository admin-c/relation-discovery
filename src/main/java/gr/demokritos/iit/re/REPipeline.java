package gr.demokritos.iit.re;

import de.mpii.clausie.ClausIE;
import de.mpii.clausie.Options;
import de.mpii.clausie.Proposition;
import gr.demokritos.iit.ner.NamedEntity;
import gr.demokritos.iit.ner.NamedEntityList;
import java.util.ArrayList;
import java.util.List;

public class REPipeline {

	private ClausIE clausIE;
	private int addedRelations = 0;
	
	public REPipeline(){
		// initialize clausie
		clausIE = new ClausIE();
		clausIE.initParser();
		Options my_options = clausIE.getOptions();
		my_options.nary = true;
		prettyPrint();
	}

	// for a single sentence
	public RelationList process(String sentence, NamedEntityList entities) {
		RelationList relations = new RelationList();
		if (!sentence.isEmpty()) {
			try {
				if(!entities.isEmpty()){
					// create dependency graph
					clausIE.parse(sentence);
					// clause detection
					// rules for specifying clause and label
					if (clausIE.detectClauses()) {
						// proposition (verb) subject arguments (object++)
						clausIE.generatePropositions();
						relations.addAll(buildRelations(clausIE.getPropositions(), entities));
						addedRelations++;
					}
				}
			} catch (Exception ex) {
				// Silence this message for now - it just means we couldnt parse this
				System.err.println("Error while extracting relations");
				ex.printStackTrace();
			}
		}
		// filter the relations to only return ones containing certain entity types
		return relations.filter();
	}

	// for a list of sentences - Warning, sentences != texts - this function
	// is meant to be used for input after sentence splitting.
	// using this for a lot of texts is inefficient since it iterates over
	// the list of entities contained in all the texts for each text
	public RelationList process(ArrayList <String> sentences, NamedEntityList entities) {
		RelationList relations = new RelationList();
		for(String sentence: sentences){
			relations.addAll(process(sentence, entities));
		}
		// filter the relations to only return ones containing certain entity types
		return relations.filter();
	}

	private RelationList buildRelations(List<Proposition> allProps,
			NamedEntityList entities) {
		RelationList relations = new RelationList();
		for (Proposition prop : allProps) {
			// get subject text
			String subject_string = prop.subject();
			// find first entity with such text if it exists
			// TODO: check that popping from list doesn't 
			// create problems (probably get less relations)
			// 2015-08-19 09:25
			NamedEntity subject = entities.pop(subject_string);
			// We only create relations that map two entities.
			if(subject !=null){
				// get relation text
				String relation_string = prop.relation();
				// get argument texts
				ArrayList<String> argument_strings = getArguments(prop);
				for (String argument_string : argument_strings) {
					NamedEntityList arguments = entities.popAllFound(argument_string);
					// if there are more than one arguments found in the string
					// add first as argument and rest as additional arguments
					if(arguments.size() > 1){
						NamedEntity argument = arguments.get(0);
						arguments.remove(argument);
						Relation relation = new Relation(subject,
														 relation_string,
														 argument,
														 arguments);
						relations.add(relation);
					}
					// if only one is found, add relation with no additional arguments
					else if(arguments.size() > 0){
						Relation relation = new Relation(subject,
														 relation_string,
														 arguments.get(0));
						relations.add(relation);
					}
				}
			}
		}
		return relations;
	}
	
	private ArrayList<String> getArguments(Proposition prop) {
		ArrayList<String> arguments = new ArrayList();
		for (int i = 0; i < prop.noArguments(); i++) {
			arguments.add(prop.argument(i));
		}
		return arguments;
	}

	// pretty output for ner start server
	public void prettyPrint() {
		System.out.println("Initialized REPipeline...");
	}
}
