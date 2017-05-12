package ru.protei.portal.hpsm.logic;

/**
 * Created by michael on 28.04.17.
 */
interface HpsmEventHandler {
    void handle(HpsmEvent request, ServiceInstance instance) throws Exception;
}
