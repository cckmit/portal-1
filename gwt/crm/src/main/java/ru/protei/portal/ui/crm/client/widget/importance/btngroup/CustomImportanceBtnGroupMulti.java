package ru.protei.portal.ui.crm.client.widget.importance.btngroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

/**
 * настраиваемый селектор критичности обращения
 */
public class CustomImportanceBtnGroupMulti extends ToggleBtnGroupMulti<En_ImportanceLevel> {

    public void init(
            String iconClassName,
            String buttonClassName,
            boolean showCaption,
            String outerClassName
    ){
        clear();

        for (En_ImportanceLevel type : En_ImportanceLevel.values()) {
            addBtnWithIcon(
                    ImportanceStyleProvider.getImportanceIcon(type) + " " + iconClassName,
                    buttonClassName,
                    showCaption ? lang.getImportanceName(type) : null,
                    type
            );
        }
    }

    @Inject
    En_CaseImportanceLang lang;


}
