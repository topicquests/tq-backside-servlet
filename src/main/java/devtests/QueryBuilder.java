/**
 * 
 */
package devtests;

import org.topicquests.backside.servlet.api.ICredentialsMicroformat;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class QueryBuilder {

	/**
	 * 
	 */
	public QueryBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public JSONObject coreQuery(String verb, String userId, String userIP, String sToken) {
		JSONObject result = new JSONObject();
		result.put(ICredentialsMicroformat.VERB, verb);
		result.put(ICredentialsMicroformat.USER_ID, userId);
		if (userIP != null)
			result.put(ICredentialsMicroformat.USER_IP, userIP);
		else
			result.put(ICredentialsMicroformat.USER_IP, "");
		if (sToken != null)
			result.put(ICredentialsMicroformat.SESSION_TOKEN, sToken);
		else
			result.put(ICredentialsMicroformat.SESSION_TOKEN, "");
			
		return result;
	}

}
