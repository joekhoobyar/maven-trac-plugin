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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Goal which edit milestone of trac.
 * 
 * @goal update-milestone
 * @phase deploy
 */
public class UpdateMilestoneMojo extends MavenTracMojo {

	/**
	 * Target milestone.
	 * 
	 * @parameter
	 * @required
	 */
	private UpdateMilestone updateMilestone;

	public void execute() throws MojoExecutionException {
		try {
			if (updateMilestone != null) {
				editMilestone();
			}
		} catch (Exception e) {
			throw new MojoExecutionException("Error from maven-trac-plugin. "
					+ e.getMessage(), e);

		}
	}

	private void editMilestone() throws MalformedURLException, XmlRpcException {
		XmlRpcClient client = new XmlRpcClient();
		XmlRpcClientConfigImpl config = getClientConfig();

		getLog().info("Connecting to " + url);

		config.setServerURL(new URL(url));

		Object execute = client.execute(config, "ticket.milestone.get",
				new Object[] { updateMilestone.getMilestone() });

		if (getLog().isDebugEnabled()) {
			getLog().debug("Get milestone. This content is {" + execute + "}");
		}

		Map milestoneAttr = (Map) execute;

		String content = (String) milestoneAttr.get("description");
		content = "== " + updateMilestone.getText() + " ==\n" + content;
		milestoneAttr.put("description", content);

		execute = client.execute(config, "ticket.milestone.update",
				new Object[] { updateMilestone.getMilestone(), milestoneAttr });
	}
}
