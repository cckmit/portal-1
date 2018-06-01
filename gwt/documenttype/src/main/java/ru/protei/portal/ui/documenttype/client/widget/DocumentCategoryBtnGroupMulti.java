package ru.protei.portal.ui.documenttype.client.widget;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DocumentCategory;
import ru.protei.portal.ui.common.client.lang.En_DocumentCategoryLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.stream.Stream;

public class DocumentCategoryBtnGroupMulti extends ToggleBtnGroupMulti<En_DocumentCategory> {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();
        Stream.of(En_DocumentCategory.values()).forEach(code -> addBtn(categoryLang.getDocumentCategoryName(code), code));
    }

    @Inject
    En_DocumentCategoryLang categoryLang;
}
