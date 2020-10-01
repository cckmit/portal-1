package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.ent.Attachment;

import java.util.List;

/**
 * Created by bondarenko on 26.01.17.
 */
public class AttachmentDAO_Impl extends PortalBaseJdbcDAO<Attachment> implements AttachmentDAO {

    @Override
    public List<Attachment> getListByCaseId(Long caseId) {
        return getListByCondition("attachment.id in (select case_attachment.att_id from case_attachment where case_attachment.case_id=?)", caseId);
    }
}
