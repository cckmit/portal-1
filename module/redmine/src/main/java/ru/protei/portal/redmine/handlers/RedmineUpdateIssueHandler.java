package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.RedmineEndpoint;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.redmine.enums.RedmineChangeType;
import ru.protei.portal.redmine.factory.CaseUpdaterFactory;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.utils.CachedPersonMapper;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RedmineUpdateIssueHandler implements RedmineEventHandler {

    @Override
    public void handle(User user, Issue issue, RedmineEndpoint endpoint) {
        final CaseObject object = caseObjectDAO.getByExternalAppCaseId(issue.getId() + "_" + endpoint.getCompanyId());
        if (object != null) {
            logger.debug("Found case object with id {}", object.getId());
            compareAndUpdate(issue, object, endpoint);
            logger.debug("Object with id {} saved", object.getId());
        } else {
            logger.debug("Object with external app id {} is not found; starting it's creation", issue.getId());
            newIssueHandler.handle(user, issue, endpoint);
        }
    }

    private void compareAndUpdate(Issue issue, CaseObject object, RedmineEndpoint endpoint) {
        CachedPersonMapper personMapper = new CachedPersonMapper(personDAO, endpoint, null);

        logger.debug("Trying to get latest synchronized comment");
        Date latestCreated = caseCommentDAO.getCaseComments(new CaseCommentQuery(object.getId())).stream()
                .filter(x -> x.getAuthor().getCreator().equals("redmine"))
                .max(Comparator.comparing(CaseComment::getCreated))
                .map(CaseComment::getCreated)
                .orElse(issue.getCreatedOn());
        logger.debug("Last comment was synced on {}", latestCreated);

        List<Journal> latestJournals = issue.getJournals().stream()
                .filter(Objects::nonNull)
                .filter(journal -> !personMapper.isTechUser(endpoint, journal.getUser()))
                .filter(journal -> journal.getCreatedOn() != null && journal.getCreatedOn().compareTo(latestCreated) > 0)
                .sorted(Comparator.comparing(Journal::getCreatedOn))
                .collect(Collectors.toList());
        logger.debug("Got {} journals after {}", latestJournals.size(), endpoint.getLastUpdatedOnDate());

        //Synchronize comments, status, priority, name, info
        latestJournals.forEach(journal -> {
            if (StringUtils.isNotBlank(journal.getNotes())) {
                caseUpdaterFactory.getCommentsUpdater().apply(object, endpoint, journal, null, personMapper);
            }
            journal.getDetails().forEach(detail ->
                    RedmineChangeType.findByName(detail.getName())
                            .ifPresent(type -> caseUpdaterFactory.getUpdater(type).apply(object, endpoint, journal, detail.getNewValue(), personMapper)
            ));
        });

        //Synchronize attachment
        commonService.processAttachments(issue, personMapper, object, endpoint);
    }

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CommonService commonService;

    @Autowired
    private CaseUpdaterFactory caseUpdaterFactory;

    @Autowired
    private RedmineNewIssueHandler newIssueHandler;

    private final Logger logger = LoggerFactory.getLogger(RedmineUpdateIssueHandler.class);
}
