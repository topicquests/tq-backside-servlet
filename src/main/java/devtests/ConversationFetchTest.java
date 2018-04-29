/**
 * 
 */
package devtests;

import java.net.URLEncoder;

import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class ConversationFetchTest {
	private QueryBuilder qb;
	private SimpleHttpClient client;
	private AuthenticateAnyone auth;
	private final String
		BASE_URL	= "http://localhost:8080/tm/",
		USER_ID		= ITQCoreOntology.SYSTEM_USER,
		//rootlox is generated in a pg-tmx devtest
		// if database is empty, must get another
		VERB		= "ColConTree",
		ROOT_LOX	= "0196fd36-f2f3-4a01-91a2-10fdd10dbc56";

	/**
	 * 
	 */
	public ConversationFetchTest() {
		qb = new QueryBuilder();
		client = new SimpleHttpClient();
		//simple test
		//IResult r = getConversation(ROOT_LOX);
		//System.out.println("A "+r.getErrorString());
		//System.out.println("B "+r.getResultObject());
		//System.exit(0);
	}

	public IResult getConversation(String rootLocator) {
		IResult result = new ResultPojo();
		JSONObject query = qb.coreQuery(VERB, "SystemUser", null, null);
		query.put("lox", rootLocator);
		query.put("Lang", "en");
		System.out.println("A "+query.toJSONString());
		String q = query.toJSONString();
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		result = client.get(BASE_URL, q);		
		return result;
	}
}
/*
 {
	"rMsg": "ok",
	"rToken": "",
	"cargo": {
		"smallImagePath": "\/images\/ibis\/issue_sm.png",
		"subject": "Why is the sky blue?",
		"details": "Inquiring minds want to know",
		"childList": [{
			"smallImagePath": "\/images\/ibis\/issue_sm.png",
			"parentLocator": "0196fd36-f2f3-4a01-91a2-10fdd10dbc56",
			"subject": "Who wants to know?",
			"details": "Inquiring minds want to know",
			"childList": [{
				"smallImagePath": "\/images\/ibis\/issue_sm.png",
				"parentLocator": "30d1e61e-f233-4a5e-ac2d-791e07b78942",
				"subject": "Why did you ask?",
				"details": "Inquiring minds want to know",
				"locator": "fdd2ee93-75d9-48b6-bdbd-a6f3ba7f3353"
			}],
			"locator": "30d1e61e-f233-4a5e-ac2d-791e07b78942"
		}, {
			"smallImagePath": "\/images\/ibis\/position_sm.png",
			"parentLocator": "0196fd36-f2f3-4a01-91a2-10fdd10dbc56",
			"subject": "Has to do with light refraction",
			"details": "Physics. Hard stuff",
			"locator": "25635f44-2bf4-4752-b0a3-014082b1c5fe"
		}],
		"locator": "0196fd36-f2f3-4a01-91a2-10fdd10dbc56"
	}
} 
 */
