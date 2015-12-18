package com.ringcentral.rc_android_sdk.rcsdk.http;

/**
 * Created by vyshakh.babji on 12/16/15.
 */
public class RingCentralException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RingCentralException(String message, Throwable t) {
        super(message, t);
    }

    public RingCentralException(String message) {
        super(message);
    }

    public RingCentralException(Throwable t) {
        super(t);
    }

    /**
     * Returns a hint as to whether it makes sense to retry upon this exception.
     * Default is true, but subclass may override.
     */
//    public boolean isRetryable() {
//        return true;
//    }
}
