package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * События по резервированию IP
 */
public class IpReservationEvents {

    /**
     * Показать список зарезервированных IP
     */
    @Url( value = "reserved_ips", primary = true )
    public static class ShowReservedIp {
    }

    /**
     * Показать форму резервирования IP
     */
    public static class CreateReservedIp {
        public CreateReservedIp(HasWidgets parent) {
            this.parent = parent;
            this.reservedIp = null;
        }
        public ReservedIp reservedIp;
        public HasWidgets parent;
    }

    /**
     * Показать форму редактирования зарезервированного IP
     */
    public static class EditReservedIp {
        public EditReservedIp (HasWidgets parent, ReservedIp reservedIp) {
            this.parent = parent;
            this.reservedIp = reservedIp;
        }
        public ReservedIp reservedIp;
        public HasWidgets parent;
    }

    public static class ChangedReservedIp {
/*        public ChangedReservedIp(ReservedIp reservedIp, boolean needRefreshList) {
            this.reservedIp = reservedIp;
            this.needRefreshList = needRefreshList;
        }*/

        public ChangedReservedIp(List<ReservedIp> reservedIps) {
            this.reservedIpList = new ArrayList<>(reservedIps);
            this.needRefreshList = true;
        }

        public List<ReservedIp> reservedIpList;
        public ReservedIp reservedIp;
        public boolean needRefreshList = false;
    }

    /**
     * Показать список подсетей
     */
    @Url( value = "subnets", primary = true )
    public static class ShowSubnet  {
    }

    /**
     * Показать форму  редактирования подсети
     */
    public static class EditSubnet {
        public EditSubnet (HasWidgets parent) {
            this.parent = parent;
            this.subnet = null;
        }
        public EditSubnet (HasWidgets parent, Subnet subnet) {
            this.parent = parent;
            this.subnet = subnet;
        }
        public Subnet subnet;
        public HasWidgets parent;
    }

    public static class ChangedSubnet {
        public ChangedSubnet(Subnet subnet, boolean needRefreshList) {
            this.subnet = subnet;
            this.needRefreshList = needRefreshList;
        }

        public Subnet subnet;
        public boolean needRefreshList = false;
    }

    public static class CloseEdit {}

    public static class ChangeModel {}

}
