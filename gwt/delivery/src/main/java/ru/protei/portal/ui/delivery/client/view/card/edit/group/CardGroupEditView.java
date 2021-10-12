package ru.protei.portal.ui.delivery.client.view.card.edit.group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.delivery.client.activity.card.edit.group.AbstractCardGroupEditView;

public class CardGroupEditView extends Composite implements AbstractCardGroupEditView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    private static CardGroupEditView.ViewUiBinder ourUiBinder = GWT.create(CardGroupEditView.ViewUiBinder.class);
    interface ViewUiBinder extends UiBinder<HTMLPanel, CardGroupEditView> {}
}
