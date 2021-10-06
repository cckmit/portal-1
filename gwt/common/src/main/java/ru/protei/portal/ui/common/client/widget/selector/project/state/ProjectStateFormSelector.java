package ru.protei.portal.ui.common.client.widget.selector.project.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.lang.ProjectStateLang;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.popup.item.PopupSelectorItem;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;
import ru.protei.portal.ui.common.client.widget.selector.region.ProjectStateModel;

/**
 * Селектор состояния проекта
 */
public class ProjectStateFormSelector extends FormPopupSingleSelector<CaseState> {

    @Inject
    public void init( ProjectStateModel model ) {
        setSearchEnabled( false );
        setAsyncModel( model );
        setItemRenderer( value -> value == null ? defaultValue : getStateName( value ) );
        setValueRenderer( value -> value == null ? defaultValue :
                         "<i class='" + getStateIcon(value) + " selector' " +
                         "style='color:" + value.getColor() + "'></i>" + getStateName(value));
    }

    @Override
    protected SelectorItem<CaseState> makeSelectorItem( CaseState element, String elementHtml ) {
        PopupSelectorItem<CaseState> item = new PopupSelectorItem();
        item.setName( elementHtml );
        item.setStyle( "project-state-item" );
        if (element != null) {
            item.setTitle( getStateName(element) );
            item.setIcon( getStateIcon(element) + " selector" );
            item.setIconColor( element.getColor() );
        }

        return item;
    }

    public String getStateName( CaseState state ) {
        return projectStateLang.getStateName( state );
    }

    public String getStateIcon( CaseState state ) {
        return projectStateLang.getStateIcon( state );
    }

    @Inject
    ProjectStateLang projectStateLang;
}