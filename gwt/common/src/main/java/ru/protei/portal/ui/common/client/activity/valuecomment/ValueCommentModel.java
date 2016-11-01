package ru.protei.portal.ui.common.client.activity.valuecomment;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueComment;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель элемента
 */
public class ValueCommentModel {

    public ValueCommentModel(HasWidgets parent, List<ValueComment> data, ValueComment vc){
        this.parent = parent;
        this.data = data;
        this.valueComment = vc;
    }

    public HasWidgets parent;
    public ValueComment valueComment;
    public List<ValueComment> data;


}
