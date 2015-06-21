package org.jsmart.simulator.impl;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsmart.simulator.base.Simulator;
import org.jsmart.simulator.domain.Api;
import org.jsmart.simulator.domain.RestApi;
import org.jsmart.simulator.domain.RestResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Method;
import utils.HttpUtils;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class SimpleRestSimulatorTest {

    public static final int HTTP_PORT = 9090;
    private Simulator simulator;

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
        Api restApi = new Api(
                "Get Customers By Id API",
                Method.GET,
                endPoint,
                new RestResponse("{\"accept-language\": \"en_gb\"}", 200, requiredResponse)
        );
        simulator = new SimpleRestSimulator(HTTP_PORT)
                .restApi(restApi)
                .run();

        // e.g. http://localhost:9090/customers/1
        String url = String.format("http://localhost:%d%s", simulator.getPort(), endPoint);

        // Now invoke the REST end point and do assertions.
        HttpResponse response = HttpUtils.get(url);
        HttpEntity entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Status was not 200.", response.getStatusLine().getStatusCode(), is(200));

        String responseString = IOUtils.toString(entity.getContent(), "UTF-8");
        assertThat("Response did not match with actual.", responseString, is(requiredResponse));
    }

    @Test
    public void willSimulateTwoRESTApisAtTheSamePort() throws Exception{
        String endPoint1 = "/customers/1";
        String customerResponse = "{\n" +
                "    \"id\": 1,\n" +
                "    \"age\": 0,\n" +
                "    \"isAdult\": false\n" +
                "}";

        String endPoint2 = "/orders/1";
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

        HttpResponse response = HttpUtils.get(url);
        HttpEntity entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Status was not 200.", response.getStatusLine().getStatusCode(), is(200));

        String responseString = IOUtils.toString(entity.getContent(), "UTF-8");
        assertThat("Customer Response did not match with actual.", responseString, is(customerResponse));

        //invoke /orders/1 endPoint and assert
        url = String.format("http://localhost:%d%s", simulator.getPort(), endPoint2);
        response = HttpUtils.get(url);
        entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Status was not 200.", response.getStatusLine().getStatusCode(), is(200));

        responseString = IOUtils.toString(entity.getContent(), "UTF-8");
        assertThat("Order Response did not match with actual.", responseString, is(orderResponse));
    }

    @Test
    public void willSimulateTThreeRESTApisAtTheSamePortWithSameRunningSimulator() throws Exception{
        //1. 1st end point
        String endPoint1 = "/customers/1";
        String customerResponse = "{\n" +
                "    \"id\": 1,\n" +
                "    \"age\": 0,\n" +
                "    \"isAdult\": false\n" +
                "}";
        Api apiCustomer = new Api(
                "Get Customer By Id API",
                Method.GET,
                endPoint1,
                new RestResponse("some-headers", 200, customerResponse)
        );
        simulator = new SimpleRestSimulator(HTTP_PORT)
                .restApi(apiCustomer)
                .run();

        //Test now: invoke Customer endPoint and assert
        String url = String.format("http://localhost:%d%s", simulator.getPort(), endPoint1);
        HttpResponse response = HttpUtils.get(url);
        HttpEntity entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Status was not 200.", response.getStatusLine().getStatusCode(), is(200));

        String responseString = IOUtils.toString(entity.getContent(), "UTF-8");
        assertThat("Customer Response did not match with actual.", responseString, is(customerResponse));

        //2. 2nd end point. With same running simulator.
        String endPoint2 = "/orders/1";
        String orderResponse = "{\n" +
                "    \"id\": 1,\n" +
                "    \"customerId\": 1,\n" +
                "    \"quantity\": 60\n" +
                "}";
        Api apiOrder = new Api(
                "Get Order Details By Order Id",
                Method.GET,
                endPoint2,
                new RestResponse(null, 200, orderResponse)
        );
        simulator = simulator
                .restApi(apiOrder)
                .run();
        //invoke "/orders/1" endPoint and assert
        url = String.format("http://localhost:%d%s", simulator.getPort(), endPoint2);
        response = HttpUtils.get(url);

        HttpEntity orderEntity = response.getEntity();
        assertNotNull(orderEntity);
        assertThat("Status was not 200.", response.getStatusLine().getStatusCode(), is(200));

        responseString = IOUtils.toString(orderEntity.getContent(), "UTF-8");
        assertThat("Order Response did not match with actual.", responseString, is(orderResponse));

        //3. 3rd end point. With same running simulator.
        String endPoint3 = "/orders/3";
        String orderResponse3 = "{\n" +
                "    \"id\": 3,\n" +
                "    \"customerId\": 1,\n" +
                "    \"quantity\": 60\n" +
                "}";
        Api apiOrder3 = new RestApi()
                .name("Get Order Details By Order Id")
                .operation(Method.GET)
                .url(endPoint3)
                .response(new RestResponse("{any-headers}", 200, orderResponse3))
                .build();
        simulator = simulator
                .restApi(apiOrder3);

        //invoke "/orders/3" endPoint and assert
        url = String.format("http://localhost:%d%s", simulator.getPort(), endPoint3);
        response = HttpUtils.get(url);
        entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Status was not 200.", response.getStatusLine().getStatusCode(), is(200));

        responseString = IOUtils.toString(entity.getContent(), "UTF-8");
        assertThat("Order Response did not match with actual.", responseString, is(orderResponse3));
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

        HttpResponse response = HttpUtils.get(url);
        HttpEntity entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Status was not 200.", response.getStatusLine().getStatusCode(), is(200));

        String simulatorResponse = IOUtils.toString(entity.getContent(), "UTF-8");
        assertThat("Response did not match with actual.", simulatorResponse, is(requiredResponse));
    }

    @Test
    public void willSimulateOneApiPOSTWithPortAndUrlUsingApi() throws Exception{
        String requiredResponse = "{\"id\": 1}";
        Api api = new RestApi()
                .name("Create Customer")
                .operation(Method.POST)
                .url("/customers")
                .response(new RestResponse("{\"accept-language\": \"en_gb\"}", 201, requiredResponse))
                .build();
        simulator = new SimpleRestSimulator(HTTP_PORT)
                .restApi(api)
                .run();

        //e.g. http://localhost:9090/customers
        String url = String.format("http://localhost:%d%s", simulator.getPort(), "/customers");

        //Now invoke the REST end point and do assertions.
        HttpResponse response = HttpUtils.post(url);
        HttpEntity entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Response status code was not 201.", response.getStatusLine().getStatusCode(), is(201));

        String responseString = IOUtils.toString(entity.getContent(), "UTF-8");
        assertThat("REST response did not match with actual.", responseString, is(requiredResponse));
    }

    @Test
    public void willSimulateOneApiPOSTWithPortAndUrlUsingRestApiBuilder() throws Exception{
        String requiredResponse = "{\"id\": 1}";
        RestApi api = new RestApi()
                .name("Create Customer")
                .operation(Method.POST)
                .url("/customers")
                .response(new RestResponse("{\"accept-language\": \"en_gb\"}", 201, requiredResponse))
                .build();

        simulator = new SimpleRestSimulator(HTTP_PORT)
                .withApi(api)
                .run();

        //e.g. http://localhost:9090/customers
        String url = String.format("http://localhost:%d%s", simulator.getPort(), api.getUrl());

        //Now invoke the REST end point and do assertions.
        HttpResponse response = HttpUtils.post(url);
        HttpEntity entity = response.getEntity();

        assertNotNull(entity);
        assertThat("Response status code was not 201.", response.getStatusLine().getStatusCode(), is(201));

        String responseString = IOUtils.toString(entity.getContent(), "UTF-8");
        assertThat("REST response did not match with actual.", responseString, is(requiredResponse));
    }
}