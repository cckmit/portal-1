package ru.protei.portal.ui.sitefolder.client.activity.plaform.edit;

import ru.protei.portal.core.model.ent.Attachment;

public interface AbstractPlatformEditActivity {

    void onSaveClicked();
    void onCancelClicked();
    void onOpenClicked();
    void onCreateClicked();
    void onCompanySelected();
    void onRemoveAttachment(Attachment attachment);
}
