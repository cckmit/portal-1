package ru.protei.portal.ui.project.client.view.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOptionCreator;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class ProjectRoleButtonSelector extends ButtonSelector<En_DevUnitPersonRoleType> {

    @Inject
    public void init(Lang lang, En_PersonRoleTypeLang roleTypeLang) {
        setDisplayOptionCreator(new DisplayOptionCreator<En_DevUnitPersonRoleType>() {
            @Override
            public DisplayOption makeDisplayOption(En_DevUnitPersonRoleType value) {
                return new DisplayOption(value == null ? lang.projectSelectRole() : roleTypeLang.getName(value));
            }
            @Override
            public DisplayOption makeDisplaySelectedOption(En_DevUnitPersonRoleType value) {
                return new DisplayOption(value == null ? lang.projectSelectRole() : "<b>" + roleTypeLang.getName(value) + "</b>");
            }
        });
    }

    public void fillOptions(List<En_DevUnitPersonRoleType> availableRoles) {
        clearOptions();
        for (En_DevUnitPersonRoleType roleType : availableRoles) {
            addOption(roleType);
        }
    }
}