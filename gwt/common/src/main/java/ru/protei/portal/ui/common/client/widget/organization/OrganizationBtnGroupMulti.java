package ru.protei.portal.ui.common.client.widget.organization;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Organization;
import ru.protei.portal.ui.common.client.lang.En_OrganizationCodeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.stream.Stream;


/**
 * Типы огранизаций оборудования НТЦ протей
 */
public class OrganizationBtnGroupMulti extends ToggleBtnGroupMulti<En_Organization> {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();
        Stream.of(En_Organization.values()).forEach(code -> addBtn(codeToString(code), code));
    }

    private String codeToString(En_Organization code) {
        return useCompanyName ? codeLang.getCompanyName(code) : codeLang.getName(code);
    }

    public void setUseCompanyName(boolean useCompanyName) {
        if (this.useCompanyName == useCompanyName) {
            return;
        }
        this.useCompanyName = useCompanyName;
        fillOptions();
    }

    @Inject
    En_OrganizationCodeLang codeLang;

    private boolean useCompanyName = true;
}
