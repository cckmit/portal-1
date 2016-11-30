package ru.protei.portal.ui.issue.client.widget.importance.btngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.common.CriticalityStyleBuilder;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroup;

/**
 * Селектор критичности обращения
 */
public class ImportanceBtnGroup extends ToggleBtnGroup<En_ImportanceLevel> {

    @Inject
    public void init() {
        fillButtons();
    }

    public void fillButtons() {
        clear();

        for ( En_ImportanceLevel type : En_ImportanceLevel.values() ) {
            addBtnWithIcon( "importance importance-lg " + CriticalityStyleBuilder.make().getClassName( type ) + " " + type.toString(),
                    "width-xs text-center button whiteC " + type.toString(),
                    lang.getImportanceName( type ),
                    "col-xs-12 col-sm-6",
                    type );
        }
    }

    @Inject
    En_CaseImportanceLang lang;
}