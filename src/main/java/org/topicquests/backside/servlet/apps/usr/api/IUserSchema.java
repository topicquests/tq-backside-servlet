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
package org.topicquests.backside.servlet.apps.usr.api;

/**
 * @author park
 */
public interface IUserSchema {
	public static final String
			USER_EMAIL = "email",
			USER_NAME = "name",
			USER_ID = "id",
			USER_FULLNAME = "fullName",
			USER_PASSWORD = "pwd",
			USER_AVATAR = "avatar",
			USER_ROLE = "role", // a collection
			USER_GEOLOC = "geoloc",
			USER_HOMEPAGE = "homepage";


	public static final String[] TABLES = {
			"CREATE TABLE users ("
					+ "email VARCHAR_IGNORECASE(255) NOT NULL,"
					+ "pwd VARCHAR(128) NOT NULL,"
					+ "id UUID PRIMARY KEY,"
					+ "name VARCHAR_IGNORECASE(128) NOT NULL," // name is user's handle
					+ "fullName VARCHAR(128) NOT NULL)",
			//a rule can be a list of roles, each a short string
			//the first role is usually the primary role, e.g. USER_ROLE from ISecurity
			"CREATE TABLE userprops ("
					+ "userId UUID NOT NULL,"
					+ "prop VARCHAR(16) NOT NULL,"
					+ "val VARCHAR(128) NOT NULL)",
			"CREATE UNIQUE INDEX emailindex ON users(email)",
			"CREATE UNIQUE INDEX nameindex ON users(name)",
			"CREATE INDEX propindex ON userprops(userId, prop)",
			"CREATE UNIQUE INDEX upropindex ON userprops(userId, prop, val)"};

	public static final String getUserByEmail =
			"SELECT * FROM users WHERE email=?";
	public static final String getUserByHandle =
			"SELECT * FROM users WHERE name=?";

	public static final String getUserById =
			"SELECT * FROM users WHERE id=?";

	public static final String removeUser =
			"DELETE FROM users WHERE name=?";
	public static final String updateUserPwd =
			"UPDATE users  SET pwd=? WHERE name=?";

	public static final String updateUserEmail =
			"UPDATE users  SET email=? WHERE name=?";

	public static final String getUserProperties =
			"SELECT * FROM userprops WHERE userId=?";

	public static final String updateUserProperty =
			"UPDATE userprops  SET val=? WHERE prop=? AND userId=?";

	/**
	 * Intended for key-value pairs which occupy multiple rows, e.g. roles
	 */
	public static final String changeUserProperty =
			"UPDATE userprops  SET val=? WHERE prop=? AND val=? AND userId=?";

	public static final String removeUserProperty =
			"DELETE FROM userprops WHERE userId=? AND prop=? AND val=?";
	public static final String putUserProperty =
			"INSERT INTO userprops values(?,?,?)";

	public static final String putUser =
			"INSERT INTO users values(?, ?, ?, ?, ?)";


	public static final String listUserNames =
			"SELECT name FROM users";

	public final String listUsers =
			"SELECT * FROM users ";

	public final String listUsersLimited =
			"SELECT * FROM users LIMIT ? OFFSET ?";

	public static final String authenticate =
			"select * FROM users WHERE email=? OR name=?";
}
