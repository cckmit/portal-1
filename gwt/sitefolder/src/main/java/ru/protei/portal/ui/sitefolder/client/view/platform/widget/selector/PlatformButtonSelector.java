package ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.widget.components.client.button.ButtonPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.impl.HtmlSelectorItemRenderer;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class PlatformButtonSelector extends ButtonPopupSingleSelector<PlatformOption> {

    @Inject
    void init(PlatformModel model) {
        setAsyncSelectorModel(model);
        setHasNullValue(true);

        setSelectorItemRenderer(value -> makeView( value ) );
    }

    private String makeView( PlatformOption platformOption ) {
        String name = platformOption == null ? defaultValue : platformOption.getDisplayText();
        if(platformOption != null
                && policyService.hasPrivilegeFor( En_Privilege.ISSUE_PLATFORM_VIEW)){
            name = name + " <a class=\"full-screen-link\" href=\"" + LinkUtils.makeLink( Platform.class, platformOption.getId() ) + "\" target=\"_blank\" id=\"" + DebugIds.SITE_FOLDER.LINK.PLATFORM + "\"></a>";
        }
        return name;
    }


    @Inject
    PolicyService policyService;

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private String defaultValue = null;
}
