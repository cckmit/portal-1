package ru.protei.portal.ui.common.client.activity.caselinkprovider;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.core.service.CaseServiceImpl;
import ru.protei.portal.ui.common.client.service.CaseLinkControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Map;

public class CaseLinkProvider {

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

    public void checkExistCrmLink(Long caseNumber, AsyncCallback<CaseInfo> async) {
        caseService.getIssueShortInfo(caseNumber, async);
    }

    @Inject
    CaseLinkControllerAsync caseLinkService;

    @Inject
    IssueControllerAsync caseService;

    private Map<En_CaseLink, String> linkMap = null;
}