package ru.protei.portal.ui.common.client.widget.timefield;

import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;

public class TimeLabel extends Label implements HasTime {

    private Long minutes;

    @Inject
    public void init(Lang lang) {
        workTime = new WorkTime(lang);
    }

    @Override
    public void setTime(Long minutes) {
        this.minutes = minutes;
        setText(workTime.asString(minutes));
    }

    @Override
    public Long getTime() {
        return minutes;
    }

    WorkTime workTime;
}
