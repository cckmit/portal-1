package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.EmployeeShortView;

import java.util.List;

public class PersonInfo {
    private Long id;

    private String displayName;

    private String email;

    private String mobilePhone;

    private String workPhone;

    private String ip;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public static PersonInfo fromEmployeeShortView(EmployeeShortView person) {
        if (person == null) {
            return null;
        }

        PersonInfo personInfo = new PersonInfo();

        personInfo.setId(person.getId());
        personInfo.setDisplayName(person.getDisplayName());
        personInfo.setIp(person.getIpAddress());

        PlainContactInfoFacade plainContactInfoFacade = new PlainContactInfoFacade(person.getContactInfo());

        personInfo.setEmail(plainContactInfoFacade.getEmail());
        personInfo.setMobilePhone(plainContactInfoFacade.getMobilePhone());
        personInfo.setWorkPhone(plainContactInfoFacade.getWorkPhone());

        return personInfo;
    }

    @Override
    public String toString() {
        return "PersonInfo{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", workPhone='" + workPhone + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
