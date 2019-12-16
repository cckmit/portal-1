package ru.protei.portal.ui.issue.client.view.edit;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.CaseObject;

public interface AbstractIssueNameWidgetActivity extends Activity {
    void onIssueNameInfoChanged( CaseObject issue );
}
