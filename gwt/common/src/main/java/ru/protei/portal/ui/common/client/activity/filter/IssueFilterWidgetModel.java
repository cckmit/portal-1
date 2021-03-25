package ru.protei.portal.ui.common.client.activity.filter;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public abstract class IssueFilterWidgetModel implements Activity, AbstractIssueFilterWidgetModel {

    @Override
    public void onRemoveClicked(Long id, Runnable afterRemove) {
        fireEvent(new ConfirmDialogEvents.Show(lang.issueFilterRemoveConfirmMessage(), removeAction(id, afterRemove)));
    }

    @Override
    public void onOkSavingFilterClicked(String filterName, CaseFilterDto<CaseQuery> filledUserFilter,
                                        Consumer<CaseFilterDto<CaseQuery>> afterSave) {
        if (isEmpty(filterName)) {
            fireEvent(new NotifyEvents.Show(lang.errFilterNameRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        filterService.saveCaseFilter(filledUserFilter, new FluentCallback<CaseFilterDto<CaseQuery>>()
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
    public void onUserFilterChanged(Long id, Consumer<CaseFilterDto<CaseQuery>> afterChange) {
        filterService.getCaseFilter(id, new FluentCallback<CaseFilterDto<CaseQuery>>()
                .withErrorMessage(lang.errNotFound())
                .withSuccess(afterChange)
        );
    }

    private Runnable removeAction(Long filterId, Runnable afterRemove) {
        return () -> filterService.removeCaseFilter(filterId, new FluentCallback<Long>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.issueFilterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.ChangeUserFilterModel());
                    afterRemove.run();
                }));
    }

    @Inject
    Lang lang;
    @Inject
    CaseFilterControllerAsync filterService;
}
