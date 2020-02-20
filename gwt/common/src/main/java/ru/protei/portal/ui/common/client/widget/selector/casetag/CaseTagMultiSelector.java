package ru.protei.portal.ui.common.client.widget.selector.casetag;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.CollectionUtils;

import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

public class CaseTagMultiSelector extends InputPopupMultiSelector<CaseTag>
{

    @Inject
    public void init(CaseTagModel model, Lang lang) {
        this.model = model;
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());

        setItemRenderer( caseTag -> makeText(caseTag) );
        setHasNullValue( true );
    }

    private String makeText( CaseTag tag ) {
        if (tag == null) {
            if (hasNullValue()) {
                return lang.tagNotSpecified();
            }
            return null;
        }

        String text = tag.getName();
        if (isProteiUser) text = text + " (" + tag.getCompanyName() + ")";
        return text;
    }

    public void setCaseType(En_CaseType caseType) {
        model.setCaseType(caseType);
    }

    public void isProteiUser(boolean isProteiUser){
        this.isProteiUser = isProteiUser;
    }

    @Inject
    private Lang lang;

    private CaseTagModel model;
    private boolean isProteiUser;
}
