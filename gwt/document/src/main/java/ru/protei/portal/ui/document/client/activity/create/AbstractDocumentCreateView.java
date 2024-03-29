package ru.protei.portal.ui.document.client.activity.create;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.wizard.WizardWidgetActivity;

public interface AbstractDocumentCreateView extends IsWidget {

    void setActivity(AbstractDocumentCreateActivity activity);

    void setWizardActivity(WizardWidgetActivity activity);

    void setWizardButtonsEnabled(boolean isEnabled);

    void resetWizard();

    // Wizard 1st tab

    void setWizardTab(String tabName);

    HasWidgets projectSearchContainer();

    HasWidgets projectCreateContainer();

    HasVisibility projectSearchContainerVisibility();

    HasVisibility projectCreateContainerVisibility();

    void setProjectSearchActive();

    void setProjectCreateActive();

    HasEnabled createEnabled();

    // Wizard 2nd tab

    HasWidgets documentContainer();
}
