package ru.protei.portal.ui.web.client.model;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;

public class MountDescriptor {
    public Element root;
    public TsWebUnit unit;
    public EventTarget emitter;

    public MountDescriptor(Element root, TsWebUnit unit, EventTarget emitter) {
        this.root = root;
        this.unit = unit;
        this.emitter = emitter;
    }
}
