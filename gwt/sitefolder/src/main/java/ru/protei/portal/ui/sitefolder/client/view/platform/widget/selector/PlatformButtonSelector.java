package ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class PlatformButtonSelector extends ButtonSelector<PlatformOption> implements SelectorWithModel<PlatformOption> {

    @Inject
    void init(PlatformModel model) {
        setSelectorModel(model);
        setSearchEnabled(true);
        setHasNullValue(true);

        setDisplayOptionCreator(value -> makeView( value ) );
    }

    private DisplayOption makeView( PlatformOption value ) {
        if (value == null) return new DisplayOption( defaultValue );

        String name = value.getId() == null ? "" : value.getDisplayText();
        if(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_VIEW)){
            name = name + " <a class=\"full-screen-link\" href=\"" + LinkUtils.makeLink( Platform.class, value.getId() ) + "\" target=\"_blank\" id=\"" + DebugIds.ISSUE_PREVIEW.PLATFORM + "\"></a>";
        }

        return new DisplayOption( name );
    }


    @Override
    public void fillOptions(List<PlatformOption> options) {
        clearOptions();

        if (defaultValue != null) {
            addOption(null);
        }

        options.forEach(this::addOption);
        reselectValueIfNeeded();
    }

    @Inject
    PolicyService policyService;

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private String defaultValue = null;
}
