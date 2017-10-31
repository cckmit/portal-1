package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Company;

/**
 * События по компаниям
 */
public class CompanyEvents {

    /**
     * Показать grid компаний
     */
    @Url( value = "companies", primary = true )
    public static class Show {

        public Show () {}

    }

    /**
     * Показать превью компании
     */
    public static class ShowPreview {

        public ShowPreview( HasWidgets parent, Company company, boolean isWatchForScroll ) {
            this.parent = parent;
            this.company = company;
            this.isWatchForScroll = isWatchForScroll;
        }

        public HasWidgets parent;
        public Company company;
        public boolean isWatchForScroll;
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

    public static class ChangeCompany{
        public long companyId;

        public ChangeCompany(long companyId) {
            this.companyId = companyId;
        }
    }

    /**
     * Обновление списка компаний
     */
    public static class ChangeModel {}

}
