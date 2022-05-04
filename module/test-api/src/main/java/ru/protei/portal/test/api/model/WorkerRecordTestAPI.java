package ru.protei.portal.test.api.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class WorkerRecordTestAPI {

    private Long personId;

    private String firstName;

    private String lastName;

    @JsonAlias("gender")
    private String sex;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthday;

    private String phone;

    private String mail;

    private Boolean contractAgreement;

    private Long companyId;

    private Long departmentId;

    private Long positionId;

    private String ip;

    private String inn;

    private String locale;

    @JsonProperty("isFired")
    private Boolean isFired;

    private String login;

    private String password;

    private Set<Long> roleIds;

    public WorkerRecordTestAPI() {
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Boolean getContractAgreement() {
        return contractAgreement;
    }

    public void setContractAgreement(Boolean contractAgreement) {
        this.contractAgreement = contractAgreement;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Boolean isFired() {
        return isFired;
    }


    public void setFired(Boolean isFired) {
        this.isFired = isFired;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }

    @Override
    public String toString() {
        return "EmployeeCreateApiQuery{" +
                "id=" + personId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday=" + birthday +
                ", phone='" + phone + '\'' +
                ", mail='" + mail + '\'' +
                ", contractAgreement=" + contractAgreement +
                ", companyId=" + companyId +
                ", departmentId=" + departmentId +
                ", position=" + positionId +
                ", ip='" + ip + '\'' +
                ", inn='" + inn + '\'' +
                ", locale='" + locale + '\'' +
                ", isFired=" + isFired +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", roleIds=" + roleIds +
                '}';
    }

    public interface Constansts {
        int ACTIVE_FLAG = 1;
        String PERSON_CREATOR = "Тестировщики";
        List<String> DEFAULT_LOCALE_LIST = Arrays.asList("ru", "en");
        int INN_LENGTH = 12;
    }

    public interface Columns {
        String ROLES = "roles";
    }

}
