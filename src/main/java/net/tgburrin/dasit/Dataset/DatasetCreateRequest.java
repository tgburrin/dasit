package net.tgburrin.dasit.Dataset;

public class DatasetCreateRequest {
	public String datasetName;
	public String ownerGroupName;

	@Override
	public String toString() {
		return "Dataset Name: "+datasetName+"\n"+"Owner Group: "+ownerGroupName+"\n";
	}
}
