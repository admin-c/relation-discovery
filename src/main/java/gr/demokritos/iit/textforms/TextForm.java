/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.textforms;

import gr.demokritos.iit.api.API;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author grv
 */
public abstract class TextForm {

	protected String id;
	protected String text;
	protected String date_created;

	public TextForm(String text) {
		this.id = "none";
		this.text = text;
		this.date_created = "none";
	}

	public TextForm(String id, String text, String date_created) {
		this.id = id;
		this.text = text;
		this.date_created = date_created;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getDate_created() {
		return date_created;
	}

	public abstract ArrayList<String> sentenceSplit();
	public abstract String toFormat();

	/**
	 * Get an instance of the correct subclass - the subclass that can parse the
	 * JSON. If an error is encountered it throws an exception like specified
	 * below.
	 *
	 * @param data, the data representing the text object
	 * @param classname, the class - subclass of TextForm to use to parse
	 * the format. Will cal fromFormat of that class.
	 * @return An instance of the class that parses the JSON
	 * @throws gr.demokritos.iit.textforms.TextForm.InvalidFormatException
	 */
	public static TextForm fromFormat(String data, Class classname)
			throws InvalidFormatException {
		try {
			Class<? extends TextForm> textform = classname.asSubclass(TextForm.class);
			Method fromFormat = textform.getMethod("fromFormat", String.class);
			return textform.cast(fromFormat.invoke(null, data));
		} catch (IllegalAccessException | IllegalArgumentException |
				InvocationTargetException | NoSuchMethodException |
				SecurityException ex) {
			if (!API.mapping.containsValue(classname)) {
				Logger.getLogger(TextForm.class.getName()).log(Level.SEVERE,
						"fromFormat generics expects one of the "
						+ "following values {0}", API.mapping.values().toString());
			}
			Logger.getLogger(TextForm.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public void prettyPrint() {
		System.out.println("text id: " + id);
		System.out.println("text: " + text);
		System.out.println("date created " + date_created);
	}


	/**
	 * Remove HTML strings from the text
	 */
	protected void removeHTML() {
		this.text = this.text.replaceAll("&amp;", "and");
		this.text = StringEscapeUtils.unescapeHtml4(this.text);
	}

	// currently not used
	protected String removeCamelCase(String str) {
		str = Arrays.toString(
				str.split("(?<!(^|\\p{Lu}))(?=\\p{Lu})|(?<!^)(?=\\p{Lu}\\p{Ll})")
		);
		str = Arrays.toString(
				str.split("(?<=[\\w&&\\D])(?=\\d)")).replaceAll("\\,|\\[|\\]", ""
				);
		str = str.replaceAll("\\s+", " ");
		return str;
	}

	public static class InvalidFormatException extends Exception {

		private static final String message = "Couldn't parse JSON, either it is"
				+ " malformed, or its fields do not correspond"
				+ " to one of the classes we were expecting";

		public InvalidFormatException() {
			super(message);
		}

		public InvalidFormatException(String message) {
			super(message);
		}
	}

	/**
	 * Replace some symbols and then tokenize the sentence
	 *
	 * @return array of Strings - the tokens
	 */
	protected String[] tokenizeSentence() {
		//TODO maybe use stanford tokenizer?
		this.text = this.text.replaceAll("–", "-");
		this.text = this.text.replaceAll("!+", "!");
		this.text = this.text.replaceAll("\\?+", "?");
		this.text = this.text.replaceAll("…|\\.+", ".");
		this.text = this.text.replaceAll("(!\\?)+", "!?");
		this.text = this.text.replaceAll("(\\?!)+", "?!");
		this.text = this.text.replaceAll("\\t|\\r|\\n|\\r\\n", " ");
		this.text = this.text.replaceAll("\"|\\^|\\*|\\(|\\)|<|>|~|`|[^\\p{ASCII}]", "");
		return this.text.split("\\s+");
	}

	protected ArrayList<String> rebuildSentences(String[] tokens) {
		ArrayList<String> clean_sentences = new ArrayList();
		String sentence = "";
		for (String s : tokens) {
			if (!s.equals("")) {
				sentence += s.trim() + " ";
				if (s.endsWith(".") || s.endsWith("!") || s.endsWith("?")) {
					sentence = sentence.trim();
					//don't add empty or small sentences
					if ((!sentence.matches("\\s+")) || sentence.length() > 2) {
						clean_sentences.add(sentence.trim());
					}
					sentence = "";
				}
			}
		}
		clean_sentences.add(sentence.trim()); // Adding last sentence
		return clean_sentences;
	}
}