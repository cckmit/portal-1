package ru.protei.portal.ui.common.client.common;

import ru.protei.portal.ui.common.shared.model.ClientConfigData;

public class ConfigStorage {

    public ClientConfigData getConfigData() {
        return configData;
    }

    public void setConfigData( ClientConfigData configData ) {
        this.configData = configData;
    }

    private ClientConfigData configData;
}
