package ru.protei.portal.ui.common.client.widget.timefield;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

public class TimeTextBox extends ValidableTextBox implements HasTime {

    public TimeTextBox() {
        super();
    }

    @Inject
    public void init(Lang lang) {
        workTimeFormatter = new WorkTimeFormatter(lang);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        getElement().setAttribute("autocapitalize", "off");
        getElement().setAttribute("autocorrect", "off");
        getElement().setAttribute("autocomplete", "off");
        getElement().setAttribute("placeholder", workTimeFormatter.getPlaceholder());
        setRegexp(workTimeFormatter.getPattern());
    }

    @Override
    public void setTime(Long minutes) {
        setValue( workTimeFormatter.asString(minutes));
    }

    @Override
    public Long getTime() {
        String value = getValue();
        if ( value.isEmpty() || !isValid()) {
            return null;
        }

        return workTimeFormatter.asTime(value);
    }

    WorkTimeFormatter workTimeFormatter;
}
