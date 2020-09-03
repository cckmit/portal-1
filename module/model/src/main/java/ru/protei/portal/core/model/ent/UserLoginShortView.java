package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.view.Identifiable;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "user_login")
public class UserLoginShortView implements Identifiable, Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "ulogin")
    private String ulogin;

    @JdbcColumn(name = "personId")
    private Long personId;

    @JdbcJoinedColumn( mappedColumn = "firstname", joinPath = {
            @JdbcJoinPath( table = "person", localColumn = "personId", remoteColumn = "id", sqlTableAlias = "p" ),
    })
    private String firstName;

    @JdbcJoinedColumn( mappedColumn = "lastname", joinPath = {
            @JdbcJoinPath( table = "person", localColumn = "personId", remoteColumn = "id", sqlTableAlias = "p" ),
    })
    private String lastName;

    @JdbcJoinedColumn( mappedColumn = "category_id", joinPath = {
            @JdbcJoinPath( table = "person", localColumn = "personId", remoteColumn = "id", sqlTableAlias = "p" ),
            @JdbcJoinPath( table = "company", localColumn = "company_id", remoteColumn = "id", sqlTableAlias = "c" )
    })
    @JdbcEnumerated( EnumType.ID )
    private En_CompanyCategory companyCategory;

    @JdbcJoinedColumn(localColumn = "personId", remoteColumn = "id", table = "person", mappedColumn = "sex")
    private String genderCode;

    public UserLoginShortView() {}

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

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    @Override
    public String toString() {
        return "UserLoginShortView{" +
                "id=" + id +
                ", ulogin='" + ulogin + '\'' +
                ", personId=" + personId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", companyCategory=" + companyCategory +
                ", genderCode='" + genderCode + '\'' +
                '}';
    }
}
