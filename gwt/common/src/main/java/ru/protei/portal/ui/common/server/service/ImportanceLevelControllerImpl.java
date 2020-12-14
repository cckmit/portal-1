package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.service.ImportanceLevelService;
import ru.protei.portal.ui.common.client.service.ImportanceLevelController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;

@Service("ImportanceLevelController")
public class ImportanceLevelControllerImpl implements ImportanceLevelController {
    @Autowired
    public ImportanceLevelControllerImpl(ImportanceLevelService importanceLevelService) {
        this.importanceLevelService = importanceLevelService;
    }

    @Override
    public List<ImportanceLevel> getImportanceLevels() throws RequestFailedException {
        log.info("getImportanceLevels()");
        return checkResultAndGetData(importanceLevelService.getImportanceLevels());
    }

    @Override
    public ImportanceLevel getImportanceLevel(Integer importanceLevelId) throws RequestFailedException {
        log.info("getImportanceLevel(): importanceLevelId={}", importanceLevelId);
        return checkResultAndGetData(importanceLevelService.getImportanceLevel(importanceLevelId));
    }

    @Override
    public List<ImportanceLevel> getImportanceLevels(Long companyId) throws RequestFailedException {
        log.info("getImportanceLevels(): companyId={}", companyId);
        return checkResultAndGetData(importanceLevelService.getImportanceLevelsByCompanyId(companyId));
    }

    private final ImportanceLevelService importanceLevelService;

    private static final Logger log = LoggerFactory.getLogger(ImportanceLevelControllerImpl.class);
}
