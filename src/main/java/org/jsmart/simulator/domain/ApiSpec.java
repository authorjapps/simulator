package org.jsmart.simulator.domain;



import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;

import org.jsmart.simulator.deserializers.ApiSpecDeserializer;

/**
 * Created by Siddha on 25/04/2015.
 */
@JsonDeserialize(using = ApiSpecDeserializer.class)
public class ApiSpec {
    private String name;
    private List<Api> apis;
    
    public ApiSpec(String name, List<Api> apis) {
        this.name = name;
        this.apis = new ArrayList<>(apis);
    }
    
    public String getName() {
        return name;
    }
    
    public List<Api> getApis() {
        return apis;
    }
}