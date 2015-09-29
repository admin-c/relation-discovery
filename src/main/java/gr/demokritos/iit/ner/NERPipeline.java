package gr.demokritos.iit.ner;


import edu.mit.ll.mitie.EntityMention;
import edu.mit.ll.mitie.EntityMentionVector;
import edu.mit.ll.mitie.StringVector;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import gr.demokritos.iit.nlp.MITIENLP;
import gr.demokritos.iit.nlp.StanfordNLP;
import java.util.*;

public class NERPipeline {

	public static enum ENGINES {STANFORD, MITIE};
	public final static Set<String> BLACKLIST_TAGS
		= new HashSet(Arrays.asList("NUMBER", "O"));
	private DBpediaSpotlight dbpedia;
	private boolean use_dbpedia = false;
	private ENGINES use_engine;

	public NERPipeline() {
		this.dbpedia = new DBpediaSpotlight();
		this.use_dbpedia = false;
		this.use_engine = ENGINES.STANFORD;
		this.prettyPrint();
	}

	public NERPipeline(boolean use_dbpedia) {
		this.dbpedia = new DBpediaSpotlight();
		this.use_dbpedia = use_dbpedia;
		this.use_engine = ENGINES.STANFORD;
		this.prettyPrint();
	}

	public NERPipeline(boolean use_dbpedia, ENGINES engine) {
		this.dbpedia = new DBpediaSpotlight();
		this.use_dbpedia = use_dbpedia;
		this.use_engine = engine;
		this.prettyPrint();
	}

	public void setUseDBpedia(boolean flag){
		this.use_dbpedia = flag;
	}

	public NamedEntityList process(List<String> cleanedSentences) {

		NamedEntityList entities = new NamedEntityList();

		if (use_engine == ENGINES.STANFORD) {
			for (String text : cleanedSentences) {
				Annotation document = new Annotation(text);
				StanfordNLP.pipeline.annotate(document);

				List<CoreMap> sentences = document.get(SentencesAnnotation.class);
				for (CoreMap sentence : sentences) {
					List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
					ListIterator<CoreLabel> itr = tokens.listIterator();
					skipBlacklistedTags(itr); //go to first tag we care about
					while (itr.hasNext()) {
						entities.add(NERFromLabels(itr)); // add
						skipBlacklistedTags(itr); // next tag we care about or end
					}
				}
			}
		}
		// engine is MITIE
		else {
			for (String text : cleanedSentences) {
				StringVector words = MITIENLP.tokenize(text);
				EntityMentionVector ents = MITIENLP.pipeline.extractEntities(words);
				for (int i=0; i<ents.size(); i++) {
					EntityMention ent = ents.get(i)	;
					String type = MITIENLP.getTag(ent);
					String entText = MITIENLP.getText(ent, words);
					String uri = "unknown";
					try{
						if(this.use_dbpedia){
							uri = dbpedia.queryURI(text);
						}
					}
					catch (Exception e){} //simply uses unknown in case of error
					NamedEntity entity = new NamedEntity(0, 0, entText, type, uri);
					entities.add(entity);
				}
			}
		}
		return entities;
	}

	// overloaded above method to work on single text
	public NamedEntityList process(String text) { 
		NamedEntityList entities = new NamedEntityList();
		Annotation document = new Annotation(text);
		if (use_engine == ENGINES.STANFORD) {
			StanfordNLP.pipeline.annotate(document);

			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			for (CoreMap sentence : sentences) {
				List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
				ListIterator<CoreLabel> itr = tokens.listIterator();
				skipBlacklistedTags(itr); //go to first tag we care about
				while (itr.hasNext()) {
					entities.add(NERFromLabels(itr)); // add
					skipBlacklistedTags(itr); // next tag we care about or end
				}
			}
		}
		// if engine is MITIE
		else {
			StringVector words = MITIENLP.tokenize(text);
			EntityMentionVector ents = MITIENLP.pipeline.extractEntities(words);
			for (int i=0; i<ents.size(); i++) {
				EntityMention ent = ents.get(i)	;
				String type = MITIENLP.getTag(ent);
				String entText = MITIENLP.getText(ent, words);
				String uri = "unknown";
				try{
					if(this.use_dbpedia){
						uri = dbpedia.queryURI(text);
					}
				}
				catch (Exception e){} //simply uses unknown in case of error
				NamedEntity entity = new NamedEntity(0, 0, entText, type, uri);
				entities.add(entity);
			}
		}
		return entities;
	}

	// pretty output for ner start server
	public void prettyPrint() {
		System.out.println("Initialized NERPipeline...");
		System.out.println("Using DBpedia: " + use_dbpedia);
		System.out.println("Using engine: " + use_engine.name());
	}

	private void skipBlacklistedTags(ListIterator<CoreLabel> itr) {
		while(itr.hasNext()){
			CoreLabel label = itr.next();
			String tag = label.ner();
			if(!BLACKLIST_TAGS.contains(tag)){
				itr.previous();
				break;
			}
		}
	}

	private NamedEntity NERFromLabels(ListIterator<CoreLabel> itr) {
		CoreLabel first_label = itr.next();
		String first_tag = first_label.ner();
		String text = first_label.originalText();
		CoreLabel next_label = first_label; // in case there is no next
		while (itr.hasNext()) {
			next_label = itr.next();
			String next_tag = next_label.ner();
			if (next_tag.equals(first_tag)) {
				// while the tags are the same concatenate tokens
				text += " " + next_label.originalText();
			} else {
				// put back tag that was different
				// this is stupid - but I couldn't find a cleaner way
				// java iterator running previous once and next once 
				// returns the same element..
				itr.previous();
				next_label = itr.previous();
				itr.next();
				break;
			}
		}
		int begin = first_label.beginPosition();
		int end = next_label.endPosition();
		String uri = "unknown";
		try{
			if(this.use_dbpedia){
				uri = dbpedia.queryURI(text);
			}
		}
		catch (Exception e){} //simply uses unknown in case of error
		NamedEntity ent = new NamedEntity(begin, end, text, first_tag, uri);
		return ent;
	}
}
