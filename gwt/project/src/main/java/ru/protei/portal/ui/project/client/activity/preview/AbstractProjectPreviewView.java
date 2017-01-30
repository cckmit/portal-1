package ru.protei.portal.ui.project.client.activity.preview;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Set;

/**
 * Абстракция вида проекта
 */
public interface AbstractProjectPreviewView extends IsWidget {

    void setActivity( AbstractProjectPreviewActivity activity );

    void watchForScroll( boolean isWatch );
//    void setPrivateIssue( boolean privateIssue );
    void setHeader( String value );
    void setCreationDate( String value );
    HasValue<En_RegionState> state();
//    void setCriticality( int value );
    HasValue<ProductDirectionInfo> direction();
//    void setDirection( String value );
//    void setCompany( String value );
//    void setContact( String value );
//    void setOurCompany( String value );
    HasValue<PersonShortView> headManager();
    HasValue<Set<PersonShortView> > deployManagers();
    void setDetails( String value );

    void showFullScreen( boolean value );
    void setName( String name );

//    HasWidgets getCommentsContainer();
}
