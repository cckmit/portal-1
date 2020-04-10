package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.Attachment;

import java.util.List;

public class CaseNameAndDescriptionChangeRequest extends AuditableObject {
    private Long id;
    private String name;
    private String info;
    private List<Attachment> attachments;

    public CaseNameAndDescriptionChangeRequest() {}

    public CaseNameAndDescriptionChangeRequest(Long id, String name, String info, List<Attachment> attachments) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.attachments = attachments;
    }

    public CaseNameAndDescriptionChangeRequest(Long id, String name, String info) {
        this.id = id;
        this.name = name;
        this.info = info;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setInfo( String info ) {
        this.info = info;
    }

    @Override
    public String getAuditType() {
        return "CaseNameAndDescriptionChangeRequest";
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
