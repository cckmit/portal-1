package ru.protei.portal.api.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by turik on 18.08.16.
 */
@XmlRootElement(name = "result")
public class ServiceResult {

    private String errCode;
    private String errInfo;
    private Long id;
    private boolean isSuccess;


    public ServiceResult() {
    }

    public static ServiceResult successResult (Long id) {
        ServiceResult x = new ServiceResult ();
        x.id = id;
        x.isSuccess = true;

        return x;
    }

    public static ServiceResult failResult (String code, String info, Long id) {
        ServiceResult x = new ServiceResult ();
        x.errCode = code;
        x.errInfo = info;
        x.isSuccess = false;
        x.id = id;
        return x;
    }

    @XmlElement(name = "err-code")
    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    @XmlElement(name = "err-info")
    public String getErrInfo() {
        return errInfo;
    }

    public void setErrInfo(String errInfo) {
        this.errInfo = errInfo;
    }

    @XmlElement(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement(name = "success")
    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String toString () {
        return new StringBuilder().append("API Result: [")
                .append(isSuccess() ? "success" : "fail")
                .append(isSuccess() ? (", nID = " + id) : (", errcode = " + errCode + ", info="+errInfo))
                .append("]")
                .toString();
    }

}