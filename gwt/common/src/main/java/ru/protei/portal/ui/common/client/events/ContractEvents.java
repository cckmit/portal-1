package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;

public class ContractEvents {

    @Url( value = "contracts", primary = true )
    public static class Show {
        @Omit
        public Boolean preScroll = false;
        public Show () {}
        public Show (Boolean preScroll) {
            this.preScroll = preScroll;
        }
    }

    @Url( value = "contract")
    public static class Edit {
        public Edit() {}

        public Edit(Long id) {
            this.id = id;
        }

        public Long id;
    }

    public static class ShowPreview {
        public ShowPreview (HasWidgets parent, Long id) {
            this.parent = parent;
            this.id = id;
        }

        public Long id;
        public HasWidgets parent;
    }

    @Url(value = "contract_preview", primary = true)
    public static class ShowFullScreen {
        public ShowFullScreen() {}

        public ShowFullScreen(Long contractId) {
            this.contractId = contractId;
        }

        @Name("id")
        public Long contractId;
    }

    public static class ShowConciseTable {
        public ShowConciseTable() {}

        public ShowConciseTable(HasWidgets parent, Long parentContractId) {
            this.parent = parent;
            this.parentContractId = parentContractId;
        }

        public HasWidgets parent;
        public Long parentContractId;
    }

    public static class ChangeModel {}
}
