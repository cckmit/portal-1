package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида превью обращения
 */
public interface AbstractIssuePreviewView extends IsWidget {

    void setActivity( AbstractIssuePreviewActivity activity );

    void setPrivateIssue( boolean privateIssue );
    void setHeader( String value );
    void setCreationDate( String value );
    void setState( long value );
    void setCriticality( int value );
    void setProduct( String value );
    void setCompany( String value );
    void setContact( String value );
    void setOurCompany( String value );
    void setManager( String value );
    void setInfo( String value );
    void showFullScreen( boolean value );
}
