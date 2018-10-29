package ru.protei.portal.ui.project.client.activity.preview;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.Set;

/**
 * Абстракция вида проекта
 */
public interface AbstractProjectPreviewView extends IsWidget {

    void setActivity( AbstractProjectPreviewActivity activity );

    void watchForScroll( boolean isWatch );

    void setInitiatorShortName(String value);

    void setCreationDate(String value );
    HasValue<En_RegionState> state();
    HasValue<ProductDirectionInfo> direction();
    HasValue<Set<PersonProjectMemberView>> team();
    HasText details();
    HasValue<EntityOption> region();
    HasValue<Set<ProductShortView>> products();
    HasValue<EntityOption> company();
    HasValue<En_CustomerType> customerType();

    void showFullScreen(boolean value );

    void setName( String name );
    String getName();

    HasWidgets getCommentsContainer();
}
