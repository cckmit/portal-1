package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseComment;

import java.util.List;

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

    HasWidgets getCommentsContainer();
}
