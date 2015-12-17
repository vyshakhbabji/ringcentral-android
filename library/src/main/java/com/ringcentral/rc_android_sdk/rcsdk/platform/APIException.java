package com.ringcentral.rc_android_sdk.rcsdk.platform;

import com.ringcentral.rc_android_sdk.rcsdk.http.APIResponse;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Created by vyshakh.babji on 12/15/15.
 */
public class APIException extends RingCentralException {
    private static final long serialVersionUID = 1L;

    private String message;
    private APIResponse response;
    private Request request;
    private String serviceName;
    private String errorCode;
    private ErrorType errorType = ErrorType.Unknown;
    private String errorMessage;
    private int statusCode;
    private String rawResponseContent;
    private String extraInfo;

    public APIException(String errorMessage) {
        super((String) null);
        this.errorMessage = errorMessage;
    }


    public APIException(String errorMessage, Exception cause) {
        super(null, cause);
        this.errorMessage = errorMessage;
    }

    public APIException(Response response) {
        super((String) null);
    }

    public APIException(APIResponse response, Exception cause) {
        super(null, cause);
        this.response = response;
        this.extraInfo = response.showError();
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


//    public APIException(APIResponse response, Exception e) {
//
//        this.response = response;
//        this.request = response.request();
//
//        String message = e.getMessage();
//        if (apiResponse() != null) {
//            this.message = apiResponse().showError();
//        } else {
//            this.message = "Unknown error";
//        }
//    }

//    public APIException(Throwable cause) {
//        super(cause);
//    }

//    public APIException(String message) {
//        super(message);
//        this.message = message;
//    }

//    public APIException(String message, Throwable cause) {
//        super("RingCentral Exception :" + message, cause);
//    }
//
//    public Request getRequest() {
//        return request;
//    }

//    @Override
//    public String getMessage() {
//        return message;
//    }

//    @Override
//    public String toString() {
//        return message;
//    }
//
//    public APIResponse apiResponse() {
//        return this.response;
//    }


}
