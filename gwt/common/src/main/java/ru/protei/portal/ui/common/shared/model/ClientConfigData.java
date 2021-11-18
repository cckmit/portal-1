package ru.protei.portal.ui.common.shared.model;

import java.io.Serializable;
import java.util.Set;

/**
 * Клиентские параметры конфигурации
 */
public class ClientConfigData implements Serializable {

    public String appVersion;
    public Long cardbatchCompanyPartnerId;
    public Set<Long> contractCuratorsDepartmentsIds;

    @Override
    public String toString() {
        return "ClientConfigData{" +
                "appVersion='" + appVersion + '\'' +
                "cardbatchCompanyPartnerId='" + cardbatchCompanyPartnerId + '\'' +
                "contractCuratorsDepartmentsIds='" + contractCuratorsDepartmentsIds + '\'' +
                '}';
    }
}
