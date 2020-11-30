package ru.protei.portal.ui.common.client.widget.issuefilterselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class IssueFilterSelector extends ButtonSelector< CaseFilterShortView > implements SelectorWithModel< CaseFilterShortView > {

    @Inject
    public void init( IssueFilterModel model ) {
        this.model = model;

        setSearchAutoFocus( true );
        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getName() ) );
    }

    @Override
    public void setValue(CaseFilterShortView value) {
        setValue(value, false);
    }

    @Override
    public void setValue(CaseFilterShortView value, boolean fireEvents) {
        fillMissingName(value);
        super.setValue(value, fireEvents);
    }

    public void changeValueName( CaseFilterShortView value ){
        if (value == null){
            return;
        }
        itemToDisplayOptionModel.get( value ).setName( value.getName() );
        refreshValue();
    }

    public void addDisplayOption( CaseFilterShortView value ){
        if (itemToDisplayOptionModel == null){
            return;
        }

        itemToDisplayOptionModel.put( value, new DisplayOption( value.getName() ) );
    }

    public void updateFilterType( En_CaseFilterType filterType ) {
        this.filterType = filterType;
        if ( model != null ) {
            model.updateFilterType( this, this.filterType );
        }
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    public void fillOptions( List< CaseFilterShortView > filters ) {
        clearOptions();

        if ( defaultValue != null ) {
            addOption( null );
        }

        if ( CollectionUtils.isEmpty( filters ) ) return;

        filters.sort( ( o1, o2 ) -> HelperFunc.compare( o1.getName(), o2.getName(), false ) );

        filters.forEach( this::addOption );
    }

    private void fillMissingName(CaseFilterShortView value) {
        if (value == null) {
            return;
        }
        if (value.getName() != null) {
            return;
        }
        if (value.getId() == null) {
            return;
        }
        String name = CollectionUtils.stream(itemToViewModel.keySet())
                .filter(Objects::nonNull)
                .filter(filter -> Objects.equals(filter.getId(), value.getId()))
                .distinct()
                .map(CaseFilterShortView::getName)
                .findFirst()
                .orElse(null);
        if (isEmpty(name)) {
            return;
        }
        value.setName(name);
    }

    private En_CaseFilterType filterType;
    private String defaultValue = null;
    private IssueFilterModel model;
}
