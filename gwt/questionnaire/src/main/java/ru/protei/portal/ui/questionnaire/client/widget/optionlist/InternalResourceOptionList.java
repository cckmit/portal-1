package ru.protei.portal.ui.questionnaire.client.widget.optionlist;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_InternalResource;
import ru.protei.portal.ui.common.client.lang.En_InternalResourceLang;
import ru.protei.portal.ui.common.client.widget.optionlist.list.OptionList;

public class InternalResourceOptionList extends OptionList<En_InternalResource> {

    @Inject
    public void init(En_InternalResourceLang lang) {
        for (En_InternalResource resource : En_InternalResource.values())
            addOption(lang.getName(resource), resource);
    }
}
