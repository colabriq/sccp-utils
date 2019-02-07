package com.goodforgoodbusiness.utils.ingest;

import static java.util.function.Predicate.not;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;

import com.goodforgoodbusiness.model.Link;
import com.goodforgoodbusiness.shared.FileLoader;
import com.goodforgoodbusiness.shared.treesort.TreeSort;
import com.goodforgoodbusiness.utils.RDFClient;
import com.goodforgoodbusiness.utils.ingest.beef.Actor;
import com.google.gson.JsonParser;

/**
 * Ingests a directory of files (sorting them first) and pushes them to the relevant endpoints of different actors.
 *
 */
public class FileIngester {	
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
			var endpoint = Actor.ACTORS.get(actor).endpoint;
			
			var unmappedLink = next.getPredecessors()
				.filter(not(linkMap::containsKey))
				.findFirst();
			
			if (unmappedLink.isPresent()) {
				throw new Exception("Unmapped link: " + unmappedLink.get());
			}
			
			var links = next.getLinks()
				.map(link -> new Link(linkMap.get(link.getFilename()), link.getRel()))
			;
			
			System.out.print( "(" + ++i + "/" + sorted.size() + ") " );
			System.out.print("Ingesting " + next.getFile().getName() + " to " + endpoint);
			
			var result = new RDFClient(endpoint).upload(next.getFile(), links);
			System.out.println(" -> " + result);
			
			var jsonObject = jsonParser.parse(result).getAsJsonObject();
			linkMap.put(next.getFile().getName(), jsonObject.get("id").getAsString());
		}
	}
}
