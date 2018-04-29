/**
 * 
 */
package devtests;

import java.util.UUID;

import org.topicquests.ks.api.ITQCoreOntology;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 * Note: these do not add create and edit dates: those are
 * added at BacksideServlet
 */
public class NodeBuilder {

	/**
	 * 
	 */
	public NodeBuilder() {
	}

	/**
	 * 
	 * @param locator can be <code>null</code>
	 * @param typeLocator
	 * @param userId
	 * @param label
	 * @param details
	 * @param language
	 * @param conversationParent TODO
	 * @param conversationContext TODO
	 * @param url TODO
	 * @param largeImagePath can be <code>null</code
	 * @param smallImagePath can be <code>null</code
	 * @param isPrivate
	 * @return
	 */
	public JSONObject newInstanceNode(String locator,
									  String typeLocator,
									  String userId,
									  String label,
									  String details,
									  String language,
									  String conversationParent,
									  String conversationContext,
									  String url, 
									  String largeImagePath, 
									  String smallImagePath, 
									  boolean isPrivate) {
		JSONObject result = new JSONObject();
		String lox = locator;
		if (lox == null)
			lox = UUID.randomUUID().toString();
		result.put(ITQCoreOntology.LOCATOR_PROPERTY, lox);
		result.put("Lang", language);
		if (label != null && !label.equals(""))
			result.put("label", label);
		if (details != null && !details.equals(""))
			result.put("details", details);
		if (null != largeImagePath) {
            result.put("lIco", largeImagePath);
        }
        if (null != smallImagePath) {
            result.put("sIco", smallImagePath);
        }
        String p = "F";
        if (isPrivate) {
            p = "T";
        }
        if (conversationParent != null && !conversationParent.equals("") && 
        	conversationContext != null && !conversationParent.equals("")) {
        	result.put("ConParentLocator", conversationParent);
        	result.put("ContextLocator", conversationContext);
        }
        if (url != null && !url.equals(""))
        	result.put("url", url);
        result.put("isPrv", p);
        result.put("inOf", typeLocator);
        result.put("uId", userId);
		return result;
	}
	
	/**
	 * 
	 * @param locator can be <code>null</code>
	 * @param superClassLocator
	 * @param userId
	 * @param label
	 * @param details
	 * @param language
	 * @param largeImagePath can be <code>null</code
	 * @param smallImagePath can be <code>null</code
	 * @param isPrivate
	 * @return
	 */
	public JSONObject newSubclassNode(String locator,
			  String superClassLocator,
			  String userId,
			  String label,
			  String details,
			  String language,
			  String largeImagePath,
			  String smallImagePath,
			  boolean isPrivate) {
		JSONObject result = new JSONObject();
		String lox = locator;
		if (lox == null)
			lox = UUID.randomUUID().toString();
		result.put(ITQCoreOntology.LOCATOR_PROPERTY, lox);
		result.put("Lang", language);
		if (label != null && !label.equals(""))
			result.put("label", label);
		if (details != null && !details.equals(""))
			result.put("details", details);
		if (null != largeImagePath) {
            result.put("lIco", largeImagePath);
        }
        if (null != smallImagePath) {
            result.put("sIco", smallImagePath);
        }
        String p = "F";
        if (isPrivate) {
            p = "T";
        }
        result.put("isPrv", p);
        result.put(ITQCoreOntology.SUBCLASS_OF_PROPERTY_TYPE, superClassLocator);	
        result.put("uId", userId);
        return result;
	}
}
