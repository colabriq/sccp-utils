package com.colabriq.utils;

import static com.colabriq.shared.ConfigLoader.loadConfig;
import static com.colabriq.webapp.ContentType.SPARQL_QUERY;
import static com.colabriq.webapp.ContentType.SPARQL_UPDATE;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Reader;
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

import com.colabriq.endpoint.webapp.MIMEMappings;
import com.colabriq.model.Link;
import com.colabriq.shared.ReaderFileBody;
import com.colabriq.shared.URIModifier;

public class RDFClient {
	public static RDFClient fromConfig(Class<?> clazz, String configFile) throws Exception {
		var config = loadConfig(clazz, configFile);
		return new RDFClient(config.getInt("port"));
	}
	
	private final HttpClient client;
	private final URI endpoint;
	
	public RDFClient(URI endpoint) {
		this.client = HttpClient.newBuilder().build();
		this.endpoint = endpoint;
	}
	
	public RDFClient(int port) throws URISyntaxException {	
		this(new URI("http://localhost:" + port));
	}
	
	public String query(String query) throws RuntimeException {
		try {
			var request = HttpRequest
				.newBuilder(URIModifier.from(endpoint).appendPath("sparql").build())
				.header("Content-Type", SPARQL_QUERY.getContentTypeString())
				.POST(BodyPublishers.ofString(query))
				.build();
			
			var response = client.send(request, BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				throw new Exception("Response = " + response.statusCode());
			}
			
			return response.body();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
		
	public String update(String update) throws RuntimeException {
		try {
			var request = HttpRequest
				.newBuilder(URIModifier.from(endpoint).appendPath("sparql").build())
				.header("Content-Type", SPARQL_UPDATE.getContentTypeString())
				.POST(BodyPublishers.ofString(update))
				.build();
			
			var response = client.send(request, BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				throw new Exception("Response = " + response.statusCode());
			}
			
			return response.body();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String upload(File file, Stream<Link> links) throws RuntimeException {
		try {
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
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String upload(String filename, String mimetype, Reader reader, Stream<Link> links) throws RuntimeException {
		try {
			// build file upload
			var entity = MultipartEntityBuilder.create()				
		       .addPart("upload", new ReaderFileBody(filename, ContentType.create(mimetype, "UTF-8"), reader))
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
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
