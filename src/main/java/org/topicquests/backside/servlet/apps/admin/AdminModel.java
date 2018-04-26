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
package org.topicquests.backside.servlet.apps.admin;

import org.topicquests.backside.servlet.ServletEnvironment;
import org.topicquests.backside.servlet.apps.admin.api.IAdminModel;
import org.topicquests.backside.servlet.apps.admin.api.IInviteDatabase;
import org.topicquests.backside.servlet.apps.admin.persist.H2InviteDatabase;
import org.topicquests.backside.servlet.apps.usr.api.IUserModel;
import org.topicquests.backside.servlet.apps.usr.api.IUserSchema;
import org.topicquests.ks.api.ITicket;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author park
 */
public class AdminModel implements IAdminModel {
	private ServletEnvironment environment;
	private IUserModel userModel;
	private IInviteDatabase database;

	/**
	 * Pools Connections for each local thread
	 * Must be closed when the thread terminates
	 */
	private ThreadLocal<Connection> localMapConnection = new ThreadLocal<Connection>();

	/**
	 *
	 */
	public AdminModel(ServletEnvironment env) throws Exception {
		environment = env;
		userModel = environment.getUserModel();
		//build invite database
		String dbName = environment.getStringProperty("InviteDatabase");
		String userName = environment.getStringProperty("MyDatabaseUser");
		String userPwd = environment.getStringProperty("MyDatabasePwd");
		String dbPath = environment.getStringProperty("UserDatabasePath");
		database = new H2InviteDatabase(environment, dbName, userName, userPwd, dbPath);

	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.admin.api.IInviteModel#existsInvite(java.lang.String)
	 */
	@Override
	public IResult existsInvite(String userEmail) {
		System.out.println("AdminModel.existsInvite " + userEmail);
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.existsInvite(con, userEmail);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.admin.api.IInviteModel#addInvite(java.lang.String)
	 */
	@Override
	public IResult addInvite(String userEmail) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		System.out.println("MODELADDINVITE " + con + " " + userEmail);
		return database.addInvite(con, userEmail);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.admin.api.IInviteModel#removeInvite(java.lang.String)
	 */
	@Override
	public IResult removeInvite(String userEmail) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.removeInvite(con, userEmail);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.admin.api.IAdminModel#removeUser(java.lang.String)
	 */
	@Override
	public IResult deactivateUser(String userId, ITicket credentials) {
		environment.logDebug("AdminModel.deactivateUser "+credentials.toJSONString());
		return userModel.deactivateUser(userId, credentials);
	}

	@Override
	public IResult reactivateUser(String userId, ITicket credentials) {
		return userModel.reactivateUser(userId, credentials);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.admin.api.IAdminModel#listUsers(int, int)
	 */
	@Override
	public IResult listUsers(int start, int count) {
		return userModel.listUsers(start, count);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.admin.api.IAdminModel#updateUserRole(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult addUserRole(String userId, String newRole) {
		System.out.println("UpdateUserRole " + userId + " " + newRole);
		return userModel.addUserRole(userId, newRole);
	}

	@Override
	public IResult removeUserRole(String userId, String oldRole) {
		System.out.println("RemoveUserRole " + userId + " " + oldRole);
		return userModel.removeUserData(userId, IUserSchema.USER_ROLE, oldRole);
	}


	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.admin.api.IAdminModel#updateUserEmail(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult updateUserEmail(String userId, String newEmail) {
		return userModel.updateUserEmail(userId, newEmail);
	}

	@Override
	public IResult listInvites(int start, int count) {
		Connection con = null;
		IResult r = getMapConnection();
		if (r.hasError())
			return r;
		con = (Connection) r.getResultObject();
		return database.listInvites(con, start, count);
	}


	private IResult getMapConnection() {
		synchronized (localMapConnection) {
			IResult result = new ResultPojo();
			try {
				Connection con = this.localMapConnection.get();
				//because we don't "setInitialValue", this returns null if nothing for this thread
				if (con == null) {
					con = database.getConnection();
					localMapConnection.set(con);
					System.out.println("GETMAPCONNECTIOn " + con);
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
