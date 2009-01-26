package com.googlecode.maven.plugin;

public class UpdateMilestone {

	/**
	 * @parameter
	 * @required
	 */
	private String milestone;

	/**
	 * @parameter expression="${pom.groupId}"
	 * @required
	 */
	private String text;

	public String getMilestone() {
		return milestone;
	}

	public void setMilestone(String milestone) {
		this.milestone = milestone;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
