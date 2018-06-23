package ru.protei.portal.ui.issue.client.widget.state.buttonselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.issue.client.widget.state.StateModel;

import java.util.List;

/**
 * Селектор статусов обращения
 */
public class IssueStatesButtonSelector extends ButtonSelector<En_CaseState> implements ModelSelector<En_CaseState> {

    @Inject
    public void init( StateModel stateModel, En_CaseStateLang lang ) {
        stateModel.subscribe(this);
        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : lang.getStateName( value ) ) );
    }

    @Override
    public void fillOptions(List<En_CaseState> options){
        clearOptions();

        if( defaultValue != null ) {
            addOption( null );
        }
        options.stream()
//                .filter(o -> filter == null || filter.isDisplayed(o))
                .forEach( this::addOption );
    }

    @Override
    public void refreshValue() {
        En_CaseState value = getValue();
        if (value == null) return;
        if (filter == null) return;
        if (!filter.isDisplayed(value)) {
            setValue(null);
        }else{
          super.refreshValue();
        }
    }

    public void setDefaultValue(String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;
}
