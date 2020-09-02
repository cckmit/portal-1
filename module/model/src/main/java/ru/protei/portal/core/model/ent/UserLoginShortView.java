package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.view.Identifiable;

import java.io.Serializable;

public class UserLoginShortView implements Identifiable, Serializable {
    private Long id;
    private Long personId;
    private String ulogin;
    private String firstName;
    private String lastName;
    private En_CompanyCategory companyCategory;
    private En_Gender gender;

    public UserLoginShortView() {}

    public UserLoginShortView(UserLogin userLogin) {
        this.id = userLogin.getId();
        this.personId = userLogin.getPersonId();
        this.ulogin = userLogin.getUlogin();
        this.firstName = userLogin.getFirstName();
        this.lastName = userLogin.getLastName();
        this.companyCategory = userLogin.getCompanyCategory();
        this.gender = En_Gender.parse(userLogin.getGenderCode());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUlogin() {
        return ulogin;
    }

    public void setUlogin(String ulogin) {
        this.ulogin = ulogin;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public En_CompanyCategory getCompanyCategory() {
        return companyCategory;
    }

    public void setCompanyCategory(En_CompanyCategory companyCategory) {
        this.companyCategory = companyCategory;
    }

    public En_Gender getGender() {
        return gender;
    }

    public void setGender(En_Gender gender) {
        this.gender = gender;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Override
    public String toString() {
        return "UserLoginShortView{" +
                "id=" + id +
                ", ulogin='" + ulogin + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", companyCategory=" + companyCategory +
                ", gender=" + gender +
                '}';
    }
}
