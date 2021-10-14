package ru.protei.portal.ui.delivery.client.activity.card.edit.group;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;

public interface AbstractCardGroupEditView extends IsWidget {
    HasValue<CaseState> state();

    HasValue<String> article();

    HasValue<PersonShortView> manager();

    HasValue<Date> testDate();

    HasValue<String> note();

    HasValue<String> comment();

    boolean articleIsValid();

    void setTestDateValid(boolean isValid);
}
