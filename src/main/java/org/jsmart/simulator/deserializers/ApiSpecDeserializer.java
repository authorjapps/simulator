package org.jsmart.simulator.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.jsmart.simulator.domain.Api;
import org.jsmart.simulator.domain.ApiSpec;
import org.jsmart.simulator.domain.RestResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiSpecDeserializer extends JsonDeserializer<ApiSpec> {
    
    @Override
    public ApiSpec deserialize(final JsonParser jp, final DeserializationContext ctxt)
                    throws IOException {
        final JsonNode node = jp.getCodec().readTree(jp);
        
        final String name = node.get("name").asText();
        final List<Api> apis = new ArrayList<>();
        
        final ArrayNode apisNode = (ArrayNode) node.get("apis");
        for(JsonNode apiNode : apisNode) {
            String apiName = apiNode.get("name").asText();
            String operation = apiNode.get("operation").asText();
            String url = apiNode.get("url").asText();
            JsonNode bodyNode = apiNode.get("body");
            String body =  (null != bodyNode) ? bodyNode.toString() : null;
            
            JsonNode ignoreBodyNode = apiNode.get("ignoreBody");
            Boolean ignoreBody = (null != ignoreBodyNode) && ignoreBodyNode.asBoolean();
            
            JsonNode jsonStatusNode = apiNode.get("response").get("status");
            int responseStatus = (null != jsonStatusNode) ? jsonStatusNode.asInt() : 200;
            final JsonNode jsonBodyNode = apiNode.get("response").get("body");
            String responseBody = jsonBodyNode != null? jsonBodyNode.toString() : null;
            
            final JsonNode stringBodyNode = apiNode.get("response").get("stringBody");
            // ------------------------------------------------------------
            // Do not read as JSONNode and then then toString. Not the same
            // ------------------------------------------------------------
            String stringBody = stringBodyNode != null? stringBodyNode.asText() : null;
            
            final JsonNode xmlBodyNode = apiNode.get("response").get("xmlBody");
            String xmlBody = xmlBodyNode != null ? xmlBodyNode.asText() : null; //TODO- Think how to handle for SOAP xml response
            
            JsonNode jsonHeaderNode = apiNode.get("response").get("headers");
            String responseHeaders = (null != jsonHeaderNode) ? jsonHeaderNode.toString() : "";
            
            Api api = new Api(apiName, operation, url, body, ignoreBody,
                            new RestResponse(responseHeaders, responseStatus, responseBody, stringBody,  xmlBody));
            apis.add(api);
        }
        
        return new ApiSpec(name, apis);
    }
    
}