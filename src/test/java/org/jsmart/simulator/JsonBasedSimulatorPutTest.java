package org.jsmart.simulator;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsmart.simulator.impl.JsonBasedSimulator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class JsonBasedSimulatorPutTest {
    public static final int HTTP_PORT = 9901;
    private final JsonBasedSimulator simulator = new JsonBasedSimulator(HTTP_PORT);
    
    @Before
    public void startSimulator() {
        simulator.start();
    }
    
    @After
    public void stopSimulator() {
        simulator.stop();
    }
    
    @Test
    public void willSimulatePutWith_headers() throws IOException, SAXException {
        String url = String.format("http://localhost:%d/api/puttest/body/2", simulator.getPort());
        
        HttpClient client = new DefaultHttpClient();
        
        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("test_header_key", "test_header_value");
       
        httpPut.setEntity(new StringEntity("{\"userName\": \"Dianne\"}"));
        
        HttpResponse response = client.execute(httpPut);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        
        String expected = "{\"name\":\"Dianne\"}";
        
        assertThat( responseString , is(expected));
    }
    
}

