package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.YoutrackProjectDAO;
import ru.protei.portal.core.model.dao.YoutrackReportDictionaryDAO;
import ru.protei.portal.core.model.dict.En_ReportYtWorkType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.YoutrackProject;
import ru.protei.portal.core.model.ent.YoutrackReportDictionary;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;

public class YoutrackReportDictionaryServiceImpl implements YoutrackReportDictionaryService {
    private static Logger log = LoggerFactory.getLogger(YoutrackReportDictionaryServiceImpl.class);

    @Autowired
    YoutrackProjectDAO youtrackProjectDAO;
    @Autowired
    YoutrackReportDictionaryDAO dictionaryDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public Result<List<YoutrackReportDictionary>> getDictionaries(AuthToken token, En_ReportYtWorkType type) {
        if (type == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return Optional.ofNullable(dictionaryDAO.getByType(type))
                .map(list -> {
                    list.forEach(this::fillProjects);
                    return list;
                })
                .map(Result::ok)
                .orElseGet(() -> error(En_ResultStatus.INTERNAL_ERROR));
    }

    @Override
    public Result<YoutrackReportDictionary> getDictionary(AuthToken token, Long id) {
        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return Optional.ofNullable(dictionaryDAO.get(id))
                .map(this::fillProjects)
                .map(Result::ok)
                .orElseGet(() -> error(En_ResultStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public Result<Long> createDictionary(AuthToken token, YoutrackReportDictionary dictionary) {
        if (dictionary == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (isValid(dictionary)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        dictionaryDAO.persist(dictionary);
        dictionary.setYoutrackProjects(fillOrSaveProjects(dictionary.getYoutrackProjects()));
        jdbcManyRelationsHelper.persist(dictionary, YoutrackReportDictionary.Fields.YOUTRACK_PROJECTS);

        return null;
    }

    @Override
    @Transactional
    public Result<Long> updateDictionary(AuthToken token, YoutrackReportDictionary dictionary) {
        if (dictionary == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (isValid(dictionary)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        YoutrackReportDictionary oldDictionary = dictionaryDAO.get(dictionary.getId());
        if (oldDictionary == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        dictionaryDAO.merge(dictionary);
        dictionary.setYoutrackProjects(fillOrSaveProjects(dictionary.getYoutrackProjects()));
        jdbcManyRelationsHelper.persist(dictionary, YoutrackReportDictionary.Fields.YOUTRACK_PROJECTS);

        return null;
    }

    @Override
    @Transactional
    public Result<Long> removeDictionary(AuthToken token, YoutrackReportDictionary dictionary) {
        if (dictionary == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (dictionaryDAO.remove(dictionary)) {
            return Result.ok(dictionary.getId());
        }
        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    private boolean isValid(YoutrackReportDictionary dictionary) {
        return StringUtils.isNotEmpty(dictionary.getName());
        // todo уникальность имени
    }

    private List<YoutrackProject> fillOrSaveProjects(List<YoutrackProject> projects) {
        return projects.stream().map(project -> {
            YoutrackProject byYoutrackId = youtrackProjectDAO.getByYoutrackId(project.getYoutrackId());
            if (byYoutrackId != null) {
                return byYoutrackId;
            } else {
                youtrackProjectDAO.persist(project);
                return project;
            }
        }).collect(Collectors.toList());
    }

    private YoutrackReportDictionary fillProjects(YoutrackReportDictionary dictionary) {
        jdbcManyRelationsHelper.fill(dictionary, YoutrackReportDictionary.Fields.YOUTRACK_PROJECTS);
        return dictionary;
    }
}
