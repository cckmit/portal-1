package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentCategory;

/**
 * Названия типов документов
 */
public class En_DocumentCategoryLang {

    public String getDocumentCategoryName(En_DocumentCategory category) {
        if (category == null) {
            return lang.errUnknownResult();
        }

        switch (category) {
            case TP: return lang.tpDocumentCategory();
            case KD: return lang.kdDocumentCategory();
            case PD: return lang.pdDocumentCategory();
            case ED: return lang.edDocumentCategory();
            case TD: return lang.tdDocumentCategory();
            default: return lang.errUnknownResult();
        }
    }

    @Inject
    Lang lang;
}
