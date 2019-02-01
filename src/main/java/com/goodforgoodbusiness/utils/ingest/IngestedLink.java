package com.goodforgoodbusiness.utils.ingest;

import com.goodforgoodbusiness.model.Link.RelType;

class IngestedLink {
	private final String filename;
	private final RelType rel;
	
	public IngestedLink(String filename, String rel) {
		this.filename = filename;
		this.rel = RelType.fromUri(rel);
	}
	
	public String getFilename() {
		return filename;
	}
	
	public RelType getRel() {
		return rel;
	}
	
	@Override
	public int hashCode() {
		return filename.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (o instanceof IngestedLink) {
			return filename.equals(((IngestedLink)o).filename);
		}
		
		return false;
	}
}