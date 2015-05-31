package org.japps.simulator.main;

import org.japps.simulator.annotations.ApiRepo;
import org.japps.simulator.impl.SimpleRestJsonSimulator;

/**
 * Created by Siddha on 27/04/2015.
 */
@ApiRepo("simulators")
public class SimpleRestJsonSimulatorsMain extends SimpleRestJsonSimulator {

    public static final int PORT = 9999;

    public SimpleRestJsonSimulatorsMain(int port) {
        super(port);
    }

    public static void main(String[] args) {
        new SimpleRestJsonSimulatorsMain(PORT).start();
    }
}
