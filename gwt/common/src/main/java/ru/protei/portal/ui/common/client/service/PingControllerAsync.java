package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Пингер
 */
public interface PingControllerAsync {

    void ping( AsyncCallback<Void> sessionCallback );
}
