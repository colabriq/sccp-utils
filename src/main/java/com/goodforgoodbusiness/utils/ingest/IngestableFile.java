package com.goodforgoodbusiness.utils.ingest;

import java.io.File;
import java.util.Set;
import java.util.stream.Stream;

import com.goodforgoodbusiness.shared.treesort.TreeNode;

class IngestableFile implements TreeNode<String> {
	private final File file;
	private final Set<IngestedLink> links;
	
	IngestableFile(File file, Set<IngestedLink> predecessors) {
		this.file = file;
		this.links = predecessors;
	}
	
	public File getFile() {
		return file;
	}
	
	@Override
	public String getValue() {
		return file.getName();
	}

	@Override
	public Stream<String> getPredecessors() {
		return links.stream().map(link -> link.getFilename());
	}
	
	public Stream<IngestedLink> getLinks() {
		return links.stream();
	}
	
	@Override
	public String toString() {
		return file.getName();
	}	
}