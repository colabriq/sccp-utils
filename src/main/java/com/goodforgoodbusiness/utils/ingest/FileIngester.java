package com.goodforgoodbusiness.utils.ingest;

import static java.util.function.Predicate.not;

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

import com.goodforgoodbusiness.model.Link;
import com.goodforgoodbusiness.shared.FileLoader;
import com.goodforgoodbusiness.shared.treesort.TreeSort;
import com.goodforgoodbusiness.utils.RDFClient;
import com.google.gson.JsonParser;

/**
 * Ingests a directory of files (sorting them first) and pushes them to the relevant endpoints of different actors.
 *
 */
public class FileIngester {
	public static Map<String, URI> ENDPOINTS = new HashMap<>();
	
	static {
		try {
			ENDPOINTS.put("RETAILER-0000000000",	new URI("http://localhost:8080"));
			
			ENDPOINTS.put("FARMER-0000000000",		new URI("http://localhost:8081"));
			ENDPOINTS.put("FARMER-0000000001",		new URI("http://localhost:8082"));
			ENDPOINTS.put("FARMER-0000000002",		new URI("http://localhost:8083"));
			ENDPOINTS.put("FARMER-0000000003",		new URI("http://localhost:8084"));
			ENDPOINTS.put("FARMER-0000000004",		new URI("http://localhost:8085"));
			ENDPOINTS.put("FARMER-0000000005",		new URI("http://localhost:8086"));
			ENDPOINTS.put("FARMER-0000000006",		new URI("http://localhost:8087"));
			
			ENDPOINTS.put("PROCESSOR-0000000000",	new URI("http://localhost:8088"));
			ENDPOINTS.put("PROCESSOR-0000000001",	new URI("http://localhost:8089"));
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static final Pattern LINK_PATTERN = Pattern.compile("^# *@link +(\\S+) +(\\S+)");
	
	private static Set<IngestedLink> readPredecessors(File file) {
		var predecessors = new HashSet<IngestedLink>();
		
		try (var reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					var matcher = LINK_PATTERN.matcher(line);
					if (matcher.find()) {
						var filename = matcher.group(2);
						var type = matcher.group(1);
						
						predecessors.add(new IngestedLink(filename, type));
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
		var jsonParser = new JsonParser();
		
		var foundFiles = new LinkedList<IngestableFile>();
		FileLoader.scan(new File(args[0]), file -> foundFiles.add(new IngestableFile(file, readPredecessors(file))));
		
		// translate files in to submitted claim IDs
		var linkMap = new HashMap<String, String>();
		
		// sort the files and ingest them in turn
		var sorted = TreeSort.sort(foundFiles);
		int i = 0;
		
		for (var next : sorted) {
			// actor determines endpoint
			var actor = next.getFile().getName().split("_")[1];
			var endpoint = new URI("http://localhost:8001"); //ENDPOINTS.get(actor);
			
			var unmappedLink = next.getPredecessors()
				.filter(not(linkMap::containsKey))
				.findFirst();
			
			if (unmappedLink.isPresent()) {
				throw new Exception("Unmapped link: " + unmappedLink.get());
			}
			
			var links = next.getLinks()
				.map(link -> new Link(linkMap.get(link.getFilename()), link.getRel()))
			;
			
			System.out.print( "(" + i++ + "/" + sorted.size() + ") " );
			System.out.print("Ingesting " + next.getFile().getName() + " to " + endpoint);
			
			var result = new RDFClient(endpoint).upload(next.getFile(), links);
			System.out.println(" -> " + result);
			
			var jsonObject = jsonParser.parse(result).getAsJsonObject();
			linkMap.put(next.getFile().getName(), jsonObject.get("id").getAsString());
		}
	}
}
