package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.user.client.ui.IsWidget;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.Attachment;

public interface AbstractIssueEditActivity extends Activity {

    void removeAttachment(Attachment attachment );

    void onNameAndDescriptionEditClicked();

    void onOpenEditViewClicked();

    void onAddTagClicked(IsWidget target);

    void onAddLinkClicked(IsWidget target);

    void onBackClicked();

    void onCopyNumberClicked();

    void onCopyNumberAndNameClicked();

    void onFavoriteStateChanged();

    void onCreateSubtaskClicked();
}
