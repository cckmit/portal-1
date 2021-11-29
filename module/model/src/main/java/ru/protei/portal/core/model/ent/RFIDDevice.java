package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.Objects;

@JdbcEntity(table = "rfid_device")
public class RFIDDevice implements Serializable {

    @JdbcId(name = Columns.ID, idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = Columns.READER_ID)
    private String readerId;

    @JdbcColumn(name = "name")
    private String name;

    public RFIDDevice() {}

    public RFIDDevice(String readerId) {
        this.readerId = readerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RFIDDevice that = (RFIDDevice) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RFIDDevice{" +
                "id=" + id +
                ", readerId='" + readerId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String READER_ID = "reader_id";
    }
}
