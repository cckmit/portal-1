package ru.protei.portal.core.model.view;

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

    @Override
    public String toString() {
        return "PlanOption{" +
                "id=" + getId() +
                "name='" + getDisplayText() + "\'" +
                "creatorId=" + creatorId +
                '}';
    }
}
