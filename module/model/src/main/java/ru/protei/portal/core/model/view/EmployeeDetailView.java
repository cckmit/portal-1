package ru.protei.portal.core.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;

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
    private String homePhone;

    @JsonProperty
    private String jid;

    @JsonProperty
    private String[] ip;

    @JsonProperty
    private String email;

    @JsonProperty
    private String icq;

    @JsonProperty
    private String fax;

    @JsonProperty
    private AbsenceEntryView[] absences;


    public EmployeeDetailView () {

    }

    public EmployeeDetailView fill(Person p) {
        this.department = p.getDepartment();

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(p.getContactInfo());

        this.mobilePhone = infoFacade.getMobilePhone();
        this.workPhone = infoFacade.getWorkPhone();
        this.homePhone = infoFacade.getHomePhone();
        this.jid = infoFacade.getJabber();
        this.ip = p.getIpAddress() != null ? new String[] {p.getIpAddress()} : null;
        this.email = infoFacade.getEmail();
        this.icq = infoFacade.getEmail();
        this.fax = infoFacade.getFax();
        return this;
    }

    public EmployeeDetailView fill (List<PersonAbsence> alist, boolean isFull) {

        this.absences = alist == null || alist.isEmpty() ? null : new AbsenceEntryView[alist.size()];

        if (this.absences != null) {
            int i = 0;
            for (PersonAbsence a : alist) {
                if(!isFull)
                    this.absences[i] = new AbsenceEntryView().fill(a);
                else
                    this.absences[i] = new AbsenceEntryView().fullFill(a);
                i++;
            }
        }

        return this;
    }

}
