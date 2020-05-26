package ru.protei.portal.ui.ipreservation.client.view.widget.mode;

/**
 * Режимы резервирования IP-адресов
 */
public enum En_ReservedMode {
    EXACT_IP,
    ANY_FREE_IPS;

    public static En_ReservedMode getButOrdinal(int ordinal) {
        for (En_ReservedMode value: values()) {
            if(ordinal == value.ordinal())
                return value;
        }

        return null;
    }
}
