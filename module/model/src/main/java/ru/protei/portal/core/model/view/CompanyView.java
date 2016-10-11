package ru.protei.portal.core.model.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by michael on 10.10.16.
 */
public class CompanyView {

    @JsonProperty
    public final Long id;

    @JsonProperty
    public final String name;

    @JsonProperty
    public final String webSite;


    @JsonProperty
    public final List<String> companyGroups;


    public CompanyView (Company comp, List<CompanyGroup> groups) {
        this.id = comp.getId();
        this.name = comp.getCname();
        this.webSite = comp.getWebsite();
        this.companyGroups = groups == null ? Collections.<String>emptyList() : new ArrayList<String>(groups.size());
        if (groups != null)
            for (CompanyGroup g : groups)
                this.companyGroups.add(g.getName());
    }

}
