package com.googlecode.mojo.trac;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Goal which create new-ticket for trac.
 * 
 * @goal new-ticket
 * @phase deploy
 */
public class NewTicketMojo extends AbstractTracMojo {

	/**
	 * Ticket Contents.
	 * 
	 * @parameter
	 * @required
	 */
	private Map[] tickets;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.mojo.trac.AbstractTracMojo#validate()
	 */
	protected void validate() {
		emptyCheck(new String[] { "summary", "description", "type",
				"component", "priority" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.mojo.trac.AbstractTracMojo#run()
	 */
	public void run() {
		newTickets();
	}

	private void emptyCheck(String[] keys) {
		for (int i = 0; i < keys.length; i++) {
			for (int j = 0; j < tickets.length; j++) {
				String value = (String) tickets[j].get(keys[i]);
				if (value == null || "".equals(value)) {
					throw new IllegalArgumentException(
							"'"
									+ keys[i]
									+ "' is required in configuration for maven-trac-plugin.");
				}
			}
		}
	}

	private void newTickets() {
		for (int i = 0; i < tickets.length; i++) {
			tickets[i].put("reporter", basicAuth.getUserName());
			newTicket(tickets[i]);
		}
	}

	private void newTicket(Map ticket) {
		if (!dryRun) {
			int id = getTracClient().newTicket(ticket);
			getLog().info("Created new-ticket #" + id + ".");
		}
	}

}
