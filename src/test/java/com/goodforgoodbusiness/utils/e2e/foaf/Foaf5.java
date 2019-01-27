package com.goodforgoodbusiness.utils.e2e.foaf;

import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.A;
import static com.goodforgoodbusiness.utils.e2e.foaf.Foaf.newRunner;

public class Foaf5 {
	public static void main(String[] args) throws Exception {
		newRunner(A).update(
			"PREFIX foaf: <http://xmlns.com/foaf/0.1/>                    \n" + 
			"INSERT DATA {                                                \n" + 
			"  <https://twitter.com/ijmad8x>  foaf:name 'Ian Maddison'.   \n" + 
			"  <https://twitter.com/ijmad8x>  foaf:age 35                 \n" + 
			"}                                                            \n" 
		);
		
		newRunner(A).update(
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

		newRunner(A).update(
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
		
		newRunner(A).query(
			"SELECT ?name                                                           \n" + 
			"WHERE {                                                                \n" + 
			"  <https://twitter.com/ijmad8x> <http://xmlns.com/foaf/0.1/name> ?name \n" + 
			"}															            \n",
			"application/xml",
			System.out
		);
	}
}
