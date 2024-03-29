package ru.protei.portal.ui.common.shared.model;

import java.io.Serializable;
import java.util.List;

/**
 * Клиентские параметры конфигурации
 */
public class ClientConfigData implements Serializable {

    public String appVersion;
    public Long cardbatchCompanyPartnerId;
    public List<String> contractCuratorsDepartmentsIds;
    public List<Long> employeeBirthdayHideIds;
    public int autoCloseDefaultDeadline;

    @Override
    public String toString() {
        return "ClientConfigData{" +
                "appVersion='" + appVersion + '\'' +
                "cardbatchCompanyPartnerId='" + cardbatchCompanyPartnerId + '\'' +
                "contractCuratorsDepartmentsIds='" + contractCuratorsDepartmentsIds + '\'' +
                "employeeBirthdayHideIds='" + employeeBirthdayHideIds + '\'' +
                '}';
    }
}
