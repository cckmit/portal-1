package ru.protei.portal.ui.issue.client.widget.buttonselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.issue.client.widget.StateModel;

import java.util.List;

/**
 * Селектор статусов обращения
 */
public class IssueStatesButtonSelector extends ButtonSelector<En_CaseState> implements ModelSelector<En_CaseState> {

    @Inject
    public void init( StateModel stateModel) {
        stateModel.subscribe(this);
    }

    @Override
    public void fillOptions(List<En_CaseState> options){
        clearOptions();

        if(defaultValue != null)
            addOption( defaultValue , null );

        options.forEach(option -> addOption(lang.getStateName(option), option));
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;

    @Inject
    En_CaseStateLang lang;

}
