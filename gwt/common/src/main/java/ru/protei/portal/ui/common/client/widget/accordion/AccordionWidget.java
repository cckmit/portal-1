package ru.protei.portal.ui.common.client.widget.accordion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.widget.htmlpanel.CustomHTMLPanel;

import java.util.Iterator;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.SHOW;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.STYLE_ATTRIBUTE;

public class AccordionWidget extends Composite implements HasWidgets {
    public AccordionWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setMaxHeightToElement(accordionCardBody, maxHeight);
    }

    public HasWidgets getBodyContainer() {
        return bodyContainer;
    }

    public void setHeader(String headerContainer) {
        headerLabel.setInnerText(headerContainer);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        setMaxHeightToElement(accordionCardBody, maxHeight);
    }

    public void setLocalStorageKey(String localStorageKey) {
        this.localStorageKey = localStorageKey;
    }

    public void setHeaderLabelDebugId(String debugId) {
        headerLabel.setId(debugId);
    }

    public void setCollapseButtonDebugId(String debugId) {
        collapseButton.setId(debugId);
    }

    @Override
    public void add(Widget w) {
        bodyContainer.add(w);
    }

    @Override
    public void clear() {
        bodyContainer.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return bodyContainer.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        return bodyContainer.remove(w);
    }

    @UiHandler("headerContainer")
    public void onAttachmentsHeaderClicked(ClickEvent event) {
        if (accordionContainer.hasClassName(SHOW)) {
            collapseBody();
        } else {
            expandBody();
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        initCollapseState(localStorageKey);
    }

    private void initCollapseState(String localStorageKey) {
        if (localStorageKey == null) {
            collapseBody();
            return;
        }

        if (!localStorageService.getBooleanOrDefault(localStorageKey, false)) {
            collapseBody();
            return;
        }

        expandBody();
    }

    private void collapseBody() {
        accordionContainer.removeClassName(SHOW);
        setMaxHeightToElement(accordionBody, 0);
        setLocalStorageValue(localStorageKey, false);
    }

    private void expandBody() {
        accordionContainer.addClassName(SHOW);
        setMaxHeightToElement(accordionBody, maxHeight);
        setLocalStorageValue(localStorageKey, true);
    }

    private void setMaxHeightToElement(Element element, int maxHeight) {
        element.setAttribute(STYLE_ATTRIBUTE, makeMaxHeightStyle(maxHeight));
    }

    private String makeMaxHeightStyle(int maxHeight) {
        return "max-height: " + maxHeight + Style.Unit.PX.getType();
    }

    private void setLocalStorageValue(String key, boolean value) {
        if (key == null) {
            return;
        }

        localStorageService.set(key, String.valueOf(value));
    }

    @UiField
    CustomHTMLPanel headerContainer;
    @UiField
    LabelElement headerLabel;
    @UiField
    HTMLPanel bodyContainer;
    @UiField
    DivElement accordionBody;
    @UiField
    DivElement accordionContainer;
    @UiField
    DivElement accordionCardBody;
    @UiField
    AnchorElement collapseButton;

    @Inject
    private LocalStorageService localStorageService;

    private int maxHeight = Integer.MAX_VALUE;
    private String localStorageKey;

    interface AccordionWidgetUiBinder extends UiBinder<HTMLPanel, AccordionWidget> {}
    private static AccordionWidgetUiBinder ourUiBinder = GWT.create(AccordionWidgetUiBinder.class);
}
