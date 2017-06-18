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
import org.topicquests.backside.servlet.apps.usr.api.IUserPersist;
import org.topicquests.backside.servlet.apps.usr.persist.H2UserDatabase;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.api.ICoreIcons;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITQDataProvider;
import org.topicquests.ks.tm.api.ISubjectProxy;
import org.topicquests.ks.tm.api.ISubjectProxyModel;
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
	private IUserPersist database;
	private ITQDataProvider topicMap;
	private ISubjectProxyModel nodeModel;
	/**
	 * Pools Connections for each local thread
	 * Must be closed when the thread terminates
	 */
	private ThreadLocal<Connection> localMapConnection = new ThreadLocal<Connection>();

	/**
	 *
	 */
	public UserModel(ServletEnvironment env) throws Exception {
		environment = env;
		String dbName = environment.getStringProperty("UserDatabase");
		String userName = environment.getStringProperty("MyDatabaseUser");
		String userPwd = environment.getStringProperty("MyDatabasePwd");
		String dbPath = environment.getStringProperty("UserDatabasePath");

		database = new H2UserDatabase(environment, dbName, userName, userPwd, dbPath);
		SystemEnvironment tmenv = environment.getTopicMapEnvironment();
		System.out.println("FOO " + tmenv);
		topicMap = tmenv.getDatabase();
		nodeModel = topicMap.getSubjectProxyModel();
		validateDefaultAdmin();
	}

	private void validateDefaultAdmin() {
		//from config-props.xml

		String id = environment.getStringProperty("DefaultAdminId");
		String email = environment.getStringProperty("DefaultAdminEmail");
		String name = environment.getStringProperty("DefaultAdminName");
		String pwd = environment.getStringProperty("DefaultAdminPwd");
		System.out.println("VALIDATE " + id + " | " + email + " | " + name + " | " + pwd);
		IResult r = authenticate(email, pwd);
		if (r.getResultObject() == null) {
			r = this.insertUser(email, name, id, pwd, "Default Admin", "", ISecurity.ADMINISTRATOR_ROLE, "", "", false);
			r = this.addUserRole(id, ISecurity.OWNER_ROLE);
		}
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult authenticate(String email, String password) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.authenticate(con, email, password);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#getTicket(java.lang.String)
	 */
	@Override
	public IResult getTicketByEmail(String email) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.getTicketByEmail(con, email);
	}


	@Override
	public IResult getTicketById(String userId) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.getTicketById(con, userId);
	}

	@Override
	public IResult getTicketByHandle(String userHandle) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.getTicketByHandle(con, userHandle);
	}


	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#insertUser(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public IResult insertUser(String email, String userHandle, String userId, String password, String userFullName, String avatar, String role, String homepage, String geolocation, boolean addTopic) {
		environment.logDebug("UserModel.insertUser "+userHandle+" "+userId);
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		String uid = userId;
		if (uid == null)
			uid = UUID.randomUUID().toString();
		IResult result = new ResultPojo();
		if (addTopic) {
			String s = userFullName;
			if (s.equals(""))
				s = userHandle;
			ISubjectProxy n = nodeModel.newInstanceNode(uid, ITQCoreOntology.USER_TYPE, s, "", "en",
					ITQCoreOntology.SYSTEM_USER, ICoreIcons.PERSON_ICON_SM, ICoreIcons.PERSON_ICON, false);
			result = topicMap.putNode(n);
		}
		IResult x = database.insertUser(con, email, userHandle, uid, password, userFullName, avatar, role, homepage, geolocation);
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
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();

		return database.insertUserData(con, userId, propertyType, propertyValue);
	}

	@Override
	public IResult removeUserData(String userId, String propertyType, String propertyValue) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();

		return database.removeUserData(con, userId, propertyType, propertyValue);
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
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.existsUsername(con, handle);
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
	public IResult removeUser(String userId) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.removeUser(con, userId);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#changeUserPassword(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult changeUserPassword(String userId, String newPassword) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.changeUserPassword(con, userId, newPassword);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#addUserRole(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult addUserRole(String userId, String newRole) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.addUserRole(con, userId, newRole);
	}

	@Override
	public IResult updateUserEmail(String userId, String newEmail) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.updateUserEmail(con, userId, newEmail);
	}


	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IUserModel#listUserLocators()
	 */
	@Override
	public IResult listUserLocators() {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.listUserLocators(con);
	}

	@Override
	public IResult listUsers(int start, int count) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.listUsers(con, start, count);
	}

	private IResult getMapConnection() {
		synchronized (localMapConnection) {
			IResult result = new ResultPojo();
			try {
				Connection con = this.localMapConnection.get();
				//because we don't "setInitialValue", this returns null if nothing for this thread
				if (con == null) {
					con = database.getConnection();
					System.out.println("GETMAPCONNECTION " + con);
					localMapConnection.set(con);
				}
				result.setResultObject(con);
			} catch (Exception e) {
				result.addErrorString(e.getMessage());
				environment.logError(e.getMessage(), e);
			}
			return result;
		}
	}

	public IResult closeLocalConnection() {
		IResult result = new ResultPojo();
		boolean isError = false;
		try {
			synchronized (localMapConnection) {
				Connection con = this.localMapConnection.get();
				if (con != null)
					con.close();
				localMapConnection.remove();
				//  localMapConnection.set(null);
			}
		} catch (SQLException e) {
			isError = true;
			result.addErrorString(e.getMessage());
		}
		if (!isError)
			result.setResultObject("OK");
		return result;
	}

	@Override
	public void shutDown() {
		closeLocalConnection();
	}


}
