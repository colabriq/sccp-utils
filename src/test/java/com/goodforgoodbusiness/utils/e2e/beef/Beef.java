package com.goodforgoodbusiness.utils.e2e.beef;

import static com.goodforgoodbusiness.shared.ConfigLoader.loadConfig;
import static com.google.inject.Guice.createInjector;

import com.goodforgoodbusiness.endpoint.EndpointModule;
import com.goodforgoodbusiness.endpoint.rdf.RDFPreloader;
import com.goodforgoodbusiness.endpoint.rdf.RDFRunner;

public class Beef {
	private static final String COW_QUERY = 
			"PREFIX com: <https://schemas.goodforgoodbusiness.com/common-operating-model/lite/>" +
			"SELECT ?buyerRef ?quantity ?unitPrice ?shipmentRef ?ain ?vaccine WHERE {" + 
			"    ?order com:buyer <urn:uuid:448c5299-b858-4eb1-bc55-0a7a6c04efee>;" + 
			"        com:buyerRef ?buyerRef;" + 
			"        com:quantity ?quantity;" + 
			"        com:unitPrice ?unitPrice;" + 
			"        com:fulfilledBy ?shipment." + 
			"    ?shipment com:consignee <urn:uuid:448c5299-b858-4eb1-bc55-0a7a6c04efee>;" + 
		    "        com:shipmentRef ?shipmentRef." + 
			"    OPTIONAL {" + 
		    "        ?shipment com:usesItem ?cow." + 
			"        OPTIONAL {" + 
		    "            ?cow com:ain ?ain." + 
			"            OPTIONAL {" + 
			"                ?cow com:vaccination ?vaccination." + 
			"                ?vaccination com:vaccine ?vaccine." + 
			"            }" + 
			"        }" + 
		    "    }" + 
		    "}";
	
	public static void main(String[] args) throws Exception {
		var injector = createInjector(new EndpointModule(loadConfig(Beef.class, "beef.properties")));
		injector.getInstance(RDFPreloader.class).preload();
		
		System.out.println(
			injector.getInstance(RDFRunner.class)
				.query(COW_QUERY, "application/xml")
		);
	}
}
