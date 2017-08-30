package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Date;
import java.util.List;

/**
 * Матрица принятия решений
 */
public class Official extends CaseShortView{

    private Long id;

    private String info;

    private String numberEmployees;

    private EntityOption region;

    private EntityOption product;

    private Date created;

    private List<OfficialMember> members;

    private boolean attachmentExists;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getNumberEmployees() { return numberEmployees; }

    public void setNumberEmployees(String numberEmployees) {
        this.numberEmployees = numberEmployees;
    }

    public EntityOption getRegion() {
        return region;
    }

    public void setRegion(EntityOption region) {
        this.region = region;
    }

    public EntityOption getProduct() {
        return product;
    }

    public void setProduct(EntityOption product) {
        this.product = product;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<OfficialMember> getMembers() {
        return members;
    }

    public void setMembers(List<OfficialMember> members) {
        this.members = members;
    }

    public boolean isAttachmentExists() {
        return attachmentExists;
    }

    public void setAttachmentExists(boolean attachmentExists) {
        this.attachmentExists = attachmentExists;
    }
}
