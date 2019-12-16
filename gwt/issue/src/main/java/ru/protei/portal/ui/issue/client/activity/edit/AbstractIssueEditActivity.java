package ru.protei.portal.ui.issue.client.activity.edit;

import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseObjectMeta;

import java.util.function.Consumer;

public interface AbstractIssueEditActivity {

    void removeAttachment(Attachment attachment);
    void renderMarkupText(String text, Consumer<String> consumer);
    void onDisplayPreviewChanged( String description, boolean isDisplay );
    void onCaseMetaChanged( CaseObjectMeta value );
    void onCopyNumberClicked();
    void onEditNameAndDescriptionClicked();
    void onSaveNameAndDescriptionClicked();
    void onCopyNumberAndNameClicked();
    void onBackClicked();
}
