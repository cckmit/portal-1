package ru.protei.portal.ui.common.client.widget.casecomment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.TimeElapsedTypeLang;
import ru.protei.portal.ui.common.client.view.selector.ElapsedTimeTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;

import java.util.Arrays;

public class EditTimeElapsedTypePopup extends PopupPanel implements HasValueChangeHandlers<En_TimeElapsedType> {

    @Inject
    public void onInit(TimeElapsedTypeLang elapsedTimeTypeLang) {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);

        windowScrollHandler = event -> {
            if (isAttached()) {
                showNear(relative);
            }
        };

        typeSelector.setDisplayOptionCreator(type ->
                new DisplayOption((type == null || En_TimeElapsedType.NONE.equals(type)) ? lang.issueCommentElapsedTimeTypeLabel() : elapsedTimeTypeLang.getName(type)));
        typeSelector.fillOptions();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<En_TimeElapsedType> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("typeSelector")
    public void onTypeChanged(ValueChangeEvent<En_TimeElapsedType> event) {
        confirmBtn.setEnabled(event.getValue() != En_TimeElapsedType.NONE);
    }

    @UiHandler("confirmBtn")
    public void onConfirmClicked(ClickEvent event) {
        if (type != typeSelector.getValue()) {
            ValueChangeEvent.fire(this, typeSelector.getValue());
        }

        hide();
    }

    public void showNear(IsWidget nearWidget) {
        this.relative = nearWidget;
        root.getElement().getStyle().setDisplay(Style.Display.FLEX);
        typeSelector.getElement().setAttribute("style", "min-width: " + SELECTOR_WIDTH + "px;");
        setPopupPositionAndShow((popupWidth, popupHeight) -> {
            int relativeLeft = nearWidget.asWidget().getAbsoluteLeft();
            int popupTop = nearWidget.asWidget().getAbsoluteTop() + nearWidget.asWidget().getOffsetHeight();
            int nearWidgetWidth = nearWidget.asWidget().getOffsetWidth();
            setPopupPosition(relativeLeft - (nearWidgetWidth >= SELECTOR_WIDTH + confirmBtnSize ? 0 : (SELECTOR_WIDTH - nearWidgetWidth) + confirmBtnSize), popupTop);
        });
    }

    public void setTimeElapsedType(En_TimeElapsedType type) {
        this.type = type;
        typeSelector.setValue(type);
    }

    @Override
    protected void onLoad() {
        typeSelector.setValue(type == null ? En_TimeElapsedType.NONE : type);
        confirmBtn.setEnabled(type != En_TimeElapsedType.NONE);
        scrollHandlerReg = Window.addWindowScrollHandler(windowScrollHandler);
        confirmBtnSize = confirmBtn.getOffsetWidth();
    }

    @Override
    protected void onUnload() {
        if (scrollHandlerReg != null) {
            scrollHandlerReg.removeHandler();
        }
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField(provided = true)
    ElapsedTimeTypeSelector typeSelector;
    @UiField
    Button confirmBtn;
    @Inject
    @UiField
    Lang lang;

    private IsWidget relative;
    private Window.ScrollHandler windowScrollHandler;
    private HandlerRegistration scrollHandlerReg;
    private int confirmBtnSize;
    private En_TimeElapsedType type;
    private static final Integer SELECTOR_WIDTH = 210;

    interface EditTimeElapsedTypePopupUiBinder extends UiBinder<HTMLPanel, EditTimeElapsedTypePopup> {}
    private static EditTimeElapsedTypePopupUiBinder ourUiBinder = GWT.create(EditTimeElapsedTypePopupUiBinder.class);
}
