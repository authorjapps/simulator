package org.jsmart.simulator.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.jsmart.simulator.domain.ApiSpec;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Siddha
 */
public class SimulatorJsonUtils {

    public static ApiSpec deserialize(Reader json) {
        final ApiSpec apiSpec;
        try {
            apiSpec = new ObjectMapper().readValue(json, ApiSpec.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return apiSpec;
    }

    public static Map getAsMap(String jsonString) {
        Map<String,String> map = new HashMap<String,String>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            //convert JSON string to Map
            map = mapper.readValue(jsonString, new TypeReference<HashMap<String,String>>(){});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public static void main(String[] args) {
        String itemJson = "";

        String json = "{\"phonetype\":\"N95\",\"cat\":\"WP\"}";

        Map<String,String> map = new HashMap<String,String>();
        ObjectMapper mapper = new ObjectMapper();

        try {

            //convert JSON string to Map
            map = mapper.readValue(json,
                    new TypeReference<HashMap<String,String>>(){});

            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readValue(String jsonString, String key) {
        String attributeValue = null;

        try {
            attributeValue = ((String) JsonPath.read(jsonString, "$."+key));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return attributeValue;

    }
}
