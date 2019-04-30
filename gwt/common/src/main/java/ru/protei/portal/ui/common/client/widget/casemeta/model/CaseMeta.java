package ru.protei.portal.ui.common.client.widget.casemeta.model;

import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseTag;

import java.io.Serializable;
import java.util.Set;

public class CaseMeta implements Serializable {

    private Set<CaseLink> links;
    private Set<CaseTag> tags;

    public CaseMeta() {}

    public CaseMeta(Set<CaseLink> links, Set<CaseTag> tags) {
        this.links = links;
        this.tags = tags;
    }

    public Set<CaseLink> getLinks() {
        return links;
    }

    public void setLinks(Set<CaseLink> links) {
        this.links = links;
    }

    public Set<CaseTag> getTags() {
        return tags;
    }

    public void setTags(Set<CaseTag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "CaseMeta{" +
                "links=" + links +
                ", tags=" + tags +
                '}';
    }
}
