package ru.protei.portal.ui.issue.client.activity.edit;

import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.ui.common.client.widget.casemeta.model.CaseMeta;

import java.util.function.Consumer;

/**
 * Created by bondarenko on 11.11.16.
 */
public interface AbstractIssueEditActivity {

    void onSaveClicked();
    void onCancelClicked();
    void removeAttachment(Attachment attachment);
    void onCompanyChanged();
    void onCreateContactClicked();
    void onLocalClicked();
    void renderMarkupText(String text, Consumer<String> consumer);

    void onDisplayPreviewChanged( String description, boolean isDisplay );

    void onCopyClicked();

    void onCaseMetaChanged( CaseMeta value );
}
