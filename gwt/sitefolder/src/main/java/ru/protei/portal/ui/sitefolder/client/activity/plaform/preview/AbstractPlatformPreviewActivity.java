package ru.protei.portal.ui.sitefolder.client.activity.plaform.preview;

import ru.brainworm.factory.generator.activity.client.activity.Activity;

public interface AbstractPlatformPreviewActivity extends Activity {
    void onOpenServersClicked();

    void onExportServersClicked();

    void onFullScreenClicked();

    void onGoToIssuesClicked();

    void onCopyPreviewLinkClicked();
}
