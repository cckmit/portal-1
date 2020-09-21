package ru.protei.winter.web.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * События для навигации
 */
public class MenuEvents {
    /**
     * Событие init
     */
    public static class Init {
        public Init(HasWidgets parent) {
            this.parent = parent;
        }
        public HasWidgets parent;
    }

    /**
     * Добавить один раздел навигации
     */
    public static class Add {
        public Add() {
        }

        public Add(String header) {
            this.header = header;
        }

        public Add(String header, String icon) {
            this.header = header;
            this.icon = icon;
        }

        public Add(String header, String icon, String ensureDebugId) {
            this(header, icon, null, ensureDebugId);
        }

        public Add(String header, String icon, String title, String ensureDebugId) {
            this.header = header;
            this.icon = icon;
            this.title = title;
            this.ensureDebugId = ensureDebugId;
        }

        public Add(String header, String icon, String title, String href, String ensureDebugId) {
            this.header = header;
            this.icon = icon;
            this.title = title;
            this.href = href;
            this.ensureDebugId = ensureDebugId;
        }

        public Add withParent(String parent) {
            this.parent = parent;
            return this;
        }

        public String parent;
        public String header;
        public String icon;
        public String title;
        public String href;
        public String ensureDebugId;
    }

    /**
     * Выделить раздел навигации
     */
    public static class Select {
        public Select() {
        }

        public Select(String identity) {
            this.identity = identity;
        }

        public Select(String identity, String... parents) {
            this.identity = identity;
            this.parents = parents;
        }

        public String identity;
        public String[] parents;
    }

    /**
     * Выбран раздел навигации
     */
    public static class Clicked extends SectionEvents.Clicked {
        public Clicked() {
        }

        public Clicked(String identity) {
            this.identity = identity;
        }
    }

    /**
     * Очистить
     */
    public static class Clear {}

    public static class CloseAll {}
}
