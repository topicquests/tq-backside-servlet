/**
 * 
 */
package devtests;

import java.net.URLEncoder;

import org.topicquests.backside.servlet.api.ICredentialsMicroformat;
import org.topicquests.backside.servlet.apps.admin.api.IAdminMicroformat;
import org.topicquests.backside.servlet.apps.auth.api.IAuthMicroformat;
import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * @author jackpark
 * <p>A bug that caused default admin to fail to be loaded, leaving GuestUser
 * for credentials proved that a non-admin cannot deactivate or reactivate a user</p>
 * <p>The issue is that the defaultadmin was not authenticated, so that sToken was null</p>
 * 
 */
public class DeactivateReactivateUserTest {
	private QueryBuilder qb;
	private SimpleHttpClient client;
	private AuthenticateDefaultAdmin admin;
	private final String
		BASE_URL	= "http://localhost:8080/admin/",
		ADMIN_ID	= "ef4da398-7440-4b23-b5a0-1331cc333141", //Run ListUsers to get the default admin
		USER_ID		= "JoeSixpack",
		DEACT_V		= IAdminMicroformat.DEACTIVATE_USER,
		REACT_V		= IAdminMicroformat.REACTIVATE_USER;

	/**
	 * 
	 */
	public DeactivateReactivateUserTest() {
		qb = new QueryBuilder();
		client = new SimpleHttpClient();
		admin = new AuthenticateDefaultAdmin();
		IResult r = admin.auth();
		String sToken = (String)r.getResultObject();
		System.out.println("X "+sToken);
//X {"rMsg":"ok","rToken":"1c8e4c44-aa7c-45a2-8f28-4248a33978fa","cargo":{"active":true,"handle":"therealdefaultadmin","uFullName":"Default Admin","roleList":["rar","ror"],"locator":"ef4da398-7440-4b23-b5a0-1331cc333141","email":"default@example.com"}}
		JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		try {
			JSONObject jo =  (JSONObject)p.parse(sToken);
			sToken = jo.getAsString("rToken");
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject query = qb.coreQuery(DEACT_V, ADMIN_ID, null, sToken);
		query.put(ICredentialsMicroformat.USER_ID, USER_ID);
		System.out.println("A "+query.toJSONString());
//A {"uId":"JoeSixpack","sToken":"1c8e4c44-aa7c-45a2-8f28-4248a33978fa","verb":"RemUser","uIP":""}

		String q = query.toJSONString();
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		r = client.put(BASE_URL, q);
		System.out.println("B "+r.getErrorString());
		System.out.println("C "+r.getResultObject());
//B 
//C {"rMsg":"ok","rToken":""}
		System.out.println("******************");
		new ListUsers();
//C {"rMsg":"ok","rToken":"","cargo":[
//{"active":false,"handle":"joe","uFullName":"Joe Sixpack","roleList":["rur"],"locator":"JoeSixpack","email":"joe@sixpack.com","homepage":"http:\/\/joe.sixpack.com\/"},{"active":true,"handle":"SystemUser","uFullName":null,"locator":"SystemUser","email":"sysusr@topicquests.org"},{"active":true,"handle":"therealdefaultadmin","uFullName":"Default Admin","roleList":["rar","ror"],"locator":"ef4da398-7440-4b23-b5a0-1331cc333141","email":"default@example.com"}]}

		query = qb.coreQuery(REACT_V, ADMIN_ID, null, sToken);
		query.put(ICredentialsMicroformat.USER_ID, USER_ID);
		System.out.println("A "+query.toJSONString());
		
		q = query.toJSONString();
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		r = client.put(BASE_URL, q);
		System.out.println("B "+r.getErrorString());
		System.out.println("C "+r.getResultObject());

		System.out.println("******************");
		new ListUsers();
//C {"rMsg":"ok","rToken":"","cargo":[
//{"active":true,"handle":"joe","uFullName":"Joe Sixpack","roleList":["rur"],"locator":"JoeSixpack","email":"joe@sixpack.com","homepage":"http:\/\/joe.sixpack.com\/"},{"active":true,"handle":"SystemUser","uFullName":null,"locator":"SystemUser","email":"sysusr@topicquests.org"},{"active":true,"handle":"therealdefaultadmin","uFullName":"Default Admin","roleList":["rar","ror"],"locator":"ef4da398-7440-4b23-b5a0-1331cc333141","email":"default@example.com"}]}
		
	}

}
