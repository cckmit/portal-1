package ru.protei.portal.core.model.dict;

public enum En_TimeElapsedType implements ru.protei.winter.core.utils.enums.HasId {

    NONE( 0 ),
    /**
     * Дежурство
     */
    WATCH( 1 ),

    /**
     * Ночные работы
     */
    NIGHT_WORK( 2 ),

    /**
     * установка ПО
     */
    SOFT_INSTALL( 3 ),

    /**
     * обновление ПО
     */
    SOFT_UPDATE( 4 ),

    /**
     * настройка ПО
     */
    SOFT_CONFIG( 5 ),

    /**
     * тестирование
     */
    TESTING( 6 ),

    /**
     * консультация
     */
    CONSULTATION( 7 ),

    /**
     * совещание/конференц колл
     */
    MEETING( 8 ),

    /**
     * обсуждение доработок
     */
    DISCUSSION_OF_IMPROVEMENTS( 9 ),

    /**
     * анализ логов
     */
    LOG_ANALYSIS( 10 ),

    /**
     * решение проблем
     */
    SOLVE_PROBLEMS( 11 )
    ;

    En_TimeElapsedType( int id ) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private final int id;


}
