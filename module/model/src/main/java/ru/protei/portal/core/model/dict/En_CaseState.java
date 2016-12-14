package ru.protei.portal.core.model.dict;

/**
 * Created by bondarenko on 10.11.16.
 */
public enum En_CaseState {
    CREATED(1, "created"),
    OPENED(2, "opened"),
    CLOSED(3, "closed"),
    PAUSED(4, "paused"),
    VERIFIED(5, "verified"),
    REOPENED(6, "reopened"),
    SOLVED_NOAP(7, "solved: not a problem"),
    SOLVED_FIX(8, "solved: fixed"),
    SOLVED_DUP(9, "solved: duplicated"),
    IGNORED(10, "ignored"),
    ASSIGNED(11, "assigned"),
    ESTIMATED(12, "estimated"),
    DISCUSS(14, "discuss"),
    PLANNED(15, "planned"),
    ACTIVE(16, "active"),
    DONE(17, "done"),
    TEST(18, "test"),
    TEST_LOCAL(19, "local test"),
    TEST_CUST(20, "customer test"),
    DESIGN(21, "design");

    public static En_CaseState getById(Long id) {
        if(id == null)
            return null;

        for (En_CaseState cs : En_CaseState.values())
            if (cs.id == id)
                return cs;

        return null;
    }

    En_CaseState(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private int id;
    private String name;


}


