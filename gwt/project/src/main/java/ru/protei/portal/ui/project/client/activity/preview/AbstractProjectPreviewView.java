package ru.protei.portal.ui.project.client.activity.preview;

import com.google.gwt.user.client.ui.HasText;
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
    void setHeader( String value );
    void setCreationDate( String value );
    HasValue<En_RegionState> state();
    HasValue<ProductDirectionInfo> direction();
    HasValue<PersonShortView> headManager();
    HasValue<Set<PersonShortView> > deployManagers();
    HasText details();

    void showFullScreen( boolean value );

    void setName( String name );
    String getName();


//    HasWidgets getCommentsContainer();
}
