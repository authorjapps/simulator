package org.jsmart.simulator.base;

import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author Siddha
 */
public class BaseSimulator implements Simulator {
    private static final Logger logger = LoggerFactory.getLogger(BaseSimulator.class);

    private static int port;
    private Connection connection;
    private InetSocketAddress socketAddress;
    private String simulatorName;
    private Container actualContainer;

    /**
     * The port at which the simulator will run. The supplied port number should be different from
     * any other port at which other applications are running.
     *
     * @param port
     */
    public BaseSimulator(int port) {
        this.port = port;
    }

    /**
     * Starts the simulator at the supplied port exposing the end point.
     */
    public void start() {
        try {
            Container container = this.getActualContainer();
            Server server = new ContainerServer(container);
            connection = new SocketConnection(server);
            SocketAddress address = new InetSocketAddress(port);
            socketAddress =(InetSocketAddress) connection.connect(address);
            logger.info("\n#Simulator: " + this.getSimulatorName() +
                    "\n#started. " +
                    "\nListening at port: " + socketAddress.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stops the simulator.
     */
    @Override
    public void stop() {
        try {
            connection.close();
            logger.info("\n#" + getSimulatorName() + "\nstopped.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getPort() {
        return socketAddress.getPort();
    }

    public String getSimulatorName() {
        return simulatorName;
    }

    public void setSimulatorName(String simulatorName) {
        this.simulatorName = simulatorName;
    }

    public Container getActualContainer() {
        return actualContainer;
    }

    public void setActualContainer(Container actualContainer) {
        this.actualContainer = actualContainer;
    }

    /**
     * Runs the simulator and returns the current state of the simulator.
     *
     * @return
     */
    @Override
    public Simulator run() {
        start();
        return this;
    }
}
