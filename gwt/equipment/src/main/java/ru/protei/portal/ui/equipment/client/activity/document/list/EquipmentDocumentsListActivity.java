package ru.protei.portal.ui.equipment.client.activity.document.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.PeriodicTaskService;
import ru.protei.portal.ui.common.client.events.EquipmentEvents;
import ru.protei.portal.ui.common.client.service.EquipmentControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.equipment.client.activity.document.list.item.AbstractEquipmentDocumentsListItemActivity;
import ru.protei.portal.ui.equipment.client.activity.document.list.item.AbstractEquipmentDocumentsListItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class EquipmentDocumentsListActivity implements Activity, AbstractEquipmentDocumentsListActivity, AbstractEquipmentDocumentsListItemActivity {

    private static final String DOWNLOAD_PATH = "springApi/document/";

    @PostConstruct
    public void init() {
        view.setActivity(this);
    }

    @Event
    public void onShow(EquipmentEvents.ShowDocumentList event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        view.kdDocumentsContainer().clear();
        view.edDocumentsContainer().clear();
        view.tdDocumentsContainer().clear();
        view.pdDocumentsContainer().clear();

        stopPeriodicTask(kdFillViewHandler);
        stopPeriodicTask(edFillViewHandler);
        stopPeriodicTask(tdFillViewHandler);
        stopPeriodicTask(pdFillViewHandler);

        if (event.equipmentId == null) {
            handleDocuments(new ArrayList<>());
            return;
        }

        equipmentController.getDocuments(event.equipmentId, new FluentCallback<List<Document>>().withSuccess((result, m) -> handleDocuments(result)));
    }

    @Override
    public void onEditClicked(AbstractEquipmentDocumentsListItemView itemView) {

        if (!policyService.hasPrivilegeFor(En_Privilege.EQUIPMENT_EDIT)) {
            return;
        }

        Document value = itemViewToModel.get(itemView);

        if (value == null || value.getId() == null) {
            return;
        }

        fireEvent(new EquipmentEvents.DocumentEdit(value.getId()));
    }

    @Override
    public void onDownloadClicked(AbstractEquipmentDocumentsListItemView itemView) {

        Document value = itemViewToModel.get(itemView);

        if (value == null || value.getId() == null || value.getProjectId() == null) {
            return;
        }

        Window.open(GWT.getModuleBaseURL() + DOWNLOAD_PATH + value.getProjectId() + "/" + value.getId(), value.getName(), "");
    }

    private void handleDocuments(List<Document> documents) {

        documents = documents.stream()
                .filter(document -> document.getType() != null)
                .filter(document -> document.getType().getDocumentCategory() != null)
                .collect(Collectors.toList());

        kdFillViewHandler = startPeriodicTask(documents, En_DocumentCategory.KD);
        edFillViewHandler = startPeriodicTask(documents, En_DocumentCategory.ED);
        tdFillViewHandler = startPeriodicTask(documents, En_DocumentCategory.TD);
        pdFillViewHandler = startPeriodicTask(documents, En_DocumentCategory.PD);
    }

    private PeriodicTaskService.PeriodicTaskHandler startPeriodicTask(List<Document> documents, En_DocumentCategory documentCategory) {
        return taskService.startPeriodicTask(documents.stream()
                        .filter(document -> documentCategory.equals(document.getType().getDocumentCategory()))
                        .collect(Collectors.toList())
                , fillViewer, 50, 50);
    }

    private void stopPeriodicTask(PeriodicTaskService.PeriodicTaskHandler handler) {
        if (handler != null) {
            handler.cancel();
        }
    }

    private AbstractEquipmentDocumentsListItemView makeItemView(Document document) {
        AbstractEquipmentDocumentsListItemView itemView = itemFactory.get();
        itemView.setActivity(this);
        itemView.setApproved(document.getApproved());
        itemView.setDecimalNumber(document.getDecimalNumber());
        itemView.setInfo((document.getInventoryNumber() == null ? "" : document.getInventoryNumber() + " ") + document.getName());
        itemView.setEditVisible(policyService.hasPrivilegeFor(En_Privilege.EQUIPMENT_EDIT));
        return itemView;
    }

    private void putToContainer(Document document, AbstractEquipmentDocumentsListItemView itemView) {
        switch (document.getType().getDocumentCategory()) {
            case KD: view.kdDocumentsContainer().add(itemView.asWidget()); break;
            case ED: view.edDocumentsContainer().add(itemView.asWidget()); break;
            case TD: view.tdDocumentsContainer().add(itemView.asWidget()); break;
            case PD: view.pdDocumentsContainer().add(itemView.asWidget()); break;
        }
    }

    @Inject
    EquipmentControllerAsync equipmentController;
    @Inject
    PeriodicTaskService taskService;
    @Inject
    PolicyService policyService;
    @Inject
    AbstractEquipmentDocumentsListView view;
    @Inject
    Provider<AbstractEquipmentDocumentsListItemView> itemFactory;

    private PeriodicTaskService.PeriodicTaskHandler kdFillViewHandler;
    private PeriodicTaskService.PeriodicTaskHandler edFillViewHandler;
    private PeriodicTaskService.PeriodicTaskHandler tdFillViewHandler;
    private PeriodicTaskService.PeriodicTaskHandler pdFillViewHandler;
    private Map<AbstractEquipmentDocumentsListItemView, Document> itemViewToModel = new HashMap<>();
    private Consumer<Document> fillViewer = document -> {
        AbstractEquipmentDocumentsListItemView itemView = makeItemView(document);
        itemViewToModel.put(itemView, document);
        putToContainer(document, itemView);
    };
}
