package ru.protei.portal.ui.issue.client.activity.edit;

import ru.protei.portal.core.model.ent.Attachment;

import java.util.function.Consumer;

public interface AbstractIssueEditActivity {

    void removeAttachment(Attachment attachment);
    void renderMarkupText(String text, Consumer<String> consumer);
    void onCopyNumberClicked();
    void onEditNameAndDescriptionClicked(AbstractIssueDetailsView view);

    void onFullScreenPreviewClicked();
    void onBackClicked();
}
