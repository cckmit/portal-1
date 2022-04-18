package ru.protei.portal.ui.common.client.activity.contractfilter;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_ContractKind;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;

import java.util.List;
import java.util.Set;

public interface AbstractContractFilterView extends IsWidget {

    void setActivity(AbstractContractFilterActivity activity);

    void resetFilter();

    void clearFooterStyle();

    void initCuratorsSelector(List<String> contractCuratorsDepartmentsIds);

    HasValue<String> searchString();

    HasValue<En_SortField> sortField();

    HasValue<Boolean> sortDir();

    HasValue<Set<PersonShortView>> managers();

    HasValue<Set<Contractor>> contractors();

    HasValue<Set<EntityOption>> organizations();

    HasValue<Set<En_ContractType>> types();

    HasValue<Set<CaseTag>> tags();

    HasValue<Set<En_ContractState>> states();

    HasValue<Set<ProductDirectionInfo>> directions();

    TakesValue<En_ContractKind> kind();

    HasValue<DateIntervalWithType> dateSigningRange();

    HasValue<DateIntervalWithType> dateValidRange();

    HasValue<Set<PersonShortView>> curators();

    HasValue<String> deliveryNumber();
}
