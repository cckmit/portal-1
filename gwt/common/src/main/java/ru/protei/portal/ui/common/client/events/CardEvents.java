package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Card;

import java.util.Set;

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

    @Url( value = "card_create")
    public static class Create {
        public Create() {}
    }

    @Url( value = "card")
    public static class Edit {
        public Edit() {}
        public Edit(Long id) {
            this.id = id;
        }

        public Long id;
    }

    public static class GroupEdit {
        public GroupEdit(Set<Card> selectedCards) {
            this.selectedCards = selectedCards;
        }

        public Set<Card> selectedCards;
    }

    public static class ShowPreview {
        public ShowPreview (HasWidgets parent, Long id) {
            this.parent = parent;
            this.id = id;
        }

        public Long id;
        public HasWidgets parent;
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

    public static class GroupChanged {
    }
}
