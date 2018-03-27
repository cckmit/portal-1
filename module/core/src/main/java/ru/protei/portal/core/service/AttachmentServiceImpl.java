package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    /**
     * remove attachment from fileStorage, DataBase (item and relations)
     */
    @Override
    @Transactional
    public CoreResponse<Boolean> removeAttachmentEverywhere( AuthToken token, Long id) {
        CaseAttachment ca = caseAttachmentDAO.getByAttachmentId(id);
        if (ca != null) {
            boolean isDeleted = caseAttachmentDAO.removeByKey(ca.getId());
            if(!isDeleted)
                return new CoreResponse<Boolean>().error(En_ResultStatus.NOT_REMOVED);

            caseService.updateCaseModified( token, ca.getCaseId(), new Date() );

            if (!caseService.isExistsAttachments(ca.getCaseId())) {
                caseService.updateExistsAttachmentsFlag(ca.getCaseId(), false);
            }

            CaseObject issue = caseObjectDAO.get(ca.getCaseId());
            Attachment attachment = attachmentDAO.get(id);
            UserSessionDescriptor ud = authService.findSession( token );

            CoreResponse<Boolean> result = removeAttachment( token, id);

            if(result.isOk() && issue != null && ud != null ) {
                jdbcManyRelationsHelper.fill(issue, "attachments");
                publisherService.publishEvent(new CaseAttachmentEvent(
                        ServiceModule.GENERAL,
                        caseService,
                        this,
                        issue,
                        null,
                        Collections.singletonList(attachment),
                        ud.getPerson()
                ));
            }

            return result;
        }else {
            return removeAttachment( token, id);
        }
    }

    /**
     * remove attachment from fileStorage and DataBase (only item)
     */
    @Override
    @Transactional
    public CoreResponse<Boolean> removeAttachment(AuthToken token, Long id) {
        Attachment attachment = attachmentDAO.partialGet(id, "ext_link");
        if(attachment == null)
            return new CoreResponse<Boolean>().error(En_ResultStatus.NOT_FOUND);

        boolean result =
                fileStorage.deleteFile(attachment.getExtLink())
                &&
                attachmentDAO.removeByKey(id);

        if (!result)
            return new CoreResponse<Boolean>().error(En_ResultStatus.NOT_REMOVED);

        return new CoreResponse<Boolean>().success(true);
    }

    @Override
    public CoreResponse<List<Attachment>> getAttachmentsByCaseId(AuthToken token, Long caseId) {
        List<Attachment> list = attachmentDAO.getListByCondition(
                "ID in (Select ATT_ID from case_attachment where CASE_ID = ?)", caseId
        );

        if(list == null)
            return new CoreResponse().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<Attachment>>().success(list);
    }

    @Override
    public CoreResponse<List<Attachment>> getAttachments( AuthToken token, List<Long> ids) {
        List<Attachment> list = attachmentDAO.getListByKeys(ids);

        if(list == null)
            return new CoreResponse().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<Attachment>>().success(list);
    }

    @Override
    public CoreResponse<List<Attachment>> getAttachments( AuthToken token, Collection<CaseAttachment> caseAttachments) {
        if(caseAttachments == null || caseAttachments.isEmpty())
            return new CoreResponse<List<Attachment>>().success(Collections.emptyList());

        return getAttachments(
                token,
                caseAttachments.stream().map(CaseAttachment::getAttachmentId).collect(Collectors.toList())
        );
    }

    @Override
    public CoreResponse<Long> saveAttachment(Attachment attachment) {
        attachment.setCreated(new Date());
        Long id = attachmentDAO.persist(attachment);
        if(id == null)
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);

        return new CoreResponse<Long>().success(id);
    }
}
