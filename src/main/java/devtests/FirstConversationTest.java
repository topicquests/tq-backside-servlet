/**
 * 
 */
package devtests;

import org.topicquests.backside.servlet.apps.admin.api.IAdminMicroformat;
import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * @author jackpark
 *
 */
public class FirstConversationTest {
	private QueryBuilder qb;
	private NodeBuilder nb;
	private SimpleHttpClient client;
	private AuthenticateAnyone auth;
	private final String
		BASE_URL	= "http://localhost:8080/admin/",
		USER_ID		= "JoeSixpack",
		USER_EMAIL	= "joe@sixpack.com",
		USER_PWD	= "joeS!",

		DEACT_V		= IAdminMicroformat.DEACTIVATE_USER,
		REACT_V		= IAdminMicroformat.REACTIVATE_USER;

	/**
	 * 
	 */
	public FirstConversationTest() {
		qb = new QueryBuilder();
		nb = new NodeBuilder();
		client = new SimpleHttpClient();
		auth = new AuthenticateAnyone();
		IResult r = auth.auth(USER_EMAIL, USER_PWD);
		String sToken = (String)r.getResultObject();
		System.out.println("X "+sToken);
		JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		try {
			JSONObject jo =  (JSONObject)p.parse(sToken);
			sToken = jo.getAsString("rToken");
		} catch (Exception e) {
			e.printStackTrace();
		}
		doConversation(sToken);
	}
	
	void doConversation(String sToken) {
	
		
		
		JSONObject query = qb.coreQuery(DEACT_V, USER_ID, null, sToken);

	}

	JSONObject newConversationNode(String locator, String type, String label, String details, String largeImage, String smallImage) {
		JSONObject result = nb.newInstanceNode(locator, type, USER_ID, label, details, "en", largeImage, smallImage, false);
		
		return result;
	}
	
}
