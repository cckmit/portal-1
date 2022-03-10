package ru.protei.portal.ui.common.client.widget.selector.personrole;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOptionCreator;

import java.util.List;

public class ProjectRoleFormSelector extends FormSelector<En_PersonRoleType> {

    @Inject
    public void init(Lang lang, En_PersonRoleTypeLang roleTypeLang) {
        setDisplayOptionCreator(new DisplayOptionCreator<En_PersonRoleType>() {
            @Override
            public DisplayOption makeDisplayOption(En_PersonRoleType value) {
                return new DisplayOption(value == null ? lang.projectSelectRole() : roleTypeLang.getName(value));
            }
            @Override
            public DisplayOption makeDisplaySelectedOption(En_PersonRoleType value) {
                return new DisplayOption(value == null ? lang.projectSelectRole() : roleTypeLang.getName(value));
            }
        });
    }

    public void fillOptions(List<En_PersonRoleType> availableRoles) {
        clearOptions();
        for (En_PersonRoleType roleType : availableRoles) {
            addOption(roleType);
        }
    }
}