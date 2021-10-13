package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.test.client.DebugIds;

import java.util.Objects;

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
        public Add( String header, String icon, String identity ) {
            this(header, icon, identity, null);
        }
        public Add( String header, String icon, String identity, String debugId ) {
            this.header = header;
            this.icon = icon;
            this.identity = identity == null ? header : identity;
            this.debugId = debugId == null ? DebugIds.ACTION_BAR.CREATE_BUTTON : debugId;
        }

        public String header;
        public String icon;
        public String identity;
        public String debugId;
    }

    /**
     * Выбрано действие
     */
    public static class Clicked {
        public Clicked( String identity ) {
            this.identity = identity;
        }

        public boolean is(String identity) {
            return Objects.equals(this.identity, identity);
        }
        public boolean isNot(String identity) {
            return !is(identity);
        }

        public String identity;
    }

    /**
     * Очистить action bar
     */
    public static class Clear {}

    /**
     * Заблокировать/разблокировать кнопку
     */
    public static class SetButtonEnabled {
        public SetButtonEnabled(String identity, boolean isEnabled) {
            this.identity = identity;
            this.isEnabled = isEnabled;
        }

        public boolean isEnabled;
        public String identity;
    }
}
