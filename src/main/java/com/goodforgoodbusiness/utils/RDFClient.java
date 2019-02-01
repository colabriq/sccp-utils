package com.goodforgoodbusiness.utils;

import static com.goodforgoodbusiness.webapp.ContentType.sparql_query;
import static com.goodforgoodbusiness.webapp.ContentType.sparql_update;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.stream.Stream;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import com.goodforgoodbusiness.endpoint.MIMEMappings;
import com.goodforgoodbusiness.model.Link;
import com.goodforgoodbusiness.shared.URIModifier;

public class RDFClient {
	private final HttpClient client;
	private final URI endpoint;
	
	public RDFClient(URI endpoint) {
		this.client = HttpClient.newBuilder().build();
		this.endpoint = endpoint;
	}
	
	public RDFClient(int port) throws URISyntaxException {	
		this(new URI("http://localhost:" + port));
	}
	
	public String query(String query) throws Exception {
		var request = HttpRequest
			.newBuilder(URIModifier.from(endpoint).appendPath("sparql").build())
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
		var request = HttpRequest
			.newBuilder(URIModifier.from(endpoint).appendPath("sparql").build())
			.header("Content-Type", sparql_update.getContentTypeString())
			.POST(BodyPublishers.ofString(update))
			.build();
		
		var response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new Exception("Response = " + response.statusCode());
		}
		
		return response.body();
	}
	
	public String upload(File file, Stream<Link> links) throws Exception {
		var fileExt = getExtension(file.getName().toLowerCase());
		var contentType = MIMEMappings.CONTENT_TYPES.get(fileExt);
		if (contentType == null) {
			throw new Exception("Could not upload file with extension " + fileExt);
		}
		
		// build file upload
		var entity = MultipartEntityBuilder.create()
	       .addPart("upload", new FileBody(file, ContentType.create(contentType, "UTF-8"), file.getName()))
	       .build();

		var byteSink = new ByteArrayOutputStream();
		entity.writeTo(byteSink);
		byteSink.close();
		
		// build HTTP request
		var request = HttpRequest
			.newBuilder(URIModifier.from(endpoint).appendPath("upload").build())
			.header("Content-Type", entity.getContentType().getValue())
			.header("X-Custody-Chain", links.map(Link::toHeader).collect(joining("; ")))
			.POST(BodyPublishers.ofByteArray(byteSink.toByteArray()))
			.build();
		
		// send HTTP request
		var response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new Exception("Response = " + response.statusCode());
		}
		
		return response.body();
	}
}
