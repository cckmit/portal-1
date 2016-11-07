package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.view.ValueComment;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author michael
 */
@JdbcEntity(table = "Company")
public class Company implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcJoinedObject( localColumn = "category_id", table = "company_category" )
    private CompanyCategory category;

    @JdbcColumn(name = "parent_company")
    private Long parentCompanyId;

    @JdbcColumn(name = "address_dejure")
    private String addressDejure;

    @JdbcColumn(name = "address_fact")
    private String addressFact;

    @JdbcColumn(name = "cname")
    private String cname;

    @JdbcColumn(name = "contactInfo", converterType = ConverterType.JSON)
    private ContactInfo contactInfo;

//    @JdbcColumn(name = "email", converterType = ConverterType.JSON)
//    private ValueComment email; //List<ValueComment>, NOT NULL!
//
//    @JdbcColumn(name = "fax")
//    private String fax;

    @JdbcColumn(name = "info")
    private String info;

//    @JdbcColumn(name = "phone", converterType = ConverterType.JSON)
//    private ValueComment phone; //List<ValueComment>, NOT NULL!

    @JdbcColumn(name = "created")
    private Date created;

//    @JdbcColumn(name = "website")
//    private String website;

    @SuppressWarnings("GwtInconsistentSerializableClass")
    private List<CompanyGroup> groups;

    public Company() {
        contactInfo = new ContactInfo();
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

//    public ValueComment defaultEmail() {
////        if(email == null)
////            return new ArrayList<ValueComment>();
//        return this.email;
//    }

//    public String getFax() {
//        return this.fax;
//    }

    public Long getId() {
        return this.id;
    }

    public String getInfo() {
        return this.info;
    }

//    public ValueComment getPhone() {
////        if(phone == null)
////            return new ArrayList<ValueComment>();
//        return this.phone;
//    }

    public Date getCreated() {
        return this.created;
    }

//    public String getWebsite() {
//        return this.website;
//    }

    public void setAddressDejure(String addressDejure) {
        this.addressDejure = addressDejure;
    }

    public void setAddressFact(String addressFact) {
        this.addressFact = addressFact;
    }

    public Long getCategoryId() {
        return category == null ? null : category.getId();
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

//    public void setEmail(ValueComment email) {
//        this.email = email;
//    }
//
//    public void setPhone(ValueComment phone){
//        this.phone = phone;
//    }
//
//    public void setFax(String fax) {
//        this.fax = fax;
//    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

//    public void setWebsite(String website) {
//        this.website = website;
//    }


    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
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
                ", categoryId=" + String.valueOf(getCategoryId()) +
                ", parentCompanyId=" + parentCompanyId +
                ", addressDejure='" + addressDejure + '\'' +
                ", addressFact='" + addressFact + '\'' +
                ", cname='" + cname + '\'' +
//                ", email='" + email + '\'' +
//                ", fax='" + fax + '\'' +
                ", info='" + info + '\'' +
//                ", phone='" + phone + '\'' +
                ", created=" + created +
//                ", website='" + website + '\'' +
                ", groups=" + groups +
                '}';
    }
}
