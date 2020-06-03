package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.struct.NotificationEntry;

import java.util.Date;
import java.util.List;

public class ReservedIpReleaseRemainingEvent extends ApplicationEvent {

    public ReservedIpReleaseRemainingEvent(Object source, List<ReservedIp> reservedIps,
                                           Date releaseDateStart, Date releaseDateEnd,
                                           List<NotificationEntry> notificationEntryList) {
        super(source);
        this.reservedIps = reservedIps;
        this.releaseDateStart = releaseDateStart;
        this.releaseDateEnd = releaseDateEnd;
        this.notificationEntryList = notificationEntryList;
    }

    public List<ReservedIp> getReservedIpList() { return reservedIps; }

    public Date getReleaseDateStart() { return releaseDateStart; }

    public Date getReleaseDateEnd() { return releaseDateEnd; }

    public List<NotificationEntry> getNotificationEntryList() { return notificationEntryList; }

    private List<ReservedIp> reservedIps;
    private Date releaseDateStart;
    private Date releaseDateEnd;
    private List<NotificationEntry> notificationEntryList;
}