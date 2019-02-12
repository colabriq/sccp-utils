package com.goodforgoodbusiness.utils.ingest;

import static com.goodforgoodbusiness.utils.ShareClient.shareKeys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import com.goodforgoodbusiness.model.TriTuple;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/** 
 * Reads a series of share keys in Joe's shares.json format
 */
public abstract class ShareUploader {
	private final JsonArray shares;
	
	protected ShareUploader(File shareFile) throws FileNotFoundException, IOException {
		if (shareFile.exists() && shareFile.isFile()) {
			try (var fp = new FileReader(shareFile)) {
				this.shares = new JsonParser().parse(fp).getAsJsonArray();
			}
		}
		else {
			throw new IllegalArgumentException(shareFile.getAbsolutePath());
		}
	}
	
	protected abstract URI getToEngine(String infoCreator);
	protected abstract URI getFromEngine(String infoCreator);
	
	protected void run(boolean startOnly) throws Exception {
		for (var i = 0; i < shares.size(); i++) {
			var shareStmt = shares.get(i).getAsJsonObject();
			
			var infoCreator = shareStmt.get("information_creator").getAsString();
			var patternsArray = shareStmt.get("patterns").getAsJsonArray();
			var includeAtStart = shareStmt.get("include_at_start").getAsBoolean();
			
//			if ((includeAtStart && startOnly) || !includeAtStart) {
				var shareToUri = getToEngine(infoCreator);
				var shareFromUri = getFromEngine(infoCreator);
				
				if (shareFromUri != null) {
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
//			}
		}
	}
}
