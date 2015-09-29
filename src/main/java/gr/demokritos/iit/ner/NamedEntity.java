package gr.demokritos.iit.ner;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.security.MessageDigest;
import org.apache.commons.codec.binary.Hex;

/**
 * NamedEntity representation class
 * @author grv
 */
public class NamedEntity {

	private static final String HASH_ALGORITHM = "SHA-1";

	private String id;
	private int begin_offset;
	private int end_offset;
	private String text;
	private String type;
	private String DBpediaLink;

	/**
	 * Construct a NamedEntity
	 * 
	 * @param begin_offset begin of token in sentence
	 * @param end_offset end_offset index of token in sentence
	 * @param text the text the entity was represented with in the sentence
	 * @param type the type of the entity - e.g. person, organization etc.
	 * @param DBpediaLink a uri for this entity on dbpedia
	 */
	public NamedEntity(int begin_offset, int end_offset, String text, String type,
		String DBpediaLink) {
		this.begin_offset = begin_offset;
		this.end_offset = end_offset;
		this.text = text;
		this.type = type;
		this.DBpediaLink = DBpediaLink;
		this.id = hash();
	}

	public String getID() {
		return id;
	}

	public int getStart() {
		return begin_offset;
	}

	public int getEnd() {
		return end_offset;
	}

	public String getText() { 
		return text;
	}

	public String getType() {
		return type;
	}

	public String getDBpediaLink() {
		return DBpediaLink;
	}

    public boolean isType(String type){
        return this.type.equals(type);
    }

	/**
	 * Get the general text representation this entity refers to. Disambiguation
	 * is attempted through use of dbpedia spotlight and the most probable named
	 * entity is chosen. If none are found the text this entity was represented
	 * with in the text it was located in is used. Generally mapping all 
	 * possible textual representations of a certain entity to the conventional
	 * name that has been assigned to it can be a difficult task.
	 * 
	 * @return String - the entity this occurrence refers to
	 **/
	public String refersTo() {
		if (DBpediaLink == null) {
			return text;
		}
		int pathEnd = DBpediaLink.lastIndexOf("/");
		// if no slashes were found or the slash is the last character
		// return the text representation we have
		if (pathEnd == -1 || pathEnd == DBpediaLink.length() - 1) {
			return text;
		}
		return DBpediaLink.substring(pathEnd + 1);
	}

	/**
	 * Check if this entity is semantically equivalent to the other - 
	 * basically check if they are presented with the same text and
	 * were classified as being of same type. For comparison that
	 * also depends on the location of the text in the sentence you
	 * can compare the id of the two.
	 * 
	 * @return boolean - whether the two entities are semantically the same
	 */
	public boolean isEquivalent(NamedEntity other){
		return this.text.equals(other.text) && this.isType(other.type);
	}
	
	/**
	 * Generate a hash using HASH_ALGORITHM to use for an id
	 * currently uses the text plus the type as input
	 * to the digest function.
	 * 
	 * @return a hash of the features mentioned above
	 */
	public final String hash() {
		try {
			MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
			String plaintext = text + type;
			md.update(plaintext.getBytes("UTF-8"));
			byte[] digest = md.digest();
			return Hex.encodeHexString(digest);
		}
		catch(Exception e){
			System.err.println(e.toString());
			System.exit(1);
			return "Please the compiler";
		}
	}

	/**
	 * Get representation of current NamedEntity instance as a json string.
	 * 
	 * @return the json representation of this class instance.
	 */
	public String toJSON() {
		GsonBuilder builder = new GsonBuilder();
		builder.disableHtmlEscaping();
		builder.setPrettyPrinting();
		return builder.create().toJson(this, NamedEntity.class);
	}

	/**
	 * Get a pretty printed representation of the class.
	 */
	public void prettyPrint() {
		System.out.println("entity id: " + id);
		System.out.println("begin_offset: " + begin_offset);
		System.out.println("end_offset: " + end_offset);
		System.out.println("text: " + text);
		System.out.println("type: " + type);
		System.out.println("dbpedia: " + DBpediaLink);
	}

	/**
	 * Get a new instance of this class from a json representation of it.
	 * @param json a json string containing the class attributes.
	 * @return a new instance of NamedEntity.
	 */
	public static NamedEntity fromJSON(String json) {
		return new Gson().fromJson(json, NamedEntity.class);
	}
}
