package org.jsmart.simulator.enums;

/**
 * This Enum was added to handle the Switch/Case as Java6 doesnt support String based switch/case.
 * After switching to Java8, this class may not be no longer required.
 *
 * @author Siddha.
 */
public enum HttpMethod {
    GET(0), POST(1), PUT(2), DELETE(3);
    private int code;

    private HttpMethod(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String toString() {
        return String.valueOf(code);
    }
}

