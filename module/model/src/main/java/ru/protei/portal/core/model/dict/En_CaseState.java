package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

/**
 * Created by bondarenko on 10.11.16.
 */
public enum En_CaseState implements HasId {
    CREATED(1, "created", false),
    OPENED(2, "opened", false),
    CLOSED(3, "closed", true),
    PAUSED(4, "paused", false),
    VERIFIED(5, "verified", true) {
        @Override
        public String getComment() {
            return "Поскольку статус является терминальным, то в него переводим только после получения подтверждения от Заказчика, что тикет можно закрыть.\n" +
                    "В противном случае ставим Request to customer или Request to NX.";
        }
    },
    REOPENED(6, "reopened", false),
    SOLVED_NOAP(7, "solved: not a problem", true),
    SOLVED_FIX(8, "solved: fixed", true),
    SOLVED_DUP(9, "solved: duplicated", true),
    IGNORED(10, "ignored", true),
    ASSIGNED(11, "assigned", false),
    ESTIMATED(12, "estimated", false),
    DISCUSS(14, "discuss", false),
    PLANNED(15, "planned", false),
    ACTIVE(16, "active", false),
    DONE(17, "done", true),
    TEST(18, "test", false),
    TEST_LOCAL(19, "local test", false),
    TEST_CUST(20, "customer test", false),
    DESIGN(21, "design", false),
    WORKAROUND(30, "workaround", false),
    INFO_REQUEST(31, "info request", false),
    CANCELED(33, "canceled", true),
    CUST_PENDING(34, "customer pending", false),
    NX_REQUEST(35, "request to NX", false) {
        @Override
        public String getComment() {
            return "Не забывать переводить в этот статус, в противном случае Заказчик ждёт реакции от нас";
        }
    },
    CUST_REQUEST(36, "request to customer", false) {
        @Override
        public String getComment() {
            return "Не забывать переводить в этот статус, в противном случае Заказчик ждёт реакции от нас";
        }
    };

    public static En_CaseState getById(Long id) {
        if(id == null)
            return null;

        for (En_CaseState cs : En_CaseState.values())
            if (cs.id == id)
                return cs;

        return null;
    }

    En_CaseState(int id, String name, boolean isTerminalState){
        this.id = id;
        this.name = name;
        this.isTerminalState = isTerminalState;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getComment() {
        return "";
    }

    public String getName() {
        return name;
    }

    public boolean isTerminalState() {
        return isTerminalState;
    }

    private int id;
    private String name;
    private boolean isTerminalState;
}


