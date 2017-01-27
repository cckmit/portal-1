package ru.protei.portal.ui.project.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_RegionState;

/**
 * Абстракция вида проекта
 */
public interface AbstractProjectPreviewView extends IsWidget {

    void setActivity( AbstractProjectPreviewActivity activity );

    void watchForScroll( boolean isWatch );
//    void setPrivateIssue( boolean privateIssue );
    void setHeader( String value );
    void setCreationDate( String value );
    void setState( En_RegionState value );
//    void setCriticality( int value );
    void setDirection( String value );
//    void setCompany( String value );
//    void setContact( String value );
//    void setOurCompany( String value );
    void setHeadManager( String value );
    void setDetails( String value );

    void showFullScreen( boolean value );

//    HasWidgets getCommentsContainer();
}
