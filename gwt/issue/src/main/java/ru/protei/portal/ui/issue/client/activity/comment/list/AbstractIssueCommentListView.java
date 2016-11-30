package ru.protei.portal.ui.issue.client.activity.comment.list;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.ContactShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Представление списка комментариев
 */
public interface AbstractIssueCommentListView extends IsWidget {

    void setActivity( AbstractIssueCommentListActivity activity );

    HasWidgets getCommentsContainer();
}
