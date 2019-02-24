package ru.protei.portal.core.service.template.htmldiff;

public class Match {

    private int startInOld;
    private int startInNew;
    private int size;

    public Match(int startInOld, int startInNew, int size) {
        this.startInOld = startInOld;
        this.startInNew = startInNew;
        this.size = size;
    }

    public int getStartInOld() {
        return startInOld;
    }

    public void setStartInOld(int startInOld) {
        this.startInOld = startInOld;
    }

    public int getStartInNew() {
        return startInNew;
    }

    public void setStartInNew(int startInNew) {
        this.startInNew = startInNew;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getEndInOld() {
        return startInOld + size;
    }

    public int getEndInNew() {
        return startInNew + size;
    }
}
