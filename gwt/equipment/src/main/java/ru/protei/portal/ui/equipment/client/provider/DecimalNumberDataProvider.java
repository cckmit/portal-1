package ru.protei.portal.ui.equipment.client.provider;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.service.EquipmentServiceAsync;
import ru.protei.portal.core.model.ent.DecimalNumber;

import java.util.List;

/**
 * Провайдер данных по децимальным номерам
 */
public class DecimalNumberDataProvider implements AbstractDecimalNumberDataProvider {

    @Override
    public void checkIfExistDecimalNumber( DecimalNumber number, AsyncCallback<Boolean> callback ) {
        equipmentService.checkIfExistDecimalNumber( number, callback );
    }

    @Override
    public void getNextAvailableRegisterNumber( DecimalNumber number, AsyncCallback<DecimalNumber> callback ) {
        equipmentService.getNextAvailableRegisterNumber( number, callback );
    }

    @Override
    public void getNextAvailableRegisterNumberModification( DecimalNumber number, AsyncCallback< DecimalNumber > callback ) {
        equipmentService.getNextAvailableRegisterNumberModification( number, callback );
    }

    @Override
    public void getNextAvailableRegNumberNotContainsInList(List<Integer> regNumbers, String classifierCode, String orgCode, AsyncCallback<DecimalNumber> callback) {
        equipmentService.getNextAvailableRegNumberNotContainsInList( regNumbers, classifierCode, orgCode, callback );
    }

    @Override
    public void getNextAvailableRegisterNumberModificationNotContainsInList(List<Integer> mods, String classifierCode, String orgCode, String regNum, AsyncCallback<DecimalNumber> callback) {
        equipmentService.getNextAvailableRegisterNumberModificationNotContainsInList( mods, classifierCode, orgCode, regNum, callback );
    }

    @Inject
    EquipmentServiceAsync equipmentService;
}
