package com.goodforgoodbusiness.utils.e2e;

import static com.goodforgoodbusiness.shared.ConfigLoader.loadConfig;
import static com.google.inject.Guice.createInjector;

import com.goodforgoodbusiness.endpoint.RDFDataModule;
import com.goodforgoodbusiness.endpoint.rdf.RDFRunner;

public class Foaf2 {
	public static void main(String[] args) throws Exception {
		// run with separate runners, as if the system was restarted.
		
		var injector1 = createInjector(new RDFDataModule(loadConfig(Foaf1.class, "data.properties")));
		var runner1 = injector1.getInstance(RDFRunner.class);
		
		runner1.update(
			"PREFIX foaf: <http://xmlns.com/foaf/0.1/>                    \n" + 
			"INSERT DATA {                                                \n" + 
			"  <https://twitter.com/ijmad8x>  foaf:name 'Ian Maddison'.   \n" + 
			"  <https://twitter.com/ijmad8x>  foaf:age 35                 \n" + 
			"}                                                            \n" 
		);
		
		var injector2 = createInjector(new RDFDataModule(loadConfig(Foaf1.class, "data.properties")));
		var runner2 = injector2.getInstance(RDFRunner.class);
		
		runner2.query(
			"SELECT ?name                                                           \n" + 
			"WHERE {                                                                \n" + 
			"  <https://twitter.com/ijmad8x> <http://xmlns.com/foaf/0.1/name> ?name \n" + 
			"}															            \n",
			"application/xml",
			System.out
		);
		
		var injector3 = createInjector(new RDFDataModule(loadConfig(Foaf1.class, "data.properties")));
		var runner3 = injector3.getInstance(RDFRunner.class);
		
		runner3.update(
			"PREFIX foaf:  <http://xmlns.com/foaf/0.1/>  \n" + 
			"DELETE {                                    \n" + 
			"  ?person foaf:name 'Ian Maddison'          \n" + 
			"}                                           \n" + 
			"INSERT {                                    \n" +  
			"  ?person foaf:name 'Hana Ijecko'           \n" + 
			"}                                           \n" + 
			"WHERE {                                     \n" +
			"  ?person foaf:name 'Ian Maddison'          \n" + 
			"}                                           \n"
		);

		var injector4 = createInjector(new RDFDataModule(loadConfig(Foaf1.class, "data.properties")));
		var runner4 = injector4.getInstance(RDFRunner.class);
		
		runner4.query(
			"SELECT ?name                                                           \n" + 
			"WHERE {                                                                \n" + 
			"  <https://twitter.com/ijmad8x> <http://xmlns.com/foaf/0.1/name> ?name \n" + 
			"}															            \n",
			"application/xml",
			System.out
		);
	}
}
