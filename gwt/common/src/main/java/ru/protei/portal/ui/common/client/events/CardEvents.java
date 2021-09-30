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
        public Create(HasWidgets parent, Runnable closeHandle) {
            this.parent = parent;
            this.closeHandle = closeHandle;
        }

        public HasWidgets parent;
        public Runnable closeHandle;
    }

    public static class Edit {
        public Edit(Long cardId, HasWidgets parent, Runnable closeHandle) {
            this.cardId = cardId;
            this.parent = parent;
            this.closeHandle = closeHandle;
        }

        public Long cardId;
        public HasWidgets parent;
        public Runnable closeHandle;
    }

    public static class EditMeta {
        public EditMeta(Card card, HasWidgets parent) {
            this.card = card;
            this.parent = parent;
        }
        public Card card;
        public HasWidgets parent;
    }

    public static class Change {
        public Change(Card card) {
            this.card = card;
        }

        public Card card;
    }
}
