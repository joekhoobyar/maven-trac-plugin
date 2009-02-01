package com.googlecode.mojo.trac;

public class UpdateMilestone {

	private String milestone;

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

	public void validate() {
		Utils.validateRequired("milestone", milestone);
	}

}
