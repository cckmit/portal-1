package ru.protei.portal.ui.issue.client.widget.btngroup;

import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.widget.togglebtngroup.ToggleBtnGroup;

import java.util.List;

/**
 * Created by frost on 11/18/16.
 */
public class ImportanceBtnGroup extends ToggleBtnGroup<En_ImportanceLevel>  {

//    @Inject
//    public void init( CategoryModel categoryModel) {
//        categoryModel.subscribe( this );
//    }

//    @Override
    public void fillOptions( List<En_ImportanceLevel> importance ) {
        clear();

        for ( En_ImportanceLevel item : importance ) {
            addBtn( item.getCode(), item );
        }
    }

}