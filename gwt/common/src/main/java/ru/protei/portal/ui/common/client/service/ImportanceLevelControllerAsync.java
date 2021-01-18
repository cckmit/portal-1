package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.ImportanceLevel;

import java.util.List;

public interface ImportanceLevelControllerAsync {
    void getImportanceLevels(AsyncCallback<List<ImportanceLevel>> async);

    void getImportanceLevel(Integer importanceLevelId, AsyncCallback<ImportanceLevel> async);

    void getImportanceLevels(Long companyId, AsyncCallback<List<ImportanceLevel>> async);
}
