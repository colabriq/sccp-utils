package com.goodforgoodbusiness.utils.e2e.foaf;

import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.ENDPOINT_A;
import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.ENDPOINT_B;
import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.newRunner;
import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.shareKeys;

public class Foaf6 {
	public static void main(String[] args) throws Exception {
		// run with separate runners, as if the system was restarted.
		
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
		
		// update from the A side - B should see this because claim contains triple 'Ian Maddison'.
		
		newRunner(ENDPOINT_A).update(
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
		
		// another update from the A side 
		
		newRunner(ENDPOINT_A).update(
			"PREFIX foaf:  <http://xmlns.com/foaf/0.1/>  \n" + 
			"DELETE {                                    \n" + 
			"  ?person foaf:name 'Hana Ijecko'           \n" + 
			"}                                           \n" + 
			"INSERT {                                    \n" +  
			"  ?person foaf:name 'Kalana Limano'         \n" + 
			"}                                           \n" + 
			"WHERE {                                     \n" +
			"  ?person foaf:name 'Hana Ijecko'           \n" + 
			"}                                           \n"
		);
		
		// B should see this one too.
		
		newRunner(ENDPOINT_B).query(
			"SELECT ?name                                                           \n" + 
			"WHERE {                                                                \n" + 
			"  <https://twitter.com/ijmad8x> <http://xmlns.com/foaf/0.1/name> ?name \n" + 
			"}															            \n",
			"application/xml",
			System.out
		);
	}
}
