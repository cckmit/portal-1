package ru.protei.portal.ui.common.client.activity.valuecomment;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.ui.common.client.view.valuecomment.ValueComment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bondarenko on 31.10.16.
 */
public class ValueCommentModel {

    public ValueCommentModel(HasWidgets parent, List<AbstractValueCommentItemView> data){
        this.parent = parent;
        this.data = data;
    }

    public HasWidgets parent;
    public List<AbstractValueCommentItemView> data;

    public List<ValueComment> getData(){
        List<ValueComment> dataList = new ArrayList<>();
        data.forEach(item -> dataList.add(new ValueComment(item.value().getText(), item.comment().getText())));
        return dataList;
    }



}
