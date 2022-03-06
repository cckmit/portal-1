package ru.protei.portal.ui.common.client.activity.caselink;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_BundleType;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.UitsIssueInfo;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.ui.common.client.service.CaseLinkControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Collections;
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

    public static String getLink(En_CaseLink caseLink, String id) {
        if (linkMap == null || !linkMap.containsKey(caseLink) || id == null) {
            return "";
        }
        return linkMap.get(caseLink).replace("%id%", id);
    }

    public void getCrmLinkInfo(Long caseNumber, AsyncCallback<CaseInfo> async) {
        caseService.getIssueShortInfo(caseNumber, async);
    }

    public void getYTLinkInfo(String ytId, AsyncCallback<YouTrackIssueInfo> async ) {
        caseLinkService.getYtLinkInfo( ytId, async );
    }

    public void getUitsLinkInfo(Long uitsId, AsyncCallback<UitsIssueInfo> async ) {
        caseLinkService.getUitsLinkInfo( uitsId, async );
    }

    public static String getLink(Long caseLinkId) {

        CaseLink caseLink = idToCaseLinkMap.get(caseLinkId);
        if (caseLink == null){
            return null;
        }
        return getLink(caseLink.getType(), caseLink.getRemoteId());
    }

    public static En_BundleType getBundleType(Long caseLinkId) {

        CaseLink caseLink = idToCaseLinkMap.get(caseLinkId);
        if (caseLink == null){
            return null;
        }
        return caseLink.getBundleType();
    }

    public static void setCaseLinkMap(Map<Long, CaseLink> idToCaseLinkMap) {
        CaseLinkProvider.idToCaseLinkMap = idToCaseLinkMap;
    }

    @Inject
    CaseLinkControllerAsync caseLinkService;

    @Inject
    IssueControllerAsync caseService;

    private static Map<En_CaseLink, String> linkMap = null;

    private static Map<Long, CaseLink> idToCaseLinkMap = Collections.emptyMap();
}
