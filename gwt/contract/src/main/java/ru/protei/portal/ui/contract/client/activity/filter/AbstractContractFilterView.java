package ru.protei.portal.ui.contract.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;
import java.util.Set;

public interface AbstractContractFilterView extends IsWidget {

    void setActivity(AbstractContractFilterActivity activity);

    void resetFilter();

    HasValue<String> searchString();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<Set<PersonShortView>> managers();

    HasValue<Set<EntityOption>> contragents();

    HasValue<Set<EntityOption>> organizations();

    HasValue<En_ContractType> type();

    HasValue<En_ContractState> state();

    HasValue<ProductDirectionInfo> direction();
}
