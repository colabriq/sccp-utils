package com.goodforgoodbusiness.utils.ingest.beef;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Actor {
	public static Map<String, Actor> ACTORS = new HashMap<>();
	
	static {
		ACTORS.put("RETAILER-0000000000",	new Actor("http://localhost:8081", "http://localhost:8091"));
		
		ACTORS.put("FARMER-0000000000",		new Actor("http://localhost:8082", "http://localhost:8092"));
		ACTORS.put("FARMER-0000000001",		new Actor("http://localhost:8082", "http://localhost:8092"));
		ACTORS.put("FARMER-0000000002",		new Actor("http://localhost:8082", "http://localhost:8092"));
		ACTORS.put("FARMER-0000000003",		new Actor("http://localhost:8082", "http://localhost:8092"));
		ACTORS.put("FARMER-0000000004",		new Actor("http://localhost:8082", "http://localhost:8092"));
		ACTORS.put("FARMER-0000000005",		new Actor("http://localhost:8082", "http://localhost:8092"));
		ACTORS.put("FARMER-0000000006",		new Actor("http://localhost:8082", "http://localhost:8092"));
		
		ACTORS.put("PROCESSOR-0000000000",	new Actor("http://localhost:8082", "http://localhost:8092"));
		ACTORS.put("PROCESSOR-0000000001",	new Actor("http://localhost:8082", "http://localhost:8092"));
	}

	public final URI endpoint, engine;
	
	private Actor(String endpoint, String engine) {
		try {
			this.endpoint = new URI(endpoint);
			this.engine = new URI(engine);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
