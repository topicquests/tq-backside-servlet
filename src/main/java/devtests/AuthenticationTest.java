/**
 * 
 */
package devtests;

import java.net.URLEncoder;

import org.topicquests.backside.servlet.api.ICredentialsMicroformat;
import org.topicquests.backside.servlet.apps.auth.api.IAuthMicroformat;
import org.topicquests.backside.servlet.apps.usr.api.IUserMicroformat;
import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;
import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class AuthenticationTest {
	private QueryBuilder qb;
	private SimpleHttpClient client;
	private final String
		BASE_URL	= "http://localhost:8080/auth/",
		USER_ID		= "JoeSixpack",
		USER_EMAIL	= "joe@sixpack.com",
		USER_PWD	= "joeS!",
		VERB		= IAuthMicroformat.AUTHENTICATE;
	/**
	 * 
	 */
	public AuthenticationTest() {
		qb = new QueryBuilder();
		client = new SimpleHttpClient();
		JSONObject query = qb.coreQuery(VERB, USER_ID, null, null);
		String pwd = java.util.Base64.getEncoder().encodeToString(USER_PWD.getBytes());
		String auth = "Basic"+USER_EMAIL+":"+USER_PWD; //pwd;
		//String auth = "Basicjoe:"+USER_PWD; //pwd;
		
		query.put("hash", auth);
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
//C {"rMsg":"ok","rToken":"0777e181-7b94-49ff-9399-55cf61abc583","cargo":{"active":true,"handle":"joe","uFullName":"Joe Sixpack","roleList":["rur"],"locator":"JoeSixpack","email":"joe@sixpack.com","homepage":"http:\/\/joe.sixpack.com\/"}}

}
