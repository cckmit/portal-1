package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.helper.HelperFunc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bondarenko on 30.12.16.
 */
public class CaseAttachmentDAO_Impl extends PortalBaseJdbcDAO<CaseAttachment> implements CaseAttachmentDAO{

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Override
    public List<CaseAttachment> getListByCommentId(Long commentId) {
        return getListByCondition("ccomment_id = ?", commentId);
    }

    @Override
    public List<CaseAttachment> getListByCaseId(Long caseId) {
        return getListByCondition("case_id = ?", caseId);
    }

    @Override
    public CaseAttachment getByAttachmentId(Long attachmentId) {
        List<CaseAttachment> result = getListByCondition("att_id = ?", attachmentId);
        return result.isEmpty()? null: result.get(0);
    }

    @Override
    public int removeByCommentId(Long commentId) {
        return removeByCondition("ccomment_id = ?", commentId);
    }

    @Override
    public int removeByCaseId(Long caseId) {
        return removeByCondition("case_id = ?", caseId);
    }

    @Override
    public int removeByAttachmentId(Long attachId) {
        return removeByCondition("att_id = ?", attachId);
    }

    @Override
    public void removeBatch(Collection<CaseAttachment> list) {
        if(list == null || list.isEmpty())
            return;

        removeByKeys(
                list.stream().map(CaseAttachment::getId).collect(Collectors.toList())
        );
    }

    @Override
    public Collection<CaseAttachment> subtractDiffAndSynchronize(Collection<CaseAttachment> oldList, Collection<CaseAttachment> newList) {
        if(newList == null)
            newList = Collections.emptyList();

        if(oldList == null)
            oldList = Collections.emptyList();


        if(newList.size() == oldList.size() && newList.containsAll(oldList))
            return Collections.emptyList();

        persistBatch(HelperFunc.subtract(newList, oldList));

        Collection<CaseAttachment> caseAttachmentsToRemove = HelperFunc.subtract(oldList, newList);
        removeBatch(caseAttachmentsToRemove);

        return caseAttachmentsToRemove;
    }
}
