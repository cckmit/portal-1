package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

/**
 * Created by turik on 28.10.16.
 */
public class IssueEvents {

    @Url( value = "issues", primary = true )
    public static class Show {

        public Show () {}

    }


    @Url( value = "issue", primary = false )
    public static class Edit {

        public Long id;
        public Long issueId;

        public Edit() { this.id = null; }
        public Edit (Long id, Long issueId ) {
            this.id = id;
            this.issueId = issueId;
        }

        public static Edit byId (Long id) {
            return new Edit(id, null);
        }

//        public static Edit newItem (EntityOption option) {
//            return new Edit(null, option != null ? option.getId() : null);
//        }
    }

    public static class ChangeStateModel {}
}

