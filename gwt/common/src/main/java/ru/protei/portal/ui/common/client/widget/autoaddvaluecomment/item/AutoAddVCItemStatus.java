package ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.item;

/**
 * Created by bondarenko on 25.10.16.
 */
public enum AutoAddVCItemStatus {
    NEW("greenC", "icon-plus"),
    FILLED("redC", "icon-cross");

    private String buttonColor;
    private String buttonIcon;

    AutoAddVCItemStatus(String buttonColor, String buttonIcon){
        this.buttonColor = buttonColor;
        this.buttonIcon = buttonIcon;
    }

    public String getButtonColor() {
        return buttonColor;
    }

    public String getButtonIcon() {
        return buttonIcon;
    }
}
