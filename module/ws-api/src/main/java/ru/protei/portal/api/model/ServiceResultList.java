package ru.protei.portal.api.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="results")
public class ServiceResultList {
    private List<ServiceResult> serviceResults = new ArrayList<>();

    public  ServiceResultList() {}

    @XmlElement(name="result")
    public List<ServiceResult> getServiceResults() {
        return serviceResults;
    }

    public void setServiceResults(List<ServiceResult> serviceResults) {
        this.serviceResults = serviceResults;
    }
}
