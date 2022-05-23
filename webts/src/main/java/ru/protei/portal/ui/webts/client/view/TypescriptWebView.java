package ru.protei.portal.ui.webts.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.protei.portal.ui.webts.client.model.Unsubscribe;

import java.util.ArrayList;
import java.util.List;

public class TypescriptWebView extends Composite {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public Element getRootElement() {
        return root.getElement();
    }

    public Unsubscribe addOnDetachListener(Runnable listener) {
        onDetachListeners.add(listener);
        return () -> onDetachListeners.remove(listener);
    }

    @Override
    protected void onDetach() {
        onDetachListeners.forEach(Runnable::run);
        super.onDetach();
    }

    @UiField
    protected HTMLPanel root;
    private final List<Runnable> onDetachListeners = new ArrayList<>();

    interface TypescriptWebViewUiBinder extends UiBinder<Widget, TypescriptWebView> {}
    private final static TypescriptWebViewUiBinder ourUiBinder = GWT.create(TypescriptWebViewUiBinder.class);
}
