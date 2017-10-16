package ru.protei.portal.core.model.view;

import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.ent.Person;

import java.io.Serializable;
import java.util.List;

/**
 * Сокращенное представление оборудования
 */
public class EquipmentShortView implements Serializable {
    private Long id;
    private String name;
    private List<DecimalNumber> decimalNumbers;

    public EquipmentShortView() {}

    public EquipmentShortView( Long id ) {
        this.id = id;
    }

    public EquipmentShortView( String name, Long id, List<DecimalNumber> decimalNumbers ) {
        this.name = name;
        this.id = id;
        this.decimalNumbers = decimalNumbers;
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List< DecimalNumber > getDecimalNumbers() {
        return decimalNumbers;
    }

    public void setDecimalNumbers( List< DecimalNumber > decimalNumbers ) {
        this.decimalNumbers = decimalNumbers;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof EquipmentShortView ) ) return false;

        EquipmentShortView that = (EquipmentShortView) o;

        return id != null ? id.equals( that.id ) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static EquipmentShortView fromEquipment( Equipment equipment ){
        if(equipment == null)
            return null;
        return new EquipmentShortView( equipment.getName(), equipment.getId(), equipment.getDecimalNumbers() );
    }
}
