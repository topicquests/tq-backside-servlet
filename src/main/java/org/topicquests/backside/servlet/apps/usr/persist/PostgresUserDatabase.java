/**
 * 
 */
package org.topicquests.backside.servlet.apps.usr.persist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.topicquests.backside.servlet.ServletEnvironment;
import org.topicquests.backside.servlet.api.IErrorMessages;
import org.topicquests.backside.servlet.api.ISecurity;
import org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist;
import org.topicquests.backside.servlet.apps.usr.api.IUserMicroformat;
import org.topicquests.backside.servlet.apps.usr.api.IUserSchema;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

//import net.minidev.json.JSONObject;
//import net.minidev.json.parser.JSONParser;

import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.pg.api.IPostgresConnection;


/**
 * @author jackpark
 *
 */
public class PostgresUserDatabase implements IPostgresUserPersist {
	private ServletEnvironment environment;
	private PostgresConnectionFactory database;
	/**
	 * 
	 */
	public PostgresUserDatabase(ServletEnvironment env) {
		environment = env;
		database = environment.getTopicMapEnvironment().getDataProvider().getDBProvider();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult authenticate(String email, String password) {
		environment.logDebug("PostgresUserDatabase.authenticate "+email+" "+password);
		IResult result = new ResultPojo();
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "SELECT * FROM tq_authentication.user_locator(?, ?)";
	        Object [] vals = new Object[2];
	        vals[0] = email;
	        vals[1] = password;
	        conn.executeSelect(sql, r, vals);
	        if (r.hasError()) {
	        	String ex = "PostgresUserDatabase.authenticate error: "+r.getErrorString();
	        	environment.logError(ex, null);
	        	result.addErrorString(ex);
	        }
	        ResultSet rs = (ResultSet)r.getResultObject();
	    	environment.logDebug("PostgresUserDatabase.authenticate-1 "+rs);
	        if (rs != null) {
	        	if (rs.next()) {
	        		String userid = rs.getString(1);
	        		if (userid != null) {
	        			r = this.getTicketById(conn, userid);
	        			result.setResultObject(r.getResultObject());
	        		} else {
	        			result.addErrorString(IErrorMessages.AUTHENTICATION_FAIL+" : "+email);
	        		}
	        	}
	        } else {
	        	result.addErrorString(IErrorMessages.AUTHENTICATION_FAIL+" : "+email);
	        }
	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
    	environment.logDebug("PostgresUserDatabase.authenticate+ "+result.getResultObject());
	    
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#getTicketByHandle(java.lang.String)
	 */
	@Override
	public IResult getTicketByHandle(String userHandle) {
		IResult result = new ResultPojo();
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "SELECT userid FROM tq_authentication.users where handle=?";
	        conn.executeSelect(sql, r, userHandle);
	        ResultSet rs = (ResultSet)r.getResultObject();
	        if (rs != null) {
	        	if (rs.next()) {
	        		String userid = rs.getString("userid");
	        		result = this.getTicketById(conn, userid);
	        	}
	        }
	        conn.endTransaction();
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#getTicketByEmail(java.lang.String)
	 */
	@Override
	public IResult getTicketByEmail(String email) {
		IResult result = new ResultPojo();
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "SELECT userid FROM tq_authentication.users where email=?";
	        conn.executeSelect(sql, r, email);
	        ResultSet rs = (ResultSet)r.getResultObject();
	        if (rs != null) {
	        	if (rs.next()) {
	        		String userid = rs.getString("userid");
	        		result = this.getTicketById(conn, userid);
	        	}
	        }
	        conn.endTransaction();
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#getTicketById(java.lang.String)
	 */
	@Override
	public IResult getTicketById(String userId) {
		IResult result = new ResultPojo();
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        result = getTicketById(conn, userId);
	        conn.endTransaction();
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		return result;
	}

	IResult getTicketById(IPostgresConnection conn, String userId) {
		environment.logDebug("PostgresUserDatabase.getTicketById "+userId);
		IResult result = new ResultPojo();
		ITicket t = new TicketPojo();
		result.setResultObject(t);
		String sql = "SELECT * FROM tq_authentication.users WHERE userid=?";
		IResult r = conn.executeSelect(sql, userId);
		ResultSet rs = (ResultSet)r.getResultObject();
		boolean isActive = true;
		boolean haveT = false;
		if (r.hasError())
			result.addErrorString(r.getErrorString());
		if (rs != null) {
			try {
				if (rs.next()) {
					t.setActive(rs.getBoolean("active"));
					t.setEmail(rs.getString("email"));
					t.setProperty(IUserMicroformat.USER_FULLNAME, rs.getString("full_name"));
					t.setHandle(rs.getString("handle"));
					t.setUserLocator(rs.getString("userid"));
					//check active
					if (!t.getActive()) {
						isActive = false;
						result.setResultObject(null);
						result.addErrorString(IErrorMessages.INACTIVE_USER);
					} else {
						haveT = true;
					}

				}
				
			} catch (SQLException e) {
				environment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		if (isActive && haveT) {
			sql = "SELECT property_key, property_val FROM tq_authentication.user_properties WHERE userid=?";
			r = conn.executeSelect(sql, userId);
			rs = (ResultSet)r.getResultObject();
			if (r.hasError())
				result.addErrorString(r.getErrorString());
			if (rs != null) {
				try {
					String key, val;
					while (rs.next()) {
						key = rs.getString("property_key");
						val = rs.getString("property_val");
						environment.logDebug("PostgresUserDatabase.getTicketById-1 "+key+" "+val);
						if (key.equals(IUserSchema.USER_ROLE))
							t.addRole(val);
						else if (key.equals(IUserMicroformat.USER_HOMEPAGE))
							t.setProperty(IUserMicroformat.USER_HOMEPAGE, val);
						else
							t.setProperty(key, val);
						//TODO
						//Some might be collections: need to see if key already exists
					}
				} catch (Exception e) {
					environment.logError(e.getMessage(), e);
					result.addErrorString(e.getMessage());
				}
			}
		}
		environment.logDebug("PostgresUserDatabase.getTicketById+ "+r.getErrorString()+" | "+t.getData());

		return result;
	}
	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#insertUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public IResult insertUser(String email, String userHandle, String languageCode, String userId, String password,
			String userFullName, String avatar, String role, String homepage, String geolocation) {
		environment.logDebug("PostgresUserDatabase.insertUser "+email+" | "+userHandle+" | "+languageCode+
				" | "+userId+" | "+password+" | "+userFullName+" | "+avatar+" | "+role+" | "+homepage);

		IResult result = new ResultPojo();
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        //core user
	        String sql = "INSERT INTO tq_authentication.users (userid, email, password, handle, full_name, language) VALUES(?, ?, ?, ?, ?, ?)";
	        Object [] vals = new Object[6];
	        vals[0] = userId;
	        vals[1] = email;
	        vals[2] = password;
	        vals[3] = userHandle;
	        vals[4] = userFullName;
	        vals[5] = "en"; //default
	        if (languageCode != null)
	        	vals[5] = languageCode;
	        conn.executeSQL(sql, r, vals);
			environment.logDebug("PostgresUserDatabase.insertUser=1 "+r.getErrorString()+" "+r.getResultObject());
	        //user properties
	        sql = "INSERT INTO tq_authentication.user_properties (userid, property_key, property_val) VALUES(?, ?, ?)";
	        //role
	        vals = new Object[3];
	        vals[0] = userId;
	        vals[1] = IUserSchema.USER_ROLE;
	        vals[2] = role;
	        conn.executeSQL(sql, r, vals);
			environment.logDebug("PostgresUserDatabase.insertUser=2 "+r.getErrorString()+" "+r.getResultObject());
	        //avatar
	        if (avatar != null && !avatar.equals("")) {
		        vals = new Object[3];
		        vals[0] = userId;
		        vals[1] = IUserSchema.USER_AVATAR;
		        vals[2] = role;
		        conn.executeSQL(sql, r, vals);
				environment.logDebug("PostgresUserDatabase.insertUser=3 "+r.getErrorString()+" "+r.getResultObject());
	        }
	        //homepage
	        if (homepage != null && !homepage.equals("")) {
		        vals = new Object[3];
		        vals[0] = userId;
		        vals[1] = IUserSchema.USER_HOMEPAGE;
		        vals[2] = homepage;
		        conn.executeSQL(sql, r, vals);
				environment.logDebug("PostgresUserDatabase.insertUser=4 "+r.getErrorString()+" "+r.getResultObject());
	        }
	        //geoloc
	        //TODO Postgres has a different way to deal with geoloc
	        if (geolocation != null && !geolocation.equals("")) {
		        vals = new Object[3];
		        vals[0] = userId;
		        vals[1] = IUserSchema.USER_GEOLOC;
		        vals[2] = geolocation;
		        conn.executeSQL(sql, r, vals);
				environment.logDebug("PostgresUserDatabase.insertUser=5 "+r.getErrorString()+" "+r.getResultObject());
	        }

	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		environment.logDebug("PostgresUserDatabase.insertUser+ "+result.getErrorString()+" "+result.getResultObject());

		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#insertUserData(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public IResult insertUserData(String userId, String propertyType, String propertyValue) {
		IResult result = new ResultPojo();
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "INSERT INTO tq_authentication.user_properties (userid, property_key, property_val) VALUES(?, ?, ?)";
	        //role
	        Object [] vals = new Object[3];
	        vals[0] = userId;
	        vals[1] = propertyType;
	        vals[2] = propertyValue;
	        conn.executeSQL(sql, r, vals);
	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#updateUserData(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public IResult updateUserData(String userId, String propertyType, String oldValue, String newValue) {
		IResult result = new ResultPojo();
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "UPDATE tq_authentication.user_properties SET property_val = ? WHERE userid= ? AND property_key = ? AND property_val = ?";
	        Object [] vals = new Object[4];
	        vals[0] = newValue;
	        vals[1] = userId;
	        vals[2] = propertyType;
	        vals[3] = oldValue;
	        conn.executeUpdate(sql, r, vals);
	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#removeUserData(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public IResult removeUserData(String userId, String propertyType, String oldValue) {
		IResult result = new ResultPojo();
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "DELETE FROM tq_authentication.user_properties WHERE userid = ? AND property_key = ? AND property_val = ?";
	        Object [] vals = new Object[3];
	        vals[0] = userId;
	        vals[1] = propertyType;
	        vals[2] = oldValue;
	        conn.executeSQL(sql, r, vals);
	        if (r.hasError()) {
	        	environment.logError("PostgresUserDatabase.removeUserData "+userId+" "+propertyType+" "+oldValue+" "+r.getErrorString(), null);
	        }
	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#existsUsername(java.lang.String)
	 */
	@Override
	public IResult existsUserHandle(String handle) {
		IResult result = new ResultPojo();
		boolean exists = false;
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "SELECT userid FROM tq_authentication.users WHERE handle = ?";
	        conn.executeSelect(sql, r, handle);
	        ResultSet rs = (ResultSet)r.getResultObject();
	        if (rs != null && rs.next())
	        	exists = true;
	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
	    result.setResultObject(new Boolean(exists));
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#removeUser(java.lang.String)
	 */
	@Override
	public IResult deactivateUser(String userId, ITicket credentials) {
		IResult result = new ResultPojo();
		if (!credentials.hasRole(ISecurity.ADMINISTRATOR_ROLE)) {
			result.addErrorString(IErrorMessages.INSUFFICIENT_CREDENTIALS);
			return result;
		}
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "UPDATE tq_authentication.users SET active = ? WHERE userid = ?";
	        Object [] vals = new Object[2];
	        vals[0] = false;
	        vals[1] = userId;
	        conn.executeUpdate(sql, r, vals);
	        if (r.hasError())
	        	result.addErrorString(r.getErrorString());
	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		return result;
	}
	
	@Override
	public IResult reactivateUser(String userId, ITicket credentials) {
		IResult result = new ResultPojo();
		if (!credentials.hasRole(ISecurity.ADMINISTRATOR_ROLE)) {
			result.addErrorString(IErrorMessages.INSUFFICIENT_CREDENTIALS);
			return result;
		}
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "UPDATE tq_authentication.users SET active = ? WHERE userid = ?";
	        Object [] vals = new Object[2];
	        vals[0] = true;
	        vals[1] = userId;
	        conn.executeUpdate(sql, r, vals);
	        if (r.hasError())
	        	result.addErrorString(r.getErrorString());
	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#changeUserPassword(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult changeUserPassword(String userId, String newPassword) {
		IResult result = new ResultPojo();
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "UPDATE tq_authentication.users SET password = ? WHERE userid = ?";
	        Object [] vals = new Object[2];
	        vals[0] = newPassword;
	        vals[1] = userId;
	        conn.executeUpdate(sql, r, vals);
	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#addUserRole(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult addUserRole(String userId, String newRole) {
		return this.insertUserData(userId, IUserSchema.USER_ROLE, newRole);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#removeUserRole(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult removeUserRole(String userId, String oldRole) {
		return removeUserData(userId, IUserSchema.USER_ROLE, oldRole);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#updateUserEmail(java.lang.String, java.lang.String)
	 */
	@Override
	public IResult updateUserEmail(String userId, String newEmail) {
		IResult result = new ResultPojo();
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "UPDATE tq_authentication.users SET email = ? WHERE userid = ?";
	        Object [] vals = new Object[2];
	        vals[0] = newEmail;
	        vals[1] = userId;
	        conn.executeUpdate(sql, r, vals);
	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#listUserLocators()
	 */
	@Override
	public IResult listUserLocators() {
		IResult result = new ResultPojo();
		List<String>l = new ArrayList<String>();
		result.setResultObject(l);
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        conn.setUsersRole(r);
	        String sql = "SELECT userid FROM tq_authentication.users";
	        conn.executeSelect(sql, r);
	        ResultSet rs = (ResultSet)r.getResultObject();
	        if (rs != null) {
	        	while (rs.next())
	        		l.add(rs.getString("userid"));
	        }
	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.backside.servlet.apps.usr.api.IPostgresUserPersist#listUsers(int, int)
	 */
	@Override
	public IResult listUsers(int start, int count) {
		environment.logDebug("PostgresUserDatabase.listUsers "+start+" | "+count);
		IResult result = new ResultPojo();
		List<ITicket>l = new ArrayList<ITicket>();
		result.setResultObject(l);
	    try {
	        IPostgresConnection conn = database.getConnection();
	        IResult r = conn.beginTransaction();
	        //conn.setUsersRORole(r);
	        conn.setUsersRole(r);
	        String sql = "SELECT userid FROM tq_authentication.users ORDER BY userid OFFSET ?";
	        int ct = 1;
	        if (count > 0)
	        	ct ++;
	        Object [] vals = new Object[ct];
	        vals[0] = start;
	        if (ct > 1) {
	        	vals[1] = count;
	        	sql += " LIMIT ?";
	        }
	        	
	        conn.executeSelect(sql, r, vals);
	        ResultSet rs = (ResultSet)r.getResultObject();
			environment.logDebug("PostgresUserDatabase.listUsers-1 "+rs);
	        IResult x;
	        if (rs != null) {
	        	while (rs.next()) {
	        		x = getTicketById(rs.getString("userid"));
	        		if (x.getResultObject() != null)
	        			l.add((ITicket)x.getResultObject());
	        	}
	        }
	        conn.endTransaction(r);
	        conn.closeConnection(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
		    result.addErrorString(e.getMessage());
		}
		environment.logDebug("PostgresUserDatabase.listUsers+ "+l);
		return result;
	}


}
