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

import java.util.Map;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.mojo.trac.AbstractTracMojo#validate()
	 */
	protected void validate() {
		updateMilestone.validate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.mojo.trac.AbstractTracMojo#run()
	 */
	public void run() {
		if (updateMilestone != null) {
			editMilestone();

			getLog().info(distUrl);

			getLog().info(distSnapshotUrl);

			getLog().info(settings.getServers().toString());
		}
	}

	private void editMilestone() {

		Map milestoneAttr = getTracClient().getMilestone(
				updateMilestone.getMilestone());

		if (getLog().isDebugEnabled()) {
			getLog().debug(
					"Get milestone. This content is {" + milestoneAttr + "}");
		}

		String description = (String) milestoneAttr.get("description");
		description = updateMilestone.getPrependDescription() + " \n\n"
				+ description + "\n\n" + updateMilestone.getAppendDescription();

		milestoneAttr.put("description", description);

		if (!dryRun) {
			getTracClient().updateMilestone(updateMilestone.getMilestone(),
					milestoneAttr);
		}
	}
}
