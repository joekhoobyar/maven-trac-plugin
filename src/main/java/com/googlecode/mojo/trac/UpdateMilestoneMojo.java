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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

/**
 * Goal which edit milestone of trac.
 * 
 * @goal update-milestone
 * @phase deploy
 */
public class UpdateMilestoneMojo extends AbstractTracMojo {

	/**
	 * Target milestone.
	 * 
	 * @parameter
	 */
	private UpdateMilestone updateMilestone;

	/**
	 * Target milestone name.
	 * 
	 * @parameter expression="${trac.milestone.name}"
	 */
	private String milestoneNames;

	/**
	 * 
	 * @component
	 */
	private Prompter prompter;

	protected void setup() throws MojoExecutionException, MojoFailureException {

		// validate settings.
		if (updateMilestone == null) {
			throw new IllegalArgumentException("Plsease set updateMilestone.");
		}
		if (updateMilestone.getMilestone() == null && milestoneNames == null
				&& !interactive) {
			throw new IllegalArgumentException(
					"Please set milestoneNames, or execute in interactive mode.");
		}

		// if milestoneNames isn't defined with pom.xml or mvn
		// option(-Dtrac.milstone.name=...), set milestoneNames interactivity.
		if (updateMilestone.getMilestone() == null && milestoneNames == null
				&& interactive) {
			milestoneNames = configureMilestoneNames();
		}

		// overwrite milestoneNames.
		if (milestoneNames != null) {
			updateMilestone.setMilestone(milestoneNames);
		}
	}

	private String configureMilestoneNames() throws MojoExecutionException,
			MojoFailureException {

		List<Map<String, Object>> milestones = getTracClient()
				.getOpenMilestoneAll();

		if (milestones.size() == 0) {
			throw new MojoFailureException(
					"Open Milestone isn't found. Please check Trac Rodadmap.");
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Open Milestone List.");
		sb.append(Utils.LINE_SEPARATOR);
		sb.append(Utils.LINE_SEPARATOR);

		for (int index = 1; index <= milestones.size(); index++) {
			sb.append(index + ": "
					+ milestones.get(index - 1).get("name").toString());
			sb.append(Utils.LINE_SEPARATOR);
		}

		sb.append(Utils.LINE_SEPARATOR);
		sb.append("Choose numbers(Space or comma delimited)");

		try {
			String[] numbers;

			do {
				String input = prompter.prompt(sb.toString());

				try {
					if (StringUtils.contains(input, ',')) {
						numbers = StringUtils.split(input, ',');
					} else if (StringUtils.contains(input, ' ')) {
						numbers = StringUtils.split(input, ' ');
					} else {
						numbers = new String[] { input };
					}

					List<String> milestoneNames = new ArrayList<String>();
					for (String number : numbers) {

						Map<String, Object> milestone = milestones.get(Integer
								.parseInt(number.trim()) - 1);

						String milestonName = milestone.get("name").toString();
						milestoneNames.add(milestonName);
					}

					return StringUtils.join(milestoneNames, ',');

				} catch (NumberFormatException ignore) {

				} catch (IndexOutOfBoundsException ignore) {

				}

			} while (true);

		} catch (PrompterException e) {
			throw new MojoExecutionException("Configure mileston name error.",
					e);
		}
	}

	public void run() throws MojoExecutionException, MojoFailureException {
		editMilestone();
	}

	private void editMilestone() {

		List<Map<String, Object>> milestoneAttrs = getTracClient()
				.getMilestones(updateMilestone.getMilestone());

		for (Map<String, Object> milestoneAttr : milestoneAttrs) {

			if (getLog().isDebugEnabled()) {
				getLog().debug(
						"Get milestone. This content is {" + milestoneAttr
								+ "}");
			}

			String description = (String) milestoneAttr.get("description");
			StringBuilder sb = new StringBuilder(description);

			prepend(sb);
			append(sb);

			milestoneAttr.put("description", sb.toString());

			if (!dryRun) {
				getTracClient().updateMilestone(
						(String) milestoneAttr.get("name"), milestoneAttr);
			}
		}
	}

	private void append(StringBuilder sb) {
		if (updateMilestone.getAppendDescription() != null) {
			sb.append("\n\n" + updateMilestone.getAppendDescription());
		}
	}

	private void prepend(StringBuilder sb) {
		if (updateMilestone.getPrependDescription() != null) {
			sb.insert(0, updateMilestone.getPrependDescription() + " \n\n");
		}
	}
}
