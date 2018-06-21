package ru.protei.portal.ui.casestate.client.activity.preview;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Company;

import java.util.List;

public interface AbstractCaseStatePreviewView extends IsWidget {
    void setActivity(AbstractCaseStatePreviewActivity activity);

    void setHeader(String header);

    void setName(String stateName);

    void setDescription(String description);

    void setCompanies(List<Company> companies);

    void setUsageInCompanies(String stateName);
}
