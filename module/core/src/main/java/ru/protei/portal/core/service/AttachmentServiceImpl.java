package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.authtoken.AuthTokenService;
import ru.protei.portal.core.service.events.EventAssemblerService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

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
    AuthService authService;

    @Autowired
    PolicyService policyService;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    AuthTokenService authTokenService;

    /**
     * remove attachment from fileStorage, DataBase (item and relations)
     */
    @Override
    @Transactional
    public Result<Boolean> removeAttachmentEverywhere( AuthToken token, En_CaseType caseType, Long id) {
        CaseAttachment ca = caseAttachmentDAO.getByAttachmentId(id);
        if (ca != null) {
            boolean isDeleted = caseAttachmentDAO.removeByKey(ca.getId());
            if(!isDeleted)
                return error( En_ResultStatus.NOT_REMOVED);

            caseService.updateCaseModified( token, ca.getCaseId(), new Date() );

            caseService.isExistsAttachments( ca.getCaseId() ).ifOk( isExists -> {
                if (!isExists) {
                    caseService.updateExistsAttachmentsFlag( ca.getCaseId(), false );
                }
            } );

            Attachment attachment = attachmentDAO.get(id);

            Result<Boolean> result = removeAttachment( token, caseType, id);

            if(result.isOk()
                    && token != null ) {
                Person person = authTokenService.getPerson(token).getData();
                publisherService.onCaseAttachmentEvent( new CaseAttachmentEvent(this, ServiceModule.GENERAL,
                        person, ca.getCaseId(), null, Collections.singletonList(attachment)));
            }

            return result;
        }else {
            return removeAttachment( token, caseType, id);
        }
    }

    /**
     * remove attachment from fileStorage and DataBase (only item)
     */
    @Override
    @Transactional
    public Result<Boolean> removeAttachment( AuthToken token, En_CaseType caseType, Long id) {
        Attachment attachment = attachmentDAO.partialGet(id, "ext_link");
        if(attachment == null)
            return error( En_ResultStatus.NOT_FOUND);

        boolean result =
                fileStorage.deleteFile(attachment.getExtLink())
                &&
                attachmentDAO.removeByKey(id);

        if (!result)
            return error( En_ResultStatus.NOT_REMOVED);

        return ok( true);
    }

    @Override
    public Result<List<Attachment>> getAttachmentsByCaseId( AuthToken token, En_CaseType caseType, Long caseId) {
        List<Attachment> list = attachmentDAO.getListByCondition(
                "ID in (Select ATT_ID from case_attachment where CASE_ID = ?)", caseId
        );

        if(list == null)
            return error( En_ResultStatus.GET_DATA_ERROR);

        return ok( list);
    }

    @Override
    public Result<List<Attachment>> getAttachments( AuthToken token, En_CaseType caseType, List<Long> ids) {
        List<Attachment> list = attachmentDAO.getListByKeys(ids);

        if(list == null)
            return error( En_ResultStatus.GET_DATA_ERROR);

        return ok( list);
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
    public Result<Long> saveAttachment( Attachment attachment) {
        /* В redmine и jira дата устанавливается из источника */
        if (attachment.getCreated() == null) {
            attachment.setCreated(new Date());
        }
        Long id = attachment.getId();
        if (id == null) {
            id = attachmentDAO.persist(attachment);

            if(id == null)
                return error( En_ResultStatus.NOT_CREATED);
        }
        else
            attachmentDAO.merge(attachment);

        return ok( id);
    }

    @Override
    public Result<String> getAttachmentNameByExtLink( String extLink) {
        Attachment attachment = attachmentDAO.partialGetByCondition("ext_link = ?", Collections.singletonList(extLink), "file_name");
        if (attachment == null) {
            return error( En_ResultStatus.NOT_FOUND);
        }
        return ok( attachment.getFileName());
    }
}
