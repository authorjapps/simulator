package org.jsmart.simulator.base;

import org.jsmart.simulator.domain.Api;

/**
 * @author Siddha.
 */
public interface Simulator {
    public int getPort();
    public Simulator run();
    public void stop();
    public boolean isRunning();
    Simulator restApi(Api apiOrder);
}
