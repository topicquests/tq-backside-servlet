/*
 * Copyright 2015, 2017 TopicQuests
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
package org.topicquests.backside.servlet;

import java.util.*;

import org.topicquests.support.RootEnvironment;
import org.topicquests.support.config.ConfigPullParser;
import org.topicquests.backside.servlet.api.IStoppable;
import org.topicquests.backside.servlet.apps.CredentialCache;
import org.topicquests.backside.servlet.apps.usr.UserModel;
import org.topicquests.backside.servlet.apps.usr.api.IUserModel;
import org.topicquests.backside.servlet.apps.util.ElasticQueryDSL;
import org.topicquests.ks.StatisticsUtility;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.support.util.LoggingPlatform;

/**
 * @author park
 *
 */
public class ServletEnvironment extends RootEnvironment {
	private static ServletEnvironment instance;
	private StatisticsUtility stats;
	private SystemEnvironment tmEnvironment=null;
	private CredentialCache cache;
	private IUserModel userModel;
	private ElasticQueryDSL queryDSL;
	private List<IStoppable>stoppables;
	private boolean isShutDown = false;
	
	/**
	 * @param isConsole <code>true</code> means boot JSONTopicMap console
	 */
	public ServletEnvironment(boolean isConsole) throws Exception { 
		super("config-props.xml", "logger.properties");
		stoppables = new ArrayList<IStoppable>();
		cache = new CredentialCache(this);
		tmEnvironment = new SystemEnvironment();
		queryDSL = new ElasticQueryDSL(this);
		stats = tmEnvironment.getStats();
		System.out.println("STARTING USER");
		userModel = new UserModel(this);
		System.out.println("STARTED USER "+getStringProperty("ServerPort"));
		isShutDown = false;
		System.out.println("ServletEnvironment+");
		instance = this;
		logDebug("ServletEnvironment+");
	}

	public static ServletEnvironment getInstance() {
		return instance;
	}
		
	public void addStoppable(IStoppable s) {
		synchronized(stoppables) {
			stoppables.add(s);
		}
	}
	public ElasticQueryDSL getQueryDSL() {
		return queryDSL;
	}
	
	public IUserModel getUserModel() {
		return userModel;
	}
	
	public CredentialCache getCredentialCache() {
		return cache;
	}
	
	public SystemEnvironment getTopicMapEnvironment() {
		return tmEnvironment;
	}
	
	public void shutDown() {
		//This might be called by several servlets
		if (!isShutDown) {
			if (tmEnvironment != null)
				tmEnvironment.shutDown();
			Iterator<IStoppable>itr = stoppables.iterator();
			while (itr.hasNext())
				itr.next().shutDown();
			isShutDown = true;
		}
	}


}
