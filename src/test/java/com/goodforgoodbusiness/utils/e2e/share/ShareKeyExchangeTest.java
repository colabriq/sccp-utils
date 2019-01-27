package com.goodforgoodbusiness.utils.e2e.share;

import static com.goodforgoodbusiness.shared.ConfigLoader.loadConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import com.goodforgoodbusiness.utils.e2e.foaf.Foaf1;
import com.goodforgoodbusiness.webapp.ContentType;

public class ShareKeyExchangeTest {
	public static void main(String[] args) throws Exception {
		// run with separate runners, as if the system was restarted.
		
		var cfgA = loadConfig(Foaf1.class, "data-a.properties");
		var cfgB = loadConfig(Foaf1.class, "data-b.properties");

		// request a share key from Engine A
		var httpClient = HttpClient.newBuilder().build();

		var requestA = HttpRequest
			.newBuilder(new URI(cfgA.getString("dht.uri") + "/share?sub=test1&obj=test3&start=2019-01-01T00:00"))
			.header("Content-Type", ContentType.json.getContentTypeString())
			.GET()
			.build();
			
		var responseA = httpClient.send(requestA, BodyHandlers.ofString());
		
		// send it back in to Engine B
		var requestB = HttpRequest
				.newBuilder(new URI(cfgB.getString("dht.uri") + "/share"))
				.header("Content-Type", ContentType.json.getContentTypeString())
				.POST(BodyPublishers.ofString(responseA.body()))
				.build();
			
		var responseB = httpClient.send(requestB, BodyHandlers.ofString());
			
		System.out.println(responseB.statusCode());
	}
}
