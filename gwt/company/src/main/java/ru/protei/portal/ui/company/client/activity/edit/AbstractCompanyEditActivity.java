package ru.protei.portal.ui.company.client.activity.edit;

import com.google.gwt.user.client.ui.HasText;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

/**
 * Created by bondarenko on 21.10.16.
 */
public interface AbstractCompanyEditActivity {
    void onSaveClicked();
    void onCancelClicked();
    void onChangeCompanyName();
    boolean validateFieldAndGetResult(HasValidable validator, HasText field);
}
