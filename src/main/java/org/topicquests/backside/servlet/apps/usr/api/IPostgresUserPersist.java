/**
 * 
 */
package org.topicquests.backside.servlet.apps.usr.api;


import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public interface IPostgresUserPersist {

	IResult authenticate(String email, String password);

	IResult getTicketByHandle(String userHandle);

	IResult getTicketByEmail(String email);

	IResult getTicketById(String userId);

	/**
	 * <p>
	 * Throws an exception if user already exists. Should
	 * use <code>existsUsername</code> first
	 * </p>
	 *
	 * @param email
	 * @param userHandle
	 * @param userId       TODO
	 * @param password
	 * @param userFullName TODO
	 * @param avatar
	 * @param role         cannot be <code>null</code>
	 * @param homepage     TODO
	 * @param geolocation  TODO
	 * @return
	 */
	IResult insertUser(String email,
					   String userHandle,
					   String userId, String password, String userFullName, String avatar, String role, String homepage, String geolocation);

	IResult insertUserData(String userName, String propertyType, String propertyValue);

	IResult updateUserData(String userName, String propertyType, String newValue);

	IResult removeUserData(String userName, String propertyType, String oldValue);

	/**
	 * Returns Boolean value as result
	 *
	 * @param con
	 * @param userName
	 * @return
	 */
	IResult existsUsername(String userName);

	/**
	 * @param userName
	 * @return
	 */
	IResult removeUser(String userName);
	
	IResult changeUserPassword(String userName, String newPassword);

	IResult addUserRole(String userName, String newRole);

	IResult removeUserRole(String userName, String oldRole);

	IResult updateUserEmail(String userName, String newEmail);

	/**
	 * <p>Return List of <code>locators</code></p>
	 *
	 * @param con
	 * @return can return <code>null</code> or List<String>
	 */
	IResult listUserLocators();

	/**
	 * @param con
	 * @param start
	 * @param count
	 * @return a possibly empty list of {@link ITicket} objects
	 */
	IResult listUsers(int start, int count);

}
