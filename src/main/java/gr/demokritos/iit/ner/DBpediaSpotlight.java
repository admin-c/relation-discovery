package gr.demokritos.iit.ner;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URLEncoder;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;

/**
 * Simple web service-based annotation client for DBpedia Spotlight.
 *
 * @author Gregory Katsios
 */
public class DBpediaSpotlight {

	//private final static String API_URL = "http://jodaiber.dyndns.org:2222/";
	//private String apiUrl = "http://spotlight.dbpedia.org:80/";
	private String apiUrl = "http://spotlight.sztaki.hu:2222/";
	private HttpClient client = new HttpClient();
	private double confidence = 0.2;
	private int support = 10;
	// private String powered_by = "non";
	//"LingPipeSpotter"=Annotate all spots 
	// private String spotter = "CoOccurrenceBasedSelector";
	//AtLeastOneNounSelector"=No verbs and adjs.	
	//"CoOccurrenceBasedSelector" =No 'common words'
	//"NESpotter"=Only Per.,Org.,Loc.
	//Default ;Occurrences=Occurrence-centric;Document=Document-centric
	private String disambiguator = "Default";
	private String showScores = "yes";

	public DBpediaSpotlight() {}

	public DBpediaSpotlight(double confidence, int support, String powered_by,
			String spotter, String disambiguator, String showScores) {
		this.confidence = confidence;
		this.support = support;
		// this.powered_by = powered_by;
		// this.spotter = spotter;
		this.disambiguator = disambiguator;
		this.showScores = showScores;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String queryURI(String text)
		throws UnsupportedEncodingException, IOException {

		text = text.replaceAll("\\s+", " ");
		//LOG.info("Querying API.");
		String spotlightResponse;
		GetMethod getMethod = new GetMethod(apiUrl + "rest/annotate/?"
				+ "confidence=" + confidence
				+ "&support=" + support
				// + "&spotter=" + spotter
				+ "&disambiguator=" + disambiguator
				+ "&showScores=" + showScores
				// + "&powered_by=" + powered_by
				+ "&text=" + URLEncoder.encode(text, "utf-8"));
		getMethod.addRequestHeader(new Header("Accept", "application/json"));

		spotlightResponse = request(getMethod);

		assert spotlightResponse != null;

		JsonObject resultJSON = new JsonParser().parse(spotlightResponse).getAsJsonObject();
		JsonArray entities = resultJSON.getAsJsonArray("Resources");

		/** Maybe try and make a better choice for the uri 
		for (int i = 0; i < entities.length(); i++) {
			entity = entities.getJSONObject(i);
			uri = entity.getString("@URI");
		}
		*/
		// for now pick 1st result - as dbpedia has done disambiguation
		JsonObject entity = entities.get(0).getAsJsonObject();
		String uri = entity.get("@URI").getAsString();
		return uri;
	}

	public String request(HttpMethod method) throws HttpException, IOException {

		String response = null;
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				//LOG.error("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			InputStream responseBody = method.getResponseBodyAsStream();
			response = IOUtils.toString(responseBody, "UTF-8");

		} catch (HttpException e) {
			//LOG.error("Fatal protocol violation: " + e.getMessage());
			throw e;
		} catch (IOException e) {
			//LOG.error("Fatal transport error: " + e.getMessage());
			//LOG.error(method.getQueryString());
			throw e;
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
		return response;
	}
}