package com.goodforgoodbusiness.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.goodforgoodbusiness.shared.FileLoader;
import com.goodforgoodbusiness.shared.treesort.TreeNode;
import com.goodforgoodbusiness.shared.treesort.TreeSort;

/**
 * Ingests a directory of files (sorting them first) and pushes them to the relevant endpoints of different actors.
 *
 */
public class QuickIngest {
	public static Map<String, URI> ENDPOINTS = new HashMap<>();
	
	static {
		try {
			ENDPOINTS.put("FARMER-0000000000",		new URI("http://localhost:8080/upload"));
			ENDPOINTS.put("FARMER-0000000001",		new URI("http://localhost:8081/upload"));
			ENDPOINTS.put("FARMER-0000000002",		new URI("http://localhost:8082/upload"));
			ENDPOINTS.put("FARMER-0000000003",		new URI("http://localhost:8083/upload"));
			ENDPOINTS.put("FARMER-0000000004",		new URI("http://localhost:8084/upload"));
			ENDPOINTS.put("FARMER-0000000005",		new URI("http://localhost:8085/upload"));
			ENDPOINTS.put("FARMER-0000000006",		new URI("http://localhost:8086/upload"));
			
			ENDPOINTS.put("PROCESSOR-0000000000",	new URI("http://localhost:8087/upload"));
			ENDPOINTS.put("PROCESSOR-0000000001",	new URI("http://localhost:8088/upload"));
			
			ENDPOINTS.put("RETAILER-0000000000",	new URI("http://localhost:8089/upload"));
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static class IngestableFile implements TreeNode<String> {
		private final File file;
		private final Set<String> predecessors;
		
		public IngestableFile(File file, Set<String> predecessors) {
			this.file = file;
			this.predecessors = predecessors;
		}
		
		@Override
		public String getValue() {
			return file.getName();
		}

		@Override
		public Stream<String> getPredecessors() {
			return predecessors.stream();
		}
		
		@Override
		public String toString() {
			return file.getName();
		}	
	}
		
	private static final Pattern LINK_PATTERN = Pattern.compile("^# *@link +(\\S+) +(\\S+)");
	
	private static Set<String> readPredecessors(File file) {
		var predecessors = new HashSet<String>();
		
		try (var reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					var matcher = LINK_PATTERN.matcher(line);
					if (matcher.find()) {
						var filename = matcher.group(2);
						predecessors.add(filename);
					}
					else {
						break;
					}
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return predecessors;
	}
		
	public static void main(String[] args) throws Exception {
		var foundFiles = new LinkedList<IngestableFile>();
		FileLoader.scan(new File(args[0]), file -> foundFiles.add(new IngestableFile(file, readPredecessors(file))));
		
		for (var file : TreeSort.sort(foundFiles)) {
			// actor determines endpoint
			
			var actor = file.file.getName().split("_")[1];
			ENDPOINTS.get(actor);
			
			System.out.println(actor);
		}
	}
}
