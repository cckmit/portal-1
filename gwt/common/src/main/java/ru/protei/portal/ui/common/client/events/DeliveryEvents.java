package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.DeliveryQuery;

public class DeliveryEvents {

    @Url(value = "deliveries", primary = true)
    public static class Show {
        public Show () {}
        public Show (Boolean preScroll) {
            this.preScroll = preScroll;
        }
        public Show (CaseFilterDto<DeliveryQuery> CaseFilterDto, Boolean preScroll) {
            this.CaseFilterDto = CaseFilterDto;
            this.preScroll = preScroll;
        }

        @Omit
        public Boolean preScroll = false;
        @Omit
        public CaseFilterDto<DeliveryQuery> CaseFilterDto;
    }


    @Url( value = "delivery_create")
    public static class Create {
        public Create() {}
    }

    @Url(value = "delivery")
    public static class Edit {
        public Edit() {}

        public Edit(Long id) {
            this.id = id;
        }

        public Long id;
    }

    public static class Change {
        public Long id;
        public Change(Long deliveryId){
            id = deliveryId;
        }
    }

    public static class ShowPreview {
        public ShowPreview (HasWidgets parent, Long id) {
            this.parent = parent;
            this.id = id;
        }

        public Long id;
        public HasWidgets parent;
    }

    @Url(value = "delivery_preview", primary = true)
    public static class ShowFullScreen {
        public ShowFullScreen() {}

        public ShowFullScreen(Long deliveryId) {
            this.deliveryId = deliveryId;
        }

        @Name("id")
        public Long deliveryId;
    }

    public static class EditMeta {
        public HasWidgets parent;
        public Delivery delivery;
        public CaseObjectMetaNotifiers metaNotifiers;

        public EditMeta(HasWidgets parent, Delivery delivery, CaseObjectMetaNotifiers metaNotifiers) {
            this.parent = parent;
            this.delivery = delivery;
            this.metaNotifiers = metaNotifiers;
        }
    }

    /**
     * Изменилась модель фильтров пользователя
     */
    public static class ChangeUserFilterModel{}
}
