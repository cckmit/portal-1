package ru.protei.portal.api.model;

/**
 * Created by turik on 19.08.16.
 */
public class Photo {

    private Long id = null;
    private byte[] photo = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
