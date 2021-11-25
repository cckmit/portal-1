package ru.protei.portal.core.model.api.rfidlabel;

import ru.protei.portal.core.utils.XmlRfidLabelDataAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement(name = "PARAMETER")
public class ApiRFIDParameter implements Serializable {

    @XmlElement(name = "TIME")
    @XmlJavaTypeAdapter(XmlRfidLabelDataAdapter.class)
    private Date time;

    @XmlElement(name = "EPC")
    private String epc;

    @XmlElement(name = "REG_TYPE")
    private Integer regType;

    @XmlElement(name = "ANT")
    private Integer ant;

    public ApiRFIDParameter() {}

    @XmlTransient
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @XmlTransient
    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    @XmlTransient
    public Integer getRegType() {
        return regType;
    }

    public void setRegType(Integer regType) {
        this.regType = regType;
    }

    @XmlTransient
    public Integer getAnt() {
        return ant;
    }

    public void setAnt(Integer ant) {
        this.ant = ant;
    }

    @Override
    public String toString() {
        return "ApiRFIDParameter{" +
                "time='" + time + '\'' +
                ", epc='" + epc + '\'' +
                ", regType=" + regType +
                ", ant=" + ant +
                '}';
    }
}
