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
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SlaInput extends Composite implements HasValue<List<ProjectSla>> {
    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public List<ProjectSla> getValue() {
        List<ProjectSla> result = new ArrayList<>();

        ProjectSla criticalSla = criticalItem.getValue();
        ProjectSla importantSla = importantItem.getValue();
        ProjectSla basicSla = basicItem.getValue();
        ProjectSla cosmeticSla = cosmeticItem.getValue();

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
    public void setValue(List<ProjectSla> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(List<ProjectSla> value, boolean fireEvents) {
        if (CollectionUtils.isNotEmpty(value)) {
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

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ProjectSla>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setEnsureDebugId(String debugId) {
        criticalItem.setEnsureDebugId(debugId + "-critical");
        importantItem.setEnsureDebugId(debugId + "-important");
        basicItem.setEnsureDebugId(debugId + "-basic");
        cosmeticItem.setEnsureDebugId(debugId + "-cosmetic");
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