package ru.protei.portal.ui.absence.client.activity.common;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractAbsenceCommonView extends IsWidget {
    void setActivity(AbstractAbsenceCommonActivity activity);
    HasValue<PersonShortView> employee();
    HasValue<En_AbsenceReason> reason();
    HasValue<String> comment();
    HasVisibility contentVisibility();
    HasVisibility loadingVisibility();
    HasEnabled employeeEnabled();
    HasEnabled reasonEnabled();
    HasValidable employeeValidator();
    HasValidable reasonValidator();
    HasWidgets getDateContainer();
}
