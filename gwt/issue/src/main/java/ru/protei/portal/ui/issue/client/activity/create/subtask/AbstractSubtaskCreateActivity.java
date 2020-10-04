package ru.protei.portal.ui.issue.client.activity.create.subtask;

import java.util.function.Consumer;

public interface AbstractSubtaskCreateActivity {

    void renderMarkupText(String text, Consumer<String> consumer);
    void onDisplayPreviewChanged(String description, boolean isDisplay);
}