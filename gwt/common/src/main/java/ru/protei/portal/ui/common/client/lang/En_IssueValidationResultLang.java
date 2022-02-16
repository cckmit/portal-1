package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_IssueValidationResult;

public class En_IssueValidationResultLang {

    @Inject
    public En_IssueValidationResultLang(Lang lang) {
        this.lang = lang;
    }

    public String getMessage( En_IssueValidationResult value )
    {
        if (value == null)
            return lang.errUnknownResult();

        switch (value)
        {
            case OK : return lang.issueValidationResultOk();
            case NULL: return lang.issueValidationResultNull();
            case NAME_EMPTY: return lang.issueValidationResultNameEmpty();
            case TYPE_EMPTY: return lang.issueValidationResultTypeEmpty();
            case CREATOR_EMPTY: return lang.issueValidationResultCreatorEmpty();
            case IMPORTANCE_EMPTY: return lang.issueValidationResultImportanceEmpty();
            case MANAGER_EMPTY: return lang.issueValidationResultManagerEmpty();
            case MANAGER_OTHER_COMPANY: return lang.issueValidationResultManagerOtherCompany();
            case MANAGER_WITHOUT_PRODUCT: return lang.issueValidationResultManagerWithoutProduct();
            case INITIATOR_EMPTY: return lang.issueValidationResultInitiatorEmpty();
            case STATUS_INVALID: return lang.issueValidationResultStatusInvalid();
            case IMPORTANCE_OTHER_COMPANY: return lang.issueValidationResultImportanceOtherCompany();
            case INITIATOR_OTHER_COMPANY: return lang.issueValidationResultInitiatorOtherCompany();
            case PLATFORM_OTHER_COMPANY: return lang.issueValidationResultPlatformOtherCompany();
            case PRODUCT_INVALID: return lang.issueValidationResultProductInvalid();
            case DEADLINE_PASSED: return lang.issueValidationResultDeadlinePassed();
            default: return lang.errUnknownResult();
        }
    }

    Lang lang;
}
