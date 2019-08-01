package com.colabriq.utils.key;

import static com.colabriq.shared.ConfigLoader.loadConfig;

import com.colabriq.kpabe.KPABEEncryption;
import com.colabriq.kpabe.KPABEKeyManager;

public class GenShareKey {
	public static void main(String[] args) throws Exception {
		var config = loadConfig(args[0]);
		
		var keyPair = KPABEKeyManager.ofKeys(
			config.getString("kpabe.publicKey"),
			config.getString("kpabe.secretKey")
		);
		
		var instance = KPABEEncryption.getInstance(keyPair);
		
		var shareKey = instance.shareKey("foo");
		
		System.out.println(shareKey.getPublic().toString());
		System.out.println(shareKey.getPrivate().toString());
	}
}
