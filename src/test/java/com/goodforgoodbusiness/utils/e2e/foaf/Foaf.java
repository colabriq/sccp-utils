package com.goodforgoodbusiness.utils.e2e.foaf;

import static com.goodforgoodbusiness.shared.ConfigLoader.loadConfig;
import static com.google.inject.Guice.createInjector;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import com.goodforgoodbusiness.endpoint.RDFDataModule;
import com.goodforgoodbusiness.endpoint.rdf.RDFRunner;
import com.goodforgoodbusiness.shared.URIModifier;
import com.goodforgoodbusiness.webapp.ContentType;

public class Foaf {
	public static String A = "data-a.properties";
	public static String B = "data-b.properties";
	
	public static RDFRunner newRunner(String configFile) throws Exception {
		var injector1 = createInjector(new RDFDataModule(loadConfig(Foaf.class, configFile)));
		return injector1.getInstance(RDFRunner.class);
	}
	
	public static void shareKeys(String from, String to, String sub, String pre, String obj) throws Exception {
		// run with separate runners, as if the system was restarted.
		
		var cfgFrom = loadConfig(Foaf.class, from);
		var cfgTo = loadConfig(Foaf.class, to);

		// request a share key from Engine A
		var httpClient = HttpClient.newBuilder().build();

		var uri = URIModifier.from(new URI(cfgFrom.getString("dht.uri") + "/share"));
		
		if (sub != null) uri.addParam("sub", sub);
		if (pre != null) uri.addParam("pre", pre);
		if (obj != null) uri.addParam("obj", obj);
		
		var requestFrom = HttpRequest
			.newBuilder(uri.build())
			.header("Content-Type", ContentType.json.getContentTypeString())
			.GET()
			.build();
			
		var responseFrom = httpClient.send(requestFrom, BodyHandlers.ofString());
		
		if (responseFrom.statusCode() != 200) {
			throw new Exception("A: " + responseFrom.statusCode());
		}
		
		// send it back in to Engine B
		var requestTo = HttpRequest
				.newBuilder(new URI(cfgTo.getString("dht.uri") + "/share"))
				.header("Content-Type", ContentType.json.getContentTypeString())
				.POST(BodyPublishers.ofString(responseFrom.body()))
				.build();
			
		var responseTo = httpClient.send(requestTo, BodyHandlers.ofString());
		
		if (responseTo.statusCode() != 200) {
			throw new Exception("B: " + responseTo.statusCode());
		}
	}
}
