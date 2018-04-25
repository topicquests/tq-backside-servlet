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

import net.minidev.json.JSONObject;
import org.topicquests.backside.servlet.ServletEnvironment;
import org.topicquests.backside.servlet.api.ICredentialsMicroformat;
import org.topicquests.backside.servlet.api.IErrorMessages;
import org.topicquests.backside.servlet.apps.BaseHandler;
import org.topicquests.backside.servlet.apps.admin.api.IAdminMicroformat;
import org.topicquests.backside.servlet.apps.admin.api.IAdminModel;
import org.topicquests.backside.servlet.apps.usr.api.IUserMicroformat;
import org.topicquests.ks.api.ITicket;
import org.topicquests.support.api.IResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author park
 */
public class AdminHandler extends BaseHandler {
	private IAdminModel model;

	/**
	 *
	 */
	public AdminHandler(ServletEnvironment env, String basePath) {
		super(env, basePath);
		try {
			model = new AdminModel(environment);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			throw new RuntimeException(e);
		}

	}

	public void handleGet(HttpServletRequest request, HttpServletResponse response, ITicket credentials, JSONObject jsonObject) throws ServletException, IOException {
		JSONObject returnMessage = newJSONObject();
		String message = "", rtoken = "";
		String verb = getVerb(jsonObject);
		System.out.println("AdminHandler.get " + verb + " " + jsonObject.toJSONString());
		int code = 0;
		IResult r;
		String startS, countS;
		int start, count;
		if (verb.equals(IAdminMicroformat.LIST_USERS)) {
			startS = getItemFrom(jsonObject);
			countS = getItemCount(jsonObject);
			start = 0;
			count = -1;
			if (!startS.equals("")) {
				try {
					start = Integer.valueOf(startS);
				} catch (Exception e1) {
				}
			}
			if (!countS.equals("")) {
				try {
					count = Integer.valueOf(countS);
				} catch (Exception e2) {
				}
			}
			//TODO: note: we are ignoring any SORT modifiers
			//This really returns some live cargo in the form of a list of user objects in JSON format
			// We are restricting this to: name, email, avatar, homepage, geolocation, role
			r = model.listUsers(start, count);
			if (r.hasError()) {
				code = BaseHandler.RESPONSE_INTERNAL_SERVER_ERROR;
				message = r.getErrorString();
			} else {
				//Time to take that list apart
				if (r.getResultObject() != null) {
					List<?> usrs = (List<?>) r.getResultObject();
					Iterator<?> itr = usrs.iterator();
					List<JSONObject> jsonUsers = new ArrayList<JSONObject>();
					while (itr.hasNext()) {
                                          jsonUsers.add(ticketToUser((ITicket)itr.next()));
					}
					returnMessage.put(ICredentialsMicroformat.CARGO, jsonUsers);
				}
				code = BaseHandler.RESPONSE_OK;
				message = "ok";
			}
		} else if (verb.equals(IAdminMicroformat.EXISTS_INVITE)) {
			String email = getEmail(jsonObject);
			System.out.println("AdminHandler.existsInvite " + email);
			r = model.existsInvite(email);
			Boolean b = (Boolean) r.getResultObject();
			if (b.booleanValue() == true) {
				code = BaseHandler.RESPONSE_OK;
				message = "ok";
			} else {
				code = BaseHandler.RESPONSE_OK;
				message = "not found";
			}
		} else if (verb.equals(IAdminMicroformat.LIST_INVITES)) {
			startS = getItemFrom(jsonObject);
			countS = getItemCount(jsonObject);
			start = 0;
			count = -1;
			if (!startS.equals("")) {
				try {
					start = Integer.valueOf(startS);
				} catch (Exception e1) {
				}
			}
			if (!countS.equals("")) {
				try {
					count = Integer.valueOf(countS);
				} catch (Exception e2) {
				}
			}
			//TODO: note: we are ignoring any SORT modifiers
			//This really returns some live cargo in the form of a list of emails in JSON format
			r = model.listInvites(start, count);
			if (r.hasError()) {
				code = BaseHandler.RESPONSE_INTERNAL_SERVER_ERROR;
				message = r.getErrorString();
			} else {

				if (r.getResultObject() != null) {
					List<?> invites = (List<?>) r.getResultObject();

					returnMessage.put(ICredentialsMicroformat.CARGO, invites);
				}
				code = BaseHandler.RESPONSE_OK;
				message = "ok";
			}
		} else {
			String x = IErrorMessages.BAD_VERB + "-AdminServletGet-" + verb;
			environment.logError(x, null);
			throw new ServletException(x);
		}
		returnMessage.put(ICredentialsMicroformat.RESP_TOKEN, rtoken);
		returnMessage.put(ICredentialsMicroformat.RESP_MESSAGE, message);
		super.sendJSON(returnMessage.toJSONString(), code, response);
		returnMessage = null;
	}


	String getUserRole(JSONObject jsonObject) {
		return jsonObject.getAsString(IUserMicroformat.USER_ROLE);
	}

	public void handlePost(HttpServletRequest request, HttpServletResponse response, ITicket credentials, JSONObject jsonObject) throws ServletException, IOException {
		JSONObject returnMessage = newJSONObject();
		String message = "", rtoken = "";
		String verb = getVerb(jsonObject);
		System.out.println("AdminHandler.post verb: " + verb);
		int code = 0;
		IResult r;
		String userid, userrole, useremail;
		if (verb.equals(IAdminMicroformat.REMOVE_USER)) {
			//TODO
		} else if (verb.equals(IAdminMicroformat.UPDATE_USER_EMAIL)) {
			//TODO: really, this belongs to User app, not admin
			userid = getUserId(jsonObject);
			useremail = getEmail(jsonObject);
			r = model.updateUserEmail(userid, useremail);
			if (!r.hasError()) {
				code = BaseHandler.RESPONSE_OK;
				message = "ok";
			}
		} else if (verb.equals(IAdminMicroformat.UPDATE_USER_ROLE)) {
			userid = getUserId(jsonObject);
			userrole = getUserRole(jsonObject);
			r = model.addUserRole(userid, userrole);
			if (!r.hasError()) {
				code = BaseHandler.RESPONSE_OK;
				message = "ok";
			}
		} else if (verb.equals(IAdminMicroformat.REMOVE_USER_ROLE)) {
			userid = getUserId(jsonObject);
			userrole = getUserRole(jsonObject);
			r = model.removeUserRole(userid, userrole);
			if (!r.hasError()) {
				code = BaseHandler.RESPONSE_OK;
				message = "ok";
			}
		} else if (verb.equals(IAdminMicroformat.NEW_INVITE)) {
			useremail = getEmail(jsonObject);
			r = model.addInvite(useremail);
			if (!r.hasError()) {
				code = BaseHandler.RESPONSE_OK;
				message = "ok";
			}
		} else if (verb.equals(IAdminMicroformat.REMOVE_INVITE)) {
			useremail = getEmail(jsonObject);
			r = model.removeInvite(useremail);
			if (!r.hasError()) {
				code = BaseHandler.RESPONSE_OK;
				message = "ok";
			}

		} else {
			String x = IErrorMessages.BAD_VERB + "-AdminServletPost-" + verb;
			environment.logError(x, null);
			throw new ServletException(x);
		}
		returnMessage.put(ICredentialsMicroformat.RESP_TOKEN, rtoken);
		returnMessage.put(ICredentialsMicroformat.RESP_MESSAGE, message);
		super.sendJSON(returnMessage.toJSONString(), code, response);
		returnMessage = null;
	}

	public void shutDown() {
		model.shutDown();
	}
}
