package ru.protei.portal.ui.common.client.widget.selector.cardbatch;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class CardBatchFormSelector extends FormPopupSingleSelector<CardBatch> {
    @Inject
    public void init() {
        setItemRenderer( value -> value == null ? defaultValue : value.getNumber() );
        setExternalPopupMessage( () -> model.isTypePresent() ? null : lang.cardSelectType() );
    }

    public void setModel(CardBatchModel model) {
        this.model = model;
        setAsyncModel( model );
    }

    private CardBatchModel model;
}
