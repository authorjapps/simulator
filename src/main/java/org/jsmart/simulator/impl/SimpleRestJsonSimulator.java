package org.jsmart.simulator.impl;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;
import org.apache.commons.lang.StringUtils;
import org.jsmart.simulator.annotations.ApiRepo;
import org.jsmart.simulator.domain.Api;
import org.jsmart.simulator.domain.ApiSpec;
import org.jsmart.simulator.utils.SimulatorJsonUtils;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsmart.simulator.base.BaseSimulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Siddha on 27/04/2015.
 */
@ApiRepo("simulators")
public class SimpleRestJsonSimulator extends BaseSimulator implements Container{
    private static final Logger logger = LoggerFactory.getLogger(SimpleRestJsonSimulator.class);

    public static String namesAsAString = "";
    private Class<?> mainRunnerClass;
    private String packageName;
    List<ApiSpec> apiSpecRequestResponseList;

    /**
     * The port at which the simulator will run. The supplied port number should be different from
     * any other port at which other applications are running.
     *
     * @param port
     */
    public SimpleRestJsonSimulator(int port) {
        super(port);
        setActualContainer(this);
        mainRunnerClass = this.getClass();
        //mainRunnerClass = this.getMainRunnerClass();
        apiSpecRequestResponseList = getDeserializedApiSpecList();
        setSimulatorName(getNamesComaSeparated(apiSpecRequestResponseList));
    }

    @Override
    public void handle(Request request, Response response){
        logger.info("\n-------  REST api  ------------ \nRequest: \n" + request.getMethod() + ":" + request.getTarget());
        try{
            PrintStream body = getPrintStreamForResponse(response);
            String responseJson = getSimulatedResponse(request, response);
            body.print(responseJson);
            body.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String respondGET(String requestTarget, Response response, String notFoundMessage) {
        logger.info("\n#Requested Target : GET: " + requestTarget);
        for(ApiSpec apiSpec : apiSpecRequestResponseList) {
            for(Api api : apiSpec.getApis()) {
                if("GET".equals(api.getOperation()) && requestTarget.equals(api.getUrl())) {
                    logger.info("\n#Found Target: api.getOperation() : " + api.getOperation() + ", api.getUrl(): " + api.getUrl());
                    logger.info("\n#Simulator Response: \nStatus:" +  api.getResponse().getStatus() + "\nbody: " + api.getResponse().getBody());
                    response.setStatus(Status.getStatus(api.getResponse().getStatus()));
                    return api.getResponse().getBody();
                }
            }
        }
        response.setStatus(Status.NOT_FOUND);
        return notFoundMessage;
    }

    private String respondPOST(String requestTarget, Response response, String notFoundMessage) {
        for(ApiSpec apiSpec : apiSpecRequestResponseList) {
            for(Api api : apiSpec.getApis()) {
                logger.info("\n#Requested Target : POST: " + requestTarget);
                logger.info("\n#Found Target: api.getOperation() : " + api.getOperation() + ", api.getUrl(): " + api.getUrl());
                if("POST".equals(api.getOperation()) && requestTarget.equals(api.getUrl())) {
                    response.setStatus(Status.getStatus(api.getResponse().getStatus()));
                    setResponseHeaders(response, api.getResponse().getHeaders());
                    return api.getResponse().getBody();
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


    private String getSimulatedResponse(Request request, Response response) {
        String notFoundMessage = request.getTarget() + " (404)Simulator did not process the request as it did not find resource for GET, POST or PUT.";

        switch( request.getMethod() ) {
            case "GET":
                return respondGET(request.getTarget(), response, notFoundMessage);

            case "POST":
                return respondPOST(request.getTarget(), response, notFoundMessage);

            case "PUT":
                //TODO
                notFoundMessage = "PUT: TOBE implemented.";
                break;

            case "DELETE":
                //TODO
                notFoundMessage = "DELETE: TOBE implemented.";
                break;

            default:
                response.setStatus(Status.BAD_REQUEST);
                return "(400)Unknown Request -Not simulated for api: " + request.getTarget();
        }

        response.setStatus(Status.NOT_FOUND);
        return notFoundMessage;
    }

    private PrintStream getPrintStreamForResponse(Response response) throws IOException {
        PrintStream body;
        body = response.getPrintStream();
        long time = System.currentTimeMillis();

        response.setContentType("application/json"); //"application/json" application/html
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
