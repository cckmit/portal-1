package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/ImportanceLevelController")
public interface ImportanceLevelController extends RemoteService {
    List<ImportanceLevel> getImportanceLevels() throws RequestFailedException;

    ImportanceLevel getImportanceLevel(Integer importanceLevelId) throws RequestFailedException;

    List<ImportanceLevel> getImportanceLevels(Long companyId) throws RequestFailedException;
}
