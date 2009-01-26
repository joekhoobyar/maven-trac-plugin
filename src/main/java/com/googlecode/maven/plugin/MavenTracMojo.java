package com.googlecode.maven.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Common super class.
 * 
 */
public abstract class MavenTracMojo extends AbstractMojo {

	/**
	 * URL of the trac.
	 * 
	 * @parameter
	 * @required
	 */
	protected String url;

	/**
	 * Basic Auth.
	 * 
	 * @parameter
	 */
	protected BasicAuth basicAuth;

	protected XmlRpcClientConfigImpl getClientConfig() {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

		if (basicAuth != null) {
			config.setBasicUserName(basicAuth.getUserName());
			config.setBasicPassword(basicAuth.getPassword());
			getLog().info("Using Basic Auth for connect to Trac.");
		}

		return config;
	}

}
