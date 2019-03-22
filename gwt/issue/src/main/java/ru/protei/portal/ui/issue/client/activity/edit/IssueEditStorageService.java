package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.storage.client.Storage;

import java.util.HashMap;
import java.util.Map;

public class IssueEditStorageService {
    public IssueEditStorageService() {
        localStorage = Storage.getLocalStorageIfSupported();
        if (localStorage != null) {
            mapStorage = new HashMap<>();
        }
    }
    public String get(String key){
        return (localStorage != null) ? localStorage.getItem(key) : mapStorage.get(key);
    }
    public void set(String key, String item){
        if (localStorage != null) {
            localStorage.setItem(key, item);
        } else {
            mapStorage.put(key, item);
        }
    }
    public void remove(String key) {
        if (localStorage != null) {
            localStorage.removeItem(key);
        } else {
            mapStorage.remove(key);
        }
    }
    private Storage localStorage;
    private Map<String, String> mapStorage;
}
