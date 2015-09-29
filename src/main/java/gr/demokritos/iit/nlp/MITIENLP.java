/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.nlp;

import edu.mit.ll.mitie.EntityMention;
import edu.mit.ll.mitie.NamedEntityExtractor;
import edu.mit.ll.mitie.StringVector;
import edu.mit.ll.mitie.global;
import java.net.URL;

/**
 *
 * @author grv
 */
public class MITIENLP {

	public static NamedEntityExtractor pipeline = initMITIE();
	public static StringVector possibleTags;
	
	public static NamedEntityExtractor initMITIE() {
		ClassLoader mitieLoader = MITIENLP.class.getClassLoader();
		URL modelFileURL = mitieLoader.getResource("ner_model.dat");
		String modelPath = modelFileURL.getPath();
		NamedEntityExtractor ner = new NamedEntityExtractor(modelPath);
		possibleTags = ner.getPossibleNerTags();
		return ner;
	}

	public static StringVector tokenize(String text) {
		StringVector words = global.tokenize(text);
		return words;
	}

	public static String getTag(EntityMention mention) {
		return possibleTags.get(mention.getTag());
	}

	public static String getText(EntityMention mention, StringVector tokens) {
		int start = mention.getStart();
		int end = mention.getEnd();
		String [] entTokens = new String[end - start];
		for (int i = start; i < end; ++i) {
			entTokens[i - start] = tokens.get(i);
        }
		return String.join(" ", entTokens);
	}
}
