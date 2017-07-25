package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;

import java.util.Date;
import java.util.List;

/**
 * Created by bondarenko on 23.01.17.
 */
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    AttachmentDAO attachmentDAO;

    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    CaseService caseService;

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

            if (!caseService.isExistsAttachments(ca.getCaseId()))
                caseService.updateExistsAttachmentsFlag(ca.getCaseId(), false);
        }

        return removeAttachment( token, id);
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

        boolean result = FileStorage.getDefault().deleteFile(attachment.getExtLink()) && attachmentDAO.removeByKey(id);

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
}
