package ru.protei.portal.ui.webts.client.integration.mount;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import ru.protei.portal.ui.webts.client.model.TsWebUnit;

public interface IntegrationMount {
    void mount(Element root, TsWebUnit unit, EventTarget emitter);
}
