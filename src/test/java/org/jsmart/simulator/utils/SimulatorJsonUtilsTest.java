package org.jsmart.simulator.utils;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SimulatorJsonUtilsTest {

    @Test
    public void willGetMapFromJsonHeaderString() {
        String headerString = "{\n" +
                "          \"Location\" : \"/customers/10000054\"\n" +
                "        }";
        Map map = SimulatorJsonUtils.getAsMap(headerString);
        assertThat("Location header did not match", map.get("Location"), Is.<Object>is("/customers/10000054"));
    }

    @Test
    public void willGetLocationValueFromHeaderJsonUsingJsonPath(){
        String headerString = "{\n" +
                "          \"Location\" : \"/customers/10000054\"\n" +
                "        }";
        String location = SimulatorJsonUtils.readValue(headerString, "Location");
        assertThat("Location header did not match", location, is("/customers/10000054"));

        // In case required to set the header individually.
        //response.addValue("Location", (String)SimulatorJsonUtils.getAsMap(api.getResponse().getHeaders()).get("Location") );
        //response.addValue("Location",  SimulatorJsonUtils.readValue(api.getResponse().getHeaders(), "Location"));
    }
    
    @Test
    public void testReqHeadersMap() throws Exception {
        
        String rawHeadersAsItWasReceived = "GET /customers HTTP/1.1\n"
                                           + "cache-control: no-cache\n"
                                           + "Postman-Token: f3fcd81a-8f9c-41bb-b28f-07540ee3f197\n"
                                           + "Content-Type: application/json\n"
                                           + "hsbc-client-id-x: rss-value-client-id-x\n"
                                           + "User-Agent: PostmanRuntime/3.0.11-hotfix.2\n"
                                           + "Accept: */*\n"
                                           + "Host: localhost:9999\n"
                                           + "accept-encoding: gzip, deflate\n"
                                           + "Connection: keep-alive";
    
        final Map<String, Object> requestHeadersMap = SimulatorJsonUtils.getRequestHeadersMap(rawHeadersAsItWasReceived);
        
        assertThat(requestHeadersMap.size(), is(9));
        assertThat(requestHeadersMap, hasEntry("hsbc-client-id-x", "rss-value-client-id-x"));
        assertThat(requestHeadersMap, hasEntry("Content-Type", "application/json"));
        assertThat(requestHeadersMap.get("GET /customers HTTP/1.1"), nullValue());
    }
}