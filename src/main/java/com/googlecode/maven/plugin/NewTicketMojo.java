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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Goal which create new-ticket for trac.
 * 
 * @goal new-ticket
 * @phase deploy
 */
public class NewTicketMojo extends MavenTracMojo {

	/**
	 * Ticket Contents.
	 * 
	 * @parameter
	 * @required
	 */
	private Map[] tickets;

	public void execute() throws MojoExecutionException {
		try {
			validate();
			newTickets();
		} catch (Exception e) {
			throw new MojoExecutionException("Error from maven-trac-plugin. "
					+ e.getMessage(), e);

		}
	}

	private void validate() {
		emptyCheck(new String[] { "summary", "description", "type",
				"component", "priority" });

	}

	private void emptyCheck(String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			for (int j = 0; j < tickets.length; j++) {
				String value = (String) tickets[j].get(keys[i]);
				if (value == null || "".equals(value)) {
					throw new IllegalArgumentException(
							"'"
									+ keys[i]
									+ "' is required in configuration of maven-trac-plugin.");
				}
			}
		}
	}

	private void newTickets() throws MalformedURLException, XmlRpcException {
		XmlRpcClient client = new XmlRpcClient();
		XmlRpcClientConfigImpl config = getClientConfig();

		for (int i = 0; i < tickets.length; i++) {
			tickets[i].put("reporter", basicAuth.getUserName());
			newTicket(client, config, tickets[i]);
		}
	}

	private void newTicket(XmlRpcClient client, XmlRpcClientConfigImpl config,
			Map ticket) throws MalformedURLException, XmlRpcException {

		if (!url.endsWith("xmlrpc") && !url.endsWith("xmlrpc/")) {
			if (!url.endsWith("/")) {
				url += "/";
			}
			url += "xmlrpc";
		}
		getLog().info("Connecting to " + url);

		config.setServerURL(new URL(url));

		String summary = (String) ticket.remove("summary");
		String description = (String) ticket.remove("description");

		Object execute = client.execute(config, "ticket.create", new Object[] {
				summary, description, ticket, Boolean.FALSE });

		getLog().info("Created new-ticket. id is " + execute + ".");
	}
}
