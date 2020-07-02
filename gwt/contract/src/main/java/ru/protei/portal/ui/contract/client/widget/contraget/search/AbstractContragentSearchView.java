package ru.protei.portal.ui.contract.client.widget.contraget.search;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractContragentSearchView extends IsWidget {
    void setActivity(AbstractContragentSearchActivity activity);

    HasValue<String> contragentINN();

    HasValue<String> contragentKPP();

    HasValue<String> contragentName();

    void setSearchSuccessResult(String name);

    void setSearchFaultResult();

    void reset();
}
