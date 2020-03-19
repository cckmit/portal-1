package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.DocumentType;
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
    public static class Show {}

    /**
     * Показать карточку зарезервированного IP
     */
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
    public static class CreateSubnet {
        public CreateSubnet(HasWidgets parent, Subnet subnet) {
            this.parent = parent;
            this.subnet = subnet;
        }

        public Subnet subnet;
        public HasWidgets parent;
    }

    public static class EditSubnet {
/*        public EditSubnet () {
            subnetId = null;
        }*/
        public EditSubnet (HasWidgets parent, Long subnetId) {
            this.parent = parent;
            this.subnetId = subnetId;
        }
        public Long subnetId;
        public HasWidgets parent;
    }

    /**
     * Показать форму резервирования IP
     */
    public static class CreateReservedIp {
        public CreateReservedIp(HasWidgets parent, ReservedIp reservedIp) {
            this.parent = parent;
            this.reservedIp = reservedIp;
        }

        public ReservedIp reservedIp;
        public HasWidgets parent;
    }

    public static class EditReservedIp {
/*        public EditReservedIp () {
            reservedIpId = null;
        }*/
        public EditReservedIp (HasWidgets parent, Long reservedIpId) {
            this.parent = parent;
            this.reservedIpId = reservedIpId;
        }
        public Long reservedIpId;
        public HasWidgets parent;
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
