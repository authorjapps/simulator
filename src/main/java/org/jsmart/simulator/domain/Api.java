package org.jsmart.simulator.domain;


/**
 * Created by Siddha on 25/04/2015.
 */
public class Api {
    private String name;
    private String operation;
    private String url;
    private String body;
    private Boolean ignoreBody;
    private RestResponse response;
    
    public Api(String name, String operation, String url, String body, Boolean ignoreBody, RestResponse response) {
        this.name = name;
        this.operation = operation;
        this.url = url;
        this.body = body;
        this.ignoreBody = ignoreBody;
        this.response = response;
    }
    
    public String getName() {
        return name;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getBody() {
        return body;
    }
    
    public Boolean getIgnoreBody() {
        return ignoreBody;
    }
    
    public RestResponse getResponse() {
        return response;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Api api = (Api) o;
        
        if (!operation.equals(api.operation)) return false;
        return url.equals(api.url);
        
    }
    
    @Override
    public int hashCode() {
        int result = operation.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "Api{" +
               "name='" + name + '\'' +
               ", operation='" + operation + '\'' +
               ", url='" + url + '\'' +
               ", response=" + response +
               '}';
    }
    
}
