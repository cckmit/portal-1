package ru.protei.portal.ui.documenttype.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.DocumentTypeEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Активность превью проекта
 */
public abstract class DocumentTypePreviewActivity implements AbstractDocumentTypePreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Event
    public void onShow( DocumentTypeEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        this.typeId = event.id;

        fillView(typeId);
    }

    @Event
    public void onShow( DocumentTypeEvents.ShowFullScreen event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        this.typeId = event.id;

        fillView(typeId);
    }

    @Override
    public void onFullScreenPreviewClicked() {
        fireEvent( new DocumentTypeEvents.ShowFullScreen(typeId) );
    }

    @Override
    public void onProjectChanged() {
        if ( !policyService.hasPrivilegeFor( En_Privilege.DOCUMENT_TYPE_EDIT ) ) {
            return;
        }

        readView();

    }

    private void fillView( Long id ) {
        if (id == null) {
            fireEvent( new NotifyEvents.Show( lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

    }

    private void fillView( DocumentType value ) {

    }

    private void readView() {

    }

    @Inject
    Lang lang;
    @Inject
    AbstractDocumentTypePreviewView view;
    @Inject
    PolicyService policyService;

    private Long typeId;
    DocumentType documentType;

    private AppEvents.InitDetails initDetails;
}
