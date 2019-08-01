package com.colabriq.utils.ingest;

import com.colabriq.model.Link.RelType;

class TurtleFileLink {
	private final String filename;
	private final RelType rel;
	
	public TurtleFileLink(String filename, String rel) {
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
		
		if (o instanceof TurtleFileLink) {
			return filename.equals(((TurtleFileLink)o).filename);
		}
		
		return false;
	}
}