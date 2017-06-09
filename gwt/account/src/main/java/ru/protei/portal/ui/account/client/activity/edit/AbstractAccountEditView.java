package ru.protei.portal.ui.account.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_UserRole;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Set;

/**
 * Представление создания и редактирования учетной записи
 */
public interface AbstractAccountEditView extends IsWidget {

    void setActivity( AbstractAccountEditActivity activity );

    HasValue< String > login();

    HasValue< EntityOption > company();

    HasValue< PersonShortView > person();

    HasText password();

    HasText confirmPassword();

    HasValue< Set< UserRole > > roles();

    HasValidable loginValidator();

    HasValidable personValidator();

    void showInfo( boolean isShow );

    void enabledFields( boolean isEnabled );

    void enabledPassword( boolean isEnabled );

    void setLoginStatus( NameStatus status );

    void changeCompany( Company company );
}
