package ru.protei.portal.ui.common.client.activity.ytwork.table;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.ytwork.dialog.AbstractYoutrackWorkDictionaryDialogView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.YoutrackWorkLang;
import ru.protei.portal.ui.common.client.service.YoutrackWorkDictionaryControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class YoutrackWorkDictionaryTableActivity implements
        AbstractYoutrackWorkDictionaryTableActivity, Activity {
    private En_YoutrackWorkType type;
    private AbstractYoutrackWorkDictionaryTableView table;

    @Inject
    public void onInit() {
        dialogDetailView.getBodyContainer().add(dialogView.asWidget());
        dialogDetailView.setActivity(new YoutrackReportDictionaryDialogDetailsActivity());
    }

    public void setTypeAndTable(En_YoutrackWorkType type, AbstractYoutrackWorkDictionaryTableView table) {
        this.type = type;
        this.table = table;
    }

    @Override
    public void refreshTable() {
        loadTable();
    }

    @Override
    public void onAddClicked() {
        dialogDetailView.setHeader(lang.reportYoutrackWorkDictionaryCreate() + ": " + youtrackWorkLang.getTypeName(type));
        dialogView.refreshProjects();

        dialogDictionaryId = null;
        dialogView.name().setValue(null);
        dialogView.projects().setValue(null);

        dialogDetailView.showPopup();
    }

    @Override
    public void onEditClicked(YoutrackWorkDictionary value) {
        dialogDetailView.setHeader(lang.reportYoutrackWorkDictionaryEdit() + ": " + youtrackWorkLang.getTypeName(type));
        dialogView.refreshProjects();

        dialogDictionaryId = value.getId();
        dialogView.name().setValue(value.getName());
        dialogView.projects().setValue(new HashSet<>(value.getYoutrackProjects()));

        dialogDetailView.showPopup();
    }

    @Override
    public void onRemoveClicked(YoutrackWorkDictionary dictionary) {
        controller.removeDictionary(dictionary, new FluentCallback<YoutrackWorkDictionary>()
                .withError(defaultErrorHandler)
                .withSuccess(d -> loadTable()));    }

    @Override
    public void onCollapseClicked(boolean isCollapsed) {
        table.setCollapsed(isCollapsed);
    }

    private void loadTable() {
        table.showLoader(true);
        table.clearRecords();
        table.hideTableOverflow();
        controller.getDictionaries(type, new FluentCallback<List<YoutrackWorkDictionary>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    table.showLoader(false);
                    table.setTotalRecords(0);
                    table.hideTableOverflow();
                })
                .withSuccess(list -> {
                    table.showLoader(false);
                    table.setTotalRecords(list.size());
                    table.putRecords(list);
                    if (list.size() > TABLE_LIMIT) {
                        table.showTableOverflow(TABLE_LIMIT);
                    }
                }));
    }

    public class YoutrackReportDictionaryDialogDetailsActivity implements AbstractDialogDetailsActivity {
        @Inject
        public void onInit() {
            dialogDetailView.getBodyContainer().add(dialogView.asWidget());
            dialogDetailView.setActivity(this);
        }

        @Override
        public void onSaveClicked() {
            if (!isValid()) {
                fireEvent(new NotifyEvents.Show(lang.errValidationError(), NotifyEvents.NotifyType.ERROR));
                return;
            }
            YoutrackWorkDictionary dictionary = new YoutrackWorkDictionary();
            dictionary.setName(dialogView.name().getValue());
            dictionary.setType(type);
            dictionary.setYoutrackProjects(new ArrayList<>(dialogView.projects().getValue()));
            dictionary.setId(dialogDictionaryId);
            if (dictionary.getId() == null) {
                performSave(controller::createDictionary, dictionary);
            } else {
                performSave(controller::updateDictionary, dictionary);
            }
            dialogDetailView.hidePopup();
        }

        @Override
        public void onCancelClicked() {
            dialogDetailView.hidePopup();
        }

        private void performSave(BiConsumer<YoutrackWorkDictionary, AsyncCallback<YoutrackWorkDictionary>> consumer, YoutrackWorkDictionary dictionary) {
            consumer.accept(dictionary, new FluentCallback<YoutrackWorkDictionary>()
                    .withError(defaultErrorHandler)
                    .withSuccess(d -> loadTable()));
        }
    }

    private boolean isValid() {
        return dialogView.isValidName();
    }

    Long dialogDictionaryId;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    Lang lang;
    @Inject
    YoutrackWorkLang youtrackWorkLang;
    @Inject
    AbstractDialogDetailsView dialogDetailView;
    @Inject
    AbstractYoutrackWorkDictionaryDialogView dialogView;
    @Inject
    YoutrackWorkDictionaryControllerAsync controller;

    private final static int TABLE_LIMIT = 50;
}
