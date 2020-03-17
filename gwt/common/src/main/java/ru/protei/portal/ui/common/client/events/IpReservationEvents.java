package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.Subnet;

/**
 * События по резервированию IP
 */
public class IpReservationEvents {

    /**
     * Показать список зарезервированных IP
     */
    @Url( value = "ip_reservation", primary = true )
    public static class Show {
        @Omit
        public Boolean clearScroll = false;
        public Show () {}
        public Show (Boolean clearScroll) {
            this.clearScroll = clearScroll;
        }
    }

    /**
     * Показать карточку зарезервированного IP
     */
/*    public static class ShowPreview {

        public ShowPreview( HasWidgets parent, ReservedIp reservedIp, boolean isShouldWrap ) {
            this.parent = parent;
            this.reservedIp = reservedIp;
            this.isShouldWrap = isShouldWrap;
        }

        public HasWidgets parent;
        public ReservedIp reservedIp;
        public boolean isShouldWrap;
    }*/

/*    @Url(value = "reserved_ip_preview", primary = true)
    public static class ShowFullScreen {
        public ShowFullScreen() {
        }

        public ShowFullScreen(Long reservedIpId) {
            this.reservedIpId = reservedIpId;
        }

        @Name("id")
        public Long reservedIpId;
    }*/

    /**
     * Показать форму создания подсети
     */
    @Url(value = "subnet")
    public static class CreateSubnet {}

    @Url( value = "subnet")
    public static class EditSubnet {
        public EditSubnet () {
            subnet = null;
        }
        public EditSubnet (Subnet subnet) {
            this.subnet = subnet;
        }
        public Subnet subnet;
    }

    /**
     * Показать форму резервирования IP
     */
    @Url(value = "reserved_ip")
    public static class CreateReservedIp {}

    @Url( value = "reserved_ip")
    public static class EditReservedIp {
        public EditReservedIp () {
            reservedIp = null;
        }
        public EditReservedIp (ReservedIp reservedIp) {
            this.reservedIp = reservedIp;
        }
        public ReservedIp reservedIp;
    }

    //public static class ChangeModel {}

    public static class ChangedSubnet {
        public ChangedSubnet(Subnet subnet, boolean needRefreshList) {
            this.subnet = subnet;
            this.needRefreshList = needRefreshList;
        }

        public Subnet subnet;
        public boolean needRefreshList = false;
    }

    public static class ChangedReservedIp {
        public ChangedReservedIp(ReservedIp reservedIp, boolean needRefreshList) {
            this.reservedIp = reservedIp;
            this.needRefreshList = needRefreshList;
        }

        public ReservedIp reservedIp;
        public boolean needRefreshList = false;
    }

    public static class CloseEdit {}

}
