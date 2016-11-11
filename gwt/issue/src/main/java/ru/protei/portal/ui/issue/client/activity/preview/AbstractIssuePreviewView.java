package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида превью обращения
 */
public interface AbstractIssuePreviewView extends IsWidget {

    void setActivity( AbstractIssuePreviewActivity activity );

    void setNumber( String value );
    void setCreationDate( String value );
    void setState( String value );
    void setCriticality( String value );
    void setProduct( String value );

    HasVisibility fullScreen ();
    HTMLPanel preview ();
}
