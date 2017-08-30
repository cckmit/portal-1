package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Date;
import java.util.List;

/**
 * Матрица принятия решений
 */
public class Official extends CaseShortView {

    private String info;

    private String numberEmployees;

    private EntityOption region;

    private DevUnit product;

    private Date created;

    private List<OfficialMember> members;

    private boolean attachmentExists;


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getNumberEmployees() {
        return numberEmployees;
    }

    public void setNumberEmployees(String numberEmployees) {
        this.numberEmployees = numberEmployees;
    }

    public EntityOption getRegion() {
        return region;
    }

    public void setRegion(EntityOption region) {
        this.region = region;
    }

    public DevUnit getProduct() {
        return product;
    }

    public void setProduct(DevUnit product) {
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

    public static Official fromCaseObject(CaseObject caseObject) {
        Official official = new Official();
        official.setId(caseObject.getId());
        official.setInfo(caseObject.getInfo());
        official.setNumberEmployees(String.valueOf(caseObject.getMembers().size()));
        official.setProduct(caseObject.getProduct());
        official.setCreated(caseObject.getCreated());
        official.setAttachmentExists(caseObject.isAttachmentExists());

        List<CaseLocation> locations = caseObject.getLocations();
        if ( locations != null && !locations.isEmpty() ) {
            official.setRegion( EntityOption.fromLocation( locations.get( 0 ).getLocation() ) );
        }

        return official;
    }

}
