package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Attachment;

public interface AbstractIssueEditActivity {

    void removeAttachment( Attachment attachment );

    void onNameAndDescriptionEditClicked();

    void onCopyNumberClicked();

    void onCopyNumberAndName();

    void onOpenEditViewClicked();

    void onAddTagClicked(IsWidget anchor);

    void onAddLinkClicked(IsWidget anchor);

    void onBackClicked();
}
