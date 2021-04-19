package ru.protei.portal.ui.sitefolder.client.activity.plaform.edit;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.function.Consumer;

public interface AbstractPlatformEditActivity extends Activity {

    void onSaveClicked();
    void onCancelClicked();
    void onCreateClicked();
    void onCompanySelected();
    void onRemoveAttachment(Attachment attachment);
    void refreshProjectSpecificFields();
    void renderMarkdownText(String text, Consumer<String> consumer);
    void onDisplayCommentPreviewClicked(boolean isDisplay);
}
