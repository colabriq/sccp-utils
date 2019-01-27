package com.goodforgoodbusiness.utils.e2e.foaf;

import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.ENDPOINT_A;
import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.ENGINE_A;
import static java.lang.System.out;

import com.goodforgoodbusiness.endpoint.RDFDataModule;
import com.goodforgoodbusiness.engine.EngineModule;
import com.goodforgoodbusiness.utils.e2e.AppTester;
import com.goodforgoodbusiness.utils.e2e.RDF;

public class Foaf8 {
	public static void main(String[] args) throws Exception {
		try (var engine = new AppTester(EngineModule.class, ENGINE_A)) {
			try (var rdf = new AppTester(RDFDataModule.class, ENDPOINT_A)) {
				out.println(new RDF(rdf).update(
					"PREFIX foaf: <http://xmlns.com/foaf/0.1/>                    \n" + 
					"INSERT DATA {                                                \n" + 
					"  <https://twitter.com/ijmad8x>  foaf:name 'Ian Maddison'.   \n" + 
					"  <https://twitter.com/ijmad8x>  foaf:age 35                 \n" + 
					"}                                                            \n" 
				));
				
				out.println(new RDF(rdf).query(
					"SELECT ?name                                                           \n" + 
					"WHERE {                                                                \n" + 
					"  <https://twitter.com/ijmad8x> <http://xmlns.com/foaf/0.1/name> ?name \n" + 
					"}															            \n"
				));
				
				out.println(new RDF(rdf).update(
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
				));
		
				out.println(new RDF(rdf).query(
					"SELECT ?name                                                           \n" + 
					"WHERE {                                                                \n" + 
					"  <https://twitter.com/ijmad8x> <http://xmlns.com/foaf/0.1/name> ?name \n" + 
					"}															            \n"
				));
			}
		}
	}
}
