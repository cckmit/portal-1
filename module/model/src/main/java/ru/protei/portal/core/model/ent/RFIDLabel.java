package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@JdbcEntity(table = "rfid_label")
public class RFIDLabel implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "epc")
    private String epc;

    @JdbcColumn(name = "name")
    private String name;

    @JdbcColumn(name = "info")
    private String info;

    @JdbcColumn(name = "last_scan_date")
    private Date lastScanDate;

    @JdbcColumn(name = "rfid_device_id")
    private Long rfidDeviceId;

    @JdbcJoinedObject(localColumn = Columns.RFID_DEVICE_ID, remoteColumn = RFIDDevice.Columns.ID)
    private RFIDDevice rfidDevice;

    public RFIDLabel() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getLastScanDate() {
        return lastScanDate;
    }

    public void setLastScanDate(Date lastScanDate) {
        this.lastScanDate = lastScanDate;
    }

    public Long getRfidDeviceId() {
        return rfidDeviceId;
    }

    public void setRfidDeviceId(Long rfidDeviceId) {
        this.rfidDeviceId = rfidDeviceId;
    }

    public RFIDDevice getRfidDevice() {
        return rfidDevice;
    }

    public void setRfidDevice(RFIDDevice rfidDevice) {
        this.rfidDevice = rfidDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RFIDLabel rfidLabel = (RFIDLabel) o;
        return id.equals(rfidLabel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RFIDLabel{" +
                "id=" + id +
                ", epc='" + epc + '\'' +
                ", name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", lastScanDate=" + lastScanDate +
                ", rfidDeviceId=" + rfidDeviceId +
                ", rfidDevice=" + rfidDevice +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String RFID_DEVICE_ID = "rfid_device_id";
    }
}
