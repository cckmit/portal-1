package ru.protei.portal.ui.document.client.activity.create;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.wizard.WizardWidgetActivity;

public interface AbstractDocumentCreateView extends IsWidget {

    void setActivity(AbstractDocumentCreateActivity activity);

    void setWizardActivity(WizardWidgetActivity activity);

    void resetWizard();

    // Wizard 1st tab

    HasWidgets projectSearchContainer();

    HasWidgets projectCreateContainer();

    HasVisibility projectSearchContainerVisibility();

    HasVisibility projectCreateContainerVisibility();

    void setProjectSearchActive();

    void setProjectCreateActive();

    // Wizard 2nd tab

    HasWidgets documentContainer();
}
