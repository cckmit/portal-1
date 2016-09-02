package ru.protei.portal.webui.controller.ws.model;

/**
 * Created by turik on 19.08.16.
 */
public class FotoByte {

    private Long id = null;
    private byte[] fotos = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getFotos() {
        return fotos;
    }

    public void setFotos(byte[] fotos) {
        this.fotos = fotos;
    }
}
