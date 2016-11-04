package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author michael
 */
@JdbcEntity(table = "Company")
public class Company implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "category_id")
    private Long categoryId;

    @JdbcJoinedObject( localColumn = "category_id")
    private CompanyCategory category;

    @JdbcColumn(name = "parent_company")
    private Long parentCompanyId;

    @JdbcColumn(name = "address_dejure")
    private String addressDejure;

    @JdbcColumn(name = "address_fact")
    private String addressFact;

    @JdbcColumn(name = "cname")
    private String cname;

    @JdbcColumn(name = "email")
    private String email;

    @JdbcColumn(name = "fax")
    private String fax;

    @JdbcColumn(name = "info")
    private String info;

    @JdbcColumn(name = "phone")
    private String phone;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "website")
    private String website;

    @SuppressWarnings("GwtInconsistentSerializableClass")
    private List<CompanyGroup> groups;


    public Company() {
        groups = null;
    }

    public Long getParentCompanyId() {
        return parentCompanyId;
    }

    public void setParentCompanyId(Long parentCompanyId) {
        this.parentCompanyId = parentCompanyId;
    }

    public String getAddressDejure() {
        return this.addressDejure;
    }

    public String getAddressFact() {
        return this.addressFact;
    }

    public String getCname() {
        return this.cname;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFax() {
        return this.fax;
    }

    public Long getId() {
        return this.id;
    }

    public String getInfo() {
        return this.info;
    }


    public String getPhone() {
        return this.phone;
    }

    public Date getCreated() {
        return this.created;
    }

    public String getWebsite() {
        return this.website;
    }

    public void setAddressDejure(String addressDejure) {
        this.addressDejure = addressDejure;
    }

    public void setAddressFact(String addressFact) {
        this.addressFact = addressFact;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public CompanyCategory getCategory() {
        return category;
    }

    public void setCategory( CompanyCategory category ) {
        this.category = category;
    }

    public void setCname( String cname) {
        this.cname = cname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setWebsite(String website) {
        this.website = website;
    }


    public void setGroups(List<CompanyGroup> groups) {
        this.groups = groups;
    }

    public List<CompanyGroup> getGroups() {
        return groups;
    }

    @Override
    public int hashCode() {
        return this.id == null ? -1 : this.id.intValue();
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", parentCompanyId=" + parentCompanyId +
                ", addressDejure='" + addressDejure + '\'' +
                ", addressFact='" + addressFact + '\'' +
                ", cname='" + cname + '\'' +
                ", email='" + email + '\'' +
                ", fax='" + fax + '\'' +
                ", info='" + info + '\'' +
                ", phone='" + phone + '\'' +
                ", created=" + created +
                ", website='" + website + '\'' +
                ", groups=" + groups +
                '}';
    }
}
