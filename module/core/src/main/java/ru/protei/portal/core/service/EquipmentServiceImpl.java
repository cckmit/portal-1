package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.DecimalNumberDAO;
import ru.protei.portal.core.model.dao.EquipmentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    @Transactional
    public CoreResponse<Equipment> saveEquipment(Equipment equipment) {
        if ( CollectionUtils.isEmpty( equipment.getDecimalNumbers() ) ) {
            return new CoreResponse<Equipment>().error( En_ResultStatus.INCORRECT_PARAMS );
        }

        equipment.setCreated( new Date() );
        if ( !equipmentDAO.saveOrUpdate(equipment) ) {
            return new CoreResponse<Equipment>().error(En_ResultStatus.INTERNAL_ERROR);
        }

        updateDecimalNumbers(equipment);

        return new CoreResponse<Equipment>().success(equipment);
    }

    @Override
    public CoreResponse<DecimalNumber> getNextAvailableDecimalNumber( DecimalNumber number ) {
        Integer maxNum = decimalNumberDAO.getMaxRegisterNumber( number );
        number.setModification( null );

        if ( maxNum == null ) {
            number.setRegisterNumber( 1 );
            return new CoreResponse<DecimalNumber>().success( number );
        }

        boolean ifExist = true;
        while ( ifExist && maxNum < 999 ) {
            maxNum += 1;
            number.setRegisterNumber( maxNum );
            ifExist = decimalNumberDAO.checkIfExist( number );
        }

        return new CoreResponse<DecimalNumber>().success( number );
    }

    @Override
    public CoreResponse<DecimalNumber> getNextAvailableDecimalNumberModification( DecimalNumber number ) {
        Integer maxNum = decimalNumberDAO.getMaxModification( number );

        if ( maxNum == null ) {
            number.setModification( 1 );
            return new CoreResponse<DecimalNumber>().success( number );
        }

        boolean ifExist = true;
        while ( ifExist && maxNum < 999 ) {
            maxNum += 1;
            number.setModification( maxNum );
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
        return new CoreResponse<Long>().success(equipmentDAO.countByQuery(query));
    }


    private boolean updateDecimalNumbers( Equipment equipment ) {
        Long equipmentId = equipment.getId();
        log.info( "binding update to linked decimal numbers for equipmentId = {}", equipmentId );

        List<Long> toRemoveNumberIds = decimalNumberDAO.getDecimalNumbersByEquipmentId( equipmentId );
        if ( CollectionUtils.isEmpty(equipment.getDecimalNumbers()) && CollectionUtils.isEmpty(toRemoveNumberIds) ) {
            return true;
        }

        List<DecimalNumber> newNumbers = new ArrayList<>();
        List< DecimalNumber > oldNumbers = new ArrayList<>();
        equipment.getDecimalNumbers().forEach( number -> {
            if ( number.getId() == null ) {
                number.setEquipmentId( equipmentId );
                newNumbers.add( number );
            } else {
                oldNumbers.add( number );
            }
        } );

        if ( !CollectionUtils.isEmpty( newNumbers ) ) {
            log.info( "persist decimal numbers = {} for equipmentId = {}", newNumbers, equipmentId );
            decimalNumberDAO.persistBatch( newNumbers );
        }

        if ( !CollectionUtils.isEmpty( oldNumbers ) ) {
            log.info( "merge decimal numbers = {} for equipmentId = {}", oldNumbers, equipmentId );
            int countMerged = decimalNumberDAO.mergeBatch( oldNumbers );
            if ( countMerged != oldNumbers.size() ) {
                return false;
            }
        }

        toRemoveNumberIds.removeAll( oldNumbers.stream().map(DecimalNumber::getId).collect( Collectors.toList() ) );
        if ( !CollectionUtils.isEmpty( toRemoveNumberIds ) ) {
            log.info( "remove decimal numbers = {} for equipmentId = {}", toRemoveNumberIds, equipmentId );
            int countRemoved = decimalNumberDAO.removeByKeys( toRemoveNumberIds );
            if ( countRemoved != toRemoveNumberIds.size() ) {
                return false;
            }
        }

        return true;
    }
}
