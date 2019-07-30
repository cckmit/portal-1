package ru.protei.portal.ui.project.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

/**
 * Абстракция вида карточки создания/редактирования проекта
 */
public interface AbstractProjectEditView extends IsWidget {

    void setActivity(AbstractProjectEditActivity activity);

    HasValue<Integer> number();
    HasValue<String> name();
    HasText description();
    HasValue<En_RegionState> state();
    HasValue<EntityOption> region();
    HasValue<ProductDirectionInfo> direction();
    HasValue<Set<ProductShortView>> products();
    HasValue<EntityOption> company();
    HasValue<En_CustomerType> customerType();
    HasValue<Set<PersonProjectMemberView>> team();

    HasWidgets getCommentsContainer();
    HasWidgets getDocumentsContainer();

    HasValidable nameValidator();

    HasVisibility numberVisibility();
    HasVisibility saveVisibility();

    HasEnabled saveEnabled();

    void showComments(boolean isShow);
    void showDocuments(boolean isShow);
}