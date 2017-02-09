package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.EquipmentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;

import java.util.List;

/**
 * Реализация сервиса управления оборудованием
 */
public class EquipmentServiceImpl implements EquipmentService {

    private static Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    @Autowired
    EquipmentDAO equipmentDAO;

    @Override
    public CoreResponse<List<Equipment>> equipmentList(EquipmentQuery query) {
        List<Equipment> list = equipmentDAO.getListByQuery(query);

        if (list == null)
            new CoreResponse<List<Equipment>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<Equipment>>().success(list);
    }

    @Override
    public CoreResponse<Equipment> getEquipment(long id) {
        Equipment equipment = equipmentDAO.get(id);

        return equipment != null ? new CoreResponse<Equipment>().success(equipment)
                : new CoreResponse<Equipment>().error(En_ResultStatus.NOT_FOUND);
    }

    // TODO: fill check equipment data
    @Override
    public CoreResponse<Equipment> saveEquipment(Equipment equipment) {

        if (equipmentDAO.saveOrUpdate(equipment)) {
            return new CoreResponse<Equipment>().success(equipment);
        }

        return new CoreResponse<Equipment>().error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    public CoreResponse<String> getNextAvailablePAMR_RegisterNum( String classifierCode ) {
        String maxNumValue = equipmentDAO.getMaxPAMR_RegNum( classifierCode );
        if ( maxNumValue == null ) {
            return new CoreResponse<String>().success( "001" );
        }

        Integer maxNum;
        try {
            maxNum  = Integer.parseInt( maxNumValue );
        } catch ( NumberFormatException e ) {
            return new CoreResponse<String>().error( En_ResultStatus.INCORRECT_PARAMS );
        }
        boolean ifExist = true;
        while ( ifExist && maxNum < 999 ) {
            maxNum += 1;
            ifExist = equipmentDAO.checkIfExistPAMR_RegNum( classifierCode, maxNum.toString() );
        }

        return new CoreResponse<String>().success( maxNum.toString() );
    }

    @Override
    public CoreResponse<String> getNextAvailablePDRA_RegisterNum( String classifierCode ) {
        String maxNumValue = equipmentDAO.getMaxPDRA_RegNum( classifierCode );
        if ( maxNumValue == null ) {
            return new CoreResponse<String>().success( "001" );
        }

        Integer maxNum;
        try {
            maxNum  = Integer.parseInt( maxNumValue );
        } catch ( NumberFormatException e ) {
            return new CoreResponse<String>().error( En_ResultStatus.INCORRECT_PARAMS );
        }
        boolean ifExist = true;
        while ( ifExist && maxNum < 999 ) {
            maxNum += 1;
            ifExist = equipmentDAO.checkIfExistPDRA_RegNum( classifierCode, maxNum.toString() );
        }

        return new CoreResponse<String>().success( maxNum.toString() );
    }

    @Override
    public CoreResponse< String > getNextAvailablePAMR_RegisterNumModification( String classifierCode, String registerNumber ) {
        String maxNumValue = equipmentDAO.getMaxPAMR_RegNumModification( classifierCode, registerNumber );
        if ( maxNumValue == null ) {
            return new CoreResponse<String>().success( "01" );
        }

        Integer maxNum;
        try {
            maxNum  = Integer.parseInt( maxNumValue );
        } catch ( NumberFormatException e ) {
            return new CoreResponse<String>().error( En_ResultStatus.INCORRECT_PARAMS );
        }
        return new CoreResponse<String>().success( maxNum.toString() );
    }

    @Override
    public CoreResponse< String > getNextAvailablePDRA_RegisterNumModification( String classifierCode, String registerNumber ) {
        String maxNumValue = equipmentDAO.getMaxPDRA_RegNumModification( classifierCode, registerNumber );
        if ( maxNumValue == null ) {
            return new CoreResponse<String>().success( "01" );
        }

        Integer maxNum;
        try {
            maxNum  = Integer.parseInt( maxNumValue );
        } catch ( NumberFormatException e ) {
            return new CoreResponse<String>().error( En_ResultStatus.INCORRECT_PARAMS );
        }
        return new CoreResponse<String>().success( maxNum.toString() );
    }

    @Override
    public CoreResponse< Boolean > checkIfExistPAMR_Number( String classifierCode, String registerNumber ) {
        boolean isExist = equipmentDAO.checkIfExistPAMR_RegNum( classifierCode, registerNumber );
        return new CoreResponse<Boolean>().success( isExist );
    }

    @Override
    public CoreResponse< Boolean > checkIfExistPDRA_Number( String classifierCode, String registerNumber ) {
        boolean isExist = equipmentDAO.checkIfExistPDRA_RegNum( classifierCode, registerNumber );
        return new CoreResponse<Boolean>().success( isExist );
    }

    @Override
    public CoreResponse<Long> count(EquipmentQuery query) {
        return new CoreResponse<Long>().success(equipmentDAO.count(query));
    }
}
