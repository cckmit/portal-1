package ru.protei.portal.ui.common.client.widget.selector.casetag;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.CaseTagControllerAsync;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class CaseTagModel extends BaseSelectorModel<CaseTag> implements Activity
//        , SelectorModel<EntityOption>
{

    @Event
    public void onChangeModel(CaseTagEvents.ChangeModel event) {
//        refreshOptions();
        clean();
    }

//    public void subscribe(SelectorWithModel<EntityOption> selector, En_CaseType caseType) {
//        if (!subscribersMap.containsKey(caseType)) {
//            subscribersMap.put(caseType, new ArrayList<>());
//            subscribersMap.get(caseType).add(selector);
//            refreshOptionsForCaseType(caseType);
//        } else {
//            subscribersMap.get(caseType).add(selector);
//            selector.fillOptions(valuesMap.get(caseType));
//        }
//    }
//
//    @Override
//    public void onSelectorLoad(SelectorWithModel<EntityOption> selector) {
//        if (selector == null) {
//            return;
//        }
//        if (CollectionUtils.isEmpty(selector.getValues())) {
//            refreshOptions();
//        }
//    }
//
//    @Override
//    public void onSelectorUnload( SelectorWithModel<EntityOption> selector ) {
//        if ( selector == null ) {
//            return;
//        }
//        selector.clearOptions();
//    }
    public void setCaseType(En_CaseType caseType) {
        this.caseType = caseType;
    }

    @Override
    protected void requestData( LoadingHandler selector, String searchText) {
        caseTagController.getTags(new CaseTagQuery(caseType), new FluentCallback<List<CaseTag>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(caseTags -> {
//                    valuesMap.put(caseType, caseTags.stream()
//                            .map(tag -> IssueFilterUtils.toEntityOption(tag, policyService.hasSystemScopeForPrivilege( En_Privilege.ISSUE_VIEW )))
//                            .collect(Collectors.toList()));
//                    notifySubscribers(caseType);
                    updateElements( caseTags, selector );
                })
        );
    }

    public void fillByIds( List<Long> tagIds, Consumer<List<CaseTag>> consumer ) {
        CaseTagQuery caseTagQuery = new CaseTagQuery();
        caseTagQuery.setIds( tagIds );
        caseTagController.getTags( caseTagQuery, new FluentCallback<List<CaseTag>>()
                .withError( throwable -> fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) ) )
                .withSuccess( caseTags -> {
                    consumer.accept( caseTags );
                } )
        );
    }

//    private void refreshOptions() {
//        subscribersMap.forEach((caseType, subscribers) -> refreshOptionsForCaseType(caseType));
//    }
//
//    private void notifySubscribers(En_CaseType caseType) {
//        List<SelectorWithModel<EntityOption>> subscribers = subscribersMap.get(caseType);
//        List<EntityOption> values = valuesMap.get(caseType);
//        for (SelectorWithModel<EntityOption> selector : subscribers) {
//            selector.fillOptions(values);
//            selector.refreshValue();
//        }
//    }

    @Inject
    CaseTagControllerAsync caseTagController;
    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;
    private En_CaseType caseType;

//    private Map<En_CaseType, List<SelectorWithModel<EntityOption>>> subscribersMap = new HashMap<>();
//    private Map<En_CaseType, List<EntityOption>> valuesMap = new HashMap<>();
}
