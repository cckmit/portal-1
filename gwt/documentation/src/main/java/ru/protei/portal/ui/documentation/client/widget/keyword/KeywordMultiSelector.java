package ru.protei.portal.ui.documentation.client.widget.keyword;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.optionlist.base.ModelList;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

public class KeywordMultiSelector
        extends MultipleInputSelector<String>
        implements ModelList<String> {


    @Inject
    public void init(Lang lang) {
        setAddName(lang.addKeyword());
    }

    @Override
    public void fillOptions(List<String> items) {
        clearOptions();
        items.forEach(i -> addOption(i, i));
    }
}
