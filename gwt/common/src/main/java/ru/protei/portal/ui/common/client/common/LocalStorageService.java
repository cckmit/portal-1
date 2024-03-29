package ru.protei.portal.ui.common.client.common;

import com.google.gwt.storage.client.Storage;

import java.util.HashMap;
import java.util.Map;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class LocalStorageService {
    public LocalStorageService() {
        localStorage = Storage.getLocalStorageIfSupported();
        if (localStorage == null) {
            mapStorage = new HashMap<>();
        }
    }
    public String get(String key){
        return (localStorage != null) ? localStorage.getItem(key) : mapStorage.get(key);
    }
    public String getOrDefault(String key, String def){
        if (localStorage != null) {
            return contains(key) ? localStorage.getItem(key) : def;
        } else {
            return mapStorage.getOrDefault(key, def);
        }
    }
    public boolean getBooleanOrDefault(String key, boolean defaultValue) {
        if (localStorage != null) {
            return contains(key) ? Boolean.parseBoolean(localStorage.getItem(key)) : defaultValue;
        } else {
            return mapStorage.containsKey(key) ? Boolean.parseBoolean(mapStorage.get(key)) : defaultValue;
        }
    }
    public void set(String key, String item){
        if (localStorage != null) {
            localStorage.setItem(key, item);
        } else {
            mapStorage.put(key, item);
        }
    }
    public String remove(String key) {
        if (localStorage != null) {
            String s = localStorage.getItem(key);
            if (!isEmpty(s)) localStorage.removeItem(key);
            return s;
        } else {
            return mapStorage.remove(key);
        }
    }
    public boolean contains(String key){
        if (localStorage != null) {
            return !isEmpty( localStorage.getItem(key) );
        } else {
            return mapStorage.containsKey(key);
        }
    }
    private Storage localStorage;
    private Map<String, String> mapStorage;
}
