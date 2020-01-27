package ru.protei.portal.ui.documenttype.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.query.DocumentTypeQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentTypeControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.documenttype.client.activity.filter.AbstractDocumentTypeFilterActivity;
import ru.protei.portal.ui.documenttype.client.activity.filter.AbstractDocumentTypeFilterView;

import java.util.List;
import java.util.Objects;

public abstract class DocumentTypeTableActivity
        implements AbstractDocumentTypeTableActivity, AbstractDocumentTypeFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity(this);
        view.setAnimation(animation);

        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {}

    @Event
    public void onShow(DocumentTypeEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_TYPE_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_TYPE_CREATE) ?
                new ActionBarEvents.Add(CREATE_ACTION, null, UiConstants.ActionBarIdentity.DOCUMENT_TYPE) :
                new ActionBarEvents.Clear()
        );

        requestDocumentTypes();
    }

    @Event
    public void onClosePreview(DocumentTypeEvents.ClosePreview event) {
        animation.closeDetails();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.DOCUMENT_TYPE.equals(event.identity)) {
            return;
        }

        view.clearSelection();

        animation.showDetails();
        fireEvent(new DocumentTypeEvents.ShowPreview(view.getPreviewContainer(), null));
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onChanged(DocumentTypeEvents.Changed event) {
        if ( event.needRefreshList ) {
            updateListAndSelect(event.doctype);
            return;
        }

        view.updateRow(event.doctype);
    }

    @Event
    public void onConfirmRemove(ConfirmDialogEvents.Confirm event) {
        if (!Objects.equals(event.identity, getClass().getName())) {
            return;
        }

        if (documentTypeToRemove == null) {
            return;
        }

        documentTypeService.removeDocumentType(documentTypeToRemove, new FluentCallback<Long>()
                .withError(throwable -> {
                    documentTypeToRemove = null;

                    if ((throwable instanceof RequestFailedException) && En_ResultStatus.UPDATE_OR_REMOVE_LINKED_OBJECT_ERROR.equals(((RequestFailedException) throwable).status)) {
                        fireEvent(new NotifyEvents.Show(lang.documentTypeUnableToRemoveUsedDocumentType(), NotifyEvents.NotifyType.ERROR));
                    } else {
                        errorHandler.accept(throwable);
                    }
                })
                .withSuccess(result -> {
                    documentTypeToRemove = null;

                    fireEvent(new NotifyEvents.Show(lang.documentTypeRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new DocumentTypeEvents.Show());
                })
        );
    }

    @Event
    public void onCancelRemove(ConfirmDialogEvents.Cancel event) {
        if (!Objects.equals(event.identity, getClass().getName())) {
            return;
        }

        documentTypeToRemove = null;
    }

    @Override
    public void onFilterChanged() {
        requestDocumentTypes();
    }

    @Override
    public void onItemClicked(DocumentType value) {
        if ( !policyService.hasPrivilegeFor( En_Privilege.DOCUMENT_TYPE_EDIT ) ) {
            return;
        }

        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new DocumentTypeEvents.ShowPreview( view.getPreviewContainer(), value ) );
        }
    }

    @Override
    public void onEditClicked(DocumentType value) {
        onItemClicked(value);
    }

    @Override
    public void onRemoveClicked(DocumentType value) {
        if (value == null) {
            return;
        }

        documentTypeToRemove = value;
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.documentTypeRemoveConfirmMessage()));
    }

    private void updateListAndSelect(DocumentType type ) {
        requestDocumentTypes();
        onItemClicked( type );
    }

    private void requestDocumentTypes() {
        view.clearRecords();
        animation.closeDetails();

        documentTypeService.getDocumentTypes(makeQuery(), new RequestCallback<List<DocumentType>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(List<DocumentType> result) {
                view.clearRecords();
                result.forEach(view::addRow);
            }
        });
    }
    
    private DocumentTypeQuery makeQuery() {
        En_SortDir sortDir = filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC;
        return new DocumentTypeQuery( filterView.name().getValue(), filterView.sortField().getValue(), sortDir, filterView.documentCategories().getValue() );
    }

    @Inject
    PolicyService policyService;
    @Inject
    DocumentTypeControllerAsync documentTypeService;
    @Inject
    Lang lang;
    @Inject
    TableAnimation animation;
    @Inject
    AbstractDocumentTypeTableView view;
    @Inject
    AbstractDocumentTypeFilterView filterView;
    @Inject
    DefaultErrorHandler errorHandler;

    private DocumentType documentTypeToRemove;

    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;
}
