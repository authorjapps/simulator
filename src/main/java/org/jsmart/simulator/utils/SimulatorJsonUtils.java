package org.jsmart.simulator.utils;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.jsmart.simulator.domain.ApiSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

/**
 * @author Siddha
 */
public class SimulatorJsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(SimulatorJsonUtils.class);

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
        Map<String,String> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            //convert JSON string to Map
            map = mapper.readValue(jsonString, new TypeReference<HashMap<String,String>>(){});
        } catch (Exception ex) {
            logger.error("\n\nMicro-Simulator: Encountered Parsing Exception probably: " + ex);
            System.err.println("\n\nMicro-Simulator: Parse Exception probably: " + ex);
            throw new RuntimeException(ex);
        }

        return map;
    }

    public static void main(String[] args) {
        String raw = "GET /customers HTTP/1.1\n"
                     + "cache-control: no-cache\n"
                     + "Postman-Token: d2b1f2b1-bfff-401a-8627-b1944dae1aa9\n"
                     + "Content-Type: application/json\n"
                     + "hsbc-client-id-x: rss-value-client-id-x\n"
                     + "User-Agent: PostmanRuntime/3.0.11-hotfix.2\n"
                     + "Accept: */*\n"
                     + "Host: localhost:9999\n"
                     + "accept-encoding: gzip, deflate\n"
                     + "Connection: keep-alive\n"
                     + "\n";
        Map<String, Object> myMap = getRequestHeadersMap(raw);
    
        System.out.println(myMap);
        
//        Map<String, String> properties = Splitter.onPattern("(?<=\\:\\d+),").withKeyValueSeparator(": ").split(raw);
//        System.out.println("output " + properties );
    
        //        String itemJson = "";
//
//        String json = "{\"phonetype\":\"N95\",\"cat\":\"WP\"}";
//
//        Map<String,String> map = new HashMap<String,String>();
//        ObjectMapper mapper = new ObjectMapper();
//
//        try {
//
//            //convert JSON string to Map
//            map = mapper.readValue(json,
//                    new TypeReference<HashMap<String,String>>(){});
//
//            System.out.println(map);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    
    public static Map<String, Object> getRequestHeadersMap(String colonSeparatedHeaders) {
        final String[] colonPairs = colonSeparatedHeaders.replaceAll("\r", "").split("\n");
        Map<String, Object> headersMap = new HashMap<>();
    
        final String KV_SEPARATOR = ": ";
        
        for (int i=0;i<colonPairs.length;i++) {
            String pair = colonPairs[i];
            String[] keyValue = pair.split(KV_SEPARATOR);
            if(keyValue.length == 2){
                headersMap.put(keyValue[0], keyValue[1]);
            }
        }
        return headersMap;
    }
    
    public static String readValue(String jsonString, String key) {
        try {
            return ((String) JsonPath.read(jsonString, "$."+key));
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    
}
