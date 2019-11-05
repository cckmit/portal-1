package ru.protei.portal.ui.common.client.widget.selector.company;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.widget.components.client.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Селектор списка компаний
 */
public class CompanyFormSelector
        extends FormSelector< EntityOption >
//        implements SelectorWithModel<EntityOption>
{

    @Inject
    public void init( CompanyModel companyModel ) {
//        model = companyModel;
//        model.subscribe(this, categories);
        setAsyncSelectorModel(companyModel);

        setSearchEnabled( true );
        setSearchAutoFocus( true );

//        setDisplayOptionCreator( value -> new DisplayOption( value == null ? defaultValue : value.getDisplayText() ) );
        setSelectorItemRenderer( value -> value == null ? defaultValue : value.getDisplayText() );
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }


    protected String defaultValue = null;
//    private boolean deferedApplyValueIfOneOption = false;
}
