/**
 * 
 */
package devtests;

import java.net.URLEncoder;

import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class FetchNode {
	private QueryBuilder qb;
	private SimpleHttpClient client;
	private final String
		BASE_URL	= "http://localhost:8080/tm/",
		USER_ID		= ITQCoreOntology.SYSTEM_USER,
		VERB		= "GetTopic";

	/**
	 * 
	 */
	public FetchNode() {
		qb = new QueryBuilder();
		client = new SimpleHttpClient();
	}
	
	public IResult fetchNode(String locator) {
		IResult result = null;
		JSONObject query = qb.coreQuery(VERB, USER_ID, null, null);
		query.put(ITQCoreOntology.LOCATOR_PROPERTY, locator);
		String q = query.toJSONString();
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("A "+q);
		result = client.get(BASE_URL, query.toJSONString());
		return result;
	}

}
