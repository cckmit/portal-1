package ru.protei.portal.ui.employee.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

import java.util.List;

public interface AbstractEmployeeTableView extends IsWidget {

    void setActivity(AbstractEmployeeTableActivity activity);

    void clearRecords();

    void addRecords(List<EmployeeShortView> employees);

    HasWidgets getPreviewContainer();

    HasWidgets getFilterContainer();

    HasWidgets getPagerContainer();


    void updateRow(EmployeeShortView item);

    void removeRow(EmployeeShortView item);

    void setAnimation(TableAnimation animation);

    void clearSelection();

    void initTable(List<Long> employeeBirthdayHideIds);
}
