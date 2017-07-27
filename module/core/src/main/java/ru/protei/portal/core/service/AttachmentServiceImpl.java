package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseAttachment;

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

    @Autowired
    FileStorage fileStorage;

    /**
     * remove attachment from fileStorage, DataBase (item and relations)
     */
    @Override
    @Transactional
    public CoreResponse<Boolean> removeAttachmentEverywhere(Long id) {
        CaseAttachment ca = caseAttachmentDAO.getByAttachmentId(id);
        if (ca != null) {
            boolean isDeleted = caseAttachmentDAO.removeByKey(ca.getId());
            if(!isDeleted)
                return new CoreResponse<Boolean>().error(En_ResultStatus.NOT_REMOVED);

            caseService.updateCaseModified(ca.getCaseId(), new Date());

            if (!caseService.isExistsAttachments(ca.getCaseId()))
                caseService.updateExistsAttachmentsFlag(ca.getCaseId(), false);
        }

        return removeAttachment(id);
    }

    /**
     * remove attachment from fileStorage and DataBase (only item)
     */
    @Override
    @Transactional
    public CoreResponse<Boolean> removeAttachment(Long id) {
        Attachment attachment = attachmentDAO.partialGet(id, "ext_link");
        if(attachment == null)
            return new CoreResponse<Boolean>().error(En_ResultStatus.NOT_FOUND);

        boolean result = fileStorage.deleteFile(attachment.getExtLink()) && attachmentDAO.removeByKey(id);

        if (!result)
            return new CoreResponse<Boolean>().error(En_ResultStatus.NOT_REMOVED);

        return new CoreResponse<Boolean>().success(true);
    }

    @Override
    public CoreResponse<List<Attachment>> getAttachmentsByCaseId(Long caseId) {
        List<Attachment> list = attachmentDAO.getListByCondition(
                "ID in (Select ATT_ID from case_attachment where CASE_ID = ?)", caseId
        );

        if(list == null)
            return new CoreResponse().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<Attachment>>().success(list);
    }

    @Override
    public CoreResponse<List<Attachment>> getAttachments(List<Long> ids) {
        List<Attachment> list = attachmentDAO.getListByKeys(ids);

        if(list == null)
            return new CoreResponse().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<Attachment>>().success(list);
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
