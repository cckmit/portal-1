package ru.protei.portal.ui.project.client.activity.preview;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;
import java.util.Map;

/**
 * Абстракция вида проекта
 */
public interface AbstractProjectPreviewView extends IsWidget {

    void setActivity( AbstractProjectPreviewActivity activity );

    void setName(String value);

    void setHeaderHref(String link);

    void setCreatedBy(String value );

    void setHeader(String name );

    void setState( String value );

    void setStateIconColor( String value );

    void setDirections(String value );

    void setTeam( String value );

    void setDescription( String value );

    void setRegion( String value );

    void setProducts(Map<Long, String> value);

    void setCompany( String value );

    void setCustomerType( String value );

    HasValue<List<ProjectSla>> slaInputReadOnly();

    HasVisibility slaContainerVisibility();

    void setContracts(Map<EntityOption, String> contractToLink);

    void setPlatforms(Map<EntityOption, String> platformToLink);

    void isFullScreen(boolean isFullScreen);

    void setTechnicalSupportValidity(String value);

    void setWorkCompletionDate(String value);

    void setPurchaseDate(String value);

    void setPauseDateValidity(String value);

    HasVisibility backButtonVisibility();

    HasWidgets getCommentsContainer();
    HasWidgets getDocumentsContainer();
    HasWidgets getLinksContainer();

    void setTechnicalSupportValidityVisible(boolean isVisible);

    void setWorkCompletionDateLabelVisible(boolean isVisible);

    void setPurchaseDateLabelVisible(boolean isVisible);

    void setSubcontractors(String value);
}
