package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.shared.model.Profile;

/**
 * События по дашборду
 */
public class DashboardEvents {

    /**
     * used in {@link ru.protei.portal.ui.common.client.common.UiConstants}
     */
    @Url( value = "dashboard", primary = true )
    public static class Show {

        public Show () {}

    }

    public static class Init{
        public Profile profile;
        public Init(Profile profile){
            this.profile = profile;
        }

    }

    public static class ShowTableBlock{

        public CaseQuery query;
        public HasWidgets parent;
        public boolean isLoaderShow;
        public String sectionName;
        public ShowTableBlock (CaseQuery query, HasWidgets parent, String sectionName) {
            if(query == null || parent == null)
                throw new NullPointerException("query or parent is nullable");

            this.query = query;
            this.parent = parent;
            this.sectionName = sectionName;
        }

        public ShowTableBlock (CaseQuery query, HasWidgets parent, String sectionName, boolean showLoader) {
            this(query, parent, sectionName);
            isLoaderShow = showLoader;
        }

    }

}