package com.goodforgoodbusiness.utils.e2e.foaf;

import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.ENDPOINT_A;
import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.ENDPOINT_B;
import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.newRunner;
import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.shareKeys;

public class Foaf4 {
	public static void main(String[] args) throws Exception {
		newRunner(ENDPOINT_A).update(
			"PREFIX foaf: <http://xmlns.com/foaf/0.1/>                    \n" + 
			"INSERT DATA {                                                \n" + 
			"  <https://twitter.com/ijmad8x>  foaf:name 'Ian Maddison'.   \n" + 
			"  <https://twitter.com/ijmad8x>  foaf:age 35                 \n" + 
			"}                                                            \n" 
		);
		
		shareKeys(ENDPOINT_A, ENDPOINT_B, "https://twitter.com/ijmad8x", "http://xmlns.com/foaf/0.1/name", null);
		
		newRunner(ENDPOINT_B).query(
			"SELECT ?name                                                           \n" + 
			"WHERE {                                                                \n" + 
			"  <https://twitter.com/ijmad8x> <http://xmlns.com/foaf/0.1/name> ?name \n" + 
			"}															            \n",
			"application/xml",
			System.out
		);
		
		// update from the B side
		
		newRunner(ENDPOINT_B).update(
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
		
		newRunner(ENDPOINT_B).query(
			"SELECT ?name                                                           \n" + 
			"WHERE {                                                                \n" + 
			"  <https://twitter.com/ijmad8x> <http://xmlns.com/foaf/0.1/name> ?name \n" + 
			"}															            \n",
			"application/xml",
			System.out
		);
		
		// the A side should not see this update
		
		newRunner(ENDPOINT_A).query(
			"SELECT ?name                                                           \n" + 
			"WHERE {                                                                \n" + 
			"  <https://twitter.com/ijmad8x> <http://xmlns.com/foaf/0.1/name> ?name \n" + 
			"}															            \n",
			"application/xml",
			System.out
		);
		
		// share the updates from B back to A
		
		shareKeys(ENDPOINT_B, ENDPOINT_A, "https://twitter.com/ijmad8x", "http://xmlns.com/foaf/0.1/name", null);
		
		// the A side should now see Hannah Ijecko
		
		newRunner(ENDPOINT_A).query(
			"SELECT ?name                                                           \n" + 
			"WHERE {                                                                \n" + 
			"  <https://twitter.com/ijmad8x> <http://xmlns.com/foaf/0.1/name> ?name \n" + 
			"}															            \n",
			"application/xml",
			System.out
		);
	}
}
