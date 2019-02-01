package com.goodforgoodbusiness.utils.e2e;

import static com.goodforgoodbusiness.webapp.ContentType.sparql_query;
import static com.goodforgoodbusiness.webapp.ContentType.sparql_update;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class RDF {
	private final AppTester tester;

	public RDF(AppTester tester) {
		this.tester = tester;
	}
	
	public String query(String query) throws Exception {
		var client = HttpClient.newBuilder().build();
		
		var request = HttpRequest
			.newBuilder(new URI("http://localhost:" + tester.config.getString("port") + "/sparql"))
			.header("Content-Type", sparql_query.getContentTypeString())
			.POST(BodyPublishers.ofString(query))
			.build();
		
		var response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new Exception("Response = " + response.statusCode());
		}
		
		return response.body();
	}
		
	public String update(String update) throws Exception {
		var client = HttpClient.newBuilder().build();
		
		var request = HttpRequest
			.newBuilder(new URI("http://localhost:" + tester.config.getString("port") + "/sparql"))
			.header("Content-Type", sparql_update.getContentTypeString())
			.POST(BodyPublishers.ofString(update))
			.build();
		
		var response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new Exception("Response = " + response.statusCode());
		}
		
		return response.body();
	}
}
