package com.goodforgoodbusiness.utils.key;

import static com.goodforgoodbusiness.shared.ConfigLoader.loadConfig;

import com.goodforgoodbusiness.kpabe.local.KPABELocalInstance;

public class GenShareKey {
	public static void main(String[] args) throws Exception {
		var config = loadConfig(args[0]);
		
		var instance = KPABELocalInstance.forKeys(
			config.getString("kpabe.publicKey"),
			config.getString("kpabe.secretKey")
		);
		
		var shareKey = instance.shareKey("foo");
		
		System.out.println(shareKey.getPublic().toString());
		System.out.println(shareKey.getPrivate().toString());
	}
}
