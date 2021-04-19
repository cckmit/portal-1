package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.query.CaseTagQuery;

import java.util.List;

public interface CaseTagControllerAsync {

    void update( CaseTag caseTag, AsyncCallback<Long> async);

    void create( CaseTag caseTag, AsyncCallback<Long> async);

    void removeTag(Long id, AsyncCallback<Long> async);

    void getTags(CaseTagQuery query, AsyncCallback<List<CaseTag>> async);

    void attachTag(Long caseId, Long tagId, AsyncCallback<Void> async);

    void detachTag( Long caseId, Long tagId, AsyncCallback<Long> async);

    void isTagNameExists(CaseTag caseTag, AsyncCallback<Boolean> booleanRequestCallback);
}
