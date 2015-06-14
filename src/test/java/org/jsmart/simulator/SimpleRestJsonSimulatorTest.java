package org.jsmart.simulator;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsmart.simulator.base.Simulator;
import org.jsmart.simulator.impl.SimpleRestJsonSimulator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class SimpleRestJsonSimulatorTest {
    public static final int HTTP_PORT = 9901;
    private final Simulator simulator = new SimpleRestJsonSimulator(HTTP_PORT);

    @Before
    public void startSimulator() {
        simulator.run();
    }

    @After
    public void stopSimulator() {
        simulator.stop();
    }

    @Test
    public void willBindPort() {
        assertThat(simulator.getPort(), allOf(greaterThan(0), lessThan(65536)));
    }

    @Test
    public void willRespondToRequestsForSingleResource() throws  Exception {
        String url = String.format("http://localhost:%d/customers/1", simulator.getPort());

        HttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);

        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        String expected = "{\"id\":1,\"name\":\"Kate\",\"sex\":\"Female\"}";

        assertThat("Response did not match with actual.", expected, is(responseString));
    }

    @Test
    public void willRespondToRequestsWithoutStatusCodeInRequest() throws  Exception {
        String url = String.format("http://localhost:%d/customers/1", simulator.getPort());

        HttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);

        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        String expected = "{\n" +
                "          \"id\": 1,\n" +
                "          \"name\": \"Kate\",\n" +
                "          \"sex\": \"Female\"\n" +
                "        }";

        JSONAssert.assertEquals(expected, responseString, true);
        assertThat("Status code was not 200 by default", response.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    public void willRespondToRequestsForResourcesWithArrayResponse() throws  Exception {
        String url = String.format("http://localhost:%d/customers", simulator.getPort());

        HttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);

        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        String expected = "[\n" +
                "          {\n" +
                "            \"id\": 1,\n" +
                "            \"name\": \"Kate\",\n" +
                "            \"sex\": \"Female\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 2,\n" +
                "            \"name\": \"Rowland\",\n" +
                "            \"sex\": \"Male\"\n" +
                "          }\n" +
                "        ]";

        JSONAssert.assertEquals(expected, responseString, true);
        assertThat("Status code was not 200 by default", response.getStatusLine().getStatusCode(), is(200));
    }

    @Test
    public void willRespondToRequestsWithErrorMessageForApi() throws  Exception {
        String url = String.format("http://localhost:%d/referralXXYY", simulator.getPort());

        HttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);

        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        String expected = "";

        assertThat("Status code was not 404.", response.getStatusLine().getStatusCode(), is(404));
        assertThat("Error string did not match", responseString,
                containsString("(404)Simulator did not process the request as"));
    }

    @Test
    public void willCreateResourceWithPOSTAndReturnLocationOfTheResource() throws ClientProtocolException, IOException, SAXException {
        String url = String.format("http://localhost:%d/orders", simulator.getPort());

        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(url);
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");

        String expected = "{\"id\":10019,\"customerId\":1,\"item\":\"Mobile Phone\"}";
        assertThat("Response did not match with actual.", responseString , is(expected));

        String location = response.getFirstHeader("Location").getValue();
        assertThat("Location header did not match", location, is("http://localhost:9901/jsmart/orders/10019"));

        assertThat("Status code was not 201.", response.getStatusLine().getStatusCode(), is(201));
    }
}