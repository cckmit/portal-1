package ru.protei.portal.core.model.ent;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by serebryakov on 24/08/17.
 */
public class OfficialMember extends CaseMember implements Serializable {

    private String amplua;

    private String relations;

    public String getAmplua() {
        return amplua;
    }

    public void setAmplua(String amplua) {
        this.amplua = amplua;
    }

    public String getRelations() {
        return relations;
    }

    public void setRelations(String relations) { this.relations = relations; }
}
