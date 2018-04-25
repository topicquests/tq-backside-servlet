/**
 * 
 */
package org.topicquests.backside.servlet.apps.usr.api;


import org.topicquests.support.api.IResult;
import org.topicquests.ks.api.ITicket;;
/**
 * @author jackpark
 *
 */
public interface IPostgresUserPersist {

	/**
	 * Authenticate will return an {@link ITicket} if authenticated,
	 * otherwise <code>null</code>
	 * @param email
	 * @param password
	 * @return
	 */
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
	 * @param languageCode 2-character code, defaults "en" if <code>null</code>
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
					   String languageCode, String userId, String password, String userFullName, String avatar, String role, String homepage, String geolocation);

	IResult insertUserData(String userId, String propertyType, String propertyValue);

	IResult updateUserData(String userId, String propertyType, String oldValue, String newValue);

	IResult removeUserData(String userId, String propertyType, String oldValue);

	/**
	 * Returns Boolean value as result
	 *
	 * @param con
	 * @param handle
	 * @return
	 */
	IResult existsUserHandle(String handle);

	/**
	 * @param userId
	 * @return
	 */
	IResult removeUser(String userId);
	
	IResult changeUserPassword(String userId, String newPassword);

	IResult addUserRole(String userId, String newRole);

	IResult removeUserRole(String userName, String oldRole);

	IResult updateUserEmail(String userId, String newEmail);

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
