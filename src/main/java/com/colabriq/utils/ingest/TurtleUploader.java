package com.colabriq.utils.ingest;

import static java.util.function.Predicate.not;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Pattern;

import com.colabriq.model.Link;
import com.colabriq.shared.FileLoader;
import com.colabriq.shared.treesort.TreeSort;
import com.colabriq.utils.RDFClient;
import com.google.gson.JsonParser;

/**
 * Ingests a directory of files (sorting them first) and pushes them to the relevant endpoints of different actors.
 *
 */
public abstract class TurtleUploader {	
	private static final Pattern LINK_PATTERN = Pattern.compile("^# *@link +(\\S+) +(\\S+)");
	
	private static Set<TurtleFileLink> readPredecessors(File file) {
		var predecessors = new HashSet<TurtleFileLink>();
		
		try (var reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					var matcher = LINK_PATTERN.matcher(line);
					if (matcher.find()) {
						var filename = matcher.group(2);
						var type = matcher.group(1);
						
						predecessors.add(new TurtleFileLink(filename, type));
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

	private File rootDir;
	
	protected TurtleUploader(File rootDir) {
		this.rootDir = rootDir;
	}
	
	protected abstract URI getEndpoint(String filename) throws URISyntaxException;
	
	public void run() throws Exception {
		var jsonParser = new JsonParser();
		
		var foundFiles = new LinkedList<TurtleFile>();
		FileLoader.scan(rootDir, file -> foundFiles.add(new TurtleFile(file, readPredecessors(file))));
		
		// translate files in to submitted claim IDs
		var linkMap = new HashMap<String, String>();
		
		// sort the files and ingest them in turn
		var sorted = TreeSort.sort(foundFiles);
		int i = 0;
		
		for (var next : sorted) {
			var endpoint = getEndpoint(next.getFile().getName());
			
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
