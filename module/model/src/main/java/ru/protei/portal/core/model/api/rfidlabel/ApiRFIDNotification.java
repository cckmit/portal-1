package ru.protei.portal.core.model.api.rfidlabel;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "NOTIFICATION")
public class ApiRFIDNotification implements Serializable {

    @XmlElement(name = "ID")
    private Integer id;

    @XmlElement(name = "DATA")
    private String data;

    @XmlElement(name = "READER_ID")
    private String readerId;

    @XmlElement(name = "READER_SUB_ID")
    private String readerSubId;

    @XmlElement(name = "RESULT")
    private String result;

    @XmlElement(name = "PARAMETER")
    private List<ApiRFIDParameter> parameters;

    public ApiRFIDNotification() {}

    @XmlTransient
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @XmlTransient
    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    @XmlTransient
    public String getReaderSubId() {
        return readerSubId;
    }

    public void setReaderSubId(String readerSubId) {
        this.readerSubId = readerSubId;
    }

    @XmlTransient
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @XmlTransient
    public List<ApiRFIDParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ApiRFIDParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "ApiRFIDNotification{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", readerId='" + readerId + '\'' +
                ", readerSubId='" + readerSubId + '\'' +
                ", result='" + result + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
