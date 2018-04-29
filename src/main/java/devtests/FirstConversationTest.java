/**
 * 
 */
package devtests;

import java.net.URLEncoder;
import java.util.UUID;

import org.topicquests.backside.servlet.apps.admin.api.IAdminMicroformat;
import org.topicquests.ks.api.INodeTypes;
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
	private FetchNode nodeFetcher;
	private ConversationFetchTest fetch;
	private SimpleHttpClient client;
	private AuthenticateAnyone auth;
	private final String
		BASE_URL	= "http://localhost:8080/tm/",
		USER_ID		= "JoeSixpack",
		USER_EMAIL	= "joe@sixpack.com",
		USER_PWD	= "joeS!",
		ROOT_LOX	= UUID.randomUUID().toString(),
		VERB		= "NewConvNode";
		

	/**
	 * 
	 */
	public FirstConversationTest() {
		qb = new QueryBuilder();
		nb = new NodeBuilder();
		fetch = new ConversationFetchTest();
		nodeFetcher = new FetchNode();
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
		System.out.println("A "+sToken);
		JSONObject a = newConversationNode(ROOT_LOX, INodeTypes.ISSUE_TYPE, "Why is the sky blue?",
				"Inquiring minds really want to know", null, null);
		IResult r = runQuery(a, sToken);
		String xx = (String)r.getResultObject();
		System.out.println("B "+xx);
//B {"rMsg":"ok","rToken":"","cargo":{"crDt":"2018-04-28T22:01:38-07:00","trCl":["TypeType","ClassType","NodeType","IssueNodeType"],"crtr":"JoeSixpack","node_type":"IssueNodeType","lox":"1cfaff24-4d75-4b14-9627-23f468844191","sIco":"\/images\/ibis\/issue_sm.png","isPrv":false,"_ver":"1524978098741","lEdDt":"2018-04-28T22:01:38-07:00","details":{"en":["Inquiring minds really want to know"]},"label":{"en":["Why is the sky blue?"]},"lIco":"\/images\/ibis\/issue.png"}}

		JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		JSONObject x = null;
		try {
			x = (JSONObject)p.parse(xx);
		} catch (Exception e) {e.printStackTrace();}
		
		r = nodeFetcher.fetchNode(ROOT_LOX);
		System.out.println("SANITY "+r.getErrorString()+" "+r.getResultObject());
		a = newConversationNode(null, INodeTypes.ISSUE_TYPE, "Who wants to know?",
				"Inquiring minds still want to know", ROOT_LOX, ROOT_LOX);
		r = runQuery(a, sToken);
		xx = (String)r.getResultObject();
		try {
			x = (JSONObject)p.parse(xx);
		} catch (Exception e) {e.printStackTrace();};
		System.out.println("C "+x);
//C {"rMsg":"ok","rToken":"","cargo":{"crDt":"2018-04-28T22:01:38-07:00","trCl":["TypeType","ClassType","NodeType","IssueNodeType"],"crtr":"JoeSixpack","node_type":"IssueNodeType","lox":"d98d69af-8506-438b-8ec5-29bf3b38b1ee","sIco":"\/images\/ibis\/issue_sm.png","isPrv":false,"_ver":"1524978099151","lEdDt":"2018-04-28T22:01:39-07:00","details":{"en":["Inquiring minds still want to know"]},"label":{"en":["Who wants to know?"]},"lIco":"\/images\/ibis\/issue.png"}}

		String parentLox = x.getAsString("lox");
		a = newConversationNode(null, INodeTypes.CON_TYPE, "That's dumb",
				"Inquiring minds need a rest", parentLox, ROOT_LOX);
		r = runQuery(a, sToken);
		xx = (String)r.getResultObject();
		try {
			x = (JSONObject)p.parse(xx);
		} catch (Exception e) {e.printStackTrace();};
		System.out.println("D "+x);

		r = fetch.getConversation(ROOT_LOX);
		System.out.println("DID "+r.getResultObject());
		System.exit(0);
	}
	
	IResult runQuery(JSONObject node, String sToken) {
		JSONObject query = qb.coreQuery(VERB, USER_ID, null, sToken);
		query.put("cargo", node);
		System.out.println("X "+node.toJSONString());
//X {"uId":"JoeSixpack","lox":"d98d69af-8506-438b-8ec5-29bf3b38b1ee","isPrv":"F","Lang":"en","details":"Inquiring minds still want to know","label":"Who wants to know?","ContextLocator":"1cfaff24-4d75-4b14-9627-23f468844191","inOf":"IssueNodeType","ConParentLocator":"1cfaff24-4d75-4b14-9627-23f468844191"}

		String q = query.toJSONString();
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		IResult r = client.put(BASE_URL, q);
		System.out.println("Y "+r.getErrorString());
		return r;
	}

	JSONObject newConversationNode(String locator, String type, String label, String details, String parentLocator, String contextLocator) {
		JSONObject result = nb.newInstanceNode(locator, type, USER_ID, label, details, "en", parentLocator, contextLocator, null, null, null, false);
		return result;
	}
	
}
