package ru.protei.portal.core.model.dict;

public enum En_TimeElapsedType implements ru.protei.winter.core.utils.enums.HasId {

    NONE(0),
    /**
     * Дежурство
     */
    WATCH(1),

    /**
     * Ночные работы
     */
    NIGHT_WORK(2);

    En_TimeElapsedType( int id ) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private final int id;


}
