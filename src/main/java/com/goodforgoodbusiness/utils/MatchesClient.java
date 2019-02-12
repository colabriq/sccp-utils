package com.goodforgoodbusiness.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import com.goodforgoodbusiness.model.TriTuple;
import com.goodforgoodbusiness.shared.URIModifier;
import com.goodforgoodbusiness.shared.encode.JSON;

public class MatchesClient {
	private final HttpClient client;
	private final URI engine;
	
	public MatchesClient(URI engine) {
		this.client = HttpClient.newBuilder().build();
		this.engine = engine;
	}
	
	public MatchesClient(int port) throws URISyntaxException {	
		this(new URI("http://localhost:" + port));
	}
	
	public String matches(TriTuple trup) throws URISyntaxException, IOException, InterruptedException {
		var uri = URIModifier
			.from(engine)
			.appendPath("matches")
			.addParam("pattern", JSON.encode(trup).toString())
			.build();
		
		var request = HttpRequest
			.newBuilder(uri)
			.GET()
			.build();
		
		var response = client.send(request, BodyHandlers.ofString());
		
		if (response.statusCode() == 200) {
			return response.body();
		}
		else {
			throw new IOException("Engine response was " + response.statusCode());
		}
	}
}
