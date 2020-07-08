package ru.protei.portal.ui.common.client.widget.selector.contractor.organizationselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Organization;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;

import java.util.Arrays;

public class OrganizationModel extends BaseSelectorModel<En_Organization> {
    @Inject
    public void onInit() {
        updateElements(Arrays.asList(En_Organization.values()));
    }
}
