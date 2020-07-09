package ru.protei.portal.ui.casestate.client.activity.preview;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CaseStateUsageInCompanies;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;

public interface AbstractCaseStatePreviewView extends IsWidget {
    void setActivity(AbstractCaseStatePreviewActivity activity);

    void setName(String stateName);

    HasValue<String> description();

    HasValue<Set<EntityOption>> companies();

    HasValue<En_CaseStateUsageInCompanies> usageInCompanies();

    HasVisibility companiesVisibility();

    void setViewEditable(boolean isEnabled);
}
