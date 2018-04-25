/**
 * 
 */
package devtests;

import java.net.URLEncoder;

import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class ListUsers {
	private QueryBuilder qb;
	private SimpleHttpClient client;
	private final String
		BASE_URL	= "http://localhost:8080/user/",
		USER_ID		= "JoeSixpack",
		VERB		= "ListUsers";

	/**
	 * 
	 */
	public ListUsers() {
		qb = new QueryBuilder();
		client = new SimpleHttpClient();
		JSONObject query = qb.coreQuery(VERB, USER_ID, null, null);
		System.out.println("A "+query.toJSONString());
		String q = query.toJSONString();
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		IResult r = client.get(BASE_URL, q);
		System.out.println("B "+r.getErrorString());
		System.out.println("C "+r.getResultObject());
	}

}
