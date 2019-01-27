package com.goodforgoodbusiness.utils.e2e;

import static com.goodforgoodbusiness.shared.ConfigLoader.loadConfig;
import static com.google.inject.Guice.createInjector;

import java.io.Closeable;
import java.io.IOException;

import org.apache.commons.configuration2.Configuration;

import com.goodforgoodbusiness.utils.e2e.foaf.Foaf;
import com.goodforgoodbusiness.webapp.Webapp;

import com.google.inject.Module;

public class AppTester implements Closeable {
	final Configuration config;
	final Module module;
	final Webapp webapp;
	
	public AppTester(Class<? extends Module> clazz, String configFile) throws Exception {
		this.config = loadConfig(Foaf.class, configFile);
		this.module = clazz.getConstructor(Configuration.class).newInstance(config);
		this.webapp = createInjector(module).getInstance(Webapp.class);
		this.webapp.start();
	}
	
	@Override
	public void close() throws IOException {
		webapp.stop();
	}

}
