package ru.protei.portal.ui.account.client.widget.role;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.ui.common.client.lang.En_PrivilegeEntityLang;
import ru.protei.portal.ui.common.client.widget.optionlist.base.ModelList;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Список ролей
 */
public class RoleOptionList extends OptionList< UserRole > implements ModelList< UserRole > {

    @Inject
    public void init( RoleModel model ) {
        setSelectorModel(model);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        filter = null;
    }

    @Override
    public void fillOptions( List< UserRole > userRoles ) {
        clearOptions();
        userRoles.forEach( option -> {
            addOption(
                    option.getCode(),
                    option.getInfo(),
                    option.getPrivileges().stream()
                            .filter( privilege -> privilege.getAction() != null )
                            .sorted( Comparator.comparingInt( En_Privilege::getOrder ) )
                            .collect( Collectors.groupingBy( En_Privilege::getEntity, Collectors.mapping( En_Privilege::getActionShortName, Collectors.joining() ) ) )
                            .entrySet().stream().map( entry -> entityLang.getName( entry.getKey() ) + ":" + entry.getValue() ).collect( Collectors.joining(", ") ),
                    option,
                    "list-item" ) ;

        } );
    }

    @Inject
    En_PrivilegeEntityLang entityLang;
}
