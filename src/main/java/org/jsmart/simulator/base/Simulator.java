package org.jsmart.simulator.base;

/**
 * @author Siddha.
 */
public interface Simulator {
    public int getPort();
    public Simulator run();
    public void stop();
}
