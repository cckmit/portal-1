package ru.protei.portal.core.service;


import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления обращениями
 */
public class CaseServiceImpl implements CaseService {

    private static Logger log = LoggerFactory.getLogger(CaseServiceImpl.class);

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CaseShortViewDAO caseShortViewDAO;

    @Autowired
    CaseStateMatrixDAO caseStateMatrixDAO;

    @Autowired
    CaseCommentDAO caseCommentDAO;

    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    AttachmentDAO attachmentDAO;

    @Autowired
    FileController fileController;

    @Override
    public CoreResponse<List<CaseShortView>> caseObjectList( CaseQuery query) {
        List<CaseShortView> list = caseShortViewDAO.getCases( query );

        if ( list == null )
            return new CoreResponse<List<CaseShortView>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<CaseShortView>>().success(list);
    }

    @Override
    public CoreResponse<CaseObject> getCaseObject(long id) {
        CaseObject caseObject = caseObjectDAO.get( id );

        if(caseObject == null)
            return new CoreResponse().error(En_ResultStatus.NOT_FOUND);

        caseObject.setAttachmentsIds(
                caseAttachmentDAO
                        .getListByCaseId(id)
                        .stream()
                        .map(CaseAttachment::getAttachmentId)
                        .collect(Collectors.toList())
        );

        return new CoreResponse<CaseObject>().success(caseObject);
    }

    @Override
    @Transactional
    public CoreResponse< CaseObject > saveCaseObject( CaseObject caseObject ) {
        if (caseObject == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        Date now = new Date();
        caseObject.setCreated(now);
        caseObject.setModified(now);

        Long caseId = caseObjectDAO.insertCase(caseObject);

        if (caseId == null)
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);

        if(CollectionUtils.isNotEmpty(caseObject.getAttachmentsIds())){
            caseAttachmentDAO.persistBatch(
                    generateCaseAttachments(caseObject.getAttachmentsIds(), caseId, null)
            );
        }

        return new CoreResponse<CaseObject>().success( caseObject );
    }

    @Override
    @Transactional
    public CoreResponse< CaseObject > updateCaseObject( CaseObject caseObject ) {
        if (caseObject == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        caseObject.setModified(new Date());
        boolean isUpdated = caseObjectDAO.merge(caseObject);

        if (!isUpdated)
            return new CoreResponse().error(En_ResultStatus.NOT_UPDATED);

        Collection<CaseAttachment> removedCaseAttachments =
                caseAttachmentDAO.subtractDiffAndSynchronize(
                        caseAttachmentDAO.getListByCaseId(caseObject.getId()),
                        generateCaseAttachments(caseObject.getAttachmentsIds(), caseObject.getId(), null)
                );

        if(!removedCaseAttachments.isEmpty())
            removeAttachments(removedCaseAttachments);

        return new CoreResponse<CaseObject>().success( caseObject );
    }

    @Override
    public CoreResponse<List<En_CaseState>> stateList( En_CaseType caseType ) {
        List<En_CaseState> states = caseStateMatrixDAO.getStatesByCaseType(caseType);

        if (states == null)
            return new CoreResponse<List<En_CaseState>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<En_CaseState>>().success(states);
    }

    @Override
    public CoreResponse<List<CaseComment>> getCaseCommentList( long caseId ) {
        List<CaseComment> list = caseCommentDAO.getCaseComments( caseId );

        if ( list == null )
            return new CoreResponse<List<CaseComment>>().error(En_ResultStatus.GET_DATA_ERROR);

        putAttachmentsToComments(list, caseAttachmentDAO.getListByCaseId(caseId));

        return new CoreResponse<List<CaseComment>>().success(list);
    }

    @Override
    @Transactional
    public CoreResponse<CaseComment> addCaseComment(CaseComment comment) {
        if ( comment == null )
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        Date now = new Date();
        comment.setCreated(now);

        Long commentId = caseCommentDAO.persist(comment);

        if (commentId == null)
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);

        List<Long> attachmentsIds = comment.getAttachmentsIds();
        if(CollectionUtils.isNotEmpty(attachmentsIds)){
            caseAttachmentDAO.persistBatch(
                generateCaseAttachments(attachmentsIds, comment.getCaseId(), commentId)
            );
        }

        boolean isCaseChanged = updateCaseModified ( comment.getCaseId(), comment.getCreated() ).getData();

        if (!isCaseChanged)
            throw new RuntimeException( "failed to update case modifiedDate " );

        CaseComment result = caseCommentDAO.get( commentId ); // ????

        return new CoreResponse<CaseComment>().success( result );
    }

