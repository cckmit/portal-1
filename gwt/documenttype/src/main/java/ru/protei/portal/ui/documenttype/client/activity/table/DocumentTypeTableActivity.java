package ru.protei.portal.ui.documenttype.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.DocumentTypeServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

public abstract class DocumentTypeTableActivity implements AbstractDocumentTypeTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();
        view.setActivity(this);
        view.setAnimation(animation);
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
    }

    @Event
    public void onShow(DocumentTypeEvents.Show event) {
        fireEvent(new AppEvents.InitPanelName(lang.issues()));
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.DOCUMENT_TYPE_CREATE) ?
                new ActionBarEvents.Add(CREATE_ACTION, UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.DOCUMENT_TYPE) :
                new ActionBarEvents.Clear()
        );

        requestDocumentTypes();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.DOCUMENT_TYPE.equals(event.identity)) {
            return;
        }
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onChanged(DocumentTypeEvents.Changed event) {
        view.updateRow(event.doctype);
    }


    @Override
    public void onItemClicked(DocumentType value) {
        if ( value == null ) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent( new DocumentTypeEvents.ShowPreview( view.getPreviewContainer(), value.getId() ) );
        }
    }

    @Override
    public void onEditClicked(DocumentType value) {
        fireEvent(new DocumentTypeEvents.Edit(value.getId()));
    }

    private void requestDocumentTypes() {
        view.clearRecords();

        documentTypeService.getDocumentTypes(new RequestCallback<List<DocumentType>>() {
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


    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;

    @Inject
    PolicyService policyService;

    @Inject
    DocumentTypeServiceAsync documentTypeService;

    @Inject
    Lang lang;

    @Inject
    TableAnimation animation;

    @Inject
    AbstractDocumentTypeTableView view;
}
