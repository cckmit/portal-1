package ru.protei.portal.ui.common.client.widget.filterwidget;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.view.filterwidget.Filter;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;

import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public abstract class FilterWidgetModel<F extends Filter<?, ?>>
        implements Activity, AbstractFilterWidgetModel<F> {

    @Override
    public void onRemoveClicked(Long id, Runnable afterRemove) {
        fireEvent(new ConfirmDialogEvents.Show(lang.filterRemoveConfirmMessage(), removeAction(id, afterRemove)));
    }

    @Override
    public void onOkSavingFilterClicked(String filterName, F filledUserFilter, Consumer<F> afterSave) {
        if (isEmpty(filterName)) {
            fireEvent(new NotifyEvents.Show(lang.errFilterNameRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        saveFilter(filledUserFilter,
                throwable -> {
                    defaultErrorHandler.accept(throwable);
                    fireEvent(new NotifyEvents.Show(lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR));
                }, filter -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    afterSave.accept(filter);
                }
        );
    }

    @Override
    public void onUserFilterChanged(Long id, Consumer<F> afterChange) {
        changeFilter(id, throwable -> lang.errNotFound(), afterChange);
    }

    private Runnable removeAction(Long filterId, Runnable afterRemove) {
        return () -> removeFilter(filterId,
                throwable -> defaultErrorHandler.accept(throwable),
                id -> {
                    fireEvent(new NotifyEvents.Show(lang.filterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                    afterRemove.run();
                }
        );
    }

    abstract protected void saveFilter(F filter, Consumer<Throwable> onError, Consumer<F> onSuccess);
    abstract protected void changeFilter(Long filter, Consumer<Throwable> onError, Consumer<F> onSuccess);
    abstract protected void removeFilter(Long filter, Consumer<Throwable> onError, Consumer<Long> onSuccess);

    @Inject
    Lang lang;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
}
