package ru.protei.portal.ui.equipment.client.activity.preview;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида превью контакта
 */
public interface AbstractEquipmentPreviewView extends IsWidget {

    void setActivity( AbstractEquipmentPreviewActivity activity );

    void setHeader( String value );

    void setHeaderHref(String value);

    void setNameBySldWrks(String value );

    void setComment( String value );

    void setType( String value );

    void setLinkedEquipment( String value );

    void setDecimalNumbers( String value );

    void setProject( String value );

    void setManager( String value );

    void setCopyBtnEnabledStyle(boolean isEnabled );

    void setRemoveBtnEnabledStyle( boolean isEnabled );

    void setCreatedBy(String created);

    HasWidgets documents();

    void setLinkedEquipmentExternalLink(String s);

    void isFullScreen(boolean isFullScreen);
}
