package com.goodforgoodbusiness.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import com.goodforgoodbusiness.model.TriTuple;
import com.goodforgoodbusiness.shared.URIModifier;
import com.goodforgoodbusiness.webapp.ContentType;

public class ShareClient {
	private static final HttpClient httpClient = HttpClient.newBuilder().build();
	
	public static void shareKeys(URI fromEndpoint, URI toEndpoint, TriTuple tt) throws Exception {		
		// request a share key from Engine A
		var shareRequestBuilder = URIModifier.from(fromEndpoint).appendPath("/share");
		
		if (tt.getSubject().isPresent()) shareRequestBuilder.addParam("sub", tt.getSubject().get());
		if (tt.getPredicate().isPresent()) shareRequestBuilder.addParam("pre", tt.getPredicate().get());
		if (tt.getObject().isPresent()) shareRequestBuilder.addParam("obj", tt.getObject().get());
		
		var shareRequest = HttpRequest
			.newBuilder(shareRequestBuilder.build())
			.header("Content-Type", ContentType.json.getContentTypeString())
			.GET()
			.build();
			
		var shareRequestResponse = httpClient.send(shareRequest, BodyHandlers.ofString());
		
		if (shareRequestResponse.statusCode() != 200) {
			throw new Exception("Response to request: " + shareRequestResponse.statusCode());
		}
		
		// send it back in to Engine B
		var shareAcceptBuilder = URIModifier.from(toEndpoint).appendPath("/share");
		
		var shareAcceptRequest = HttpRequest
				.newBuilder(shareAcceptBuilder.build())
				.header("Content-Type", ContentType.json.getContentTypeString())
				.POST(BodyPublishers.ofString(shareRequestResponse.body()))
				.build();
			
		var shareAcceptResponse = httpClient.send(shareAcceptRequest, BodyHandlers.ofString());
		
		if (shareAcceptResponse.statusCode() != 200) {
			throw new Exception("Response to accept request: " + shareAcceptResponse.statusCode());
		}
	}
}
