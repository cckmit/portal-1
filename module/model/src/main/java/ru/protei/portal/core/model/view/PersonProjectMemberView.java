package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.ent.Person;

public class PersonProjectMemberView extends PersonShortView {

    private En_DevUnitPersonRoleType role;

    public PersonProjectMemberView() {}

    public PersonProjectMemberView(String displayShortName, Long id, boolean isFired, En_DevUnitPersonRoleType role) {
        super(displayShortName, id, isFired);
        this.role = role;
    }

    public PersonProjectMemberView( Person person, En_DevUnitPersonRoleType role ) {
        super(person);
        this.role = role;
    }

    public PersonProjectMemberView( PersonShortView psv, En_DevUnitPersonRoleType role ) {
        super(psv);
        this.role = role;
    }

    public En_DevUnitPersonRoleType getRole() {
        return role;
    }

    public void setRole(En_DevUnitPersonRoleType role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PersonProjectMemberView) {
            PersonProjectMemberView ppm = (PersonProjectMemberView) obj;
            return this.role == null ? ppm.getRole() == null : ppm.getRole() != null && this.role.equals(ppm.getRole()) && super.equals(ppm);
        }
        return super.equals(obj);
    }
}
