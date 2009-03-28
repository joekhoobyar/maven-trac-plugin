package com.googlecode.mojo.trac;

import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.SystemStreamLog;

public class TestTracXmlRpcClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		BasicAuth auth = new BasicAuth();
		auth.setUserName("admin");
		auth.setPassword("admin");

		TracXmlRpcClient client = new TracXmlRpcClient(new SystemStreamLog(),
				"http://localhost:8000/myproject/login/xmlrpc", auth);

		List<Map<String, Object>> ms = client.getMilestoneAll();
		
		List<Map<String, Object>> openMs = client.getOpenMilestoneAll();
		
		System.out.println(openMs.size());
		System.out.println(openMs.get(0).get("due").getClass());
		
	}

}
