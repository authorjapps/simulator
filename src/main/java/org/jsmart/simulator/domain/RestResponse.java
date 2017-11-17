package org.jsmart.simulator.domain;

/**
 * Created by Siddha on 27/04/2015.
 */
public class RestResponse {
    private final String headers;
    private final int status;
    private final String body; //JSON body
    private final String stringBody; //non-json body
    private final String xmlBody; //TODO- For SOAP responses
    
    public RestResponse(String headers, int status, String body, String stringBody, String xmlBody) {
        this.headers = headers;
        this.status = status;
        this.body = body;
        this.stringBody = stringBody;
        this.xmlBody = xmlBody;
    }
    
    public String getHeaders() {
        return headers;
    }
    
    public int getStatus() {
        return status;
    }
    
    public String getBody() {
        return body;
    }
    
    public String getStringBody() {
        return stringBody;
    }
    
    public String getXmlBody() {
        return xmlBody;
    }
    
    @Override
    public String toString() {
        return "RestResponse{" +
               "headers='" + headers + '\'' +
               ", status=" + status +
               ", body='" + body + '\'' +
               ", stringBody='" + stringBody + '\'' +
               ", xmlBody='" + xmlBody + '\'' +
               '}';
    }
}
