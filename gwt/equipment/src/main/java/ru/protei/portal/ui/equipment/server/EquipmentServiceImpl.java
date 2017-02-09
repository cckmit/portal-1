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
import ru.protei.portal.ui.common.shared.model.DecimalNumber;

import java.util.List;

/**
 * Реализация сервиса по работе с оборудованием
 */
@Service( "EquipmentService" )
public class EquipmentServiceImpl implements EquipmentService {

    @Override
    public List<Equipment> getEquipments( EquipmentQuery query ) throws RequestFailedException {

        log.debug( "getEquipments(): nameBySldWrks={} | classifierCode={} | pamrRegisterNumber={} | pdraRegistreNumber={}",
                query.getName(), query.getClassifierCode(), query.getPAMR_RegisterNumber(), query.getPDRA_RegisterNumber() );

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

    @Override
    public boolean checkIfExistDecimalNumber( DecimalNumber number ) throws RequestFailedException {
        if (number == null) {
            log.warn("null number in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }
        String regNum = makeRegisterNumber( number );
        log.debug( "check exist decimal number: organizationCode={}, classifierCode={}, regNum={}",
                number.getOrganizationCode(), number.getClassifierCode(), regNum );

        CoreResponse<Boolean> response = null;
        switch ( number.getOrganizationCode() ) {
            case PAMR:
                response = equipmentService.checkIfExistPDRA_Number( number.getClassifierCode(), regNum );
                break;
            case PDRA:
                response = equipmentService.checkIfExistPAMR_Number( number.getClassifierCode(), regNum );
        }

        if (response.isOk()) {
            log.debug("check exist decimal number, result: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public DecimalNumber getNextAvailableRegisterNumber( DecimalNumber number ) throws RequestFailedException {
        if (number == null) {
            log.warn("null number in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        String regNum = makeRegisterNumber( number );
        log.debug( "get next available decimal number: organizationCode={}, classifierCode={}, regNum={}",
                number.getOrganizationCode(), number.getClassifierCode(), regNum );

        CoreResponse<String> response = null;
        switch ( number.getOrganizationCode() ) {
            case PAMR:
                response = equipmentService.getNextAvailablePAMR_RegisterNum( number.getClassifierCode() );
                break;
            case PDRA:
                response = equipmentService.getNextAvailablePDRA_RegisterNum( number.getClassifierCode() );
        }

        if (response.isOk()) {
            log.debug("get next available decimal number, result: {}", response.getData());
            number.setModification( null );
            number.setRegisterNumber( response.getData() );
            return number;
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public DecimalNumber getNextAvailableRegisterNumberModification( DecimalNumber number ) throws RequestFailedException  {
        if (number == null) {
            log.warn("null number in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        String regNum = makeRegisterNumber( number );
        log.debug( "get next available decimal number: organizationCode={}, classifierCode={}, regNum={}",
                number.getOrganizationCode(), number.getClassifierCode(), regNum );

        CoreResponse<String> response = null;
        switch ( number.getOrganizationCode() ) {
            case PAMR:
                response = equipmentService.getNextAvailablePAMR_RegisterNumModification( number.getClassifierCode(), number.getRegisterNumber() );
                break;
            case PDRA:
                response = equipmentService.getNextAvailablePDRA_RegisterNumModification( number.getClassifierCode(), number.getRegisterNumber()  );
        }

        if (response.isOk()) {
            log.debug("get next available decimal number, result: {}", response.getData());
            number.setModification( response.getData() );
            return number;
        }

        throw new RequestFailedException(response.getStatus());
    }

    private String makeRegisterNumber( DecimalNumber number ) {
        String regNum = number.getRegisterNumber();
        if ( number.getModification() != null && !number.getModification().isEmpty() ) {
            regNum += "-" + number.getModification();
        }

        return regNum;
    }

    @Autowired
    ru.protei.portal.core.service.EquipmentService equipmentService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
