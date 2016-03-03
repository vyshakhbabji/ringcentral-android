package com.ringcentral.android.sdk.http;

import com.ringcentral.android.sdk.core.RingCentralException;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Created by vyshakh.babji on 12/15/15.
 */
public class ApiException extends RingCentralException {
    private static final long serialVersionUID = 1L;

    private String message;
    private ApiResponse response;
    private Request request;
    private String serviceName;
    private String errorCode;
    private ErrorType errorType = ErrorType.Unknown;
    private String errorMessage;
    private int statusCode;
    private String rawResponseContent;
    private String extraInfo;

    public ApiException(String errorMessage) {
        super((String) null);
        this.errorMessage = errorMessage;
    }


    public ApiException(String errorMessage, Exception cause) {
        super(null, cause);
        this.errorMessage = errorMessage;
    }

    public ApiException(Response response) {
        super((String) null);
    }

    public ApiException(ApiResponse response, Exception cause) {
        super(null, cause);
        this.response = response;
        this.extraInfo = response.error();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String value) {
        errorMessage = value;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    /**
     * Returns the name of the service that sent this error response.
     *
     * @return The name of the service that sent this error response.
     */
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getMessage() {
        String errormessage = getErrorMessage()
                + " (Service: " + getServiceName()
                + "; "
                + "; Error Code: " + getErrorCode() + ")";
        return extraInfo == "" ? errormessage : errormessage + extraInfo;
    }

    public String getRawResponseContent() {
        return rawResponseContent;
    }

    public void setRawResponseContent(String rawResponseContent) {
        this.rawResponseContent = rawResponseContent;
    }

    public enum ErrorType {
        Client,
        Service,
        Unknown
    }

}


