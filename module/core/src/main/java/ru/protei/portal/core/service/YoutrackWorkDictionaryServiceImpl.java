package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.YoutrackProjectDAO;
import ru.protei.portal.core.model.dao.YoutrackWorkDictionaryDAO;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.YoutrackProject;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class YoutrackWorkDictionaryServiceImpl implements YoutrackWorkDictionaryService {
    private static Logger log = LoggerFactory.getLogger(YoutrackWorkDictionaryServiceImpl.class);

    @Autowired
    YoutrackProjectDAO youtrackProjectDAO;
    @Autowired
    YoutrackWorkDictionaryDAO dictionaryDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public Result<List<YoutrackWorkDictionary>> getDictionaries(AuthToken token, En_YoutrackWorkType type) {
        if (type == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<YoutrackWorkDictionary> dictionaries = dictionaryDAO.getByType(type);
        dictionaries.forEach(this::fillProjects);
        return ok(dictionaries);
    }

    @Override
    @Transactional
    public Result<YoutrackWorkDictionary> createDictionary(AuthToken token, YoutrackWorkDictionary dictionary) {
        if (dictionary == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (!isValid(dictionary)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }
        if (dictionaryDAO.isNameExist(dictionary.getName(), dictionary.getId())) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }
        dictionaryDAO.persist(dictionary);
        dictionary.setYoutrackProjects(saveProjects(dictionary.getYoutrackProjects()));
        jdbcManyRelationsHelper.persist(dictionary, YoutrackWorkDictionary.Fields.YOUTRACK_PROJECTS);

        return ok(dictionary);
    }

    @Override
    @Transactional
    public Result<YoutrackWorkDictionary> updateDictionary(AuthToken token, YoutrackWorkDictionary dictionary) {
        if (dictionary == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (!isValid(dictionary)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }
        if (dictionaryDAO.isNameExist(dictionary.getName(), dictionary.getId())) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }
        YoutrackWorkDictionary oldDictionary = dictionaryDAO.get(dictionary.getId());
        if (oldDictionary == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }
        dictionaryDAO.merge(dictionary);
        dictionary.setYoutrackProjects(saveProjects(dictionary.getYoutrackProjects()));
        jdbcManyRelationsHelper.persist(dictionary, YoutrackWorkDictionary.Fields.YOUTRACK_PROJECTS);

        return ok(dictionary);
    }

    @Override
    @Transactional
    public Result<YoutrackWorkDictionary> removeDictionary(AuthToken token, YoutrackWorkDictionary dictionary) {
        if (dictionary == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (dictionaryDAO.remove(dictionary)) {
            return ok(dictionary);
        }
        return error(En_ResultStatus.NOT_FOUND);
    }

    private boolean isValid(YoutrackWorkDictionary dictionary) {
        return StringUtils.isNotEmpty(dictionary.getName());
    }

    private List<YoutrackProject> saveProjects(List<YoutrackProject> projects) {
        projects.forEach(project -> {
            YoutrackProject byYoutrackId = youtrackProjectDAO.getByYoutrackId(project.getYoutrackId());
            if (byYoutrackId != null) {
                project.setId(byYoutrackId.getId());
                youtrackProjectDAO.merge(project);
            } else {
                youtrackProjectDAO.persist(project);
            }});
        return projects;
    }

    private void fillProjects(YoutrackWorkDictionary dictionary) {
        jdbcManyRelationsHelper.fill(dictionary, YoutrackWorkDictionary.Fields.YOUTRACK_PROJECTS);
    }
}
