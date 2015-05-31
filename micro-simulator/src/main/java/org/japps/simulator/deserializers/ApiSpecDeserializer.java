package org.japps.simulator.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.japps.simulator.domain.Api;
import org.japps.simulator.domain.ApiSpec;
import org.japps.simulator.domain.RestResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiSpecDeserializer extends JsonDeserializer<ApiSpec> {

    @Override
    public ApiSpec deserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        final JsonNode node = jp.getCodec().readTree(jp);

        final String name = node.get("name").asText();
        final List<Api> apis = new ArrayList<>();

        final ArrayNode apisNode = (ArrayNode) node.get("apis");
        for(JsonNode apiNode : apisNode) {
            String apiName = apiNode.get("name").asText();
            String operation = apiNode.get("operation").asText();
            String url = apiNode.get("url").asText();

            JsonNode jsonStatusNode = apiNode.get("response").get("status");
            int responseStatus = (null != jsonStatusNode) ? jsonStatusNode.asInt() : 200;
            String responseBody = apiNode.get("response").get("body").toString();
            JsonNode jsonHeaderNode = apiNode.get("response").get("headers");
            String responseHeaders = (null != jsonHeaderNode) ? jsonHeaderNode.toString() : "";

            Api api = new Api(apiName, operation, url, new RestResponse(responseHeaders, responseStatus, responseBody));
            apis.add(api);
        }

        return new ApiSpec(name, apis);
    }

}