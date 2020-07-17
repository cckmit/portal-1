package ru.protei.portal.ui.education.client.activity.entry.edit;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.EducationEntryType;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.EducationEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EducationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.education.client.model.Approve;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.ui.education.client.util.AccessUtil.*;

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
        HasWidgets container = event.parent != null ? event.parent : initDetails.parent;
        entry = event.entry == null ? new EducationEntry() : event.entry;

        boolean isCreationMode = isCreationMode();
        boolean isAdmin = isAdmin(policyService);
        boolean isWorkerCanRequest = isCreationMode && isWorkerCanRequest(policyService);

        if (!isWorkerCanRequest && !isAdmin) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        container.clear();
        container.add(view.asWidget());
        fillView(entry, isAdmin, isCreationMode);
    }

    @Override
    public void onTypeChanged(EducationEntryType type) {
        syncViewRequiredState(type);
    }

    @Override
    public void onSaveClicked() {
        boolean isCreationMode = isCreationMode();
        boolean isAdmin = isAdmin(policyService);

        if (isCreationMode) {
            createNewEntry();
        } else if (isAdmin) {
            saveEntry();
        }
    }

    private void createNewEntry() {
        fillDto(entry);
        List<Long> workers = stream(view.participants().getValue())
                .map(WorkerEntryShortView::getId)
                .collect(Collectors.toList());
        educationController.requestNewEntry(entry, workers, new FluentCallback<EducationEntry>()
                .withSuccess(en -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new Back());
                }));
    }

    private void saveEntry() {
        fillDto(entry);
        Map<Long, Boolean> worker2approve = stream(view.attendance().getValue().entrySet())
                .filter(entry -> entry.getValue() == Approve.APPROVED
                        || entry.getValue() == Approve.DECLINED
                        || entry.getValue() == Approve.APPROVED_FINAL_DECLINED)
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getWorkerId(),
                        entry -> entry.getValue() == Approve.APPROVED
                ));
        educationController.adminSaveEntryAndAttendance(entry, worker2approve, new FluentCallback<EducationEntry>()
                .withSuccess(en -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new Back());
                }));
    }

    @Override
    public void onCloseClicked() {
        fireEvent(new Back());
    }

    private void fillView(EducationEntry entry, boolean isAdmin, boolean isCreationMode) {
        view.title().setValue(entry.getTitle());
        view.type().setValue(entry.getType());
        view.typeEnabled().setEnabled(isCreationMode);
        view.coins().setValue(entry.getCoins() != null ? String.valueOf(entry.getCoins()) : "");
        view.link().setValue(entry.getLink());
        view.location().setValue(entry.getLocation());
        view.dates().setValue(new DateInterval(entry.getDateStart(), entry.getDateEnd()));
        view.description().setValue(entry.getDescription());
        view.image().setValue(entry.getImage());
        view.participants().setValue(new HashSet<>());
        view.participantsVisibility().setVisible(isCreationMode);
        view.attendanceVisibility().setVisible(isAdmin && !isCreationMode);
        if (isAdmin && !isCreationMode) {
            @SuppressWarnings("RedundantStreamOptionalCall")
            Map<EducationEntryAttendance, Approve> value = stream(entry.getAttendanceList())
                    .sorted(Comparator.comparing(EducationEntryAttendance::isApproved))
                    .collect(Collectors.toMap(
                        attendance -> attendance,
                        attendance -> attendance.isApproved() ? Approve.APPROVED_FINAL : Approve.UNKNOWN
                    ));
            view.attendance().setValue(value);
        } else {
            view.attendance().setValue(Collections.emptyMap());
        }
        syncViewRequiredState(entry.getType());
    }

    private void syncViewRequiredState(EducationEntryType type) {
        view.setTitleRequired(false);
        view.setTypeRequired(false);
        view.setCoinsRequired(false);
        view.setLinkRequired(false);
        view.setLocationRequired(false);
        view.setDatesRequired(false);
        view.setDescriptionRequired(false);
        view.setImageRequired(false);
        view.setParticipantsRequired(false);
        if (type == null) {
            view.setTypeRequired(true);
            return;
        }
        switch (type) {
            case COURSE:
            case CONFERENCE: {
                view.setTitleRequired(true);
                view.setTypeRequired(true);
                view.setCoinsRequired(true);
                view.setLinkRequired(true);
                view.setLocationRequired(true);
                view.setDatesRequired(true);
                view.setDescriptionRequired(true);
                view.setImageRequired(true);
                view.setParticipantsRequired(true);
                break;
            }
            case LITERATURE: {
                view.setTitleRequired(true);
                view.setTypeRequired(true);
                view.setCoinsRequired(true);
                view.setLinkRequired(true);
                view.setDescriptionRequired(true);
                view.setImageRequired(true);
                view.setParticipantsRequired(true);
                break;
            }
        }
    }

    private void fillDto(EducationEntry entry) {
        entry.setTitle(view.title().getValue());
        entry.setType(view.type().getValue());
        entry.setCoins(NumberUtils.parseInteger(view.coins().getValue()));
        entry.setLink(view.link().getValue());
        entry.setLocation(view.location().getValue());
        entry.setDateStart(view.dates().getValue() == null ? null : view.dates().getValue().from);
        entry.setDateEnd(view.dates().getValue() == null ? null : view.dates().getValue().to);
        entry.setDescription(view.description().getValue());
        entry.setImage(view.image().getValue());
    }

    private boolean isCreationMode() {
        return entry.getId() == null;
    }

    @Inject
    Lang lang;
    @Inject
    EducationControllerAsync educationController;
    @Inject
    AbstractEducationEntryEditView view;
    @Inject
    PolicyService policyService;

    private EducationEntry entry;
    private AppEvents.InitDetails initDetails;
}
