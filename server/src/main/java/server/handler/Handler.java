package server.handler;

import com.google.gson.Gson;

public class Handler {

    static public Gson gson = new Gson();

    static public <T> T fromJson(String json, Class<T> ClassOfT) {
        return gson.fromJson(json, ClassOfT);
    }

    static public String toJson(Object src) {
        return gson.toJson(src);
    }
}
