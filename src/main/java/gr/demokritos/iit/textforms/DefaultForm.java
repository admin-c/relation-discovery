/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.textforms;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import gr.demokritos.iit.nlp.StanfordNLP;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author grv
 * 
 * A default text form.
 * Basically simple string text, no complicated structure
 * Simply performs sentence splitting - no other preprocessing
 */
public class DefaultForm extends TextForm {

	public DefaultForm(String text) {
		super(text);
	}
	public DefaultForm(String id, String text, String date_created) {
		super(id, text, date_created);
	}

	@Override
	public ArrayList<String> sentenceSplit() {
		ArrayList <String> sents = new ArrayList();
		Annotation doc = new Annotation(this.text);
		StanfordNLP.pipeline.annotate(doc);
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			sents.add(sentence.toString());
		}
		return sents;
	}

	public static DefaultForm fromFormat(String text)
			throws InvalidFormatException{
			return new DefaultForm(text);
	}
	
	@Override
	public String toFormat() {
		return text;
	}
	
}
