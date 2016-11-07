package ru.protei.portal.core.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;

import java.util.Date;

/**
 * Created by michael on 06.04.16.
 */
public class WorkerView {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("created")
    private Date created;

    @JsonProperty("cid")
    private Long companyId;

    @JsonProperty("cname")
    private String companyName;

    @JsonProperty("wpos")
    private String position;

    @JsonProperty("fname")
    private String firstName;
    @JsonProperty("lname")
    private String lastName;
    @JsonProperty("sname")
    private String secondName;
    @JsonProperty("dname")
    private String displayName;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("bday")
    private Date birthday;

    @JsonProperty("ip-addr")
    private String ipAddress;

    @JsonProperty("ph-work")
    private String workPhone;

    @JsonProperty("ph-mob")
    private String mobilePhone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("icq")
    private String icq;
    @JsonProperty("jid")
    private String jabber;

    @JsonProperty("info")
    private String info;

    @JsonProperty("del")
    private boolean isDeleted;


    public WorkerView(Person p, Company c) {
        this.id = p.getId();
        this.birthday = p.getBirthday();
        this.companyId = p.getCompanyId();
        this.companyName = c.getCname();
        this.created = p.getCreated();
        this.displayName = p.getDisplayName();
        this.firstName = p.getFirstName();
        this.lastName = p.getLastName();
        this.secondName = p.getSecondName();
        this.position = p.getPosition();
        this.gender = p.getGender().getCode();
        this.ipAddress = p.getIpAddress();
        this.workPhone = p.getWorkPhone();
        this.mobilePhone = p.getMobilePhone();
        this.email = p.getEmail();
        this.icq = p.getIcq();
        this.jabber = p.getJabber();
        this.info = p.getInfo();
    }
}
