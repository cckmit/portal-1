package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.DecimalNumberDAO;
import ru.protei.portal.core.model.dao.EquipmentDAO;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
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

    @Autowired
    PolicyService policyService;

    @Override
    public CoreResponse<List<Equipment>> equipmentList(AuthToken token, EquipmentQuery query ) {

        List<Equipment> list = equipmentDAO.getListByQuery(query);

        if (list == null)
            new CoreResponse<List<Equipment>>().error(En_ResultStatus.GET_DATA_ERROR);
        jdbcManyRelationsHelper.fillAll( list );

        return new CoreResponse<List<Equipment>>().success(list);
    }

    @Override
    public CoreResponse<Equipment> getEquipment( AuthToken token, long id) {

        Equipment equipment = equipmentDAO.get(id);
        jdbcManyRelationsHelper.fillAll( equipment );

        return equipment != null ? new CoreResponse<Equipment>().success(equipment)
                : new CoreResponse<Equipment>().error(En_ResultStatus.NOT_FOUND);
    }

    @Override
    @Transactional
    public CoreResponse<Equipment> saveEquipment( AuthToken token, Equipment equipment ) {

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
    public CoreResponse<DecimalNumber> getNextAvailableDecimalNumber( AuthToken token, DecimalNumber number ) {
        Integer maxNum = decimalNumberDAO.getMaxRegisterNumber( number );
        number.setModification( null );

        if ( maxNum == null ) {
            number.setRegisterNumber( 1 );
            return new CoreResponse<DecimalNumber>().success( number );
        }

        Integer nextAvailableRegNumber = decimalNumberDAO.getNextAvailableRegNumber(number);

        boolean ifExist = true;
        while ( ifExist && nextAvailableRegNumber < 999 ) {
            number.setRegisterNumber(nextAvailableRegNumber);
            ifExist = decimalNumberDAO.checkIfExist( number );
        }

        return new CoreResponse<DecimalNumber>().success( number );
    }

    @Override
    public CoreResponse<DecimalNumber> getNextAvailableRegNumberNotContainsInList(AuthToken authToken, List<Integer> regNumbers, String classifierCode, String orgCode) {

        DecimalNumber number = new DecimalNumber();
        number.setModification(null);
        Integer nextAvailableRegNumber = decimalNumberDAO.getNextAvailableRegNumberNotContainsInList(regNumbers, classifierCode, orgCode);
        number.setRegisterNumber(nextAvailableRegNumber);

        return new CoreResponse<DecimalNumber>().success( number );
    }

    @Override
    public CoreResponse<DecimalNumber> getNextAvailableDecimalNumberModification( AuthToken token, DecimalNumber number ) {
        Integer maxNum = decimalNumberDAO.getMaxModification( number );

        if ( maxNum == null ) {
            number.setModification( 1 );
            return new CoreResponse<DecimalNumber>().success( number );
        }

        Integer nextAvailableModification = decimalNumberDAO.getNextAvailableModification(number);


        boolean ifExist = true;
        while ( ifExist && nextAvailableModification < 999 ) {
            number.setModification( nextAvailableModification );
            ifExist = decimalNumberDAO.checkIfExist( number );
        }

        return new CoreResponse<DecimalNumber>().success( number );
    }

    @Override
    public CoreResponse<DecimalNumber> getNextAvailableRegisterNumberModificationNotContainsInList(AuthToken authToken, List<Integer> mods, String classifierCode, String orgCode, String regNum) {
        DecimalNumber number = new DecimalNumber();
        Integer nextAvailableMod = decimalNumberDAO.getNextAvailableRegisterNumberModificationNotContainsInList(mods, classifierCode, orgCode, regNum);
        number.setModification(nextAvailableMod);

        return new CoreResponse<DecimalNumber>().success( number );
    }

    @Override
    public CoreResponse< Boolean > checkIfExistDecimalNumber( DecimalNumber number ) {
        boolean isExist = decimalNumberDAO.checkIfExist( number );
        return new CoreResponse<Boolean>().success( isExist );
    }

    @Override
    public CoreResponse<Long> copyEquipment( AuthToken token, Long equipmentId, String newName, Long authorId ) {

        if (equipmentId == null || newName == null) {
            return new CoreResponse<Long>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Equipment equipment = equipmentDAO.get(equipmentId);
        if (equipment == null) {
            return new CoreResponse<Long>().error(En_ResultStatus.NOT_FOUND);
        }

        Equipment newEquipment = new Equipment(equipment);
        newEquipment.setAuthorId( authorId );
        newEquipment.setCreated( new Date() );
        newEquipment.setName( newName );

        Long newId = equipmentDAO.persist(newEquipment);
        if (newId == null) {
            return new CoreResponse<Long>().error(En_ResultStatus.INTERNAL_ERROR);
        }

        return new CoreResponse<Long>().success( newId );
    }

    @Override
    public CoreResponse<Boolean> removeEquipment( AuthToken token, Long equipmentId ) {

        if (equipmentId == null) {
            return new CoreResponse<Boolean>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Boolean removeStatus = equipmentDAO.removeByKey(equipmentId);
        return new CoreResponse<Boolean>().success( removeStatus );
    }

    @Override
    public CoreResponse<Long> count( AuthToken token, EquipmentQuery query ) {

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
