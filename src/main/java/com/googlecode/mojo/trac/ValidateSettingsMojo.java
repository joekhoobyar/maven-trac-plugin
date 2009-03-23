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

import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal which validate Trac settings.
 * 
 * @goal validate
 * @phase validate
 * @requiresDependencyResolution
 * 
 */
public class ValidateSettingsMojo extends AbstractTracMojo {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.mojo.trac.AbstractTracMojo#run()
	 */
	public void run() throws MojoExecutionException, MojoFailureException {
		try {
			List<Map<String, Object>> milestoneAll = getTracClient()
					.getMilestoneAll();

			System.out.println(milestoneAll);

		} catch (RuntimeXmlRpcException e) {
			throw new MojoFailureException("Connect to Trac Error. "
					+ e.getMessage());
		}
	}

}
