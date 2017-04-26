package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseAttachment;

import java.util.Collections;
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
    FileController fileController;

    @Autowired
    CaseService caseService;

    @Override
    @Transactional
    public CoreResponse<Boolean> removeAttachmentEverywhere(Long id) {

        CaseAttachment ca = caseAttachmentDAO.getByAttachmentId(id);
        if (ca != null) {
            caseAttachmentDAO.removeByKey(ca.getId());
            caseService.updateCaseModified(ca.getCaseId(), new Date());
        }

        fileController.removeFiles(Collections.singletonList(id));
        attachmentDAO.removeByKey(id);

//        if (!result) {
//            return new CoreResponse().error(En_ResultStatus.NOT_REMOVED);
//        }

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
}
