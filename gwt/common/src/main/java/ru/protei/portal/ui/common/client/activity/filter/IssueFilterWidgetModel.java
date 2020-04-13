package ru.protei.portal.ui.common.client.activity.filter;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.function.Consumer;
import java.util.function.Function;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public abstract class IssueFilterWidgetModel implements Activity, AbstractIssueFilterWidgetModel {

    @Override
    public void onRemoveClicked(Long id, Runnable afterRemove) {
        fireEvent(new ConfirmDialogEvents.Show(lang.issueFilterRemoveConfirmMessage(), removeAction(id, afterRemove)));
    }

    @Override
    public void onOkSavingFilterClicked(String filterName, CaseFilter filledUserFilter,
                                        Consumer<CaseFilter> afterSave) {
        if (isEmpty(filterName)) {
            fireEvent(new NotifyEvents.Show(lang.errFilterNameRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        if (additionalValidate != null && !additionalValidate.apply(filledUserFilter)) {
            return;
        }

        filterService.saveIssueFilter(filledUserFilter, new RequestCallback<CaseFilter>() {
            @Override
            public void onError(Throwable throwable) {
                defaultErrorHandler.accept(throwable);
                fireEvent(new NotifyEvents.Show(lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(CaseFilter filter) {
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new IssueEvents.ChangeUserFilterModel());

                afterSave.accept(filter);
            }
        });
    }

    @Override
    public void onUserFilterChanged(Long id, Consumer<CaseFilter> afterChange) {
        filterService.getIssueFilter(id, new FluentCallback<CaseFilter>()
                .withErrorMessage(lang.errNotFound())
                .withSuccess(caseFilter -> {
                    afterChange.accept(caseFilter);
                })
        );
    }

    @Override
    public void addAdditionalFilterValidate(Function<CaseFilter, Boolean> validate) {
        additionalValidate = validate;
    }

    private Runnable removeAction(Long filterId, Runnable afterRemove) {
        return () -> filterService.removeIssueFilter(filterId, new FluentCallback<Boolean>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errNotRemoved(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(aBoolean -> {
                    fireEvent(new NotifyEvents.Show(lang.issueFilterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.ChangeUserFilterModel());
                    afterRemove.run();
                }));
    }

    @Inject
    Lang lang;
    @Inject
    IssueFilterControllerAsync filterService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    private Function<CaseFilter, Boolean> additionalValidate;
}
