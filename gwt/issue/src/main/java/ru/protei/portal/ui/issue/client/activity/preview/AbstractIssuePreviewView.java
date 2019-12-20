package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueView;

/**
 * Абстракция вида превью обращения
 */
public interface AbstractIssuePreviewView extends IsWidget, AbstractIssueView {

    void setActivity( AbstractIssuePreviewActivity activity );

    void setFullScreen( boolean isFullScreen );

    HasVisibility backBtnVisibility();

    boolean isAttached();
}
