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
package org.topicquests.backside.servlet.apps.usr;

import org.topicquests.backside.servlet.ServletEnvironment;
import org.topicquests.backside.servlet.api.ISecurity;
import org.topicquests.backside.servlet.apps.usr.api.IUserModel;
import org.topicquests.backside.servlet.apps.usr.api.IH2UserPersist;
import org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist;
import org.topicquests.backside.servlet.apps.usr.persist.H2UserDatabase;
import org.topicquests.backside.servlet.apps.usr.persist.PostgresUserDatabase;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.api.ICoreIcons;
import org.topicquests.ks.api.IExtendedCoreOntology;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.IDataProvider;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author park
 */
public class UserModel implements IUserModel {
	private ServletEnvironment environment;
	//private IH2UserPersist database;
	private IPostgresUserPersist database;
	private IDataProvider topicMap;
	private IProxyModel nodeModel;

	/**
	 *
	 */
	public UserModel(ServletEnvironment env) throws Exception {
		environment = env;

		database = new PostgresUserDatabase(environment);
		SystemEnvironment tmenv = environment.getTopicMapEnvironment();
		System.out.println("FOO " + tmenv);
		topicMap = tmenv.getDataProvider();
		nodeModel = tmenv.getProxyModel();
		validateDefaultAdmin();
	}

	private void validateDefaultAdmin() {
		//from config-props.xml

		String id = environment.getStringProperty("DefaultAdminId");
		String email = environment.getStringProperty("DefaultAdminEmail");
		String name = environment.getStringProperty("DefaultAdminName");
		String pwd = environment.getStringProperty("DefaultAdminPwd");
		String languageCode = "en";
		System.out.println("VALIDATE " + id + " | " + email + " | " + name + " | " + pwd);
		IResult r = authenticate(email, pwd);
		if (r.getResultObject() == null) {
			r = this.insertUser(email, name, languageCode, id, pwd, "Default Admin", "", ISecurity.ADMINISTRATOR_ROLE, "", "", false);
			r = this.addUserRole(id, ISecurity.OWNER_ROLE);
		}
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult authenticate(String email, String password) {
		return database.authenticate(email, password);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#getTicket(java.lang.String)
	 */
	@Override
	public IResult getTicketByEmail(String email) {
		return database.getTicketByEmail(email);
	}


	@Override
	public IResult getTicketById(String userId) {
		return database.getTicketById(userId);
	}

	@Override
	public IResult getTicketByHandle(String userHandle) {
		return database.getTicketByHandle(userHandle);
	}


	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#insertUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public IResult insertUser(String email, String userHandle, String languageCode, String userId, String password, String userFullName, String avatar, String role, String homepage, String geolocation, boolean addTopic) {
		environment.logDebug("UserModel.insertUser "+userHandle+" "+userId);
		String uid = userId;
		if (uid == null)
			uid = UUID.randomUUID().toString();
		IResult result = new ResultPojo();
		if (addTopic) {
			String s = userFullName;
			if (s.equals(""))
				s = userHandle;
			IProxy n = nodeModel.newInstanceNode(uid, ITQCoreOntology.USER_TYPE, s, "", "en",
					ITQCoreOntology.SYSTEM_USER, IExtendedCoreOntology.BOOTSTRAP_PROVENANCE_TYPE,
					ICoreIcons.PERSON_ICON_SM, ICoreIcons.PERSON_ICON, false);
			result = topicMap.putNode(n);
		}
		IResult x = database.insertUser(email, userHandle, languageCode, uid, password, userFullName, avatar, role, homepage, geolocation);
		if (x.hasError())
			result.addErrorString(x.getErrorString());
		result.setResultObject(x.getResultObject());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#insertEncryptedUser(java.lang.String, java.lang.String, java.lang.String)
	 * /
	@Override
	public IResult insertEncryptedUser(String userName, String password,
			String grant) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#insertUserData(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public IResult insertUserData(String userId, String propertyType,
								  String propertyValue) {
		return database.insertUserData(userId, propertyType, propertyValue);
	}

	@Override
	public IResult removeUserData(String userId, String propertyType, String propertyValue) {
		return database.removeUserData(userId, propertyType, propertyValue);
	}


	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#updateUserData(java.lang.String, java.lang.String, java.lang.String)
	 * /
	@Override
	public IResult addUserData(String userId, String propertyType,
			String newValue) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection)r.getResultObject();
		return database.insertUserData(con, userId, propertyType, newValue);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#existsUsername(java.lang.String)
	 */
	@Override
	public IResult existsUsername(String handle) {
		return database.existsUserHandle(handle);
	}

	@Override
	public IResult existsUserEmail(String email) {
		IResult result = new ResultPojo();
		IResult r = this.getTicketByEmail(email);
		//System.out.println("CHECKEMAIL "+email+" "+r.getResultObject());
		//return true only if this email exists
		if (r.getResultObject() == null)
			result.setResultObject(new Boolean(false));
		else
			result.setResultObject(new Boolean(true));
		return result;
	}


	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#removeUser(java.lang.String)
	 */
	@Override
	public IResult deactivateUser(String userId, ITicket credentials) {
		return database.deactivateUser(userId, credentials);
	}
	
	@Override
	public IResult reactivateUser(String userId, ITicket credentials) {
		return database.reactivateUser(userId, credentials);
	}


	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#changeUserPassword(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult changeUserPassword(String userId, String newPassword) {
		return database.changeUserPassword(userId, newPassword);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#addUserRole(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult addUserRole(String userId, String newRole) {
		return database.addUserRole(userId, newRole);
	}

	@Override
	public IResult updateUserEmail(String userId, String newEmail) {
		return database.updateUserEmail(userId, newEmail);
	}


	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#listUserLocators()
	 */
	@Override
	public IResult listUserLocators() {
		return database.listUserLocators();
	}

	@Override
	public IResult listUsers(int start, int count) {
		return database.listUsers(start, count);
	}

	@Override
	public void shutDown() {
		//
	}

}
