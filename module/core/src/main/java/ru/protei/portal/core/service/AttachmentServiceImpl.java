package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.ProjectAttachmentEvent;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.EventAssemblerService;
import ru.protei.portal.core.service.events.EventProjectAssemblerService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

/**
 * Created by bondarenko on 23.01.17.
 */
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    AttachmentDAO attachmentDAO;

    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CaseService caseService;

    @Autowired
    FileStorage fileStorage;

    @Autowired
    EventAssemblerService publisherService;

    @Autowired
    EventProjectAssemblerService projectPublisherService;

    @Autowired
    AuthService authService;

    @Autowired
    PolicyService policyService;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

/*
    @Autowired
    private ClientEventService clientEventService;
*/

    /**
     * remove attachment from fileStorage, DataBase (item and relations)
     */
    @Override
    @Transactional
    public Result<Boolean> removeAttachmentEverywhere( AuthToken token, En_CaseType caseType, Long id) {
        CaseAttachment caseAttachment = caseAttachmentDAO.getByAttachmentId(id);

        if (caseAttachment == null) {
            return removeAttachment(token, caseType, id);
        } else {
            boolean isDeleted = caseAttachmentDAO.removeByKey(caseAttachment.getId());
            if (!isDeleted) {
                return error(En_ResultStatus.NOT_REMOVED);
            }

            caseService.updateCaseModified(token, caseAttachment.getCaseId(), new Date());

            caseService.isExistsAttachments(caseAttachment.getCaseId()).ifOk(isExists -> {
                if (!isExists) {
                    caseService.updateExistsAttachmentsFlag(caseAttachment.getCaseId(), false);
                }
            });

            Attachment attachment = attachmentDAO.get(id);

            Result<Boolean> result = removeAttachment(token, caseType, id);

            if (result.isOk() && token != null) {

/*
                if (caseAttachment.getCommentId() != null) {
                    clientEventService.fireEvent( new CaseCommentSavedClientEvent( token.getPersonId(), caseAttachment.getCaseId(), caseAttachment.getCommentId() ) );
                }
*/

                if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
                    publisherService.onCaseAttachmentEvent(new CaseAttachmentEvent(this, ServiceModule.GENERAL,
                            token.getPersonId(), caseAttachment.getCaseId(), null, Collections.singletonList(attachment)));
                }

                if (En_CaseType.PROJECT.equals(caseType)) {
                    projectPublisherService.onProjectAttachmentEvent(new ProjectAttachmentEvent(this, Collections.emptyList(),
                            Collections.singletonList(attachment), caseAttachment.getCommentId(), token.getPersonId(), caseAttachment.getCaseId()));
                }

            }

            return result;
        }
    }

    /**
     * remove attachment from fileStorage and DataBase (only item)
     */
    @Override
    @Transactional
    public Result<Boolean> removeAttachment( AuthToken token, En_CaseType caseType, Long id) {
        Attachment attachment = attachmentDAO.partialGet(id, "ext_link");
        if (attachment == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        boolean result = attachmentDAO.removeByKey(id)
                && fileStorage.deleteFile(attachment.getExtLink());

        if (!result) {
            throw new ResultStatusException(En_ResultStatus.NOT_REMOVED);
        }

        return ok(true);
    }

    @Override
    public Result<List<Attachment>> getAttachmentsByCaseId( AuthToken token, En_CaseType caseType, Long caseId) {
        if (hasAccessForPrivateAttachments(token, caseType)) {
            return ok(attachmentDAO.getAttachmentsByCaseId(caseId));
        }

        return ok(attachmentDAO.getPublicAttachmentsByCaseId(caseId));
    }

    @Override
    public Result<List<Attachment>> getAttachments( AuthToken token, En_CaseType caseType, List<Long> ids) {
        if (isEmpty(ids)) {
            return ok(new ArrayList<>());
        }

        if (hasAccessForPrivateAttachments(token, caseType)) {
            return ok(attachmentDAO.getListByKeys(ids));
        }

        return ok(attachmentDAO.getPublicAttachmentsByIds(ids));
    }

    @Override
    public Result<List<Attachment>> getAttachments( AuthToken token, En_CaseType caseType, Collection<CaseAttachment> caseAttachments) {
        if(caseAttachments == null || caseAttachments.isEmpty())
            return ok( Collections.emptyList());

        return getAttachments(
                token, caseType,
                caseAttachments.stream().map(CaseAttachment::getAttachmentId).collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public Result<Long> saveAttachment( Attachment attachment) {
        /* В redmine и jira дата устанавливается из источника */
        if (attachment.getCreated() == null) {
            attachment.setCreated(new Date());
        }

        Long id = attachment.getId();
        if (id == null) {
            id = attachmentDAO.persist(attachment);

            if (id == null) {
                return error(En_ResultStatus.NOT_CREATED);
            }
        } else {
            attachmentDAO.merge(attachment);
        }

        return ok( id);
    }

    @Override
    public Result<Attachment> getAttachmentByExtLink( String extLink ) {
        Attachment attachment = attachmentDAO.getByCondition("ext_link = ?", Collections.singletonList(extLink));
        if (attachment == null) {
            return error( En_ResultStatus.NOT_FOUND);
        }
        return ok( attachment );
    }

    private boolean hasAccessForPrivateAttachments(AuthToken token, En_CaseType caseType) {
        if (!En_CaseType.CRM_SUPPORT.equals(caseType)) {
            return true;
        }

        if (policyService.hasGrantAccessFor(token.getRoles(), En_Privilege.ISSUE_VIEW)) {
            return true;
        }

        return false;
    }
}
