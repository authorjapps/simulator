package org.jsmart.simulator.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jsmart.simulator.deserializers.ApiSpecDeserializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the Api Specification of a Simulator consisting of single or multiple REST apis.
 * @author Siddha.
 */
@JsonDeserialize(using = ApiSpecDeserializer.class)
public class ApiSpec {
    private String name;
    private List<Api> apis;

    /**
     * @param name name of the simulator exposing one or more REST apis
     * @param apis one or more REST apis
     */
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
