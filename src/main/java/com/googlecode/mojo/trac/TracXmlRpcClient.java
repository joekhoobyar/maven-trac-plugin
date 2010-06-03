package com.googlecode.mojo.trac;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
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
	 * Get Version.
	 * 
	 * @param version
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getVersion(String version) {
		Object execute = execute("ticket.version.get", new Object[] { version });
		return (Map<String, Object>) execute;
	}

	/**
	 * Get all Versions that match the given pattern.
	 * 
	 * @param pattern
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getVersionsMatching(Pattern pattern) {
		String[] list = (String[]) execute("ticket.version.getAll", new Object[] {});

		List<Object> command = new ArrayList<Object>();
		for (String versionName : list) {
			if (pattern != null && ! pattern.matcher (versionName).matches ())
				continue;
			Map<String, Object> table = new HashMap<String, Object>();
			table.put("methodName", "ticket.version.get");
			table.put("params", new Object[] { versionName });
			command.add(table);
		}

		Object[] result = (Object[]) execute("system.multicall",
				new Object[] { command.toArray(new Object[command.size()]) });

		List<Map<String, Object>> rtn = new ArrayList<Map<String, Object>>();
		for (Object object : result) {
			rtn.add((Map<String, Object>) ((Object[]) object)[0]);
		}

		return rtn;
	}

	/**
	 * Create Version.
	 * 
	 * @param version
	 * @param versionAttr
	 * @return
	 */
	public int createVersion(String version, Map<String, Object> versionAttr) {
		getLog().info ("Create version... : " + version);
		if (getLog().isDebugEnabled())
			getLog().debug (versionAttr.toString());
		Object execute = execute ("ticket.version.create", new Object[] {
				version, versionAttr });
		return ((Integer) execute).intValue();
	}

	/**
	 * Update Version.
	 * 
	 * @param version
	 * @param versionAttr
	 * @return
	 */
	public int updateVersion(String version, Map<String, Object> versionAttr) {
		getLog().info ("Update version... : " + version);
		if (getLog().isDebugEnabled())
			getLog().debug (versionAttr.toString());
		Object execute = execute ("ticket.version.update", new Object[] {
				version, versionAttr });
		return ((Integer) execute).intValue();
	}

	/**
	 * Delete Version.
	 * 
	 * @param version
	 * @return
	 */
	public int deleteVersion(String version) {
		getLog().info ("Delete version... : " + version);
		Object execute = execute ("ticket.version.delete", new Object[] { version });
		return ((Integer) execute).intValue();
	}

	/**
	 * Get Milestone.
	 * 
	 * @param milestone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMilestone(String milestone) {
		Object execute = execute("ticket.milestone.get",
				new Object[] { milestone });

		return (Map<String, Object>) execute;
	}

	/**
	 * Get Milestones.
	 * 
	 * @param milestones
	 *            comma separeted milestoneName.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMilestones(String milestones) {

		String[] split = StringUtils.split(milestones, ',');

		List<Map<String, Object>> rtn = new ArrayList<Map<String, Object>>();

		for (String milestone : split) {
			Map<String, Object> milestoneMap = getMilestone(milestone);
			rtn.add(milestoneMap);
		}
		return rtn;
	}

	/**
	 * Get All Milestones.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMilestoneAll() {

		Object[] list = (Object[]) execute("ticket.milestone.getAll",
				new Object[] {});

		List<Object> command = new ArrayList<Object>();

		for (Object milestoneName : list) {
			Map<String, Object> table = new HashMap<String, Object>();
			table.put("methodName", "ticket.milestone.get");
			table.put("params", new Object[] { milestoneName });
			command.add(table);
		}

		Object[] result = (Object[]) execute("system.multicall",
				new Object[] { command.toArray(new Object[command.size()]) });

		List<Map<String, Object>> rtn = new ArrayList<Map<String, Object>>();

		for (Object object : result) {
			rtn.add((Map<String, Object>) ((Object[]) object)[0]);
		}

		return rtn;
	}

	/**
	 * Get All Milestones which not completed.
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getOpenMilestoneAll() {
		List<Map<String, Object>> milestones = getMilestoneAll();

		List<Map<String, Object>> rtn = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> milestone : milestones) {
			Object comp = milestone.get("completed");
			if (comp instanceof Integer) {

				if ((Integer) comp == 0) {
					rtn.add(milestone);
				}
			} else {
				;
			}
		}

		return rtn;
	}

	/**
	 * Update Milestone Operation.
	 * 
	 * @param milestone
	 * @param description
	 * @return
	 */
	public int updateMilestone(String milestone,
			Map<String, Object> milestoneAttr) {

		getLog().info("Update milestone... : " + milestone);

		if (getLog().isDebugEnabled()) {
			getLog().debug(milestoneAttr.toString());
		}

		// remove "due" because of a probrem(http://trac-hacks.org/ticket/3011)
		Object object = milestoneAttr.get("due");
		if (object != null) {
			milestoneAttr.remove("due");
		}

		Object execute = execute("ticket.milestone.update", new Object[] {
				milestone, milestoneAttr });

		return ((Integer) execute).intValue();
	}
}
