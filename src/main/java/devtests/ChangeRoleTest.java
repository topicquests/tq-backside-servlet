/**
 * 
 */
package devtests;

import java.net.URLEncoder;

import org.topicquests.backside.servlet.api.ICredentialsMicroformat;
import org.topicquests.backside.servlet.api.ISecurity;
import org.topicquests.backside.servlet.apps.admin.api.IAdminMicroformat;
import org.topicquests.backside.servlet.apps.usr.api.IUserMicroformat;
import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * @author jackpark
 * Add a new role to JoeSixpack, show it's there, then remove it
 * and show it's gone
 */
public class ChangeRoleTest {
	private QueryBuilder qb;
	private SimpleHttpClient client;
	private AuthenticateDefaultAdmin admin;
	private final String
		BASE_URL	= "http://localhost:8080/admin/",
		ADMIN_ID	= "ef4da398-7440-4b23-b5a0-1331cc333141", //Run ListUsers to get the default admin
		USER_ID		= "JoeSixpack",
		ADD_R		= IAdminMicroformat.UPDATE_USER_ROLE,
		REM_R		= IAdminMicroformat.REMOVE_USER_ROLE,
		NEW_R		= ISecurity.MODERATOR_ROLE;

	/**
	 * 
	 */
	public ChangeRoleTest() {
		qb = new QueryBuilder();
		client = new SimpleHttpClient();
		admin = new AuthenticateDefaultAdmin();
		IResult r = admin.auth();
		String sToken = (String)r.getResultObject();
		System.out.println("X "+sToken);
		JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		try {
			JSONObject jo =  (JSONObject)p.parse(sToken);
			sToken = jo.getAsString("rToken");
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject query = qb.coreQuery(ADD_R, ADMIN_ID, null, sToken);
		query.put(IUserMicroformat.USER_ROLE, NEW_R);
		query.put(ICredentialsMicroformat.USER_ID, USER_ID);
		System.out.println("A "+query.toJSONString());
		String q = query.toJSONString();
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
//C {"rMsg":"ok","rToken":"","cargo":[{"active":true,"handle":"joe","uFullName":"Joe Sixpack","roleList":["rur","mor"],"locator":"JoeSixpack","email":"joe@sixpack.com","homepage":"http:\/\/joe.sixpack.com\/"},{"active":true,"handle":"SystemUser","uFullName":null,"locator":"SystemUser","email":"sysusr@topicquests.org"},{"active":true,"handle":"therealdefaultadmin","uFullName":"Default Admin","roleList":["rar","ror"],"locator":"ef4da398-7440-4b23-b5a0-1331cc333141","email":"default@example.com"}]}

		query = qb.coreQuery(REM_R, ADMIN_ID, null, sToken);
		query.put(IUserMicroformat.USER_ROLE, NEW_R);
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
//C {"rMsg":"ok","rToken":"","cargo":[{"active":true,"handle":"joe","uFullName":"Joe Sixpack","roleList":["rur"],"locator":"JoeSixpack","email":"joe@sixpack.com","homepage":"http:\/\/joe.sixpack.com\/"},{"active":true,"handle":"SystemUser","uFullName":null,"locator":"SystemUser","email":"sysusr@topicquests.org"},{"active":true,"handle":"therealdefaultadmin","uFullName":"Default Admin","roleList":["rar","ror"],"locator":"ef4da398-7440-4b23-b5a0-1331cc333141","email":"default@example.com"}]}

	}

}
