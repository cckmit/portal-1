package ru.protei.portal.ui.web.client.activity;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.web.client.integration.NativeWebIntegration;
import ru.protei.portal.ui.web.client.model.TsWebUnit;
import ru.protei.portal.ui.web.client.view.TypescriptWebView;

public abstract class TypescriptUnitActivity implements Activity {

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    public void showUnit(TsWebUnit unit) {
        EventTarget emitter = makeEmitter();
        Element dispatcher = emitter.cast();
        TypescriptWebView view = viewProvider.get();
        view.addOnDetachListener(() -> {
            dispatcher.dispatchEvent(makeNativeEvent("detach"));
        });
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        nativeWebIntegration.mount(view.getRootElement(), unit, emitter);
    }

    private native EventTarget makeEmitter()/*-{
        return $wnd.document.createDocumentFragment();
    }-*/;

    private native NativeEvent makeNativeEvent(String name)/*-{
        return new Event(name);
    }-*/;

    @Inject
    private Provider<TypescriptWebView> viewProvider;
    @Inject
    private NativeWebIntegration nativeWebIntegration;
    private AppEvents.InitDetails initDetails;
}
