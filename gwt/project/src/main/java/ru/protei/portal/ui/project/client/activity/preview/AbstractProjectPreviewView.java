package ru.protei.portal.ui.project.client.activity.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.Set;

/**
 * Абстракция вида проекта
 */
public interface AbstractProjectPreviewView extends IsWidget {

    void setActivity( AbstractProjectPreviewActivity activity );

    void watchForScroll( boolean isWatch );
    void setHeader( String value );
    void setCreationDate( String value );
    void setName( String name );
    void setState( long value );
    void setDirection( String value );
    void setTeam( Set<PersonProjectMemberView> value );
    void setDescription( String value );
    void setRegion( String value );
    void setProducts( Set<ProductShortView> value );
    void setCompany( String value );
    void setCustomerType( String value );

    void showFullScreen(boolean value );

    HasWidgets getCommentsContainer();
    HasWidgets getDocumentsContainer();
}
