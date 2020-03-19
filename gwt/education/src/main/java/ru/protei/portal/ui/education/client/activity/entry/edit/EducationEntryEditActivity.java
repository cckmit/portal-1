package ru.protei.portal.ui.education.client.activity.entry.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EducationEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EducationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Collections;
import java.util.List;

public abstract class EducationEntryEditActivity implements Activity, AbstractEducationEntryEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(EducationEvents.EditEducationEntry event) {
        EducationEntry entry = event.entry;
        boolean isWorker = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_VIEW);
        boolean isWorkerCanRequest = isWorker && policyService.hasPrivilegeFor(En_Privilege.EDUCATION_EDIT);
        boolean isWorkerCreationMode = isWorkerCanRequest && entry == null;
        boolean isAdmin = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_CREATE);
        if (!isWorkerCreationMode && !isAdmin) {
            fireEvent(new ForbiddenEvents.Show(initDetails.parent));
            return;
        }
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        fillView(entry == null ? new EducationEntry() : entry, isAdmin);
    }

    @Override
    public void onApproveClicked() {
        boolean isAdmin = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_CREATE);
        boolean isApproved = entry.isApproved();
        if (!isAdmin || isApproved) {
            return;
        }
        fillDto(entry);
        educationController.adminModifyEntry(entry, new FluentCallback<EducationEntry>()
                .withSuccess(en -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new Back());
                }));
    }

    @Override
    public void onDeclineClicked() {
        boolean isAdmin = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_CREATE);
        if (!isAdmin) {
            return;
        }
        educationController.adminDeleteEntry(entry.getId(), new FluentCallback<EducationEntry>()
                .withSuccess(en -> {
                    fireEvent(new NotifyEvents.Show(lang.educationEntryActionDeclined(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new Back());
                }));
    }

    @Override
    public void onSaveClicked() {
        boolean isAdmin = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_CREATE);
        boolean isWorker = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_VIEW);
        boolean isWorkerCanRequest = isWorker && policyService.hasPrivilegeFor(En_Privilege.EDUCATION_EDIT);
        fillDto(entry);
        if (isAdmin) {
            educationController.adminModifyEntry(entry, new FluentCallback<EducationEntry>()
                    .withSuccess(en -> {
                        fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                        fireEvent(new Back());
                    }));
        } else if (isWorkerCanRequest) {
            List<Long> workers = Collections.emptyList(); // TODO
            educationController.requestNewEntry(entry, workers, new FluentCallback<EducationEntry>()
                    .withSuccess(en -> {
                        fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                        fireEvent(new Back());
                    }));
        }
    }

    @Override
    public void onCloseClicked() {
        fireEvent(new Back());
    }

    private void fillView(EducationEntry entry, boolean isAdmin) {
        this.entry = entry;
        this.isAdmin = isAdmin;
        view.title().setValue(entry.getTitle());
        view.type().setValue(entry.getType());
        view.coins().setValue(entry.getCoins() != null ? String.valueOf(entry.getCoins()) : "");
        view.link().setValue(entry.getLink());
        view.location().setValue(entry.getLocation());
        view.dates().setValue(new DateInterval(entry.getDateStart(), entry.getDateEnd()));
        view.description().setValue(entry.getDescription());
        view.image().setValue(entry.getImage());
        view.approveButtonVisibility().setVisible(isAdmin);
        view.declineButtonVisibility().setVisible(isAdmin);
        view.saveButtonVisibility().setVisible(!isAdmin || entry.isApproved());
        view.approveButtonEnabled().setEnabled(isAdmin && !entry.isApproved());
    }

    private void fillDto(EducationEntry entry) {
        entry.setTitle(view.title().getValue());
        entry.setType(view.type().getValue());
        entry.setCoins(parseIntOrNull(view.coins().getValue()));
        entry.setLink(view.link().getValue());
        entry.setLocation(view.location().getValue());
        entry.setDateStart(view.dates().getValue() == null ? null : view.dates().getValue().from);
        entry.setDateEnd(view.dates().getValue() == null ? null : view.dates().getValue().to);
        entry.setDescription(view.description().getValue());
        entry.setImage(view.image().getValue());
    }

    private Integer parseIntOrNull(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;
    @Inject
    EducationControllerAsync educationController;
    @Inject
    AbstractEducationEntryEditView view;

    private boolean isAdmin;
    private EducationEntry entry;
    private AppEvents.InitDetails initDetails;
}
