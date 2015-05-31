package org.japps.simulator.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.japps.simulator.domain.ApiSpec;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ApiSpecDeserializerTest {

    @Test
    public final void willDeserializeApiSpecWithApisArray() throws Exception {
        final String json = "{\n" +
                "  \"name\": \"Micro-Service-Function-Simulator\",\n" +
                "  \"apis\": [\n" +
                "    {\n" +
                "      \"name\": \"Get referral by referralId\",\n" +
                "      \"operation\": \"GET\",\n" +
                "      \"url\": \"/referral/1\",\n" +
                "      \"response\": {\n" +
                "        \"header\": {\n" +
                "        },\n" +
                "        \"status\": 200,\n" +
                "        \"body\": {\n" +
                "          \"id\": 1,\n" +
                "          \"receivedDate\": 1429113133000,\n" +
                "          \"numberInGroup\": 0,\n" +
                "          \"asf1Received\": false,\n" +
                "          \"recentAccomodationTypeId\": 1,\n" +
                "          \"referralSourceId\": 1,\n" +
                "          \"applicationStatusId\": 1,\n" +
                "          \"asylumSeeker\": false\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        final ObjectMapper mapper = new ObjectMapper();

        final SimpleModule module = new SimpleModule();
        module.addDeserializer(ApiSpec.class, new ApiSpecDeserializer());
        mapper.registerModule(module);

        final ApiSpec readValue = mapper.readValue(json, ApiSpec.class);
        assertThat(readValue, notNullValue());
        assertThat(readValue.getApis().get(0).getResponse(), notNullValue());
        assertThat(readValue.getApis().get(0).getResponse().getStatus()+"", not(isEmptyOrNullString()));
    }

    @Test
    public final void willDeserializeUsingDeserialzerAnnotation() throws Exception {
        final String json = "{\n" +
                "  \"name\": \"Micro-Service-Function-Simulator\",\n" +
                "  \"apis\": [\n" +
                "    {\n" +
                "      \"name\": \"Get referral by referralId\",\n" +
                "      \"operation\": \"GET\",\n" +
                "      \"url\": \"/referral/1\",\n" +
                "      \"response\": {\n" +
                "        \"header\": {\n" +
                "        },\n" +
                "        \"status\": 200,\n" +
                "        \"body\": {\n" +
                "          \"id\": 1,\n" +
                "          \"receivedDate\": 1429113133000,\n" +
                "          \"numberInGroup\": 0,\n" +
                "          \"asf1Received\": false,\n" +
                "          \"recentAccomodationTypeId\": 1,\n" +
                "          \"referralSourceId\": 1,\n" +
                "          \"applicationStatusId\": 1,\n" +
                "          \"asylumSeeker\": false\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        final ApiSpec readValue = new ObjectMapper().readValue(json, ApiSpec.class);
        assertThat(readValue, notNullValue());
    }

    @Test
        public final void willDeserializeApiSpecWithMoreApisArrayUsingJsonPath() throws Exception {
            final String json = "{\n" +
                    "  \"name\": \"Micro-Service-Function-Simulator\",\n" +
                    "  \"apis\": [\n" +
                "    {\n" +
                "      \"name\": \"Get referral by referralId\",\n" +
                "      \"operation\": \"GET\",\n" +
                "      \"url\": \"/referral/1\",\n" +
                "      \"response\": {\n" +
                "        \"header\": {\n" +
                "        },\n" +
                "        \"status\": 200,\n" +
                "        \"body\": {\n" +
                "          \"id\": 1,\n" +
                "          \"receivedDate\": 1429113133000,\n" +
                "          \"numberInGroup\": 0,\n" +
                "          \"asf1Received\": false,\n" +
                "          \"recentAccomodationTypeId\": 1,\n" +
                "          \"referralSourceId\": 1,\n" +
                "          \"applicationStatusId\": 1,\n" +
                "          \"asylumSeeker\": false\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Get referral by referralId\",\n" +
                "      \"operation\": \"GET\",\n" +
                "      \"url\": \"/referral/2\",\n" +
                "      \"response\": {\n" +
                "        \"header\": {\n" +
                "        },\n" +
                "        \"status\": 200,\n" +
                "        \"body\": {\n" +
                "          \"id\": 2,\n" +
                "          \"receivedDate\": 1429113133002,\n" +
                "          \"numberInGroup\": 0,\n" +
                "          \"referralSourceId\": 2,\n" +
                "          \"applicationStatusId\": 2,\n" +
                "          \"asylumSeeker\": false\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        final ObjectMapper mapper = new ObjectMapper();

        final SimpleModule module = new SimpleModule();
        module.addDeserializer(ApiSpec.class, new ApiSpecDeserializer());
        mapper.registerModule(module);

        final ApiSpec readValue = mapper.readValue(json, ApiSpec.class);
        assertThat(readValue, notNullValue());
        assertThat(readValue.getApis().get(0).getResponse().getBody(), not(isEmptyOrNullString()));
        assertThat(readValue.getApis().get(1).getResponse().getBody(), not(isEmptyOrNullString()));
    }

    @Test
    public final void willDeserializeAndReadResponseJsonUsingJsonPath() throws  Exception {
        final String json = "{\n" +
                "  \"name\": \"Micro-Service-Function-Simulator\",\n" +
                "  \"apis\": [\n" +
                "    {\n" +
                "      \"name\": \"Get referral by referralId\",\n" +
                "      \"operation\": \"GET\",\n" +
                "      \"url\": \"/referral/1\",\n" +
                "      \"response\": {\n" +
                "        \"header\": {\n" +
                "        },\n" +
                "        \"status\": 200,\n" +
                "        \"body\": {\n" +
                "          \"id\": 1,\n" +
                "          \"receivedDate\": 1429113133000,\n" +
                "          \"numberInGroup\": 0,\n" +
                "          \"referralSourceId\": 1,\n" +
                "          \"applicationStatusId\": 1,\n" +
                "          \"asylumSeeker\": false\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        final ObjectMapper mapper = new ObjectMapper();

        final SimpleModule module = new SimpleModule();
        module.addDeserializer(ApiSpec.class, new ApiSpecDeserializer());
        mapper.registerModule(module);

        final ApiSpec readValue = mapper.readValue(json, ApiSpec.class);
        assertThat(readValue, notNullValue());
        assertThat(readValue.getApis().get(0).getResponse().getBody(), not(isEmptyOrNullString()));

        //String requestJson = readValue.getApis().get(0).getResponseJson();
        //int status = ((Long) JsonPath.read(requestJson, "$.status")).intValue();
        assertThat("response status did not match", 200, is(readValue.getApis().get(0).getResponse().getStatus()));

        //Map body = ((HashMap) JsonPath.read(requestJson, "$.body"));
        assertThat(readValue.getApis().get(0).getResponse().getBody(), notNullValue());
    }
}