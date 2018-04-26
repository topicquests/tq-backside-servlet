/*
 * Copyright 2015, TopicQuests
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.topicquests.backside.servlet.apps.admin.api;

import org.topicquests.ks.api.ITicket;
import org.topicquests.support.api.IResult;

/**
 * @author park
 */
public interface IAdminModel extends IInviteModel {

	/**
	 * @param userId
	 * @param credentials requires <em>Admin</em> credentials
	 * @return
	 */
	IResult deactivateUser(String userId, ITicket credentials);

	/**
	 * 
	 * @param userId
	 * @param credentials requires <em>Admin</em> credentials
	 * @return
	 */
	IResult reactivateUser(String userId, ITicket credentials);
	
	/**
	 * List users for inspection
	 *
	 * @param start
	 * @param count
	 * @return a possibly empty list of {@link ITicket} objects
	 */
	IResult listUsers(int start, int count);

	/**
	 * <p>NOTE: <em>role</em> is really a string of comma-delimited roles
	 * up to 255 characters in length</p>
	 * <p>At the UI, an Admin will insert or delete a role code from that string.</p>
	 * <p>What is returned here is that revised role string.</p>
	 *
	 * @param userId
	 * @param newRole
	 * @return
	 */
	IResult addUserRole(String userId, String newRole);

	IResult removeUserRole(String userId, String oldRole);

	IResult updateUserEmail(String userId, String newEmail);

	void shutDown();
}
