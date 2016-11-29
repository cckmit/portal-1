package ru.protei.portal.ui.common.client.widget.pager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by shagaleev on 29/11/16.
 */
public class Pager extends Composite {
    public Pager() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }


    interface PagerUiBinder extends UiBinder< HTMLPanel, Pager > {}
    private static PagerUiBinder ourUiBinder = GWT.create( PagerUiBinder.class );

}