package ru.protei.portal.ui.common.client.widget.selector.casetag;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

public class CaseTagMultiSelector extends MultipleInputSelector<EntityOption> implements SelectorWithModel<EntityOption> {

    @Inject
    public void init(CaseTagModel model, Lang lang) {
        this.model = model;
        setSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        subscribeIfReady();
    }

    public void setCaseType(En_CaseType caseType) {
        this.caseType = caseType;
        subscribeIfReady();
    }

    public void setHasNullValue(boolean hasNullValue) {
        this.hasNullValue = hasNullValue;
    }

    private void subscribeIfReady() {
        if (isSubscribed || model == null || caseType == null) {
            return;
        }
        model.subscribe(this, caseType);
        isSubscribed = true;
    }

    @Override
    public void fillOptions(List<EntityOption> options) {
        clearOptions();
        if (CollectionUtils.isEmpty(options)) {
            return;
        }
        if (hasNullValue) {
            addOption(lang.tagNotSpecified(), new EntityOption(lang.tagNotSpecified(), CrmConstants.CaseTag.NOT_SPECIFIED));
        }
        options.forEach(caseTag -> addOption(caseTag.getDisplayText(), caseTag));
    }

    @Inject
    private Lang lang;

    private boolean isSubscribed = false;
    private CaseTagModel model;
    private En_CaseType caseType;
    private boolean hasNullValue = true;
}
