package ru.protei.portal.ui.contract.client.activity.date.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.ContractCostType;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.core.model.struct.Money;
import ru.protei.portal.core.model.struct.MoneyWithCurrency;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemView;

import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;

public interface AbstractContractDateEditView extends IsWidget {

    void setActivity(AbstractContractDateEditActivity activity);

    HasValue<En_ContractDatesType> type();

    HasValue<Date> date();

    HasValue<Boolean> notifyFlag();

    HasEnabled notifyFlagEnabled();

    HasValue<String> comment();

    HasValue<MoneyWithCurrency> moneyWithCurrency();

    HasEnabled moneyWithCurrencyEnabled();

    HasValue<Double> moneyPercent();

    HasEnabled moneyPercentEnabled();

    void setCostChangeListener(Consumer<Money> onCostChanged);

    void setMoneyValidationFunction(Function<Money, Boolean> validationFunction);

    HasValue<ContractCostType> costType();

    HasEnabled calendarDaysEnabled();
}
