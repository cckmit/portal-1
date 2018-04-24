package ru.protei.portal.ui.documenttype.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.DocumentTypeEvents;
import ru.protei.portal.ui.common.client.lang.Lang;

public abstract class DocumentTypeTableActivity implements AbstractDocumentTypeTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();
        view.setActivity(this);
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
    }

    @Override
    public void onEditClicked(DocumentType value) {
        fireEvent(new DocumentTypeEvents.Edit(value.getId()));
    }


    private static String CREATE_ACTION;
    private AppEvents.InitDetails initDetails;

    @Inject
    PolicyService policyService;

    @Inject
    Lang lang;

    @Inject
    AbstractDocumentTypeTableView view;
}
