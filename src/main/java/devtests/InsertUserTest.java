/**
 * 
 */
package devtests;

import java.net.URLEncoder;

import org.topicquests.backside.servlet.api.ICredentialsMicroformat;
import org.topicquests.backside.servlet.api.ISecurity;
import org.topicquests.backside.servlet.apps.usr.api.IUserMicroformat;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.support.SimpleHttpClient;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class InsertUserTest {
	private QueryBuilder qb;
	private SimpleHttpClient client;
	private final String
		BASE_URL	= "http://localhost:8080/user/",
		USER_ID		= "JoeSixpack",
		USER_EMAIL	= "joe@sixpack.com",
		USER_LANG	= "en",
		USER_HOMEPAGE	= "http://joe.sixpack.com/",
		USER_HANDLE		= "joe",
		USER_NAME	= "Joe Sixpack",
		USER_PWD	= "joeS!",
		VERB		= "NewUser",
		VERB2		= "ListUsers";

	/**
	 * 
	 */
	public InsertUserTest() {
		qb = new QueryBuilder();
		client = new SimpleHttpClient();
		JSONObject query = qb.coreQuery(VERB, USER_ID, null, null);
		query.put(ICredentialsMicroformat.USER_EMAIL, USER_EMAIL);
		query.put(ICredentialsMicroformat.USER_HANDLE, USER_HANDLE);
		query.put(IUserMicroformat.USER_FULLNAME, USER_NAME);
		query.put(IUserMicroformat.USER_ROLE, ISecurity.USER_ROLE);
		String pwd = java.util.Base64.getEncoder().encodeToString(USER_PWD.getBytes());
		query.put(IUserMicroformat.USER_PWD, pwd);
		query.put(IUserMicroformat.USER_HOMEPAGE, USER_HOMEPAGE);
		query.put(IUserMicroformat.USER_LANGUAGE, USER_LANG);
		System.out.println("A "+query.toJSONString());
		String q = query.toJSONString();
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		IResult r = client.put(BASE_URL, q);
		System.out.println("B "+r.getErrorString());
		System.out.println("C "+r.getResultObject());
		query = qb.coreQuery(VERB2, USER_ID, null, null);
		query.put(ICredentialsMicroformat.ITEM_FROM, 0);
		query.put(ICredentialsMicroformat.ITEM_COUNT, -1);
		q = query.toJSONString();
		System.out.println("x "+q);
		try {
			q = URLEncoder.encode(q, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		r = client.get(BASE_URL, q);
		System.out.println("D "+r.getErrorString());
		System.out.println("E "+r.getResultObject());
	}

}
//A {"uId":"JoeSixpack","uEmail":"joe@sixpack.com","uHomepage":"http:\/\/joe.sixpack.com\/","uName":"joe","sToken":"","uLang":"en","verb":"NewUser","uFullName":"Joe Sixpack","uRole":"rur","uIP":"","uPwd":"am9lUyE="}
//B 
//C {"rMsg":"ok","rToken":"","cargo":[{"active":true,"handle":"SystemUser","uFullName":null,"locator":"SystemUser","email":"sysusr@topicquests.org"},{"active":true,"handle":"therealdefaultadmin","uFullName":"Default Admin","roleList":["rar","ror"],"locator":"ef4da398-7440-4b23-b5a0-1331cc333141","email":"default@example.com"}]}
// NOTE: JoeSixpack not listed due to active = false and failed to be fetched
//D 
//E {"rMsg":"ok","rToken":"","cargo":[{"active":true,"handle":"joe","uFullName":"Joe Sixpack","roleList":["rur"],"locator":"JoeSixpack","email":"joe@sixpack.com","homepage":"http:\/\/joe.sixpack.com\/"},{"active":true,"handle":"SystemUser","uFullName":null,"locator":"SystemUser","email":"sysusr@topicquests.org"},{"active":true,"handle":"therealdefaultadmin","uFullName":"Default Admin","roleList":["rar","ror"],"locator":"ef4da398-7440-4b23-b5a0-1331cc333141","email":"default@example.com"}]}

