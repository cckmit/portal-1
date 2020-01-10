package ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector;

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

        setItemRenderer( value -> makeView( value ) );
    }

    private String makeView( PlatformOption platformOption ) {
        String name = platformOption == null ? defaultValue : platformOption.getDisplayText();
        if(platformOption != null
                && policyService.hasPrivilegeFor( En_Privilege.ISSUE_PLATFORM_VIEW)){
            name = name + " <a class=\"fa fa-share m-l-5\" href=\"" + LinkUtils.makeLink( Platform.class, platformOption.getId() ) + "\" target=\"_blank\" id=\"" + DebugIds.SITE_FOLDER.LINK.PLATFORM + "\"></a>";
        }
        return name;

    }

//    public void setDefaultValue(String defaultValue) {
//        this.defaultValue = defaultValue;
//    }

    @Inject
    PolicyService policyService;

//    private String defaultValue = null;
}
