package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;

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
    public static class ShowPreview {

        public ShowPreview( HasWidgets parent, Long reservedIpId, boolean isShouldWrap ) {
            this.parent = parent;
            this.reservedIpId = reservedIpId;
            this.isShouldWrap = isShouldWrap;
        }

        public HasWidgets parent;
        public Long reservedIpId;
        public boolean isShouldWrap;
    }

    @Url(value = "reserved_ip_preview", primary = true)
    public static class ShowFullScreen {
        public ShowFullScreen() {
        }

        public ShowFullScreen(Long reservedIpId) {
            this.reservedIpId = reservedIpId;
        }

        @Name("id")
        public Long reservedIpId;
    }

    /**
     * Показать форму создания подсети
     */
    @Url(value = "subnet")
    public static class CreateSubnet {}

    @Url( value = "subnet")
    public static class EditSubnet {
        public EditSubnet () {
            subnetId = null;
        }
        public EditSubnet (Long id) {
            this.subnetId = id;
        }
        public Long subnetId;
    }

    /**
     * Показать форму резервирования IP
     */
    @Url(value = "reserved_ip")
    public static class CreateReservedIp {}

    @Url( value = "reserved_ip")
    public static class EditReservedIp {
        public EditReservedIp () {
            reservedIpId = null;
        }
        public EditReservedIp (Long id) {
            this.reservedIpId = id;
        }
        public Long reservedIpId;
    }

    public static class ChangeModel {}

}
