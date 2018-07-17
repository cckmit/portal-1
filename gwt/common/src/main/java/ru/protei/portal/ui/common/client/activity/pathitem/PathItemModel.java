package ru.protei.portal.ui.common.client.activity.pathitem;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.struct.PathItem;

import java.util.List;

/**
 * Модель элемента
 */
public class PathItemModel {

    public PathItemModel(HasWidgets parent, PathItem item, List<PathItem> data){
        this.parent = parent;
        this.item = item;
        this.data = data;
    }

    public HasWidgets parent;
    public PathItem item;
    public List<PathItem> data;
}
