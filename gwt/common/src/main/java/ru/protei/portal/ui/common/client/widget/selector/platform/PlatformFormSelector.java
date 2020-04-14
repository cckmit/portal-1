package ru.protei.portal.ui.common.client.widget.selector.platform;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class PlatformFormSelector extends FormPopupSingleSelector<PlatformOption> {

    @Inject
    void init(PlatformModel model) {
        setAsyncModel( model );
        setHasNullValue( true );

        setItemRenderer(this::makeView);
    }

    protected SelectorItem<PlatformOption> makeSelectorItem(PlatformOption element, String elementHtml ) {
        PopupSelectorItem<PlatformOption> item = new PopupSelectorItem<>();
        item.setName(element == null ? defaultValue : element.getDisplayText());
        return item;
    }

    private String makeView(PlatformOption platformOption) {
        String name = platformOption == null ? defaultValue : platformOption.getDisplayText();
        if (platformOption != null && policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_VIEW)) {
            name = name + " <a class=\"fa fa-share m-l-5\" href=\"" + LinkUtils.makeLink(Platform.class, platformOption.getId()) + "\" target=\"_blank\" id=\"" + DebugIds.SITE_FOLDER.LINK.PLATFORM + "\"></a>";
        }

        return name;
    }

    @Inject
    PolicyService policyService;
}