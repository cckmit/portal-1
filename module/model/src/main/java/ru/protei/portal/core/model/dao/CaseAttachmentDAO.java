package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseAttachment;

import java.util.Collection;
import java.util.List;

/**
 * Created by bondarenko on 30.12.16.
 */
public interface CaseAttachmentDAO extends PortalBaseDAO<CaseAttachment>{

    List<CaseAttachment> getListByCommentId(Long commentId);

    List<CaseAttachment> getListByCaseId(Long caseId);

    CaseAttachment getByAttachmentId(Long attachmentId);

    int removeByCaseId(Long commentId);

    int removeByAttachmentId(Long attachmentId);

    int removeByCommentId(Long commentId);

    void removeBatch(Collection<CaseAttachment> list);

    /**
     * Высчитывает разницу между старым и новым списками сохраняя и удаляя нужные связи
     * @return удалённые CaseAttachments
     */
    Collection<CaseAttachment> calcDiffAndSynchronize(Collection<CaseAttachment> oldList, Collection<CaseAttachment> newList);


}
