package ru.protei.portal.ui.common.client.activity.filter;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

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

        filterService.saveIssueFilter(filledUserFilter, new FluentCallback<CaseFilter>()
                .withError((throwable, defaultErrorHandler, status) -> {
                    fireEvent(new NotifyEvents.Show(lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR));
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(filter -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.ChangeUserFilterModel());

                    afterSave.accept(filter);
                })
        );
    }

    @Override
    public void onUserFilterChanged(Long id, Consumer<CaseFilter> afterChange) {
        filterService.getIssueFilter(id, new FluentCallback<CaseFilter>()
                .withErrorMessage(lang.errNotFound())
                .withSuccess(afterChange)
        );
    }

    private Runnable removeAction(Long filterId, Runnable afterRemove) {
        return () -> filterService.removeIssueFilter(filterId, new FluentCallback<Long>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.issueFilterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.ChangeUserFilterModel());
                    afterRemove.run();
                }));
    }

    @Inject
    Lang lang;
    @Inject
    IssueFilterControllerAsync filterService;
}
