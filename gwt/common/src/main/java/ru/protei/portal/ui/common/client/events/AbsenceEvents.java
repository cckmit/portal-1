package ru.protei.portal.ui.common.client.events;

public class AbsenceEvents {

    public static class Edit {

        public Long id;

        public Edit() { this.id = null; }
        public Edit (Long id) {
            this.id = id;
        }
    }
}
