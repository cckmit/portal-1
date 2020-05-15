package ru.protei.portal.ui.common.client.widget.selector.platform;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class PlatformFormSelector extends FormPopupSingleSelector<PlatformOption> {

    @Inject
    void init(PlatformModel model) {
        setAsyncModel( model );
        setHasNullValue( true );
        setValueRenderer(this::makeStringFromValue);

        setItemRenderer(platformOption -> platformOption == null ? defaultValue : platformOption.getDisplayText());
    }

    private String makeStringFromValue(PlatformOption value) {
        String name = value == null ? defaultValue : value.getDisplayText();
        if (value != null && policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_VIEW)) {
            name = " <a class=\"fa fa-share m-r-5\" href=\"" + LinkUtils.makeLink(Platform.class, value.getId()) + "\" target=\"_blank\" id=\"" + DebugIds.SITE_FOLDER.LINK.PLATFORM + "\"></a>" + name;
        }

        return name;
    }

    @Inject
    PolicyService policyService;
}
