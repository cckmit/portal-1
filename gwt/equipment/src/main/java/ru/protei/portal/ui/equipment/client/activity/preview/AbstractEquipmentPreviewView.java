package ru.protei.portal.ui.equipment.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида превью контакта
 */
public interface AbstractEquipmentPreviewView extends IsWidget {

    void setActivity( AbstractEquipmentPreviewActivity activity );

    void setHeader( String value );

    void setName( String value );

    void setNameBySldWrks( String value );

    void setComment( String value );

    void setType( String value );

    void setLinkedEquipment( String value );

    void setDecimalNumbers( String value );

    void setProject( String value );

    void setManager( String value );

    void setCopyBtnEnabledStyle( boolean isEnabled );

    void setRemoveBtnEnabledStyle( boolean isEnabled );

    void setCreatedDate(String created);

    void showFullScreen( boolean value );
}
