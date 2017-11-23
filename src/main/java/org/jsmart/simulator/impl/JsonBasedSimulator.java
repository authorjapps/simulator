package org.jsmart.simulator.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsmart.simulator.annotations.ApiRepo;
import org.jsmart.simulator.base.BaseSimulator;
import org.jsmart.simulator.domain.Api;
import org.jsmart.simulator.domain.ApiSpec;
import org.jsmart.simulator.domain.RestResponse;
import org.jsmart.simulator.utils.SimulatorJsonUtils;
import org.json.JSONException;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;

/**
 * Created by Nirmal on 27/04/2015.
 */
@ApiRepo("simulators")
public class JsonBasedSimulator extends BaseSimulator implements Container{
    private static final Logger logger = LoggerFactory.getLogger(JsonBasedSimulator.class);
    
    private final String NOT_FOUND_PLACEHOLDER = "$NOT_FOUND";
    public static String namesAsAString = "";
    private Class<?> mainRunnerClass;
    private String packageName;
    List<ApiSpec> apiSpecRequestResponseList;
    
    public JsonBasedSimulator(int port) {
        super(port);
        setActualContainer(this);
        mainRunnerClass = this.getClass();
        //mainRunnerClass = this.getMainRunnerClass();
        apiSpecRequestResponseList = getDeserializedApiSpecList();
        setSimulatorName(getNamesComaSeparated(apiSpecRequestResponseList));
    }
    
    public void addApiSpec(ApiSpec apiSpec) {
        if (null != apiSpec) {
            this.apiSpecRequestResponseList.add(apiSpec);
        }
    }
    
