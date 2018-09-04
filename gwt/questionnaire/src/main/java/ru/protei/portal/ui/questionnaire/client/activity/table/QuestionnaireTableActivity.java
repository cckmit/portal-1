package ru.protei.portal.ui.questionnaire.client.activity.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.core.model.query.QuestionnaireQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.QuestionnaireEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.QuestionnaireControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.questionnaire.client.activity.filter.AbstractQuestionnaireFilterActivity;
import ru.protei.portal.ui.questionnaire.client.activity.filter.AbstractQuestionnaireFilterView;

import java.util.List;

public abstract class QuestionnaireTableActivity implements AbstractQuestionnaireTableActivity,
        AbstractQuestionnaireFilterActivity, Activity {

    @PostConstruct
    public void init() {
        view.setActivity(this);
        view.setAnimation(animation);
        filterView.setActivity(this);
        view.getFilterContainer().add(filterView.asWidget());
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(QuestionnaireEvents.Show event) {
        init.parent.clear();
        init.parent.add(view.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.QUESTIONNAIRE_CREATE) ?
                new ActionBarEvents.Add(lang.buttonCreate(), UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.QUESTIONNAIRE) :
                new ActionBarEvents.Clear()
        );

        filterView.resetFilter();
        requestQuestionnairesCount(makeQuery());
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.QUESTIONNAIRE.equals(event.identity)) {
            return;
        }
        fireEvent(new QuestionnaireEvents.Create());
    }

    @Override
    public void onItemClicked(Questionnaire value) {
        showPreview(value);
    }

    @Override
    public void onFilterChanged() {
        requestQuestionnairesCount(makeQuery());
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Questionnaire>> asyncCallback) {
        QuestionnaireQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        questionnaireService.getQuestionnaires(query, new RequestCallback<List<Questionnaire>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<Questionnaire> questionnaires) {
                asyncCallback.onSuccess(questionnaires);
            }
        });

    }

    private QuestionnaireQuery makeQuery() {
        QuestionnaireQuery query = new QuestionnaireQuery();
        query.searchString = filterView.searchString().getValue();
        query.setCreatedFrom(filterView.dateRange().getValue().from);
        query.setCreatedTo(filterView.dateRange().getValue().to);
        query.setStates(filterView.states().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(filterView.sortField().getValue());
        return query;
    }

    private void showPreview(Questionnaire value) {

        if (value == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new QuestionnaireEvents.ShowPreview(view.getPreviewContainer(), value));
        }
    }

    private void requestQuestionnairesCount(QuestionnaireQuery query) {
        view.clearRecords();
        animation.closeDetails();

        questionnaireService.getQuestionnaireCount(query, new RequestCallback<Integer>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Integer count) {
                view.setRecordCount(count);
            }
        });
    }


    @Inject
    private Lang lang;
    @Inject
    private TableAnimation animation;
    @Inject
    private PolicyService policyService;
    @Inject
    private AbstractQuestionnaireTableView view;
    @Inject
    private AbstractQuestionnaireFilterView filterView;
    @Inject
    private QuestionnaireControllerAsync questionnaireService;

    private AppEvents.InitDetails init;
}
