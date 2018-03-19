package ru.protei.portal.ui.equipment.client.widget.organization;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.ui.common.client.lang.En_OrganizationCodeLang;
import ru.protei.portal.ui.common.client.widget.togglebtn.group.ToggleBtnGroupMulti;

import java.util.stream.Stream;


/**
 * Типы огранизаций оборудования НТЦ протей
 */
public class OrganizationBtnGroupMulti extends ToggleBtnGroupMulti<En_OrganizationCode> {

    @Inject
    public void init() {
        fillOptions();
    }

    private void fillOptions() {
        clear();
        Stream.of(En_OrganizationCode.values()).forEach(code -> addBtn(codeToString(code), code));
    }

    private String codeToString(En_OrganizationCode code) {
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