    @Override
    public void handle(Request request, Response response){
        logger.info("\n-------  REST api  ------------ \nRequest: \n" + request.getMethod() + ":" + request.getTarget());
        try{
            PrintStream body = getPrintStreamForResponse(response);
            String responseBody = generateSimulatedResponse(request, response);
            body.print(responseBody);
            body.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String generateSimulatedResponse(Request request, Response response) throws IOException, JSONException {
        String notFoundMessage ="{\n" +
                                "    \"class\": \"org.jsmart.exceptions.exceptions.service.InternalServerErrorException\",\n" +
                                "    \"correlationId\": \"ncb5a561-yye7-44a0-bdea-6d918f310cdXX\",\n" +
                                "    \"description\": \"An exception occurred Because could not find the end point in Simulator\",\n" +
                                "    \"errorId\": \"org.jsmart.simulator.could.not.find.end.point:" + request.getTarget() + "\",\n" +
                                "    \"causes\": [],\n" +
                                "    \"suppressed\": []\n" +
                                "}";
        
        final String REQUEST_METHOD = request.getMethod();
        
        switch(REQUEST_METHOD) {
            case "GET":
                return respondGET(request.getTarget(), response, notFoundMessage);
            
            case "POST":
                return respondPOST(request, response, notFoundMessage);
            
            case "PUT":
                return respondToPUT(request.getTarget(), response, notFoundMessage);
            
            case "PATCH":
                listenToPATCHForGETResponse(request);
                return respondToPATCH(request.getTarget(), response, notFoundMessage);
            
            case "OTHERS":
                //TODO
                notFoundMessage = "Not implemented.Please raise an issue in GitHub and let the author know. Contribute to OSS";
                break;
            
            default:
                response.setStatus(Status.NOT_IMPLEMENTED);
                String message = "{\"message\": \"" + REQUEST_METHOD + " #method not yet implemented.\"}";
                logger.info("\nSimulator Response:" + message );
                return message;
        }
        
        response.setStatus(Status.NOT_FOUND);
        return notFoundMessage;
    }
    
    private String respondGET(String requestTarget, Response response, String notFoundMessage) {
        logger.info("# Requested Target : GET: " + requestTarget);
        for(ApiSpec apiSpec : apiSpecRequestResponseList) {
            for(Api api : apiSpec.getApis()) {
                if( "GET".equals(api.getOperation()) && requestTarget.equals(api.getUrl()) ){
//                    logger.info("# Found Target: api.getOperation() : " + api.getOperation() + ", api.getUrl(): " + api.getUrl());
//                    logger.info("\nSimulator Response: \nStatus:" + api.getResponse().getStatus() + "\nbody: " + api.getResponse().getBody());
//
//                    response.setStatus(Status.getStatus(api.getResponse().getStatus()));
//
//                    setResponseHeaders(response, api.getResponse().getHeaders());
//
//                    return responseBodyFromInputJson(api.getResponse());
    
                    return createResponse(response, api);
                    
                }
            }
            
            String body = handleNotFoundEndPoints(requestTarget, response, apiSpec);
            if (body != null) return body;
        }
        
        response.setStatus(Status.NOT_FOUND);
        return notFoundMessage;
    }
    
    private String responseBodyFromInputJson(RestResponse response) {
        if(! StringUtils.isEmpty(response.getStringBody())){
            return response.getStringBody();
            
        } else if(! StringUtils.isEmpty(response.getXmlBody())){
            return response.getXmlBody();
            
        } else {
            return response.getBody();
        }
    }
    
    private String handleNotFoundEndPoints(String requestTarget, Response response, ApiSpec apiSpec) {
        for(Api api : apiSpec.getApis()) {
            if( ("GET".equals(api.getOperation()) && urlMatchesForNotFound(api.getUrl(), requestTarget)) ){
                logger.info("# Found Target: api.getOperation() : "
                            + api.getOperation() + ", api.getUrl(): " + api.getUrl());
                logger.info("\nSimulator Response: \nStatus:"
                            + api.getResponse().getStatus() + "\nbody: " + api.getResponse().getBody());
                response.setStatus(Status.getStatus(api.getResponse().getStatus()));
                return api.getResponse().getBody();
            }
        }
        return null;
    }
    
    private String handleNotFoundPOSTEndPoints(Request request, Response response) {
        for(ApiSpec apiSpec : apiSpecRequestResponseList) {
            for(Api api : apiSpec.getApis()) {
                if ("POST".equals(api.getOperation())){
                    if (((api.getBody() != null && api.getBody().contains(NOT_FOUND_PLACEHOLDER)))
                        && urlMatchesForNotFound(api.getUrl(), request.getTarget())) {
                        logger.info("# Found Target: api.getOperation() : "
                                    + api.getOperation() + ", api.getUrl(): " + api.getUrl());
                        logger.info("\nSimulator Response: \nStatus:"
                                    + api.getResponse().getStatus() + "\nbody: " + api.getResponse().getBody());
                        response.setStatus(Status.getStatus(api.getResponse().getStatus()));
                        return api.getResponse().getBody();
                    }
                }
            }
        }
        logger.info("No default target found for: " + request.getTarget());
        return null;
    }
    
    private boolean urlMatchesForNotFound(String apiUrl, String requestedUrl) {
        if (apiUrl.contains(NOT_FOUND_PLACEHOLDER)) {
            int indexOfNF = apiUrl.indexOf(NOT_FOUND_PLACEHOLDER);
            int lengthAfterNF = apiUrl.length() - (indexOfNF + NOT_FOUND_PLACEHOLDER.length());
            String startOfApiURL = apiUrl.substring(0, indexOfNF);
            if (requestedUrl.length() < indexOfNF) {
                //No way these URLs match.
                return false;
            }
            String startOfRequestedUrl = requestedUrl.substring(0, indexOfNF);
            boolean startMatch = (startOfApiURL.equals(startOfRequestedUrl));
            
            String endOfApiURL = apiUrl.substring(apiUrl.length() - lengthAfterNF);
            if (requestedUrl.length() < lengthAfterNF) {
                //No way these URLs match
                return false;
            }
            String endOfRequestedUrl = requestedUrl.substring(requestedUrl.length() - lengthAfterNF);
            boolean endMatch = (endOfApiURL.equals(endOfRequestedUrl));
            
            return startMatch && endMatch;
        } else {
            return apiUrl.equals(requestedUrl);
        }
    }
    
    private String respondPOST(Request request, Response response, String notFoundMessage) throws IOException, JSONException {
        String requestTarget = request.getTarget();
        String requestContent = request.getContent();
        logger.info("# Requested Target : POST: " + requestTarget);
        for(ApiSpec apiSpec : apiSpecRequestResponseList) {
            for(Api api : apiSpec.getApis()) {
                if("POST".equals(api.getOperation()) &&  requestTarget.equals(api.getUrl())) {
                    
                    if ((StringUtils.isBlank(api.getBody()) && StringUtils.isBlank(requestContent))
                        || compareJson(api, requestContent)) {
                        return createResponse(response, api);
                    }
                }
            }
        }
        
        logger.info("No specific target found for: " + request.getTarget());
        String body = handleNotFoundPOSTEndPoints(request, response);
        if (body != null) return body;
        
        response.setStatus(Status.NOT_FOUND);
        return notFoundMessage;
    }
    
    private String createResponse(Response response, Api api) {
        logger.info("# Found simulated Target: api.getOperation() : " + api.getOperation() + ", api.getUrl(): " + api.getUrl() + ", api.getName(): " + api.getName());
        logger.info("\nSimulator Response: \nStatus:" +  api.getResponse().getStatus() + "\nbody: " + api.getResponse().getBody());
        
        response.setStatus(Status.getStatus(api.getResponse().getStatus()));
        setResponseHeaders(response, api.getResponse().getHeaders());
        
        return responseBodyFromInputJson(api.getResponse());
        
    }
    
    private boolean compareJson(Api api, String str2) {
        if (api.getIgnoreBody() == null || !api.getIgnoreBody()) {
            boolean passed = false;
            
            try {
                
                passed = JSONCompare
                                .compareJSON(api.getBody(), str2, JSONCompareMode.LENIENT)
                                .passed();
            } catch (Exception e) {
                
                // If we got exceptions, they weren't equal.
                logger.info("Exception while comparing: " + e.getMessage());
                passed = false;
            }
            
            if(!passed){
                logger.info("#REST end point found, but the request body did not match with simulated body."
                            + "\n=>Request body: " + api.getBody()
                            + "\n=>Simulated body: " + str2);
            }
            
            return passed;
        } else {
            
            // return true if ignoreBody is true
            return true;
        }
    }
    
    private String respondToPUT(String requestTarget, Response response, String notFoundMessage) {
        
        final String OPERATION = "PUT";
        
        for(ApiSpec apiSpec : apiSpecRequestResponseList) {
            logger.info("# Requested Target : PUT: " + requestTarget);
            for(Api api : apiSpec.getApis()) {
                if(OPERATION.equals(api.getOperation()) && requestTarget.equals(api.getUrl())) {
                    return createResponse(response, api);
                }
            }
        }
        
        response.setStatus(Status.NOT_FOUND);
        return notFoundMessage;
    }
    
    private String respondToPATCH(String requestTarget, Response response, String notFoundMessage) {
        
        final String OPERATION = "PATCH";
        
        for(ApiSpec apiSpec : apiSpecRequestResponseList) {
            logger.info("# Requested Target : PATCH: " + requestTarget);
            for(Api api : apiSpec.getApis()) {
                if(OPERATION.equals(api.getOperation()) && requestTarget.equals(api.getUrl())) {
                    logger.info("# Found simulated Target: api.getOperation() : "
                                + api.getOperation() + ", api.getUrl(): " + api.getUrl());
                    logger.info("\nSimulator Response: \nStatus:"
                                +  api.getResponse().getStatus() + "\nbody: " + api.getResponse().getBody());
                    response.setStatus(Status.getStatus(api.getResponse().getStatus()));
                    setResponseHeaders(response, api.getResponse().getHeaders());
    
                    return responseBodyFromInputJson(api.getResponse());
                }
            }
        }
        
        response.setStatus(Status.NOT_FOUND);
        return notFoundMessage;
    }
    
    private void setResponseHeaders(Response response, String headersJson) {
        if(StringUtils.isEmpty(headersJson)){
            return;
        }
        Map<String, String> headersMap = SimulatorJsonUtils.getAsMap(headersJson);
        for (String key : headersMap.keySet()) {
            response.addValue(key, headersMap.get(key));
        }
    }
    
    
    
    
    private void listenToPATCHForGETResponse(Request request) {
        try {
            String body = request.getContent();
            // POST api, add to in-memory DB
            Api inMemoryPATCHApi = new Api("End point for PATCH request",
                            "PATCH",
                            request.getTarget(),
                            null,
                            false, new RestResponse(null, 201, body, null, null));
            addOrReplaceInMemoryApi(inMemoryPATCHApi);
            
            // GET api, add to in-memory DB
            Api inMemoryGETApi = new Api("Simulated using PATCH: End point for for GET request",
                            "GET",
                            request.getTarget(),
                            null,
                            false, new RestResponse("{\"Language\":\"en_gb\"}", 200, body, null, null));
            addOrReplaceInMemoryApi(inMemoryGETApi);
            
        } catch (Exception excp) {
            excp.printStackTrace();
            logger.error("Exception was: " + excp);
            throw new RuntimeException(excp.getMessage());
        }
    }
    
    private void addOrReplaceInMemoryApi(Api inMemoryApi) {
        // .indexOf() uses equals() method.
        // See Api.java class's equals() method. Currently based on URL and method.
        // Can be enhanced to include more. But for time being QA folks can live with this.
        int itemIndex = apiSpecRequestResponseList.get(0).getApis().indexOf(inMemoryApi);
        
        if (itemIndex != -1) {
            logger.info("\n#Overridden: Listened to PATCH and simulated in-memory {} api: {}", inMemoryApi.getOperation(), inMemoryApi);
            apiSpecRequestResponseList.get(0).getApis().set(itemIndex, inMemoryApi);
        } else {
            logger.info("\n#Added: Listened to PATCH and simulated in-memory {} api: {}", inMemoryApi.getOperation(), inMemoryApi);
            apiSpecRequestResponseList.get(0).getApis().add(inMemoryApi);
        }
    }
    
    private PrintStream getPrintStreamForResponse(Response response) throws IOException {
        PrintStream body;
        body = response.getPrintStream();
        long time = System.currentTimeMillis();
        
        response.setContentType("application/json");
        response.setDescription(getSimulatorName());
        
        response.setDate("Date", time);
        response.setDate("Last-Modified", time);
        return body;
    }
    
    
    private String getNamesComaSeparated(List<ApiSpec> requestResponseList) {
        String simulatorNames = "All Simulators::";
        for(ApiSpec apiSpec : requestResponseList) {
            simulatorNames = simulatorNames + ":" + apiSpec.getName();
        }
        return simulatorNames;
    }
    
    private List<ApiSpec> getDeserializedApiSpecList() {
        // Read the simulator files
        ClassPathFactory factory = new ClassPathFactory();
        ClassPath jvmClassPath = factory.createFromJVM();
        
        ApiRepo annotation = (ApiRepo)getMainRunnerClass().getAnnotation(ApiRepo.class);
        packageName = annotation.value();
        String[] allSimulationFiles = jvmClassPath.findResources(packageName, new RegExpResourceFilter(".*", ".*\\.json$"));
        
        if(null == allSimulationFiles || allSimulationFiles.length == 0) {
            throw new RuntimeException("YouTriedToSimulateNothingException: Check the (" + packageName + ") integration test repo folder(empty?). " );
        }
        
        // deserialize the ApiSpec
        List<ApiSpec> apiSpecList = new ArrayList<>();
        for (String resourceName : allSimulationFiles) {
            
            try {
                InputStream stream = jvmClassPath.getResourceAsStream(resourceName);
                InputStreamReader reader = new InputStreamReader(stream);
                
                apiSpecList.add(SimulatorJsonUtils.deserialize(reader));
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (RuntimeException e) {
                throw new RuntimeException(String.format("Error while parsing '%s': %s", resourceName, e.getMessage()),
                                e);
            }
        }
        
        return apiSpecList;
    }
    
    public Class getMainRunnerClass() {
        return mainRunnerClass;
    }
}

