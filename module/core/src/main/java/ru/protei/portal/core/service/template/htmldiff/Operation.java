package ru.protei.portal.core.service.template.htmldiff;

public class Operation {

    private Action action;
    private int startInOld;
    private int endInOld;
    private int startInNew;
    private int endInNew;

    public Operation(Action action, int startInOld, int endInOld, int startInNew, int endInNew) {
        this.action = action;
        this.startInOld = startInOld;
        this.endInOld = endInOld;
        this.startInNew = startInNew;
        this.endInNew = endInNew;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getStartInOld() {
        return startInOld;
    }

    public void setStartInOld(int startInOld) {
        this.startInOld = startInOld;
    }

    public int getEndInOld() {
        return endInOld;
    }

    public void setEndInOld(int endInOld) {
        this.endInOld = endInOld;
    }

    public int getStartInNew() {
        return startInNew;
    }

    public void setStartInNew(int startInNew) {
        this.startInNew = startInNew;
    }

    public int getEndInNew() {
        return endInNew;
    }

    public void setEndInNew(int endInNew) {
        this.endInNew = endInNew;
    }
}
