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
        workTime = new WorkTime(lang);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        getElement().setAttribute("autocapitalize", "off");
        getElement().setAttribute("autocorrect", "off");
        getElement().setAttribute("autocomplete", "off");
        getElement().setAttribute("placeholder", workTime.getPlaceholder());
        setRegexp(workTime.getPattern());
    }

    @Override
    public void setTime(Long minutes) {
        setValue( workTime.asString(minutes));
    }

    @Override
    public Long getTime() {
        String value = getValue();
        if ( value.isEmpty() || !isValid()) {
            return null;
        }

        return workTime.asTime(value);
    }

    WorkTime workTime;
}
