package ru.protei.portal.ui.issuereport.client.widget.issuefilter.model;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Set;
import java.util.function.Function;

public interface AbstractIssueFilter {
    void setModel(AbstractIssueFilterModel model);
    void reset();
    CaseQuery getValue();
    HasVisibility productsVisibility();
    HasVisibility managersVisibility();
    HasVisibility searchPrivateVisibility();
    HasValue<Set<EntityOption>> companies();
    void updateInitiators();
    void setTransliterationFunction(Function<String, String> transliterationFunction);
}
