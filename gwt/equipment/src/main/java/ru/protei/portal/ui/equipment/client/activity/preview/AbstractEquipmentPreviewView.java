package ru.protei.portal.ui.equipment.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида превью контакта
 */
public interface AbstractEquipmentPreviewView extends IsWidget {

    void setActivity( AbstractEquipmentPreviewActivity activity );

    void setHeader( String value );

    void setNameBySpecification( String value );

    void setNameBySldWrks( String value );

    void setComment( String value );

    void setPDRA_decimalNumber( String value );

    void setPAMR_decimalNumber( String value );

    void setType( String value );

    void setStage( String value, String styleNamePrefix );
}
