package ru.protei.portal.ui.common.client.activity.valuecomment;

/**
 * Состояния ValueCommentItem
 */
public enum ValueCommentStatus {
    NEW("greenC", "icon-plus"),
    FILLED("redC", "icon-cross");

    private String buttonColor;
    private String buttonIcon;

    ValueCommentStatus(String buttonColor, String buttonIcon){
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
