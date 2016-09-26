package ru.protei.portal.ui.crm.client.event;

/**
 * Событие для App
 */
public class AuthEvents {

    /**
     *   Отобразить App
     */
    public static class Success {

        public Success (String username)
        {
            this.username = username;
        }

        public String username;

    }
}
