package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactEmailSubscriptionType;
import ru.protei.portal.core.model.dict.En_ContactItemType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonAutoDetect
public class ContactInfo implements Serializable, AbstractContactInfo {

    @JsonProperty("items")
    private List<ContactItem> itemList;

    public ContactInfo () {
        itemList = new ArrayList<>();
    }

    public ContactInfo (ContactInfo src) {
        this.itemList = new ArrayList<>(src.itemList);
    }

    public ContactInfo(List<ContactItem> itemList) {
        this.itemList = itemList == null ? new ArrayList<>() : itemList;
    }


    /**
     * возвращает список всех элементов
     * @return
     */
    @Override
    @JsonIgnore
    public List<ContactItem> getItems() {
        return itemList;
    }

    /**
     * Возвращает список всех найденных элементов с типом type
     * @param type
     * @return
     */
    @Override
    @JsonIgnore
    public List<ContactItem> getItems(En_ContactItemType type) {
        return itemList.stream()
                .filter(contactItem -> contactItem != null && contactItem.isItemOf(type))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список всех найденных элементов с типом подписки subscriptionType
     * @param subscriptionType
     * @return
     */
    @Override
    @JsonIgnore
    public List<ContactItem> getItems(En_ContactEmailSubscriptionType subscriptionType ) {
        return itemList.stream()
                .filter(contactItem -> contactItem !=null && contactItem.subscriptionType() == subscriptionType)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает первый найденный элемент с типом type или null, если элемента с таким типом нет в списке
     * @param type
     * @return
     */
    @Override
    public ContactItem findFirst(En_ContactItemType type) {
        return itemList.stream()
                .filter(contactItem -> contactItem != null && contactItem.isItemOf(type))
                .findFirst().orElse(null);
    }

    @Override
    public ContactItem findFirst(En_ContactItemType type, En_ContactDataAccess accessType) {
        return itemList.stream()
                .filter(contactItem -> contactItem != null && contactItem.isItemOf(type) && contactItem.accessType() == accessType)
                .findFirst().orElse(null);
    }

    /**
     * Производит поиск элемента с типом type и доступом accessType и возвращает первый найденный.
     * Если таких элементов нет, то создает и добавляет новый с указанными параметрами, возвращая его в качестве результата
     *
     * @param type
     * @param accessType
     * @return элемент с типом type и доступом accessType
     */
    @Override
    public ContactItem findOrCreate(En_ContactItemType type, En_ContactDataAccess accessType) {
        ContactItem item = findFirst(type, accessType);
        if (item == null) {
            item = addItem(type,accessType);
        }
        return item;
    }

    /**
     * Добавляет новый элемент указанного типа type и уровня доступа accessType
     *
     * @param type
     * @param accessType
     * @return
     */
    @Override
    public ContactItem addItem(En_ContactItemType type, En_ContactDataAccess accessType) {
        ContactItem item = new ContactItem(type,accessType);
        this.itemList.add(item);
        return item;
    }

    @Override
    public ContactItem addItem(En_ContactItemType type) {
        return addItem(type, En_ContactDataAccess.PUBLIC);
    }

    /**
     *
     * Удаляет все элементы с таким же типом как у item и добавляет указанный item
     *
     * @param item
     * @return
     */
    @Override
    public ContactItem replaceOthers(ContactItem item) {
        itemList.removeIf(contactItem -> contactItem.isItemOf(item.type()));
        itemList.add(item);
        return item;
    }

    @Override
    public ContactItem replaceOthers(En_ContactItemType type) {
        itemList.removeIf(contactItem -> contactItem.isItemOf(type));
        return addItem(type);
    }

    @Override
    public List<ContactItem> addItems(List<ContactItem> items) {
        itemList.addAll(items);
        return items;
    }
}
