package ru.protei.portal.core.model.ent;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by serebryakov on 24/08/17.
 */
public class OfficialMember implements Serializable {

    private Long id;

    private String firstName;

    private String lastName;

    private String secondName;

    private String company;

    private String position;

    private String amplua;

    private String relations;

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

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAmplua() {
        return amplua;
    }

    public void setAmplua(String amplua) {
        this.amplua = amplua;
    }

    public String getRelations() {
        return relations;
    }

    public void setRelations(String relations) {
        this.relations = relations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
