package ru.protei.portal.hpsm.struct;

import ru.protei.portal.hpsm.api.HpsmStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by michael on 24.04.17.
 */
public class EventSubject {
    public static final String SUBJ_FORMAT = "ID_HPSM=[%s]ID_VENDOR=[%s]STATUS=[%s]";
    public static final Pattern SUBJ_PATTERN = Pattern.compile("ID_HPSM=\\[([^\\]]+)\\]ID_VENDOR=\\[([^\\]]*)\\]STATUS=\\[([^\\]]+)\\]");

    private String hpsmId;
    private String ourId;
    private HpsmStatus status;


    public EventSubject (String hpsmId, String ourId, HpsmStatus status) {
        this.hpsmId = hpsmId;
        this.ourId = ourId == null ? "" : ourId;
        this.status = status;
    }

    public boolean isNewCaseRequest () {
        return this.ourId == null || this.ourId.isEmpty();
    }

    public String getHpsmId() {
        return hpsmId;
    }

    public String getOurId() {
        return ourId;
    }

    public HpsmStatus getStatus() {
        return status;
    }

    public String toString () {
        return String.format(SUBJ_FORMAT, this.hpsmId, this.ourId, this.status.getHpsmCode());
    }

    public static EventSubject parse (String subject) {

        Matcher m = SUBJ_PATTERN.matcher(subject);
        if (!m.matches())
            return null;

        HpsmStatus status = HpsmStatus.parse(m.group(3));

        if (status == null) {
            return null;
        }

        return new EventSubject(m.group(1), m.group(2), status);
    }
}
