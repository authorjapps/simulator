package org.japps.simulator.impl;

import org.japps.simulator.base.BaseSimulator;
import org.japps.simulator.domain.Api;
import org.japps.simulator.domain.RestResponse;
import org.simpleframework.http.Method;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siddha on 23/04/2015.
 */
public class SimpleRestSimulator extends BaseSimulator implements Container {
    private static final Logger logger = LoggerFactory.getLogger(SimpleRestSimulator.class);

    private List<Api> apiList = new ArrayList<>();
    private Api api;

    public List<Api> getApiList() {
        return apiList;
    }

    public SimpleRestSimulator(int port) {
        super(port);
        setSimulatorName("RESTFUL-simulator");
        setActualContainer(this);
    }

    @Override
    public void handle(Request request, Response response) {
        try {
            PrintStream body = getPrintStreamForResponse(response);
            String responseString = "Not decided";

            logger.info("\n#" + request.getMethod() + ": Target URL: " + request.getTarget());
            for (Api api : apiList) {
                if (request.getTarget().equals(api.getUrl()) && request.getMethod().equals(api.getOperation())) {
                    response.setStatus(Status.getStatus(api.getResponse().getStatus()));
                    responseString = api.getResponse().getBody();
                    break;
                }
            }
            logger.info("\n# Response Status: " + response.getCode());
            logger.info("\n# Response body: \n" + responseString);
            body.print(responseString);
            body.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendResponse(Request request, Response response, PrintStream body, String responseString) {
        for (Api api : apiList) {
            if (request.getTarget().equals(api.getUrl()) && request.getMethod().equals(api.getOperation())) {
                response.setStatus(Status.getStatus(api.getResponse().getStatus()));
                responseString = api.getResponse().getBody();
                break;
            }
        }
        logger.info("\n# Response Status: " + response.getCode());
        logger.info("\n# Response body: \n" + responseString);
        body.print(responseString);
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

    public SimpleRestSimulator withApi(Api api) {
        if (null != api) {
            this.getApiList().add(api);
        }
        return this;
    }

    public SimpleRestSimulator run() {
        this.start();
        return this;
    }
}
