package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;
import java.util.function.Function;

/**
 * Селектор сотрудников домашней компании
 */
public class EmployeeFormSelector extends FormSelector<PersonShortView> implements SelectorWithModel<PersonShortView> {

    @Inject
    public void init(EmployeeModel employeeModel) {
        setSelectorModel(employeeModel);
        setSearchEnabled(true);
        setSearchAutoFocus(true);
        setFilter(personView -> !personView.isFired());

        setDisplayOptionCreator(value -> {
            if (value == null) {
                return new DisplayOption(transliterationFunction.apply(defaultValue));
            }

            return new DisplayOption(
                    transliterationFunction.apply(value.getDisplayShortName()),
                    value.isFired() ? "not-active" : "",
                    value.isFired() ? "fa fa-ban ban" : "");
        } );
    }

    @Override
    public void fillOptions(List<PersonShortView> persons){
        clearOptions();

        if(defaultValue != null) {
            addOption(null);
        }

        persons.forEach(value -> {
            value.setDisplayShortName(transliterationFunction.apply(value.getDisplayShortName()));
            addOption(value);
        });
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    public void setTransliterationFunction(Function<String, String> transliterationFunction) {
        this.transliterationFunction = transliterationFunction;
    }

    private Function<String, String> transliterationFunction = str -> str;

    private String defaultValue = null;
}
