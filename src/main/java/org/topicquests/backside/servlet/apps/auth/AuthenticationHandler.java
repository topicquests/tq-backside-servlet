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
package org.topicquests.backside.servlet.apps.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.topicquests.backside.servlet.ServletEnvironment;
import org.topicquests.backside.servlet.api.ICredentialsMicroformat;
import org.topicquests.backside.servlet.api.IErrorMessages;
import org.topicquests.backside.servlet.apps.BaseHandler;
import org.topicquests.backside.servlet.apps.auth.api.IAuthMicroformat;
import org.topicquests.backside.servlet.apps.usr.api.IUserModel;
import org.topicquests.support.api.IResult;
import org.topicquests.ks.api.ITicket;

import com.google.common.io.BaseEncoding;

/**
 * @author park
 *
 */
public class AuthenticationHandler  extends BaseHandler {
	private IUserModel model;
	/**
	 * 
	 */
	public AuthenticationHandler(ServletEnvironment env, String basePath) {
		super(env,basePath);
		model = environment.getUserModel();
	}

	//////////////////////////////////////////////
	// There are two legal verbs in this app:
	//  IAuthMicroformat.AUTHENTICATE
	//  and
	//  IAuthMicroformat.LOGOUT
	// Both come in as Post commands
	//////////////////////////////////////////////
	public void handleGet(HttpServletRequest request, HttpServletResponse response, ITicket credentials, JSONObject jsonObject) throws ServletException, IOException {
		JSONObject returnMessage = newJSONObject();
		String message = "", rtoken="";
		String verb = getVerb(jsonObject);
		System.out.println("VERB "+verb);
		int code = 0;
		if (verb.equals(IAuthMicroformat.VALIDATE)) {
			String handle = (String)jsonObject.get(ICredentialsMicroformat.USER_HANDLE);
			IResult r = model.existsUsername(handle);
			Boolean x = (Boolean)r.getResultObject();
			//returns ok only if this username does not exist
			if (!x.booleanValue()) {
				message = "ok";
				code = BaseHandler.RESPONSE_OK;
			} else {
				code = BaseHandler.RESPONSE_OK;
				message = "not found";
			}
		} else if (verb.equals(IAuthMicroformat.EXISTS_EMAIL)) {
			String email = (String)jsonObject.get(ICredentialsMicroformat.USER_EMAIL);
			IResult r = model.existsUserEmail(email);
			Boolean x = (Boolean)r.getResultObject();
			System.out.println("EXISTSEMAIL "+x);
			//returns OK only if this email exists
			if (x.booleanValue()) {
				message = "ok";
				code = BaseHandler.RESPONSE_OK;
			} else {
				code = BaseHandler.RESPONSE_OK;
				message = "not found";
			}
		} else if (verb.equals(IAuthMicroformat.AUTHENTICATE)) {
			System.out.println("AUTH");
			//We expect to see something like:
			//{"verb":"Auth","uIP":"173.164.129.250","hash":"amFja3BhcmtAZ21haWwuY29tOmpiaW5reTQ0IQ=="}
			String auth = (String)jsonObject.get("hash");//
			if (auth == null) {
				//have to go looking for it.
				auth = request.getHeader("Authorization");
				if (auth == null)
					auth = request.getHeader("auth"); // meteor apps do this
			}
			if (auth != null) {
				System.out.println("AUTHORIZATION "+auth);
				String creds = "";
				// we do not know if it was Base64 encoded or not
				if (auth.startsWith("Basic")) {
					auth = auth.substring("Basic".length()).trim();
				}
				int where = auth.indexOf(':');
				if (where == -1) {
					System.out.println("AUTHORIZATION2 "+auth);
					//This can hurl chunks
					byte [] foo = BaseEncoding.base64().decode(auth);
					creds = new String(foo);
				} else {
					creds = auth;
				}
				System.out.println("AUTHORIZATION3 "+creds);
				//AUTHORIZATION2 foo:bar
				where = creds.indexOf(':');
				String email = creds.substring(0,where);
				String pwd = creds.substring(where+1);
				
				System.out.println("LOGIN "+email+" "+pwd);
				IResult r = model.authenticate(email, pwd);
				System.out.println("LOGIN-1 "+r.getErrorString()+" | "+r.getResultObject());
				ITicket t = (ITicket)r.getResultObject();
				if (t != null) {
					rtoken = newUUID();
					returnMessage.put(ICredentialsMicroformat.CARGO, ticketToUser(t));
					message = "ok";
					code = BaseHandler.RESPONSE_OK;
					credentialCache.putTicket(rtoken, t);
				} else {
					code = BaseHandler.RESPONSE_OK;
					message = r.getErrorString();
				}
			} else {
				//missing auth
				System.out.println("WE ARE MISSING AUTH");
				environment.logError("Auth TMAHandler:WeAreMissingAuth", null);
			}
		} else if (verb.equals(IAuthMicroformat.LOGOUT)) {
			String token = (String)jsonObject.get(ICredentialsMicroformat.SESSION_TOKEN);
			if (token != null)
				credentialCache.removeTicket(token);
			message = "ok";
			code = BaseHandler.RESPONSE_OK;
		} else {
			String x = IErrorMessages.BAD_VERB+"-AuthServletPost-"+verb;
			environment.logError(x, null);
			throw new ServletException(x);			
		}
		
		returnMessage.put(ICredentialsMicroformat.RESP_TOKEN, rtoken);
		returnMessage.put(ICredentialsMicroformat.RESP_MESSAGE, message);
		System.out.println("AUTHGET "+returnMessage.toJSONString());
		super.sendJSON(returnMessage.toJSONString(), code, response);
		returnMessage = null;

	}
	
	public void handlePost(HttpServletRequest request, HttpServletResponse response, ITicket credentials, JSONObject jsonObject) throws ServletException, IOException {
		JSONObject returnMessage = newJSONObject();
		System.out.println("AUTHPOST "+jsonObject.toJSONString());
		String message = "", rtoken="";
		String verb = getVerb(jsonObject);
		int code = 0;
		
		//We have nothing to do here
		returnMessage.put(ICredentialsMicroformat.RESP_TOKEN, rtoken);
		returnMessage.put(ICredentialsMicroformat.RESP_MESSAGE, message);
		super.sendJSON(returnMessage.toJSONString(), code, response);
		returnMessage = null;
	}

}
