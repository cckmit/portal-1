package ru.protei.portal.ui.project.client.view.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class ProjectRoleButtonSelector extends ButtonSelector<En_DevUnitPersonRoleType> {

    @Inject
    public void init(Lang lang, En_PersonRoleTypeLang roleTypeLang) {
        setDisplayOptionCreator(value -> {
            if (value == null) {
                return new DisplayOption(lang.projectSelectRole());
            }
            if (En_DevUnitPersonRoleType.HEAD_MANAGER.equals(value)) {
                return new DisplayOption("<b>" + roleTypeLang.getName(value) + "</b>");
            }
            return new DisplayOption(roleTypeLang.getName(value));
        });
    }

    public void fillOptions(List<En_DevUnitPersonRoleType> availableRoles) {
        clearOptions();
        for (En_DevUnitPersonRoleType roleType : availableRoles) {
            addOption(roleType);
        }
    }
}