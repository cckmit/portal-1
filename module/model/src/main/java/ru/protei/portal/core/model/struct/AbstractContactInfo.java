package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactEmailSubscriptionType;
import ru.protei.portal.core.model.dict.En_ContactItemType;

import java.util.List;

/**
 * Created by michael on 11.11.16.
 */
public interface AbstractContactInfo {

    /**
     * возвращает список всех элементов
     * @return
     */
    @JsonIgnore
    List<ContactItem> getItems();

    /**
     * Возвращает список всех найденных элементов с типом type
     * @param type
     * @return
     */
    @JsonIgnore
    List<ContactItem> getItems (En_ContactItemType type);


    /**
     * Возвращает список всех найденных элементов с типом подписки subscriptionType
     * @param subscriptionType
     * @return
     */
    @JsonIgnore
    List<ContactItem> getItems(En_ContactEmailSubscriptionType subscriptionType);

    /**
     * Возвращает первый найденный элемент с типом type или null, если такого элемента нет в списке
     * @param type
     * @return
     */
    ContactItem findFirst(En_ContactItemType type);

    /**
     * Возвращает первый найденный элемент с типом type и доступом accessType или null, если такого элемента нет в списке
     * @param type
     * @return
     */
    ContactItem findFirst(En_ContactItemType type, En_ContactDataAccess accessType);

    /**
     * Производит поиск элемента с типом type и доступом accessType и возвращает первый найденный.
     * Если таких элементов нет, то создает и добавляет новый с указанными параметрами, возвращая его в качестве результата
     *
     * @param type
     * @param accessType
     * @return элемент с типом type и доступом accessType
     */
    ContactItem findOrCreate (En_ContactItemType type, En_ContactDataAccess accessType);

    /**
     * Добавляет новый элемент указанного типа type и уровня доступа accessType
     *
     * @param type
     * @param accessType
     * @return
     */
    ContactItem addItem(En_ContactItemType type, En_ContactDataAccess accessType);

    /**
     * Добавляет новый элемент указанного типа type с доступом public
     * @param type
     * @return
     */
    ContactItem addItem(En_ContactItemType type);

    /**
     *
     * Удаляет все элементы с таким же типом как у item и добавляет указанный item
     *
     * Метод может быть удобен для реализации подхода 'singletone', когда не нужен список значений и достаточно хранить
     * только один элемент нужного типа
     *
     * @param item
     * @return
     */
    ContactItem replaceOthers(ContactItem item);

    /**
     * Удаляет все элементы заданного типа, добавляет новый и возвращает его
     * Операция аналогична replaceOthers (ContactItem item), только создание происходит внутри реализации
     * @param type
     * @return
     */
    ContactItem replaceOthers(En_ContactItemType type);

    List<ContactItem> addItems(List<ContactItem> items);
}
