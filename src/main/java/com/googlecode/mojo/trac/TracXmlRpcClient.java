package com.googlecode.mojo.trac;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class TracXmlRpcClient {

	private Log log;
	private BasicAuth basicAuth;
	private XmlRpcClient client;

	public TracXmlRpcClient(Log log, String url, BasicAuth basicAuth) {
		this.log = log;
		this.basicAuth = basicAuth;
		setupClientConfig(url);
	}

	public Log getLog() {
		return log;
	}

	protected void setupClientConfig(String url) {

		if (!url.endsWith("xmlrpc") && !url.endsWith("xmlrpc/")) {
			if (!url.endsWith("/")) {
				url += "/";
			}
			url += "xmlrpc";
		}

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		if (basicAuth != null) {
			config.setBasicUserName(basicAuth.getUserName());
			config.setBasicPassword(basicAuth.getPassword());
			getLog().info("Using Basic Auth for connect to Trac.");
		}
		try {
			config.setServerURL(new URL(url));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}

		client = new XmlRpcClient();
		client.setConfig(config);
	}

	protected Object execute(String method, Object[] param)
			throws RuntimeXmlRpcException {
		try {
			return client.execute(method, param);
		} catch (XmlRpcException e) {
			throw new RuntimeXmlRpcException(e);
		}
	}

	/**
	 * New Ticket Operation.
	 * 
	 * @param ticket
	 * @return
	 */
	public int newTicket(Map ticket) {
		String summary = (String) ticket.remove("summary");
		String description = (String) ticket.remove("description");

		Object execute = execute("ticket.create", new Object[] { summary,
				description, ticket, Boolean.FALSE });

		return ((Integer) execute).intValue();
	}

	/**
	 * Get Milestone.
	 * 
	 * @param milestone
	 * @return
	 */
	public Map getMilestone(String milestone) {
		Object execute = execute("ticket.milestone.get",
				new Object[] { milestone });

		return (Map) execute;
	}

	/**
	 * Update Milestone Operation.
	 * 
	 * @param milestone
	 * @param description
	 * @return
	 */
	public int updateMilestone(String milestone, Map milestoneAttr) {
		Object execute = execute("ticket.milestone.update", new Object[] {
				milestone, milestoneAttr });

		return ((Integer) execute).intValue();
	}
}
