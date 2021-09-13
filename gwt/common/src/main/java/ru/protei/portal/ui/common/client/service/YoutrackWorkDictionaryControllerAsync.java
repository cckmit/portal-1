package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;

import java.util.List;

public interface YoutrackWorkDictionaryControllerAsync {
    
    void getDictionaries(En_YoutrackWorkType type, AsyncCallback<List<YoutrackWorkDictionary>> async);
    
    void createDictionary(YoutrackWorkDictionary dictionary, AsyncCallback<YoutrackWorkDictionary> async);
    
    void updateDictionary(YoutrackWorkDictionary dictionary, AsyncCallback<YoutrackWorkDictionary> async);
    
    void removeDictionary(YoutrackWorkDictionary dictionary, AsyncCallback<YoutrackWorkDictionary> async);
}
