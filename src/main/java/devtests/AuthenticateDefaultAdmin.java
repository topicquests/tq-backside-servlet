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
	private AuthenticateAnyone auth;
	private final String
//		AUTH_URL	= "http://localhost:8080/auth/",
//		ADMIN_ID	= "ef4da398-7440-4b23-b5a0-1331cc333141", //from config-props.xml
		ADMIN_EMAIL	= "default@example.com", // from config-props.xml
		ADMIN_PWD	= "antiquing"; // from config-props.xml

	/**
	 * 
	 */
	public AuthenticateDefaultAdmin() {
		auth = new AuthenticateAnyone();
	}
	
	public IResult auth() {
		return auth.auth(ADMIN_EMAIL, ADMIN_PWD);
	}

}
