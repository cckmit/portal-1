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

    public En_DevUnitPersonRoleType getRole() {
        return role;
    }

    public void setRole(En_DevUnitPersonRoleType role) {
        this.role = role;
    }

    public static PersonProjectMemberView fromPersonShortView(PersonShortView psv, En_DevUnitPersonRoleType role) {
        return new PersonProjectMemberView(psv.getDisplayShortName(), psv.getId(), psv.isFired(), role);
    }

    public static PersonProjectMemberView fromPerson(Person person, En_DevUnitPersonRoleType role) {
        if (person == null) {
            return null;
        }
        return new PersonProjectMemberView(person.getDisplayShortName(), person.getId(), person.isFired(), role);
    }
}
