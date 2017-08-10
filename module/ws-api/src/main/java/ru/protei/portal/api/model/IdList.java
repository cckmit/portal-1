package ru.protei.portal.api.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="ids")
public class IdList {
    private List<Long> ids = new ArrayList<>();

    public  IdList() {}

    @XmlElement(name="id")
    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
