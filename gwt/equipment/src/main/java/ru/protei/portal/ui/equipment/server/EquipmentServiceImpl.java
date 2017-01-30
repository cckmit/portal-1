package ru.protei.portal.ui.equipment.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.ui.common.client.service.EquipmentService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Реализация сервиса по работе с оборудованием
 */
@Service( "EquipmentService" )
public class EquipmentServiceImpl implements EquipmentService {

    @Override
    public List<Equipment> getEquipments( EquipmentQuery query ) throws RequestFailedException {

        log.debug( "getEquipments(): name={} | number={}",
                query.getName(), query.getNumber() );

        CoreResponse<List<Equipment>> response = equipmentService.equipmentList( query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public Equipment getEquipment(long id) throws RequestFailedException {
        log.debug("get equipment, id: {}", id);

        CoreResponse<Equipment> response = equipmentService.getEquipment(id);

        log.debug("get equipment, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        return response.getData();
    }

    @Override
    public Equipment saveEquipment(Equipment p) throws RequestFailedException {
        if (p == null) {
            log.warn("null equipment in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.debug("store equipment, id: {} ", HelperFunc.nvl(p.getId(), "new"));

        CoreResponse<Equipment> response = equipmentService.saveEquipment(p);

        log.debug("store equipment, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("store equipment, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Long getEquipmentCount( EquipmentQuery query ) throws RequestFailedException {
        log.debug( "getEquipmentCount(): query={}", query );
        return equipmentService.count( query ).getData();
    }

    @Autowired
    ru.protei.portal.core.service.EquipmentService equipmentService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
