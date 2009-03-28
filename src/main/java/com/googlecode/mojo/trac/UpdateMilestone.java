package com.googlecode.mojo.trac;

public class UpdateMilestone {

	private String milestone;

	private String prependDescription;

	private String appendDescription;

	private boolean createArtifactTable = false;

	public String getMilestone() {
		return milestone;
	}

	public void setMilestone(String milestone) {
		this.milestone = milestone;
	}

	public String getPrependDescription() {
		return prependDescription;
	}

	public void setPrependDescription(String prependDescription) {
		this.prependDescription = prependDescription;
	}

	public String getAppendDescription() {
		return appendDescription;
	}

	public void setAppendDescription(String appendDescription) {
		this.appendDescription = appendDescription;
	}

	public boolean isCreateArtifactTable() {
		return createArtifactTable;
	}

	public void setCreateArtifactTable(boolean createArtifactTable) {
		this.createArtifactTable = createArtifactTable;
	}

}
