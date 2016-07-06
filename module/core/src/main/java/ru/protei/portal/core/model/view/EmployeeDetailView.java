package ru.protei.portal.core.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.List;

/**
 * Created by michael on 06.07.16.
 */
public class EmployeeDetailView {

    @JsonProperty
    private String department;

    @JsonProperty
    private String mobilePhone;

    @JsonProperty
    private String workPhone;

    @JsonProperty
    private String jid;

    @JsonProperty
    private String[] ip;

    @JsonProperty
    private String email;

    @JsonProperty
    private String icq;

    @JsonProperty
    private AbsenceEntryView[] absences;


    public EmployeeDetailView () {

    }

    public EmployeeDetailView fill(Person p) {
        this.department = p.getPosition();
        this.mobilePhone = p.getMobilePhone();
        this.workPhone = p.getWorkPhone();
        this.jid = p.getJabber();
        this.ip = p.getIpAddress() != null ? new String[] {p.getIpAddress()} : null;
        this.email = p.getEmail();
        this.icq = p.getIcq();
        return this;
    }

    public EmployeeDetailView fill (List<PersonAbsence> alist) {

        this.absences = alist == null || alist.isEmpty() ? null : new AbsenceEntryView[alist.size()];

        if (this.absences != null) {
            int i = 0;
            for (PersonAbsence a : alist) {
                this.absences[i] = new AbsenceEntryView(a);
                i++;
            }
        }



        return this;
    }
}
