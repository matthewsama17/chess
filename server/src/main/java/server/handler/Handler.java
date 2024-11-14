package server.handler;

import result.ErrorMessageWrapper;
import result.ServiceException;
import spark.*;
import com.google.gson.Gson;

public class Handler {

    static public Gson gson = new Gson();

    static public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    static public String toJson(Object src) {
        return gson.toJson(src);
    }

    static public Object handleHeaderFail(Response res) {
        res.status(401);
        ErrorMessageWrapper errorMessageWrapper = new ErrorMessageWrapper("Error: unauthorized");
        return toJson(errorMessageWrapper);
    }

    static public Object handleJsonFail(Response res) {
        res.status(400);
        ErrorMessageWrapper errorMessageWrapper = new ErrorMessageWrapper("Error: bad request");
        return toJson(errorMessageWrapper);
    }

    static public Object handleServiceException(ServiceException ex, Response res) {
        res.status(ex.getStatusCode());
        ErrorMessageWrapper errorMessageWrapper = new ErrorMessageWrapper(ex.getMessage());
        return toJson(errorMessageWrapper);
    }

    static public Object handleException(Exception ex, Response res) {
        res.status(500);
        ErrorMessageWrapper errorMessageWrapper = new ErrorMessageWrapper("Error: " + ex.getMessage());
        return toJson(errorMessageWrapper);
    }

    static public Object handleError(Error ex, Response res) {
        res.status(500);
        ErrorMessageWrapper errorMessageWrapper = new ErrorMessageWrapper("Error: " + ex.getMessage());
        return toJson(errorMessageWrapper);
    }
}
