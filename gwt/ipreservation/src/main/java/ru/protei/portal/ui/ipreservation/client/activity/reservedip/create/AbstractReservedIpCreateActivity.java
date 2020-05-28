package ru.protei.portal.ui.ipreservation.client.activity.reservedip.create;

/**
 * Абстракция активности резервирования IP адресов
 */
public interface AbstractReservedIpCreateActivity {
    void onSaveClicked();
    void onCancelClicked();
    void onReservedModeChanged();
    void onOwnerChanged();
    void onChangeIpAddress();
    void checkCreateAvailable();
}