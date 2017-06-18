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
package org.topicquests.backside.servlet.apps.admin.persist;

import org.topicquests.backside.servlet.ServletEnvironment;
import org.topicquests.backside.servlet.apps.admin.api.IInviteDatabase;
import org.topicquests.backside.servlet.apps.admin.api.IInviteSchema;
import org.topicquests.backside.servlet.persist.rdbms.H2DatabaseDriver;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author park
 */
public class H2InviteDatabase extends H2DatabaseDriver implements
		IInviteDatabase {

	/**
	 * @param env
	 * @param dbName
	 * @param userName
	 * @param pwd
	 * @param filePath
	 * @throws Exception
	 */
	public H2InviteDatabase(ServletEnvironment env, String dbName,
							String userName, String pwd, String filePath) throws Exception {
		super(env, dbName, userName, pwd, filePath);
		init();
	}

	private void init() throws Exception {
		Connection con = null;
		ResultSet rs = null;
		Statement s = null;
		try {
			con = getSQLConnection();
			System.out.println("H2InviteDatabase.init-1 " + con);
			s = con.createStatement();
			rs = s.executeQuery("select * from invites");
			environment.logDebug("H2InviteDatabase.init " + rs.next());
			System.out.println("H2InviteDatabase.init-2");
		} catch (Exception e) {
			System.out.println("H2InviteDatabase.init-3 ");
			environment.logDebug("H2InviteDatabase.init fail " + e.getMessage());
			exportSchema();
		} finally {
			if (s != null)
				s.close();
			if (rs != null)
				rs.close();
			if (con != null)
				con.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.admin.api.IInviteDatabase#existsInvite(java.sql.Connection, java.lang.String)
	 */
	@Override
	public IResult existsInvite(Connection con, String userEmail) {
		IResult result = new ResultPojo();
		PreparedStatement s = null;
		ResultSet rs = null;
		Boolean x;
		try {
			System.out.println("H2InviteDatabase.existsInvite " + userEmail);
			s = con.prepareStatement(IInviteSchema.getInvite);
			s.setString(1, userEmail);
			rs = s.executeQuery();
			if (rs.next())
				x = new Boolean(true);
			else
				x = new Boolean(false);
			result.setResultObject(x);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		} finally {
			closeStatement(s, result);
			closeResultSet(rs, result);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.admin.api.IInviteDatabase#addInvite(java.sql.Connection, java.lang.String)
	 */
	@Override
	public IResult addInvite(Connection con, String userEmail) {
		IResult result = new ResultPojo();
		PreparedStatement s = null;
		try {
			s = con.prepareStatement(IInviteSchema.insertInvite);
			s.setString(1, userEmail);
			boolean t = s.execute();
			System.out.println("ADDINVITE " + t + " " + userEmail);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		} finally {
			closeStatement(s, result);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.admin.api.IInviteDatabase#removeInvite(java.sql.Connection, java.lang.String)
	 */
	@Override
	public IResult removeInvite(Connection con, String userEmail) {
		IResult result = new ResultPojo();
		PreparedStatement s = null;
		try {
			s = con.prepareStatement(IInviteSchema.removeInvite);
			s.setString(1, userEmail);
			s.execute();
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		} finally {
			closeStatement(s, result);
		}
		return result;
	}

	@Override
	public IResult listInvites(Connection con, int start, int count) {
		IResult result = new ResultPojo();
		PreparedStatement s = null;
		ResultSet rs = null;
		String sql = IInviteSchema.listInvites;
		if (count > -1)
			sql = IInviteSchema.listInvitesLimited;

		try {
			List<String> users = new ArrayList<String>();
			result.setResultObject(users);
			s = con.prepareStatement(sql);
			if (count > -1) {
				s.setInt(1, count);
				s.setInt(2, start);
			}
			rs = s.executeQuery();
			while (rs.next()) {
				users.add(rs.getString("email"));
			}
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		} finally {
			closeStatement(s, result);
			closeResultSet(rs, result);
		}
		return result;
	}

	void exportSchema() throws Exception {
		Connection con = null;
		try {
			String[] sql = IInviteSchema.INVITE_SCHEMA;
			int len = sql.length;
			con = getSQLConnection();
			Statement s = con.createStatement();
			for (int i = 0; i < len; i++) {
				environment.logDebug(sql[i]);
				System.out.println("EXPORT: " + sql[i]);
				s.execute(sql[i]);
			}
			s.close();
			con.close();
		} catch (SQLException e) {
			throw new Exception(e);
		}
	}

}
