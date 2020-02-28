package ru.protei.portal.core.model.dto;

import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.EmployeeShortView;

import java.util.List;

public class PersonInfo {
    private Long id;

    private List<String> emails;

    private List<String> phones;

    private String workPhone;

    private String ip;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
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
        personInfo.setIp(person.getIpAddress());

        PlainContactInfoFacade plainContactInfoFacade = new PlainContactInfoFacade(person.getContactInfo());

        personInfo.setEmails(plainContactInfoFacade.publicEmails());
        personInfo.setPhones(plainContactInfoFacade.publicPhones());
        personInfo.setWorkPhone(plainContactInfoFacade.getWorkPhone());

        return personInfo;
    }

    @Override
    public String toString() {
        return "PersonInfo{" +
                "id=" + id +
                ", emails=" + emails +
                ", phones=" + phones +
                ", workPhone='" + workPhone + '\'' +
                ", ip=" + ip +
                '}';
    }
}
