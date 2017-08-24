package ru.protei.portal.ui.official.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.ui.common.client.events.OfficialEvents;

/**
 * Created by serebryakov on 23/08/17.
 */
public class OfficialPreviewActivity implements AbstractOfficialPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(OfficialEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget());
        view.showFullScreen(false);
    }

    @Override
    public void onFullScreenClicked() {

    }

    @Inject
    private AbstractOfficialPreviewView view;


}