    @Override
    @Transactional
    public CoreResponse<CaseComment> updateCaseComment ( CaseComment comment) {
        if (comment == null || comment.getId() == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        if (!isChangeAvailable ( comment.getCreated() ))
            return new CoreResponse().error( En_ResultStatus.NOT_UPDATED );

        boolean isUpdated = caseCommentDAO.merge(comment);

        if (!isUpdated)
            return new CoreResponse().error( En_ResultStatus.NOT_UPDATED );


        Collection<CaseAttachment> removedCaseAttachments =
                caseAttachmentDAO.subtractDiffAndSynchronize(
                        caseAttachmentDAO.getListByCommentId(comment.getId()),
                        generateCaseAttachments(comment.getAttachmentsIds(), comment.getCaseId(), comment.getId())
                );

        if(!removedCaseAttachments.isEmpty()){
            removeAttachments(removedCaseAttachments);
        }

        boolean isCaseChanged = updateCaseModified ( comment.getCaseId(), new Date() ).getData();

        if (!isCaseChanged)
            throw new RuntimeException( "failed to update case modifiedDate " );

        return new CoreResponse<CaseComment>().success( comment );
    }


    @Override
    @Transactional
    public CoreResponse<CaseComment> removeCaseComment( CaseComment caseComment ) {
        if (caseComment == null || caseComment.getId() == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        if (!isChangeAvailable ( caseComment.getCreated() ))
            return new CoreResponse().error(En_ResultStatus.NOT_REMOVED);

        long caseId = caseComment.getCaseId();

        boolean isRemoved = caseCommentDAO.remove(caseComment);

        if (!isRemoved)
            return new CoreResponse().error( En_ResultStatus.NOT_REMOVED );

        List<Long> attachmentsIds = caseComment.getAttachmentsIds();
        if(CollectionUtils.isNotEmpty(attachmentsIds)){
            caseAttachmentDAO.removeByCommentId(caseId);
            removeAttachments(attachmentsIds);
        }

        boolean isCaseChanged = updateCaseModified(caseId, new Date()).getData();

        if (!isCaseChanged)
            throw new RuntimeException( "failed to update case modifiedDate " );

        return new CoreResponse<CaseComment>().success(caseComment);
    }

    @Override
    public CoreResponse<Long> count(CaseQuery query) {
        Long count = caseObjectDAO.count(query);

        if (count == null)
            return new CoreResponse<Long>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<Long>().success(count);
    }

    @Override
    public CoreResponse<Boolean> updateCaseModified(Long caseId, Date modified) {

        if(caseId == null || !caseObjectDAO.checkExistsByKey(caseId))
            return new CoreResponse<Boolean>().success(false);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setModified(modified == null? new Date(): modified);

        boolean isUpdated = caseObjectDAO.partialMerge(caseObject, "MODIFIED");

        return new CoreResponse<Boolean>().success(isUpdated);
    }

    private boolean isChangeAvailable ( Date date ) {
        Calendar c = Calendar.getInstance();
        long current = c.getTimeInMillis();
        c.setTime( date );
        long checked = c.getTimeInMillis();

        return current - checked < CHANGE_LIMIT_TIME;
    }

    private void removeAttachments(Collection<CaseAttachment> list){
        removeAttachments(
                list.stream().map(CaseAttachment::getAttachmentId).collect(Collectors.toList())
        );
    }

    private void removeAttachments(List<Long> attachmentsIds){
        fileController.removeFiles(attachmentsIds);
        attachmentDAO.removeByKeys(attachmentsIds);
    }

    private void putAttachmentsToComments(Collection<CaseComment> comments, Collection<CaseAttachment> caseAttachments){
        if(CollectionUtils.isEmpty(comments) || CollectionUtils.isEmpty(caseAttachments))
            return;

        Collection<CaseAttachment> nonEmptyCaseAttachments = caseAttachments
                .stream()
                .filter(ca -> ca.getCommentId() != null)
                .collect(Collectors.toList());

        Collection<Long> nonEmptyCommentsIds = nonEmptyCaseAttachments
                .stream()
                .map(CaseAttachment::getCommentId)
                .collect(Collectors.toSet());

        for(CaseComment comment: comments){
            if(!nonEmptyCommentsIds.contains(comment.getId()))
                continue;

            List<Long> attachmentsIds = new ArrayList<>();
            nonEmptyCaseAttachments.forEach(ca -> {
                if(comment.getId().equals(ca.getCommentId()))
                    attachmentsIds.add(ca.getAttachmentId());
            });
            comment.setAttachmentsIds(attachmentsIds);
        }
    }

    private Collection<CaseAttachment> generateCaseAttachments(Collection<Long> attachmentsIds, Long caseId, Long commentId){
        if(attachmentsIds == null)
            return Collections.emptyList();

        return attachmentsIds
                .stream()
                .map(attachId -> new CaseAttachment(caseId, attachId, commentId))
                .collect(Collectors.toList());
    }

    static final long CHANGE_LIMIT_TIME = 300000;  // 5 минут  (в мсек)
}
