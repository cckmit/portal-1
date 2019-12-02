package ru.protei.portal.ui.project.client.activity.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.DocumentEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.project.client.activity.list.item.AbstractProjectDocumentsListItemActivity;
import ru.protei.portal.ui.project.client.activity.list.item.AbstractProjectDocumentsListItemView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class ProjectDocumentsListActivity implements Activity, AbstractProjectDocumentsListItemActivity {

    private static final String DOWNLOAD_PATH = "springApi/document/";

    @PostConstruct
    public void init() { ; }

    @Event
    public void onShow(ProjectEvents.ShowProjectDocuments event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        isModifyEnabled = event.isModifyEnabled;

        view.documentsContainer().clear();

        if (event.projectId == null) {
            handleDocuments(new ArrayList<>());
            return;
        }
        documentController.getProjectDocuments(event.projectId, new FluentCallback<SearchResult<Document>>()
                .withSuccess(documents -> handleDocuments(documents.getResults())));
    }


    @Override
    public void onEditClicked(AbstractProjectDocumentsListItemView itemView) {

        if (!policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_EDIT)) {
            return;
        }

        Document value = itemViewToModel.get(itemView);

        if (value == null || value.getId() == null) {
            return;
        }

        fireEvent(new DocumentEvents.Edit(value.getId()));
    }

    @Override
    public void onDownloadClicked(AbstractProjectDocumentsListItemView itemView) {

        Document value = itemViewToModel.get(itemView);

        if (value == null || value.getId() == null || value.getProjectId() == null) {
            return;
        }

        Window.open(GWT.getModuleBaseURL() + DOWNLOAD_PATH + value.getProjectId() + "/" + value.getId(), value.getName(), "");
    }

    private void handleDocuments(List<Document> documents) {
        documents.stream()
                .filter(document -> document.getType() != null)
                .filter(document -> document.getType().getDocumentCategory() != null)
                .forEach(fillViewer);
    }

    private AbstractProjectDocumentsListItemView makeItemView(Document document) {
        AbstractProjectDocumentsListItemView itemView = itemFactory.get();
        itemView.setActivity(this);
        itemView.setApproved(document.getApproved());
        itemView.setDecimalNumber(document.getDecimalNumber());
        StringBuilder infoBuilder = new StringBuilder();
        if ( document.getInventoryNumber() != null ) {
            infoBuilder.append(document.getInventoryNumber()).append(" ");
        }
        infoBuilder.append(document.getName())
                .append(", ")
                .append(document.getType().getName().toLowerCase())
                .append(" ")
                .append(lang.from().toLowerCase())
                .append(" ")
                .append(DateFormatter.formatDateOnly(document.getCreated()));

        itemView.setInfo(infoBuilder.toString());
        itemView.setEditVisible(policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_EDIT) && isModifyEnabled);
        return itemView;
    }

    private void putToContainer(AbstractProjectDocumentsListItemView itemView) {
        view.documentsContainer().add(itemView.asWidget());
    }

    @Inject
    Lang lang;
    @Inject
    DocumentControllerAsync documentController;
    @Inject
    PolicyService policyService;
    @Inject
    AbstractProjectDocumentsListView view;
    @Inject
    Provider<AbstractProjectDocumentsListItemView> itemFactory;

    private boolean isModifyEnabled;
    private Map<AbstractProjectDocumentsListItemView, Document> itemViewToModel = new HashMap<>();
    private Consumer<Document> fillViewer = document -> {
        AbstractProjectDocumentsListItemView itemView = makeItemView(document);
        itemViewToModel.put(itemView, document);
        putToContainer(itemView);
    };
}
