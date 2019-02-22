package ru.protei.portal.ui.common.client.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class JavaScriptObjectBuilder extends JSONObject {

    public static JSONObject fromJsonString(String jsonObject) {
        return new JSONObject(JsonUtils.unsafeEval(jsonObject));
    }

    public JavaScriptObject build() {
        return getJavaScriptObject();
    }

    protected JSONValue put(String key, String javaScriptObjectAsString) {
        return put(key, fromJsonString(javaScriptObjectAsString));
    }

}
