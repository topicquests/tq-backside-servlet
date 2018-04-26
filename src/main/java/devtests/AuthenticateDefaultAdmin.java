/**
 * 
 */
package devtests;

import java.net.URLEncoder;

import org.topicquests.backside.servlet.apps.auth.api.IAuthMicroformat;
import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class AuthenticateDefaultAdmin {
	private QueryBuilder qb;
	private SimpleHttpClient client;
	private final String
		AUTH_URL	= "http://localhost:8080/auth/",
		ADMIN_ID	= "ef4da398-7440-4b23-b5a0-1331cc333141", //from config-props.xml
		ADMIN_EMAIL	= "default@example.com", // from config-props.xml
		ADMIN_PWD	= "antiquing"; // from config-props.xml

	/**
	 * 
	 */
	public AuthenticateDefaultAdmin() {
		qb = new QueryBuilder();
		
		
	}
	
	public IResult auth() {
		client = new SimpleHttpClient();
		JSONObject query = qb.coreQuery(IAuthMicroformat.AUTHENTICATE, ADMIN_ID, null, null);
		String auth = "Basic"+ADMIN_EMAIL+":"+ADMIN_PWD; 
		
		query.put("hash", auth);
		System.out.println("ADA-1 "+query.toJSONString());
		String q = query.toJSONString();
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("ADA-2 "+q);
		IResult r = client.get(AUTH_URL, q);
		System.out.println("ADA-3 "+r.getErrorString()+" "+r.getResultObject());
		client = null;
		return r;
	}

}
