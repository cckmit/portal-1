package ru.protei.portal.ui.plan.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;

public interface AbstractPlanPreviewView extends IsWidget {
    void setActivity(AbstractPlanPreviewActivity activity);

    void setHeader(String value);

    void setName(String value);

    void setCreatedBy(String value);

    void setPeriod(String value);

    void showFullScreen(boolean isFullScreen);

    void clearRecords();

    void putRecords(List<CaseShortView> list);
}
