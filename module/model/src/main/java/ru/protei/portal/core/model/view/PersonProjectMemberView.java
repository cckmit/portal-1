package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.ent.Person;

public class PersonProjectMemberView extends PersonShortView {

    private En_PersonRoleType role;

    public PersonProjectMemberView() {}

    public PersonProjectMemberView(String displayShortName, Long id, boolean isFired, En_PersonRoleType role) {
        super(displayShortName, id, isFired);
        this.role = role;
    }

    public PersonProjectMemberView( Person person, En_PersonRoleType role ) {
        super(person);
        this.role = role;
    }

    public PersonProjectMemberView( PersonShortView psv, En_PersonRoleType role ) {
        super(psv);
        this.role = role;
    }

    public En_PersonRoleType getRole() {
        return role;
    }

    public void setRole(En_PersonRoleType role) {
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
