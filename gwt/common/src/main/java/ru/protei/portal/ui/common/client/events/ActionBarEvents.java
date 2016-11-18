package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.winter.web.common.client.events.SectionEvents;

/**
 * События для action bar'a
 */
public class ActionBarEvents {
    /**
     * Событие init
     */
    public static class Init {
        public Init( HasWidgets parent ) {
            this.parent = parent;
        }
        public HasWidgets parent;
    }

    /**
     * Добавить кнопку
     */
    public static class Add {
        public Add( String header, String icon ) {
            this.header = header;
            this.icon = icon;
        }

        public String header;
        public String icon;
    }

    /**
     * Выбрано действие
     */
    public static class Clicked extends SectionEvents.Clicked {
        public Clicked() {
        }

        public Clicked( String identity ) {
            this.identity = identity;
        }
    }

    /**
     * Очистить action bar
     */
    public static class Clear {}
}
