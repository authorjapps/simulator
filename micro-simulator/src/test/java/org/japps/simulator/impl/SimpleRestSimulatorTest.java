package org.japps.simulator.impl;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.japps.simulator.domain.Api;
import org.japps.simulator.domain.RestResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Method;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class SimpleRestSimulatorTest {

    public static final int HTTP_PORT = 9090;
    private SimpleRestSimulator simulator;

    @Before
    public void startSimulator() {
        //Start the REST simulator inside the TestCase.
    }

    @After
    public void stopSimulator() {
        simulator.stop();
    }

    @Test
    public void willSimulateGETAtRunTimeWithPortAndUrlUsingApiBuilder() throws Exception{
        String endPoint = "/customers/1";
        String requiredResponse = "{\n" +
                "    \"id\": 1,\n" +
                "    \"age\": 0,\n" +
                "    \"isAdult\": false\n" +
                "}";
        Api api = new Api(
                "Get Customers By Id API",
                Method.GET,
                endPoint,
                new RestResponse("{\"accept-language\": \"en_gb\"}", 200, requiredResponse)
        );
        simulator = new SimpleRestSimulator(HTTP_PORT)
                .withApi(api)
                .run();

        // e.g. http://localhost:9090/customers/1
        String url = String.format("http://localhost:%d%s", simulator.getPort(), endPoint);

        // Now invoke the REST end point and do assertions.
        //HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(new HttpGet(url));
        HttpEntity entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Status was not 200.", response.getStatusLine().getStatusCode(), is(200));

        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        assertThat("Response did not match with actual.", responseString, is(requiredResponse));
    }

    @Test
    public void willSimulateTwoRESTApisAtTheSamePort() throws Exception{
        String endPoint1 = "/customers/1";
        String endPoint2 = "/orders/1";

        String customerResponse = "{\n" +
                "    \"id\": 1,\n" +
                "    \"age\": 0,\n" +
                "    \"isAdult\": false\n" +
                "}";

        String orderResponse = "{\n" +
                "    \"id\": 1,\n" +
                "    \"customerId\": 1,\n" +
                "    \"quantity\": 60\n" +
                "}";

        Api apiCustomer = new Api(
                "Get Customer By Id API",
                Method.GET,
                endPoint1,
                new RestResponse("some-headers", 200, customerResponse)
        );
        Api apiOrder = new Api(
                "Get Order Details By Order Id",
                Method.GET,
                endPoint2,
                new RestResponse(null, 200, orderResponse)
        );
        simulator = new SimpleRestSimulator(HTTP_PORT)
                .withApi(apiCustomer)
                .withApi(apiOrder)
                .run();

        //invoke Customer endPoint and assert
        String url = String.format("http://localhost:%d%s", simulator.getPort(), endPoint1);

        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Status was not 200.", response.getStatusLine().getStatusCode(), is(200));

        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        assertThat("Customer Response did not match with actual.", responseString, is(customerResponse));

        //invoke /orders/1 endPoint and assert
        url = String.format("http://localhost:%d%s", simulator.getPort(), endPoint2);

        get = new HttpGet(url);
        client = new DefaultHttpClient();
        response = client.execute(get);
        entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Status was not 200.", response.getStatusLine().getStatusCode(), is(200));

        content = entity.getContent();
        responseString = IOUtils.toString(content, "UTF-8");
        assertThat("Order Response did not match with actual.", responseString, is(orderResponse));
    }

    @Test
    public void willSimulatePOSTToCreateCustomer() throws Exception{
        String endPoint = "/customers";
        String requiredResponse = "{\n" +
                "    \"id\": 1,\n" +
                "    \"age\": 0,\n" +
                "    \"isAdult\": false\n" +
                "}";

        Api api = new Api(
                "Get Customers By Id API",
                Method.GET,
                endPoint,
                new RestResponse(null, 200, requiredResponse)
        );

        simulator = new SimpleRestSimulator(HTTP_PORT)
                .withApi(api)
                .run();

        String url = String.format("http://localhost:%d%s", simulator.getPort(), endPoint);

        HttpGet get = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Status was not 200.", response.getStatusLine().getStatusCode(), is(200));

        InputStream content = entity.getContent();
        String simulatorResponse = IOUtils.toString(content, "UTF-8");
        assertThat("Response did not match with actual.", simulatorResponse, is(requiredResponse));
    }

    @Test
    public void willSimulateOneApiPOSTWithPortAndUrlUsingApiBuilder() throws Exception{
        String requiredResponse = "{\"id\": 1}";
        Api api = new Api(
                "Create Customer",//Short description of the api
                Method.POST,//HTTP method
                "/customers",//End point
                new RestResponse("{\"accept-language\": \"en_gb\"}", 201, requiredResponse)//Headers, Status code, Response String
        );
        simulator = new SimpleRestSimulator(HTTP_PORT)
                .withApi(api)
                .run();

        //e.g. http://localhost:9090/customers
        String url = String.format("http://localhost:%d%s", simulator.getPort(), "/customers");

        //Now invoke the REST end point and do assertions.
        HttpPost post = new HttpPost(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Response status code was not 201.", response.getStatusLine().getStatusCode(), is(201));

        InputStream content = entity.getContent();
        String responseString = IOUtils.toString(content, "UTF-8");
        assertThat("REST response did not match with actual.", responseString, is(requiredResponse));
    }
}