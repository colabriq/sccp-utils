package com.goodforgoodbusiness.utils.ingest;

import static com.goodforgoodbusiness.utils.ShareClient.shareKeys;
import static com.goodforgoodbusiness.utils.ingest.beef.Actor.ACTORS;

import java.io.File;
import java.io.FileReader;
import java.util.Optional;

import com.goodforgoodbusiness.model.TriTuple;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ShareIngester {
	private static final JsonParser PARSER = new JsonParser();

	public static void main(String[] args) throws Exception {
		var shareToUri = ACTORS.get("RETAILER-0000000000").engine;
		
		var shareFile = new File(args[0]);
		if (shareFile.exists() && shareFile.isFile()) {
			JsonElement elm;
			try (var fp = new FileReader(shareFile)) {
				elm = PARSER.parse(fp);
			}
			
			var arr = elm.getAsJsonArray();
			for (var i = 0; i < arr.size(); i++) {
				var shareStmt = arr.get(i).getAsJsonObject();
				
				var infoCreator = shareStmt.get("information_creator").getAsString();
				var includeAtStart = shareStmt.get("include_at_start").getAsBoolean();
				var patternsArray = shareStmt.get("patterns").getAsJsonArray();
				
				if (ACTORS.containsKey(infoCreator)) {
					var shareFromUri = ACTORS.get(infoCreator).engine;
					for (var j = 0; j < patternsArray.size(); j++) {
						var pattern = patternsArray.get(j).getAsJsonObject();
						var spec = 
							TriTuple.fromN3Quoted(
								Optional.ofNullable(pattern.get("s")).map(JsonElement::getAsString),
								Optional.ofNullable(pattern.get("p")).map(JsonElement::getAsString),
								Optional.ofNullable(pattern.get("o")).map(JsonElement::getAsString)
							)
						;
						
						shareKeys(shareFromUri, shareToUri, spec);
					}
				}
				else {
					System.err.println("Skipping share statements for " + infoCreator + " (unknown actor)");
				}
			}
		}
		else {
			throw new IllegalArgumentException(args[0]);
		}
	}
}
