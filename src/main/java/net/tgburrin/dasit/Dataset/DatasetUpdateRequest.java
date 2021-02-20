package net.tgburrin.dasit.Dataset;

public class DatasetUpdateRequest extends DatasetCreateRequest {
	public String status;

	@Override
	public String toString() {
		return "Dataset Name: "+datasetName+"\n"+
				"Owner Group: "+ownerGroupName+"\n"+
				"Status: "+status+"\n";
	}
}
