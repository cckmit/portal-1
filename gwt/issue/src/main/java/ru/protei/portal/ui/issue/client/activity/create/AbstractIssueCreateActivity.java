package ru.protei.portal.ui.issue.client.activity.create;

import ru.protei.portal.core.model.ent.Attachment;

import java.util.function.Consumer;

public interface AbstractIssueCreateActivity {
    void onSaveClicked();

    void onCancelClicked();

    void removeAttachment(Attachment attachment);

    void onLocalClicked();

    void renderMarkupText(String text, Consumer<String> consumer);

    void onDisplayPreviewChanged(String description, boolean isDisplay);
}
