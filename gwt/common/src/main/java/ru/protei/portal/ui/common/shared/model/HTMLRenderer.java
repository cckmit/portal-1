package ru.protei.portal.ui.common.shared.model;

import java.util.function.Consumer;

@FunctionalInterface
public interface HTMLRenderer {
    void render(String text, Consumer<String> consumer);
}
