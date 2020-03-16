package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.EducationEntryType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JdbcEntity(table = "education_entry")
public class EducationEntry implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "type")
    @JdbcEnumerated(EnumType.ID)
    private EducationEntryType type;

    @JdbcColumn(name="coins")
    private Integer coins;

    @JdbcColumn(name="approved")
    private boolean approved;

    @JdbcColumn(name = "title")
    private String title;

    @JdbcColumn(name = "description")
    private String description;

    @JdbcColumn(name = "link")
    private String link;

    @JdbcColumn(name = "image")
    private String image;

    @JdbcColumn(name = "location")
    private String location;

    @JdbcColumn(name = "date_start")
    private Date dateStart;

    @JdbcColumn(name = "date_end")
    private Date dateEnd;

    @JdbcColumn(name = "extra_info")
    private String extraInfo;

    @JdbcOneToMany(remoteColumn = "education_entry_id", additionalConditions = {
            @JdbcManyJoinData(remoteColumn = "approved", value = "true", valueClass = Boolean.class)
    })
    private List<EducationEntryAttendance> attendanceList;

    public EducationEntry() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EducationEntryType getType() {
        return type;
    }

    public void setType(EducationEntryType type) {
        this.type = type;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public List<EducationEntryAttendance> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<EducationEntryAttendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @Override
    public String toString() {
        return "EducationEntry{" +
                "id=" + id +
                ", type=" + type +
                ", approved=" + approved +
                ", coins=" + coins +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", image='" + image + '\'' +
                ", location='" + location + '\'' +
                ", dateStart=" + dateStart +
                ", dateEnd=" + dateEnd +
                ", extraInfo='" + extraInfo + '\'' +
                ", attendanceList=" + attendanceList +
                '}';
    }
}
