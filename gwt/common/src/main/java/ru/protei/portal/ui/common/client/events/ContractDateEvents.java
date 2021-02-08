package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.struct.Money;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

public class ContractDateEvents {
    public static class Init {
        public Init(Supplier<Money> contractCostSupplier, Supplier<Date> dateSignedSupplier) {
            this.contractCostSupplier = contractCostSupplier;
            this.dateSignedSupplier = dateSignedSupplier;
        }

        public Supplier<Money> contractCostSupplier;
        public Supplier<Date> dateSignedSupplier;
    }
    public static class ShowTable {
        public ShowTable(HasWidgets parent, List<ContractDate> contractDates) {
            this.parent = parent;
            this.contractDates = contractDates;
        }

        public HasWidgets parent;
        public List<ContractDate> contractDates;
    }

    public static class ShowEdit {
        public ShowEdit(ContractDate value) {
            this.value = value;
        }

        public ContractDate value;
    }

    public static class Refresh {}

    public static class Removed {
        public Removed(ContractDate value) {
            this.value = value;
        }

        public ContractDate value;
    }
}
