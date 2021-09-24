package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Card;

public class CardEvents {

    @Url( value = "cards", primary = true )
    public static class Show {
        public Show () {}
        public Show (Boolean preScroll) {
            this.preScroll = preScroll;
        }

        @Omit
        public Boolean preScroll = false;
    }

    public static class Create {
        public Create(HasWidgets parent, Long batchId, Runnable closeHandle) {
            this.parent = parent;
            this.batchId = batchId;
            this.closeHandle = closeHandle;
        }

        public HasWidgets parent;
        public Long batchId;
        public Runnable closeHandle;
    }

    public static class Edit {
        public Edit() {}

        public Edit(Long id) {
            this.id = id;
        }

        public Long id;
    }

    public static class EditMeta {
        public EditMeta(HasWidgets parent, Card card, boolean isReadOnly) {
            this.parent = parent;
            this.card = card;
            this.isReadOnly = isReadOnly;
        }

        public Card card;
        public HasWidgets parent;
        public boolean isReadOnly;
    }

    public static class Change {
        public Change(Long id) {
            this.id = id;
        }

        public Long id;
    }
}
