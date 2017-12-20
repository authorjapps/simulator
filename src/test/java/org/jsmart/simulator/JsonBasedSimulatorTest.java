package org.jsmart.simulator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsmart.simulator.domain.Api;
import org.jsmart.simulator.domain.ApiSpec;
import org.jsmart.simulator.domain.RestResponse;
import org.jsmart.simulator.impl.JsonBasedSimulator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Method;
import org.skyscreamer.jsonassert.JSONAssert;
import org.xml.sax.SAXException;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class JsonBasedSimulatorTest {
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
    public void willBindPort() {
        assertThat(simulator.getPort(), allOf(greaterThan(0), lessThan(65536)));
    }
    
    @Test
    public void willCreatehomeDeliveryAndReturnSserviceIdByPOST() throws IOException, SAXException {
        String url = String.format("http://localhost:%d/home-delivery/home-deliveries", simulator.getPort());
        
        HttpClient client = new DefaultHttpClient();
        
        HttpPost post = new HttpPost(url);
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        
        String expected = "{}";
        
        assertThat(responseString , is(expected));
    }
    
    @Test
    public void willCreatehomeDeliveryAndReturnSserviceIdByPUT() throws IOException, SAXException {
        String url = String.format("http://localhost:%d/api/puttest/1", simulator.getPort());
        
        HttpClient client = new DefaultHttpClient();
        
        HttpPut ht = new HttpPut(url);
        HttpResponse response = client.execute(ht);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        
        String expected = "{\"id\":1}";
        
        assertThat( responseString , is(expected));
    }
    
    @Test
    public void willRespondToRequestsForSingleReferral() throws  Exception {
        String url = String.format("http://localhost:%d/application-service/referral/1", simulator.getPort());
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        String expected = "{\"id\":1," +
                          "\"receivedDate\":142911313300099999," +
                          "\"numberInGroup\":0," +
                          "\"asf1Received\":false," +
                          "\"recentAccomodationTypeId\":1," +
                          "\"applicationStatusId\":1," +
                          "\"shopSeeker\":false" +
                          "}";
        
        assertThat("Response did not match with actual.", expected, is(responseString));
    }

    /*@Test
    public void willRespondToRequestsForSingleReferralSpecialChar() throws  Exception {
        String url = String.format("http://localhost:%d/application-service/referral/1/special", simulator.getPort());

        HttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);

        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        String expected = "{\"id\":1," +
                "\"receivedDate\":142911313300099999," +
                "\"numberInGroup\":0," +
                "\"asf1Received\":false," +
                "\"recentAccomodationTypeId\":1," +
                "\"applicationStatusId\":1," +
                "\"shopSeeker\":\"something� �\"" +
                "}";

        assertThat("Response did not match with actual.", expected, is(responseString));
    }*/
    
    @Test
    public void willRespondToRequestsWithoutStatusCodeInRequest() throws  Exception {
        String url = String.format("http://localhost:%d/application-service/referral/1", simulator.getPort());
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        String expected = "{\"id\":1," +
                          "\"receivedDate\":142911313300099999," +
                          "\"numberInGroup\":0," +
                          "\"asf1Received\":false," +
                          "\"recentAccomodationTypeId\":1," +
                          "\"applicationStatusId\":1," +
                          "\"shopSeeker\":false" +
                          "}";
        
        JSONAssert.assertEquals(expected, responseString, true);
        assertThat("Status code was not 200 by default", response.getStatusLine().getStatusCode(), is(200));
    }
    
    @Test
    public void willRespondToRequestsForReferralsWithArrayResponse() throws  Exception {
        String url = String.format("http://localhost:%d/application-service/referrals", simulator.getPort());
        
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
                          "            \"receivedDate\": 1429113133000,\n" +
                          "            \"numberInGroup\": 0,\n" +
                          "            \"asf1Received\": false,\n" +
                          "            \"recentAccomodationTypeId\": 1,\n" +
                          "            \"applicationStatusId\": 1,\n" +
                          "            \"shopSeeker\": false\n" +
                          "          },\n" +
                          "          {\n" +
                          "            \"id\": 2,\n" +
                          "            \"receivedDate\": 1429113133000,\n" +
                          "            \"numberInGroup\": 0,\n" +
                          "            \"asf1Received\": false,\n" +
                          "            \"recentAccomodationTypeId\": 1,\n" +
                          "            \"applicationStatusId\": 1,\n" +
                          "            \"shopSeeker\": false\n" +
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
        assertThat("Error string did not match", responseString, containsString("An exception occurred Because could not find the end point in Simulator"));
    }
    
    @Test
    public void willSimulateOneApiUsingPUTAndGet201Status() throws Exception {
        String expectedResponse = "{\"id\":444}";
        String createhomeDeliveryEndPoint = "/home-deliveries/444";
        
        //e.g. http://localhost:9090/customers
        String url = String.format("http://localhost:%d%s", simulator.getPort(), createhomeDeliveryEndPoint);
        
        //Now invoke the REST end point and do assertions.
        HttpPatch post = new HttpPatch(url);
        StringEntity entityBody = new StringEntity("{\"id\":444}");
        post.setEntity(entityBody);
        
        //execute
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        
        assertNotNull(entity);
        assertThat("Response status code was not 201.", response.getStatusLine().getStatusCode(), is(201));
        
        InputStream content = entity.getContent();
        String responseStringActual = IOUtils.toString(content, "UTF-8");
        assertThat("REST response did not match with actual.", responseStringActual, is(expectedResponse));
    }
    
    @Test
    public void willSimulateOneApiPATCHANdGetTheSameBodyBackAsGETResponse() throws Exception{
        String expectedGETResponse = "{\"id\":555,\"type\":\"AS_CLAIM\"}";
        String homeDeliveryEndPointForPOST = "/home-deliveries/555";
        
        //e.g. http://localhost:9090/customers
        String url = String.format("http://localhost:%d%s", simulator.getPort(), homeDeliveryEndPointForPOST);
        
        //Now invoke the REST end point and do assertions.
        HttpPatch patch = new HttpPatch(url);
        StringEntity entityBody =new StringEntity(expectedGETResponse); // "{"id":555,"type":"AS_CLAIM"}
        patch.setEntity(entityBody);
        
        //execute
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(patch);
        HttpEntity entity = response.getEntity();
        
        assertNotNull(entity);
        assertThat("Response status code was not 201.", response.getStatusLine().getStatusCode(), is(201));
        
        InputStream content = entity.getContent();
        String responseStringActual = IOUtils.toString(content, "UTF-8");
        assertThat("REST response did not match with actual.", responseStringActual, is(expectedGETResponse));
        
        //GET the response for the same URL
        String endPointForGET = "/home-deliveries/555";
        
        // e.g. http://localhost:9090/customers/1
        String urlGET = String.format("http://localhost:%d%s", simulator.getPort(), endPointForGET);
        
        // Now invoke the REST end point and do assertions.
        //HttpGet get = new HttpGet(url);
        HttpClient clientForGet = new DefaultHttpClient();
        
        final HttpGet httpGetRequest = new HttpGet(urlGET);
        httpGetRequest.setHeader("Language", "en_USA");
        HttpResponse responseOfGet = clientForGet.execute(httpGetRequest);
        HttpEntity entityOfGetResponse = responseOfGet.getEntity();
        
        assertNotNull(entity);
        assertThat(responseOfGet.getStatusLine().getStatusCode(), is(200));
        
        InputStream contentOfGetResponse = entityOfGetResponse.getContent();
        String responseStringOfGet = IOUtils.toString(contentOfGetResponse, "UTF-8");
        assertThat(responseStringOfGet, is(expectedGETResponse));
    }
    
    @Test
    public void willSimulateOneApiPUTAndOverrideTheSamePUTApiIfPostedAGain() throws Exception{
        String expectedGETResponse = "{\"id\":555,\"type\":\"AS_CLAIM\"}";
        String homeDeliveryEndPointForPOST = "/home-deliveries/555";
        
        //e.g. http://localhost:9090/customers
        String url = String.format("http://localhost:%d%s", simulator.getPort(), homeDeliveryEndPointForPOST);
        
        //Now invoke the REST end point and do assertions.
        HttpPatch post = new HttpPatch(url);
        StringEntity entityBody =new StringEntity(expectedGETResponse); // "{"id":555,"type":"AS_CLAIM"}
        post.setEntity(entityBody);
        
        //execute
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        
        assertNotNull(entity);
        assertThat("Response status code was not 201.", response.getStatusLine().getStatusCode(), is(201));
        
        InputStream content = entity.getContent();
        String responseStringActual = IOUtils.toString(content, "UTF-8");
        assertThat("REST response did not match with actual.", responseStringActual, is(expectedGETResponse));
        
        //GET the response for the same URL
        String endPointForGET = "/home-deliveries/555";
        
        // e.g. http://localhost:9090/customers/1
        String urlGET = String.format("http://localhost:%d%s", simulator.getPort(), endPointForGET);
        
        // Now invoke the REST end point and do assertions.
        //HttpGet get = new HttpGet(url);
        HttpClient clientForGet = HttpClientBuilder.create().build();
        
        final HttpGet httpGetRequest = new HttpGet(urlGET);
        httpGetRequest.setHeader("Language", "en_USA");
    
        HttpResponse responseOfGet = clientForGet.execute(httpGetRequest);
        HttpEntity entityOfGetResponse = responseOfGet.getEntity();
        
        assertNotNull(entity);
        assertThat("Status was not 200.", responseOfGet.getStatusLine().getStatusCode(), is(200));
        
        InputStream contentOfGetResponse = entityOfGetResponse.getContent();
        String responseStringOfGet = IOUtils.toString(contentOfGetResponse, "UTF-8");
        assertThat("Response did not match with actual.", responseStringOfGet, is(expectedGETResponse));
        
        // *** Override with another body ***
        // now post again with the same URL with different or updated body
        String expectedGETResponse2 = "{\"id\":555,\"type\":\"AS_CLAIM_2\"}";
        
        //Now invoke the REST end point and do assertions.
        HttpPatch post2 = new HttpPatch(url);
        StringEntity entityBody2 =new StringEntity(expectedGETResponse2);
        post2.setEntity(entityBody2);
        
        //execute
        HttpClient client2 = HttpClientBuilder.create().build();
        HttpResponse response2 = client2.execute(post2);
        HttpEntity entity2 = response2.getEntity();
        
        assertNotNull(entity2);
        assertThat("2nd POST Response status code was not 201.", response2.getStatusLine().getStatusCode(), is(201));
        
        String responseStringActual2 = IOUtils.toString(entity2.getContent(), "UTF-8");
        assertThat("2nd POST REST response did not match with actual.", responseStringActual2, is(expectedGETResponse2));
        
        // Now invoke the REST end point and do assertions.
        //HttpGet get = new HttpGet(url);
        HttpClient clientForGet2 = HttpClientBuilder.create().build();
    
        final HttpGet httpGetRequest2 = new HttpGet(urlGET);
        httpGetRequest2.setHeader("Language", "en_USA");
    
        HttpResponse responseOfGet2 = clientForGet2.execute(httpGetRequest2);
        HttpEntity entityOfGetResponse2 = responseOfGet2.getEntity();
        
        assertNotNull(entity2);
        assertThat("2nd GET Status was not 200.", responseOfGet2.getStatusLine().getStatusCode(), is(200));
        
        InputStream contentOfGetResponse2 = entityOfGetResponse2.getContent();
        String responseStringOfGet2 = IOUtils.toString(contentOfGetResponse2, "UTF-8");
        assertThat("2nd GET Response did not match with actual.", responseStringOfGet2, is(expectedGETResponse2));
    }
    
    @Test
    public void willRespondToPOSTAndRequestBody() throws ClientProtocolException, IOException, SAXException {
        String endpoint = "/posttest";
        ArrayList<Api> apis = new ArrayList<Api>();
        Api api = new Api(
                        "POST with body (empty)",
                        Method.POST,
                        endpoint,
                        null,
                        false, null, new RestResponse("{\"accept-language\": \"en_gb\"}", 200, "No body", null, null)
                        );
        apis.add(api);
        api = new Api(
                        "POST with body (test1)",
                        Method.POST,
                        endpoint,
                        "{\"test\":\"1\"}",
                        false, null, new RestResponse("{\"accept-language\": \"en_gb\"}", 200, "Test 1", null, null)
                        );
        apis.add(api);
        api = new Api(
                        "POST with body (test2)",
                        Method.POST,
                        endpoint,
                        "{\"test\":\"2\"}",
                        false, null, new RestResponse("{\"accept-language\": \"en_gb\"}", 200, "Test 2", null, null)
                        );
        apis.add(api);
        ApiSpec spec = new ApiSpec("Test POST", apis);
        simulator.addApiSpec(spec);
        String url = String.format("http://localhost:%d/%s", simulator.getPort(), endpoint);
        
        HttpClient client = new DefaultHttpClient();
        
        HttpPost post = new HttpPost(url);
        StringEntity requestEntity = new StringEntity("{\"test\":\"1\"}");
        requestEntity.setContentType("application/json");
        post.setEntity(requestEntity);
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        
        String expected = "Test 1";
        
        assertThat("Response did not match with actual.", responseString , is(expected));
        
        post = new HttpPost(url);
        requestEntity = new StringEntity("{\"test\":\"2\"}");
        requestEntity.setContentType("application/json");
        post.setEntity(requestEntity);
        response = client.execute(post);
        entity = response.getEntity();
        assertNotNull(entity);
        content = entity.getContent();
        responseString = IOUtils.toString(content, "UTF-8");
        
        expected = "Test 2";
        
        assertThat("Response did not match with actual.", responseString , is(expected));
        
        post = new HttpPost(url);
        response = client.execute(post);
        entity = response.getEntity();
        assertNotNull(entity);
        content = entity.getContent();
        responseString = IOUtils.toString(content, "UTF-8");
        
        expected = "No body";
        
        assertThat(responseString , is(expected));
        
        /**** Check NOT_FOUND logic *****/
        apis = new ArrayList<>();
        api = new Api(
                        "POST with NOT_FOUND",
                        Method.POST,
                        endpoint,
                        "$NOT_FOUND",
                        false, null, new RestResponse("{\"accept-language\": \"en_gb\"}", 200, "Not found", null, null)
                        );
        apis.add(api);
        spec = new ApiSpec("Test POST with default", apis);
        simulator.addApiSpec(spec);
        
        post = new HttpPost(url);
        response = client.execute(post);
        entity = response.getEntity();
        assertNotNull(entity);
        content = entity.getContent();
        responseString = IOUtils.toString(content, "UTF-8");
        
        expected = "No body";
        
        assertThat("Response did not match with actual.", responseString , is(expected));
        
        post = new HttpPost(url);
        requestEntity = new StringEntity("{\"test\":\"XXX\"}");
        requestEntity.setContentType("application/json");
        post.setEntity(requestEntity);
        response = client.execute(post);
        entity = response.getEntity();
        assertNotNull(entity);
        content = entity.getContent();
        responseString = IOUtils.toString(content, "UTF-8");
        
        expected = "Not found";
        
        assertThat("Response did not match with actual.", responseString , is(expected));
    }
    
    @Test
    public void willRespondToPOSTAndIgnoreTheRequestBodyAsFalse() throws ClientProtocolException, IOException, SAXException {
        String endpoint = "/posttest";
        ArrayList<Api> apis = new ArrayList<Api>();
        // 1st test as ignoreBody as false
        Api api = new Api(
                        "POST with body (test1)",
                        Method.POST,
                        endpoint,
                        "{\"test\":\"1\"}",
                        false, null,
                        new RestResponse("{\"accept-language\": \"en_gb\"}", 200, "Test 1", null, null)
                        );
        apis.add(api);
        
        ApiSpec spec = new ApiSpec("Test POST", apis);
        simulator.addApiSpec(spec);
        String url1 = String.format("http://localhost:%d/%s", simulator.getPort(), endpoint);
        
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url1);
        StringEntity requestEntity = new StringEntity("{\"test\":\"1\"}");
        requestEntity.setContentType("application/json");
        post.setEntity(requestEntity);
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        
        String expected = "Test 1";
        assertThat("Response did not match with actual.", responseString , is(expected));
    }
    
    @Test
    public void willRespondToPOSTAndIgnoreTheRequestBodyAsTrue() throws ClientProtocolException, IOException, SAXException {
        ArrayList<Api> apis = new ArrayList<Api>();
        
        // 2nd test with ignoreBody as true
        String endpoint2 = "/posttest2";
        Api api = new Api(
                        "POST with body (test2)",
                        Method.POST,
                        endpoint2,
                        "{\"test\":\"XX\"}",
                        true, null,
                        new RestResponse("{\"accept-language\": \"en_gb\"}", 200, "Test 2", null, null)
                        );
        apis.add(api);
        ApiSpec spec = new ApiSpec("Test POST", apis);
        simulator.addApiSpec(spec);
        
        HttpPost post = new HttpPost(String.format("http://localhost:%d/%s", simulator.getPort(), endpoint2));
        StringEntity requestEntity = new StringEntity("{\"test\":\"9999\"}");
        requestEntity.setContentType("application/json");
        requestEntity.setContentType("application/json");
        post.setEntity(requestEntity);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        
        String expected = "Test 2";
        assertThat("Response did not match with actual.", responseString , is(expected));
    }
    
    @Test
    public void willRespondToGet_nonJsonrawBody() throws IOException, SAXException {
        String endpoint = "/nonjsontest";
        Boolean ignoreBody = true;
        Api api = new Api(
                        "GET with non json body",
                        Method.GET,
                        endpoint,
                        null,
                        ignoreBody, null,
                        new RestResponse("{\"accept-language\": \"en_gb\"}", 200, null, "non-json{}", null)
                        );
        
        ApiSpec spec = new ApiSpec("Test GET", Arrays.asList(api));
        
        simulator.addApiSpec(spec); // This will make the endpoint ready. See @Before
        
        String fullUrl = String.format("http://localhost:%d%s", simulator.getPort(), endpoint);
        
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getReq = new HttpGet(fullUrl);

        HttpResponse response = client.execute(getReq);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        
        String expected = "non-json{}";
        assertThat(responseString , is(expected));
    }
    
    @Test
    public void willRespondWith_nonJsonrawBody() throws  Exception {
        String url = String.format("http://localhost:%d/nonjson/1", simulator.getPort());
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        
        assertThat(responseString, is("non-json-123{}"));
    }
    
    @Test
    public void willRespondWith_jsonBaretextNode() throws  Exception {
        String url = String.format("http://localhost:%d/textnodejson/1", simulator.getPort());
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
    
        // ---------------------------------------------------------------
        // Mark the extra double quotes. That's becaz its a valid JSON node
        // ie a TextNode. If you do a get request in browser, it appears
        // with double quotes.
        // ---------------------------------------------------------------
        assertThat(responseString, is("\"text-valid-json-123{}\""));
    }
    
    @Test
    public void willRespondWith_headers() throws  Exception {
        String url = String.format("http://localhost:%d/customers", simulator.getPort());
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        
        final Header[] allHeaders = response.getAllHeaders();
        
        assertThat(allHeaders.length, is(7));
        assertThat(allHeaders[3].getName(), is("language"));
        assertThat(allHeaders[3].getValue(), is("en_GB"));
        assertThat(allHeaders[4].getName(), is("client_id"));
        assertThat(allHeaders[4].getValue(), is("abcd-client-001"));
    }
    
    @Test
    public void willRespondWillMatchWithRequest_headers() throws  Exception {
        String url = String.format("http://localhost:%d/api/mule/vanilla/1", simulator.getPort());
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet get = new HttpGet(url);
        
        get.setHeader("client_id_x", "idx-001");
        get.setHeader("client_secret_x", "sec-001");
        HttpResponse response = client.execute(get);
        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }
    
    @Test
    public void willSimulatePutWith_headers() throws IOException, SAXException {
        String url = String.format("http://localhost:%d/api/puttest/headers/2", simulator.getPort());
        
        HttpClient client = new DefaultHttpClient();
        
        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("test_header_key", "test_header_value");
        httpPut.setHeader("test_header_something", "test_val_doesnt_matter");
    
        HttpResponse response = client.execute(httpPut);
        HttpEntity entity = response.getEntity();
        assertNotNull(entity);
        
        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        
        String expected = "{\"id\":1}";
        
        assertThat( responseString , is(expected));
    }
    
    @Test
    public void willRespondWillNotMatchWithRequest_wrongHeaders() throws  Exception {
        String url = String.format("http://localhost:%d/api/mule/vanilla/1", simulator.getPort());
        
        HttpClient client = new DefaultHttpClient();
        
        HttpGet get = new HttpGet(url);
    
        get.setHeader("client_id_x", "INVALID-idx-009");
        get.setHeader("client_secret_x", "INVALID-sec-009");
        try {
            HttpResponse response = client.execute(get);
        } catch (IOException ex) {
            assertThat(ex.getMessage(), containsString("localhost:9901 failed to respond"));
            assertThat(ex.getClass().getName(), is("org.apache.http.NoHttpResponseException"));
        }
    }
}

