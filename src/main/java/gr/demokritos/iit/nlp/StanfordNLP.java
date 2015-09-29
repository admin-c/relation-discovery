/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.nlp;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.Properties;

/**
 *
 * @author grv
 */
public class StanfordNLP {

	public static StanfordCoreNLP pipeline = initNLP();

	public static StanfordCoreNLP initNLP() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		// load only one ner model.
		props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
		StanfordCoreNLP cnlp = new StanfordCoreNLP(props);
		return cnlp;
	}
	
}
