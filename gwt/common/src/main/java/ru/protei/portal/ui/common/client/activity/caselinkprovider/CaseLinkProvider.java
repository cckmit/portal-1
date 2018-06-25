package ru.protei.portal.ui.common.client.activity.caselinkprovider;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.ui.common.client.service.CaseLinkServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Map;

public abstract class CaseLinkProvider implements Activity {

    @Inject
    public void init() {
        caseLinkService.getLinkMap(new RequestCallback<Map<En_CaseLink, String>>() {
            @Override
            public void onError(Throwable throwable) {}
            @Override
            public void onSuccess(Map<En_CaseLink, String> result) {
                linkMap = result;
            }
        });
    }

    public String getLink(En_CaseLink caseLink, String id) {
        if (linkMap == null || !linkMap.containsKey(caseLink)) {
            return "";
        }
        return linkMap.get(caseLink).replace("%id%", id);
    }

    @Inject
    CaseLinkServiceAsync caseLinkService;

    private Map<En_CaseLink, String> linkMap = null;
}
