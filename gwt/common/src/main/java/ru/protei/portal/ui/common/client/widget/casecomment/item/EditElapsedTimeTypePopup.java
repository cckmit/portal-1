package ru.protei.portal.ui.common.client.widget.casecomment.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ResizeHandler;
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
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.TimeElapsedTypeLang;
import ru.protei.portal.ui.common.client.view.selector.ElapsedTimeTypeSelector;
import ru.protei.portal.ui.common.client.widget.enterabletextbox.EnterableTextBox;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.event.SelectorChangeValEvent;
import ru.protei.portal.ui.common.client.widget.selector.event.SelectorChangeValHandler;

import java.util.Arrays;

public class EditElapsedTimeTypePopup extends PopupPanel implements HasValueChangeHandlers<En_TimeElapsedType> {

    @Inject
    public void onInit(TimeElapsedTypeLang elapsedTimeTypeLang) {
        setWidget(ourUiBinder.createAndBindUi(this));
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);

        resizeHandler = resizeEvent -> {
            if (isAttached()) {
                showNear(relative);
            }
        };

        windowScrollHandler = event -> {
            if (isAttached()) {
                showNear(relative);
            }
        };

        relativeLeftIndent = Arrays.stream(En_TimeElapsedType.values())
                .mapToInt(type -> elapsedTimeTypeLang.getName(type).length())
                .max()
                .orElse(0);

        relativeLeftIndent *= 10;

        typeSelector.setDisplayOptionCreator(type ->
                new DisplayOption((type == null || En_TimeElapsedType.NONE.equals(type)) ? lang.issueCommentElapsedTimeTypeLabel() : elapsedTimeTypeLang.getName(type)));
        typeSelector.fillOptions();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<En_TimeElapsedType> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    protected void onLoad() {
        typeSelector.setValue(type == null ? En_TimeElapsedType.NONE : type);
        confirmBtn.setEnabled(type != En_TimeElapsedType.NONE);
        resizeHandlerReg = Window.addResizeHandler(resizeHandler);
        scrollHandlerReg = Window.addWindowScrollHandler(windowScrollHandler);
    }

    @Override
    protected void onUnload() {
        if (resizeHandlerReg != null) {
            resizeHandlerReg.removeHandler();
        }
        if (scrollHandlerReg != null) {
            scrollHandlerReg.removeHandler();
        }
    }

    public void showNear(IsWidget nearWidget) {
        this.relative = nearWidget;

        root.getElement().getStyle().setPosition(Style.Position.RELATIVE);
        root.getElement().getStyle().setDisplay(Style.Display.FLEX);
        typeSelector.getElement().setAttribute("style", "min-width: " + relativeLeftIndent + "px;");
        setPopupPositionAndShow((popupWidth, popupHeight) -> {
            int relativeLeft = nearWidget.asWidget().getAbsoluteLeft();
            int popupTop = nearWidget.asWidget().getAbsoluteTop() + nearWidget.asWidget().getOffsetHeight();
            int width = nearWidget.asWidget().getOffsetWidth();
            setPopupPosition(relativeLeft - (width - 50 >= relativeLeftIndent ? 0 : relativeLeftIndent - (width - 50)), popupTop);
        });
    }

    public void setTimeElapsedType(En_TimeElapsedType type) {
        this.type = type;
        typeSelector.setValue(type);
    }

    @UiHandler("typeSelector")
    public void onTypeChanged(ValueChangeEvent<En_TimeElapsedType> event) {
        confirmBtn.setEnabled(event.getValue() != En_TimeElapsedType.NONE);
    }

    @UiHandler("confirmBtn")
    public void onConfirmClicked(ClickEvent event) {
        ValueChangeEvent.fire(this, typeSelector.getValue());
        hide();
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
    private ResizeHandler resizeHandler;
    private Window.ScrollHandler windowScrollHandler;
    private HandlerRegistration resizeHandlerReg;
    private HandlerRegistration scrollHandlerReg;

    private Integer relativeLeftIndent;

    private En_TimeElapsedType type;

    interface EditElapsedTimeTypePopupUiBinder extends UiBinder<HTMLPanel, EditElapsedTimeTypePopup> {}
    private static EditElapsedTimeTypePopupUiBinder ourUiBinder = GWT.create(EditElapsedTimeTypePopupUiBinder.class);
}
