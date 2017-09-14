package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

/**
 * Created by serebryakov on 31/08/17.
 */
public class OfficialEvents {

    @Url(value = "official")
    public static class Edit {

        public Long id;

        public Edit() {
            this.id = null;
        }

        public Edit(Long id) {
            this.id = id;
        }
    }
}
