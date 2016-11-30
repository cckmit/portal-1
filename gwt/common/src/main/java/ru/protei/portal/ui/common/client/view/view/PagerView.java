package ru.protei.portal.ui.common.client.view.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Created by shagaleev on 29/11/16.
 */
public class PagerView extends Composite {
    public PagerView() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    public void setActivity( AbstractPagerActivity activity ) {
        this.activity = activity;
    }

    public void setPageSize( int value ) {
        pageSize = value;
        updateLabel();
    }

    public void setCurrentPage( int value ) {
        currentPage = value;
        updateLabel();
    }

    public void setTotalPages( int value ) {
        totalPages = value;
        updateLabel();
    }

    @UiHandler("fastBackward")
    public void onFastBackawardClicked( ClickEvent event ) {
        activity.onFirstClicked();
    }

    @UiHandler( "fastForward" )
    public void onFastForwardClicked( ClickEvent event ) {
        activity.onLastClicked();
    }

    private void updateLabel() {
        label.setInnerText( lang.pagerLabel( currentPage, totalPages, pageSize ) );
    }

    @UiField
    DivElement label;
    @UiField
    Anchor fastBackward;
    @UiField
    Anchor fastForward;

    @Inject
    Lang lang;

    AbstractPagerActivity activity;

    int pageSize = 0;
    int currentPage = 0;
    int totalPages = 0;

    interface PagerUiBinder extends UiBinder< HTMLPanel, PagerView> {}
    private static PagerUiBinder ourUiBinder = GWT.create( PagerUiBinder.class );

}