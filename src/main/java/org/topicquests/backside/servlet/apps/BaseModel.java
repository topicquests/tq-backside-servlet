/**
 * 
 */
package org.topicquests.backside.servlet.apps;

import org.topicquests.backside.servlet.ServletEnvironment;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.api.ICoreIcons;
import org.topicquests.ks.tm.api.IDataProvider;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.pg.PostgresConnectionFactory;

/**
 * @author jackpark
 *
 */
public class BaseModel {
	protected ServletEnvironment environment;
	protected SystemEnvironment tmEnvironment;
	protected IDataProvider topicMap;
	protected IProxyModel nodeModel;
	protected PostgresConnectionFactory dbProvider;

	/**
	 * 
	 */
	public BaseModel(ServletEnvironment env) {
		environment = env;
		tmEnvironment = environment.getTopicMapEnvironment();
		topicMap = tmEnvironment.getDataProvider();
		nodeModel = tmEnvironment.getProxyModel();
		dbProvider = topicMap.getDBProvider();
	}

	protected IResult relateNodeToUser(IProxy node, String userId, String provenanceLocator, ITicket credentials) {
		IResult result = new ResultPojo();
		String subjectRoleLocator = null; //TODO
		String objectRoleLocator = null; //TODO
		//NOW, relate this puppy
		String relation = "DocumentCreatorRelationType";
		IResult r;
			r = topicMap.getNode(userId, credentials);
			if (r.hasError())
				result.addErrorString(r.getErrorString());
			IProxy user = (IProxy)r.getResultObject();
			if (user != null) {
				//ISubjectProxy sourceNode,ISubjectProxy targetNode, String relationTypeLocator, String userId,
				//String smallImagePath, String largeImagePath, boolean isTransclude,boolean isPrivate
				environment.logDebug("RELATING-3 \n"+node.toJSONString()+"\n"+user.toJSONString());
				r = nodeModel.relateExistingNodes(node, user, relation, subjectRoleLocator, objectRoleLocator,
						userId, provenanceLocator,
						ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, false, false);
				if (r.hasError())
					result.addErrorString(r.getErrorString());
			} else
				result.addErrorString("Missing User "+userId);
		return result;
	}
}
