package ru.protei.portal.ui.contract.client.widget.contraget.create;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

public interface AbstractContragentCreateView extends IsWidget, HasValidable {
    HasValue<String> contragentName();

    HasValue<String> contragentFullname();

    HasValue<String> contragentINN();

    HasValue<String> contragentKPP();

    HasValue<String> contragentCountry();

    HasValue<Boolean> contragentResident();

    void reset();

    void setError(String value);
}
