package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;
import ru.protei.portal.core.model.ent.YoutrackReportDictionary;

import java.util.List;

public interface YoutrackReportDictionaryControllerAsync {
    
    void getDictionaries(En_ReportYoutrackWorkType type, AsyncCallback<List<YoutrackReportDictionary>> async);
    
    void createDictionary(YoutrackReportDictionary dictionary, AsyncCallback<YoutrackReportDictionary> async);
    
    void updateDictionary(YoutrackReportDictionary dictionary, AsyncCallback<YoutrackReportDictionary> async);
    
    void removeDictionary(YoutrackReportDictionary dictionary, AsyncCallback<YoutrackReportDictionary> async);
}
