/**
 * 
 */
package devtests;

import java.net.URLEncoder;
import java.util.UUID;

import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class SecondStoreTest {
	private QueryBuilder qb;
	private NodeBuilder nb;
	private SimpleHttpClient client;
	private final String
		BASE_URL	= "http://localhost:8080/tm/",
		USER_ID		= ITQCoreOntology.SYSTEM_USER,
		VERB		= "NewSub",
		VERB2		= "GetTopic",
		LOX			= UUID.randomUUID().toString();

	/**
	 * 
	 */
	public SecondStoreTest() {
		qb = new QueryBuilder();
		nb = new NodeBuilder();
		client = new SimpleHttpClient();
		JSONObject query = qb.coreQuery(VERB, USER_ID, null, null);
		JSONObject topic = nb.newSubclassNode(LOX, "ClassType", USER_ID, "Hello World", "Made by devtests/NodeBulder", "en", null, null, false);
		query.put("cargo", topic);
		System.out.println("X "+topic.toJSONString());
		String q = query.toJSONString();
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("A "+q);
		IResult r = client.put(BASE_URL, q);
		System.out.println("B "+r.getErrorString()+" | "+r.getResultObject());
		query = qb.coreQuery(VERB2, USER_ID, null, null);
		query.put(ITQCoreOntology.LOCATOR_PROPERTY, LOX);
		q = query.toJSONString();
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		r = client.get(BASE_URL, q);
		System.out.println("C "+r.getErrorString()+" | "+r.getResultObject());
	}

}
/*
{
	"rMsg": "ok",
	"rToken": "",
	"cargo": {
		"crtr": "SystemUser",
		"_ver": "1524542384543",
		"lEdDt": "1524542384700",
		"label": {
			"en": ["Hello World"]
		},
		"url": null,
		"crDt": ["1524542384700", "2018-04-23T20:59:44-07:00"],
		"trCl": ["TypeType", "ClassType"],
		"tpL": ["7d046868-702a-47de-bbba-a6e095c29de0SubclassRelationTypeClassType", "7d046868-702a-47de-bbba-a6e095c29de0DocumentCreatorRelationTypeSystemUser"],
		"isLiv": true,
		"node_type": null,
		"isVrt": false,
		"sbOf": ["ClassType"],
		"lox": "7d046868-702a-47de-bbba-a6e095c29de0",
		"isPrv": false,
		"details": {
			"en": ["Made by devtests\/NodeBulder"]
		}
	}
}
 */
