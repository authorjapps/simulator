package org.jsmart.simulator.main;

import org.jsmart.simulator.annotations.ApiRepo;
import org.jsmart.simulator.impl.JsonBasedSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Siddha on 27/04/2015.
 */
@ApiRepo("simulators")
public class SimpleRestJsonSimulatorsMain extends JsonBasedSimulator {
    private static final Logger logger = LoggerFactory.getLogger(SimpleRestJsonSimulatorsMain.class);

    public static final int PORT = 9999;

    public SimpleRestJsonSimulatorsMain(int port) {
        super(port);
    }

    public static void main(String[] args) {
        logger.info("###new release");
        new SimpleRestJsonSimulatorsMain(PORT).start();
    }
}
