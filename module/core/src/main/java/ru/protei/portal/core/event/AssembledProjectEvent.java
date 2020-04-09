package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.util.DiffResult;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.*;

public class AssembledProjectEvent extends ApplicationEvent {
    private Project oldProjectState;
    private Project newProjectState;
    private Person initiator;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AssembledProjectEvent(Object source, Project oldProjectState, Project newProjectState, Person initiator) {
        super(source);
        this.oldProjectState = oldProjectState;
        this.newProjectState = newProjectState;
        this.initiator = initiator;
    }

    public boolean isNameChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getName(), newProjectState.getName());
    }

    public boolean isDescriptionChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getDescription(), newProjectState.getDescription());
    }

    public boolean isStateChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getState(), newProjectState.getState());
    }

    public boolean isRegionChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getRegion(), newProjectState.getRegion());
    }

    public boolean isCompanyChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getCustomer(), newProjectState.getCustomer());
    }

    public boolean isCustomerTypeChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getCustomerType(), newProjectState.getCustomerType());
    }

    public boolean isProductDirectionChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getProductDirection(), newProjectState.getProductDirection());
    }

    public boolean isProductChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getSingleProduct(), newProjectState.getSingleProduct());
    }

    public boolean isSupportValidityChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getTechnicalSupportValidity(), newProjectState.getTechnicalSupportValidity());
    }

    public boolean isTeamChanged() {
        if (!isEditEvent()) {
            return false;
        }

        DiffCollectionResult<PersonProjectMemberView> diffResult = CollectionUtils.diffCollection(oldProjectState.getTeam(), newProjectState.getTeam());

        if (CollectionUtils.isNotEmpty(diffResult.getAddedEntries())) {
            return true;
        }

        if (CollectionUtils.isNotEmpty(diffResult.getRemovedEntries())) {
            return true;
        }

        return false;
    }

    public boolean isSlaChanged() {
        if (!isEditEvent()) {
            return false;
        }

        if (Arrays.stream(En_ImportanceLevel.values()).allMatch(
                importanceLevel -> isSameSla(
                        getSlaByImportance(oldProjectState.getProjectSlas(), importanceLevel.getId()),
                        getSlaByImportance(newProjectState.getProjectSlas(), importanceLevel.getId())
                ))
        ) {
            return false;
        }

        return true;
    }

    public Map<En_DevUnitPersonRoleType, DiffCollectionResult<PersonShortView>> getTeamDiffs() {
        Map<En_DevUnitPersonRoleType, DiffCollectionResult<PersonShortView>> teamDiffs = new HashMap<>();

        if (!isEditEvent()) {
            DiffCollectionResult<PersonProjectMemberView> diffResult = CollectionUtils.diffCollection(null, newProjectState.getTeam());

            for (PersonProjectMemberView currentPerson : CollectionUtils.emptyIfNull(diffResult.getAddedEntries())) {
                DiffCollectionResult<PersonShortView> roleDiffs = teamDiffs.computeIfAbsent(currentPerson.getRole(), k -> new DiffCollectionResult<>());
                roleDiffs.putAddedEntry(currentPerson);
            }

            return teamDiffs;
        }

        DiffCollectionResult<PersonProjectMemberView> diffResult = CollectionUtils.diffCollection(oldProjectState.getTeam(), newProjectState.getTeam());

        for (PersonProjectMemberView currentPerson : CollectionUtils.emptyIfNull(diffResult.getAddedEntries())) {
            DiffCollectionResult<PersonShortView> roleDiffs = teamDiffs.computeIfAbsent(currentPerson.getRole(), k -> new DiffCollectionResult<>());
            roleDiffs.putAddedEntry(currentPerson);
        }

        for (PersonProjectMemberView currentPerson : CollectionUtils.emptyIfNull(diffResult.getRemovedEntries())) {
            DiffCollectionResult<PersonShortView> roleDiffs = teamDiffs.computeIfAbsent(currentPerson.getRole(), k -> new DiffCollectionResult<>());
            roleDiffs.putRemovedEntry(currentPerson);
        }

        for (PersonProjectMemberView currentPerson : CollectionUtils.emptyIfNull(diffResult.getSameEntries())) {
            DiffCollectionResult<PersonShortView> roleDiffs = teamDiffs.computeIfAbsent(currentPerson.getRole(), k -> new DiffCollectionResult<>());
            roleDiffs.putSameEntry(currentPerson);
        }

        return teamDiffs;
    }

    public Map<En_ImportanceLevel, DiffResult<ProjectSla>> getSlaDiffs() {
        Map<En_ImportanceLevel, DiffResult<ProjectSla>> slaDiffs = new HashMap<>();

        if (!isEditEvent()) {
            CollectionUtils.emptyIfNull(newProjectState.getProjectSlas()).forEach(sla -> slaDiffs.put(
                    En_ImportanceLevel.find(sla.getImportanceLevelId()),
                    new DiffResult<>(sla, sla))
            );

            return slaDiffs;
        }

        List<ProjectSla> oldSlaList = CollectionUtils.emptyIfNull(oldProjectState.getProjectSlas());
        List<ProjectSla> newSlaList = CollectionUtils.emptyIfNull(newProjectState.getProjectSlas());

        for (En_ImportanceLevel level : En_ImportanceLevel.values()) {
            ProjectSla oldSla = oldSlaList
                    .stream()
                    .filter(sla -> sla.getImportanceLevelId() == level.getId())
                    .findAny()
                    .orElse(null);

            ProjectSla newSla = newSlaList
                    .stream()
                    .filter(sla -> sla.getImportanceLevelId() == level.getId())
                    .findAny()
                    .orElse(null);

            if (oldSla == null || newSla == null) {
                continue;
            }

            slaDiffs.put(level, new DiffResult<>(oldSla, newSla));
        }

        return slaDiffs;
    }

    private ProjectSla getSlaByImportance(List<ProjectSla> slaList, int importanceLevelId) {
        return slaList
                .stream()
                .filter(sla -> sla.getImportanceLevelId() == importanceLevelId)
                .findAny()
                .orElse(new ProjectSla());
    }

    private boolean isSameSla(ProjectSla sla1, ProjectSla sla2) {
        if (!Objects.equals(sla1.getImportanceLevelId(), sla1.getImportanceLevelId())) {
            return false;
        }

        if (!Objects.equals(sla1.getReactionTime(), sla2.getReactionTime())) {
            return false;
        }

        if (!Objects.equals(sla1.getTemporarySolutionTime(), sla2.getTemporarySolutionTime())) {
            return false;
        }

        if (!Objects.equals(sla1.getFullSolutionTime(), sla2.getFullSolutionTime())) {
            return false;
        }

        return true;
    }

    public Project getOldProjectState() {
        return oldProjectState;
    }

    public void setOldProjectState(Project oldProjectState) {
        this.oldProjectState = oldProjectState;
    }

    public Project getNewProjectState() {
        return newProjectState;
    }

    public void setNewProjectState(Project newProjectState) {
        this.newProjectState = newProjectState;
    }

    public Person getInitiator() {
        return initiator;
    }

    public boolean isEditEvent() {
        return oldProjectState != null;
    }
}
