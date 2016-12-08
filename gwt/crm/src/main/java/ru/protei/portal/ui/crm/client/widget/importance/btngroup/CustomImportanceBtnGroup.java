package ru.protei.portal.ui.crm.client.widget.importance.btngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.common.CriticalityStyleBuilder;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

/**
 * настраиваемый селектор критичности обращения
 */
public class CustomImportanceBtnGroup extends ToggleBtnGroup<En_ImportanceLevel> {

    public void init(
            String iconClassName,
            String buttonClassName,
            boolean showCaption,
            String outerClassName
    ){
        clear();

        for ( En_ImportanceLevel type : En_ImportanceLevel.values() )
            addBtnWithIcon( iconClassName +" "+ CriticalityStyleBuilder.make().getClassName( type ) +" "+ type.toString().toLowerCase(),
                    buttonClassName,
                    showCaption? lang.getImportanceName( type ): null,
                    outerClassName,
                    type );
    }

    @Inject
    En_CaseImportanceLang lang;


}
