package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.OfficialMember;

/**
 * Created by serebryakov on 21/08/17.
 */
public class OfficialMemberEvents {

    @Url( value = "officials", primary = true )
    public static class Show {

        public Show () {}

    }

    public static class ShowPreview {
        public HasWidgets parent;
        public Long id;

        public ShowPreview(HasWidgets previewContainer, Long id) {
            this.parent = previewContainer;
            this.id = id;
        }
    }

    @Url( value = "member", primary = false )
    public static class Edit {
        public Long id;

        public Edit() {
            this.id = null;
        }

        public Edit(Long id) {
            this.id = id;
        }
    }

    public static class ReloadPreview {
    }
}
