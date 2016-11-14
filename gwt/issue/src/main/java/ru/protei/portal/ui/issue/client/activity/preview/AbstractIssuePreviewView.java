package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида превью обращения
 */
public interface AbstractIssuePreviewView extends IsWidget {

    void setActivity( AbstractIssuePreviewActivity activity );

    void setLocal( int local );
    void setHeader( String value );
    void setCreationDate( String value );
    void setState( String value );
    void setCriticality( String value );
    void setProduct( String value );
    void setCompany( String value );
    void setContact( String value );
    void setOurCompany( String value );
    void setManager( String value );
    void setInfo( String value );
    void showFullScreen( boolean value );
}
