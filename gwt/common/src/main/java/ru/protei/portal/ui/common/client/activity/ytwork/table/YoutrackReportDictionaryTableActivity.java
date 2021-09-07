package ru.protei.portal.ui.common.client.activity.ytwork.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;
import ru.protei.portal.core.model.ent.YoutrackReportDictionary;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.ytwork.dialog.AbstractYoutrackReportDictionaryDialogView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.YoutrackReportDictionaryControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class YoutrackReportDictionaryTableActivity implements
                            AbstractYoutrackReportDictionaryTableActivity, Activity {
    private En_ReportYoutrackWorkType type;
    private AbstractYoutrackReportDictionaryTableView table;

    @Inject
    public void onInit() {
        dialogDetailView.getBodyContainer().add(dialogView.asWidget());
        dialogDetailView.setActivity(new YoutrackReportDictionaryDialogDetailsActivity());
    }

    public void setTypeAndTable(En_ReportYoutrackWorkType type, AbstractYoutrackReportDictionaryTableView table) {
        this.type = type;
        this.table = table;
    }

    @Override
    public void onShow() {
        loadTable();
    }

    @Override
    public void onAddClicked() {
        dialogDetailView.setHeader(lang.reportYoutrackWorkDictionaryCreate());
        dialogView.refreshProjects();

        dialogDictionaryId = null;
        dialogView.name().setValue(null);
        dialogView.type().setValue(type);
        dialogView.projects().setValue(null);

        dialogDetailView.showPopup();
    }

    @Override
    public void onEditClicked(YoutrackReportDictionary value) {
        dialogDetailView.setHeader(lang.reportYoutrackWorkDictionaryEdit());
        dialogView.refreshProjects();

        dialogDictionaryId = value.getId();
        dialogView.name().setValue(value.getName());
        dialogView.type().setValue(value.getDictionaryType());
        dialogView.projects().setValue(new HashSet<>(value.getYoutrackProjects()));

        dialogDetailView.showPopup();
    }

    @Override
    public void onRemoveClicked(YoutrackReportDictionary value) {
        controller.removeDictionary(value, new FluentCallback<YoutrackReportDictionary>()
                .withError(e -> defaultErrorHandler.accept(e))
                .withSuccess(d -> loadTable()));    }

    @Override
    public void onCollapseClicked(boolean isCollapsed) {
        table.setCollapsed(isCollapsed);
    }

    private void loadTable() {
        table.showLoader(true);
        table.clearRecords();
        table.hideTableOverflow();
        controller.getDictionaries(type, new FluentCallback<List<YoutrackReportDictionary>>()
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
            YoutrackReportDictionary dictionary = new YoutrackReportDictionary();
            dictionary.setName(dialogView.name().getValue());
            dictionary.setDictionaryType(dialogView.type().getValue());
            dictionary.setYoutrackProjects(new ArrayList<>(dialogView.projects().getValue()));
            dictionary.setId(dialogDictionaryId);
            if (dictionary.getId() == null) {
                controller.createDictionary(dictionary, new FluentCallback<YoutrackReportDictionary>()
                        .withError(throwable -> {
                            defaultErrorHandler.accept(throwable);
                            fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                        })
                        .withSuccess(d -> loadTable()));
            } else {
                controller.updateDictionary(dictionary, new FluentCallback<YoutrackReportDictionary>()
                        .withError(throwable -> {
                            defaultErrorHandler.accept(throwable);
                            fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                        })
                        .withSuccess(d -> loadTable()));
            }
            dialogDetailView.hidePopup();
        }

        @Override
        public void onCancelClicked() {
            dialogDetailView.hidePopup();
        }
    }

    Long dialogDictionaryId;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    Lang lang;
    @Inject
    AbstractDialogDetailsView dialogDetailView;
    @Inject
    AbstractYoutrackReportDictionaryDialogView dialogView;
    @Inject
    YoutrackReportDictionaryControllerAsync controller;

    private final static int TABLE_LIMIT = 50;
}
