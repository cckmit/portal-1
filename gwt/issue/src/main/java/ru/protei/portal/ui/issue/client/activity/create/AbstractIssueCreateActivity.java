package ru.protei.portal.ui.issue.client.activity.create;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Attachment;

import java.util.function.Consumer;

public interface AbstractIssueCreateActivity {
    void onSaveClicked();

    void onCancelClicked();

    void onAddTagClicked(IsWidget target);

    void onAddLinkClicked(IsWidget target);

    void removeAttachment(Attachment attachment);

    void onPrivacyChanged();

    void renderMarkupText(String text, Consumer<String> consumer);

    void onDisplayPreviewChanged(String description, boolean isDisplay);

    void onFavoriteStateChanged();
}
