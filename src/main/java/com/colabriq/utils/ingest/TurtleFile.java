package com.colabriq.utils.ingest;

import java.io.File;
import java.util.Set;
import java.util.stream.Stream;

import com.colabriq.shared.treesort.TreeNode;

class TurtleFile implements TreeNode<String> {
	private final File file;
	private final Set<TurtleFileLink> links;
	
	TurtleFile(File file, Set<TurtleFileLink> predecessors) {
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
	
	public Stream<TurtleFileLink> getLinks() {
		return links.stream();
	}
	
	@Override
	public String toString() {
		return file.getName();
	}	
}