package ru.protei.portal.ui.issue.client.view.edit;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;

public interface AbstractIssueNameDescriptionEditWidgetActivity extends Activity {
    void onIssueNameInfoChanged( CaseNameAndDescriptionChangeRequest issue );
}
