package ru.protei.portal.ui.common.client.widget.sla;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.ContractSla;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SlaInput extends Composite implements HasValue<List<ContractSla>> {
    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public List<ContractSla> getValue() {
        List<ContractSla> result = new ArrayList<>();

        ContractSla criticalSla = criticalItem.getValue();
        ContractSla importantSla = importantItem.getValue();
        ContractSla basicSla = basicItem.getValue();
        ContractSla cosmeticSla = cosmeticItem.getValue();

        if (criticalSla != null) {
            criticalSla.setImportanceLevelId(En_ImportanceLevel.CRITICAL.getId());
            result.add(criticalSla);
        }

        if (importantSla != null) {
            importantSla.setImportanceLevelId(En_ImportanceLevel.IMPORTANT.getId());
            result.add(importantSla);
        }

        if (basicSla != null) {
            basicSla.setImportanceLevelId(En_ImportanceLevel.BASIC.getId());
            result.add(basicSla);
        }

        if (cosmeticSla != null) {
            cosmeticSla.setImportanceLevelId(En_ImportanceLevel.COSMETIC.getId());
            result.add(cosmeticSla);
        }

        return result;
    }

    @Override
    public void setValue(List<ContractSla> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(List<ContractSla> value, boolean fireEvents) {
        if (CollectionUtils.isEmpty(value)) {
            criticalItem.setValue(null);
            importantItem.setValue(null);
            basicItem.setValue(null);
            cosmeticItem.setValue(null);
        }

        value.forEach(contractSla -> {
            if (contractSla.getImportanceLevelId() == En_ImportanceLevel.CRITICAL.getId()) {
                criticalItem.setValue(contractSla);
            }

            if (contractSla.getImportanceLevelId() == En_ImportanceLevel.IMPORTANT.getId()) {
                importantItem.setValue(contractSla);
            }

            if (contractSla.getImportanceLevelId() == En_ImportanceLevel.BASIC.getId()) {
                basicItem.setValue(contractSla);
            }

            if (contractSla.getImportanceLevelId() == En_ImportanceLevel.COSMETIC.getId()) {
                cosmeticItem.setValue(contractSla);
            }
        });
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ContractSla>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Inject
    @UiField(provided = true)
    SlaRowItem criticalItem;

    @Inject
    @UiField(provided = true)
    SlaRowItem importantItem;

    @Inject
    @UiField(provided = true)
    SlaRowItem basicItem;

    @Inject
    @UiField(provided = true)
    SlaRowItem cosmeticItem;

    interface SlaInputUiBinder extends UiBinder<HTMLPanel, SlaInput> {
    }

    private static SlaInputUiBinder ourUiBinder = GWT.create(SlaInputUiBinder.class);
}