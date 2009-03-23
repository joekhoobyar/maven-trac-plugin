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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;

/**
 * Common super class.
 * 
 */
public abstract class AbstractTracMojo extends AbstractMojo {

	/**
	 * @parameter expression="${project}"
	 * 
	 * @required
	 */
	protected MavenProject project;

	/**
	 * @parameter expression="${settings}"
	 * 
	 * @required
	 */
	protected Settings settings;

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

	/**
	 * Dry Run.
	 * 
	 * @parameter expression="${dryRun}"
	 */
	protected boolean dryRun;

	/**
	 * 
	 * @parameter expression="${project.distributionManagement.repository.url}"
	 * @readonly
	 */
	protected String distUrl;

	/**
	 * 
	 * @parameter expression="${project.distributionManagement.snapshotRepository.url}"
	 * @readonly
	 */
	protected String distSnapshotUrl;

	/**
	 * 
	 * @parameter expression="${project.distributionManagement}"
	 * @readonly
	 */
	protected DistributionManagement distributionManagement;

	/**
	 * @parameter
	 */
	protected DownloadUrls downloadUrls;

	public void setDownloadUrls(DownloadUrls downloadUrls) {
		getLog().debug("setDownloadUrls.");
		this.downloadUrls = downloadUrls;
	}

	/**
	 * set property "maven.trac.distUrl".
	 * 
	 * @param project
	 */
	public void setProject(MavenProject project) {
		this.project = project;

		String downloadUrl = getDownloadUrl();

		getLog().info("Setting \"maven.trac.distUrl\" = " + downloadUrl);

		this.project.getProperties()
				.put("maven.trac.distUrl", getDownloadUrl());

	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			validate();
			run();

			Artifact artifact = project.getArtifact();
			System.out.println(artifact);

			System.out.println(project.getAttachedArtifacts());

		} catch (RuntimeException e) {
			handleException(e);
		}
	}

	protected String getDownloadUrl() {

		if (downloadUrls == null) {
			return "undefiened";
		}

		String url;

		if (project.getVersion().endsWith("-SNAPSHOT")) {
			url = downloadUrls.getSnapshotUrl();
		} else {
			url = downloadUrls.getReleaseUrl();
		}

		if (!url.endsWith("/")) {
			url += "/";
		}
		return url;
	}

	protected String getDistUrl() {
		String url = distUrl;
		if (project.getVersion().endsWith("-SNAPSHOT")) {
			url = distSnapshotUrl;
		}
		if (!url.endsWith("/")) {
			url += "/";
		}
		return url;
	}

	protected void validate() {
	}

	abstract protected void run() throws MojoExecutionException,
			MojoFailureException;

	protected TracXmlRpcClient getTracClient() {
		return new TracXmlRpcClient(getLog(), url, basicAuth);
	}

	protected void handleException(Exception e) throws MojoExecutionException {
		throw new MojoExecutionException("Error at maven-trac-plugin. "
				+ e.getMessage(), e);
	}

}
