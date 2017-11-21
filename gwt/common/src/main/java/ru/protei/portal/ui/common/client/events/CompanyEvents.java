package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;

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

    public static class ShowDefinite {
        public ShowDefinite (ViewType type, Widget filter, CompanyQuery query) {
            this.viewType = type;
            this.filter = filter;
            this.query = query;
        }

        public ViewType viewType;
        public Widget filter;
        public CompanyQuery query;
    }

    /**
     * Показать превью компании
     */
    public static class ShowPreview {

        public ShowPreview( HasWidgets parent, Company company, boolean isWatchForScroll, boolean isShouldWrap ) {
            this.parent = parent;
            this.company = company;
            this.isWatchForScroll = isWatchForScroll;
            this.isShouldWrap = isShouldWrap;
        }

        public HasWidgets parent;
        public Company company;
        public boolean isWatchForScroll;
        public boolean isShouldWrap;
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
