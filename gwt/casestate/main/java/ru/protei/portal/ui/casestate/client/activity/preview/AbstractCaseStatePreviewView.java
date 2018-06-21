package ru.protei.portal.ui.casestate.client.activity.preview;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;
import java.util.Set;

public interface AbstractCaseStatePreviewView extends IsWidget {
    void setActivity(AbstractCaseStatePreviewActivity activity);

    void setHeader(String header);

    void setName(String stateName);

    HasValue<String> description();

    HasValue<Set<EntityOption>> companies();



    void setUsageInCompanies(String stateName);
}
