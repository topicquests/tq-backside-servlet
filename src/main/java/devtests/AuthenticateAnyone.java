/**
 * 
 */
package devtests;

import java.net.URLEncoder;

import org.topicquests.backside.servlet.apps.auth.api.IAuthMicroformat;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class AuthenticateAnyone {
	private QueryBuilder qb;
	private SimpleHttpClient client;
	private final String
		AUTH_URL	= "http://localhost:8080/auth/";

	/**
	 * 
	 */
	public AuthenticateAnyone() {
		qb = new QueryBuilder();
	}
	
	public IResult auth(String email, String pwd ) {
		client = new SimpleHttpClient();
		String auth = "Basic"+email+":"+pwd; 
		JSONObject query = qb.coreQuery(IAuthMicroformat.AUTHENTICATE, ITQCoreOntology.SYSTEM_USER, null, null);
		
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
