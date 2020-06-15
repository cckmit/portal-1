package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.ent.Plan;

public class PlanOption extends EntityOption {
    private Long creatorId;

    public PlanOption() {}

    public PlanOption(Long id, String displayName, Long creatorId) {
        super(displayName, id);
        this.creatorId = creatorId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public static PlanOption fromPlan(Plan plan) {
        if (plan == null) {
            return null;
        }

        return new PlanOption(plan.getId(), plan.getName(), plan.getCreatorId());
    }

    @Override
    public String toString() {
        return "PlanOption{" +
                "id=" + getId() +
                "name='" + getDisplayText() + "\'" +
                "creatorId=" + creatorId +
                '}';
    }
}
