package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

/**
 * Created by turik on 27.09.16.
 */
public class CompanyEvents {

    @Url( value = "companies", primary = true )
    public static class Show {

        public Show () {}

    }

    @Url( value = "company", primary = false )
    public static class Edit {

        public Edit() {}

        public Long id;
        public Edit (Long id) {
            this.id = id;
        }
        public Long getCompanyId(){
            return id;
        }

    }

    /**
     * Обновление списка компаний
     */
    public static class ChangeModel {}

}
