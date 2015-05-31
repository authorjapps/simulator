package org.japps.simulator.utils;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SimulatorJsonUtilsTest {

    @Test
    public void willGetMapFromJsonHeaderString() {
        String headerString = "{\n" +
                "          \"Location\" : \"/customers/10000054\"\n" +
                "        }";
        Map map = SimulatorJsonUtils.getAsMap(headerString);
        assertThat("Location header did not match", map.get("Location"), is("/customers/10000054"));
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
}