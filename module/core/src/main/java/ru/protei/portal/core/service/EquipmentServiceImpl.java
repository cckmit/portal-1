package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.DecimalNumberDAO;
import ru.protei.portal.core.model.dao.EquipmentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.List;

/**
 * Реализация сервиса управления оборудованием
 */
public class EquipmentServiceImpl implements EquipmentService {

    private static Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    @Autowired
    EquipmentDAO equipmentDAO;

    @Autowired
    DecimalNumberDAO decimalNumberDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public CoreResponse<List<Equipment>> equipmentList(EquipmentQuery query) {
        List<Equipment> list = equipmentDAO.getListByQuery(query);

        if (list == null)
            new CoreResponse<List<Equipment>>().error(En_ResultStatus.GET_DATA_ERROR);
        jdbcManyRelationsHelper.fillAll( list );

        return new CoreResponse<List<Equipment>>().success(list);
    }

    @Override
    public CoreResponse<Equipment> getEquipment(long id) {
        Equipment equipment = equipmentDAO.get(id);
        jdbcManyRelationsHelper.fillAll( equipment );

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
    public CoreResponse<DecimalNumber> getNextAvailableDecimalNumber( DecimalNumber number ) {
        Integer maxNum = decimalNumberDAO.getMaxRegisterNumber( number );
        number.setModification( null );

        if ( maxNum == null ) {
            number.setRegisterNumber( "001" );
            return new CoreResponse<DecimalNumber>().success( number );
        }

        boolean ifExist = true;
        while ( ifExist && maxNum < 999 ) {
            maxNum += 1;
            number.setRegisterNumber( maxNum.toString() );
            ifExist = decimalNumberDAO.checkIfExist( number );
        }

        return new CoreResponse<DecimalNumber>().success( number );
    }

    @Override
    public CoreResponse<DecimalNumber> getNextAvailableDecimalNumberModification( DecimalNumber number ) {
        Integer maxNum = decimalNumberDAO.getMaxModification( number );

        if ( maxNum == null ) {
            number.setModification( "01" );
            return new CoreResponse<DecimalNumber>().success( number );
        }

        boolean ifExist = true;
        while ( ifExist && maxNum < 999 ) {
            maxNum += 1;
            number.setModification( maxNum.toString() );
            ifExist = decimalNumberDAO.checkIfExist( number );
        }

        return new CoreResponse<DecimalNumber>().success( number );
    }

    @Override
    public CoreResponse< Boolean > checkIfExistDecimalNumber( DecimalNumber number ) {
        boolean isExist = decimalNumberDAO.checkIfExist( number );
        return new CoreResponse<Boolean>().success( isExist );
    }

    @Override
    public CoreResponse<Long> count(EquipmentQuery query) {
        return new CoreResponse<Long>().success(equipmentDAO.count(query));
    }
}
