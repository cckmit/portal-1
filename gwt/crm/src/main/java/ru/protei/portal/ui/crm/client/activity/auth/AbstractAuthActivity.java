package ru.protei.portal.ui.crm.client.activity.auth;

import ru.protei.portal.ui.common.client.common.Initialization;

/**
 * Created by turik on 23.09.16.
 */
public interface AbstractAuthActivity extends Initialization {
    void onLoginClicked();
    void onResetClicked();
}
