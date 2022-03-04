package ru.protei.portal.ui.web.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.Test1Events;
import ru.protei.portal.ui.web.client.integration.NativeWebIntegration;
import ru.protei.portal.ui.web.client.model.TsWebUnit;
import ru.protei.portal.ui.web.client.model.TypescriptWebGwtEvents;
import ru.protei.portal.ui.web.client.model.event.TestEventBusEvent;
import ru.protei.portal.ui.web.client.view.TypescriptWebView;

public abstract class TypescriptWebActivity implements Activity {

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onTypescriptWebInit(TypescriptWebGwtEvents.Init ev) {
        nativeWebIntegration.setup();
        testEventbus();
    }

    @Event(Type.FILL_CONTENT)
    public void onShowUnitTest1(Test1Events.Show ev) {
        showUnit(TsWebUnit.test1);
    }

    private void showUnit(TsWebUnit unit) {
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

    private void testEventbus() { // TODO test remove later
        nativeWebIntegration.<TestEventBusEvent>listenEvent(TestEventBusEvent.type, (event) -> {
            GWT.log("TEST EVENT RECEIVED AT GWT " + event.getType() + " " + event.getSource() + " " + event.getText());
        });
        nativeWebIntegration.fireEvent(TestEventBusEvent.create("hello from gwt"));
    }

    @Inject
    private Provider<TypescriptWebView> viewProvider;
    @Inject
    private NativeWebIntegration nativeWebIntegration;
    private AppEvents.InitDetails initDetails;
}
