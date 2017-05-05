package ru.protei.portal.ui.common.client.widget.selector.workinggroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.WorkingGroup;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

/**
 * Button селектор с рабочими группами
 */
public class WorkingGroupButtonSelector extends ButtonSelector<WorkingGroup> implements ModelSelector<WorkingGroup> {

    @Inject
    public void init( WorkingGroupModel productModel) {
        productModel.subscribe( this );
        setSearchEnabled( true );
        setSearchAutoFocus( true );
    }

    public void fillOptions( List< WorkingGroup > groups) {
        clearOptions();

//        if(defaultValue != null) {
        addOption( "-", null );
        setValue(null);
//        }

        groups.forEach(group -> addOption(new DisplayOption(
                        group.getName(),
                         "" ,
                         ""),
                group));
     }

//    public void setDefaultValue( String value ) {
//        this.defaultValue = value;
//    }

//    @Inject
//    Lang lang;

//    private String defaultValue = null;

}
