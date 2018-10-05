package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.DecimalNumberDAO;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.dao.EquipmentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

/**
 * Реализация сервиса управления оборудованием
 */
public class EquipmentServiceImpl implements EquipmentService {

    private static Logger log = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    @Autowired
    EquipmentDAO equipmentDAO;

    @Autowired
    DecimalNumberDAO decimalNumberDAO;

    @Autowired
    DocumentDAO documentDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PolicyService policyService;

    @Autowired
    DocumentService documentService;

    @Override
    public CoreResponse<List<Equipment>> equipmentList(AuthToken token, EquipmentQuery query ) {

        List<Equipment> list = equipmentDAO.getListByQuery(query);

        if (list == null) {
            return new CoreResponse<List<Equipment>>().error(En_ResultStatus.GET_DATA_ERROR);
        }

        fillDecimalNumbers(list);

        return new CoreResponse<List<Equipment>>().success(list);
    }

    @Override
    public CoreResponse< List< EquipmentShortView > > shortViewList( AuthToken token, EquipmentQuery query ) {

        List<Equipment > list = equipmentDAO.getListByQuery(query);

        if (list == null) {
            return new CoreResponse<List<EquipmentShortView>>().error(En_ResultStatus.GET_DATA_ERROR);
        }

        fillDecimalNumbersWithoutLinkedEquipmentDN(list);

        List<EquipmentShortView> result = list.stream().map(EquipmentShortView::fromEquipment).collect(Collectors.toList());

        return new CoreResponse<List<EquipmentShortView>>().success(result,result.size());
    }

    @Override
    public CoreResponse<Equipment> getEquipment( AuthToken token, long id) {

        Equipment equipment = equipmentDAO.get(id);
        jdbcManyRelationsHelper.fill(equipment, "decimalNumbers");
        jdbcManyRelationsHelper.fill(equipment, "linkedEquipmentDecimalNumbers");

        return equipment != null ? new CoreResponse<Equipment>().success(equipment)
                : new CoreResponse<Equipment>().error(En_ResultStatus.NOT_FOUND);
    }

    @Override
    public CoreResponse<List<DecimalNumber>> getDecimalNumbersOfEquipment(AuthToken token, long id) {

        List<DecimalNumber> numbers = decimalNumberDAO.getDecimalNumbersByEquipmentId(id);

        if (numbers == null) {
            return new CoreResponse<List<DecimalNumber>>().error(En_ResultStatus.GET_DATA_ERROR);
        }

        return new CoreResponse<List<DecimalNumber>>().success(numbers);
    }

    @Override
    @Transactional
    public CoreResponse<Equipment> saveEquipment( AuthToken token, Equipment equipment ) {

        if (StringUtils.isBlank(equipment.getName()) || StringUtils.isBlank(equipment.getNameSldWrks())) {
            return new CoreResponse<Equipment>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (equipment.getProjectId() == null) {
            return new CoreResponse<Equipment>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if ( CollectionUtils.isEmpty( equipment.getDecimalNumbers() ) ) {
            return new CoreResponse<Equipment>().error( En_ResultStatus.INCORRECT_PARAMS );
        }

        for (DecimalNumber newNumber : selectNewNumbers(equipment.getDecimalNumbers())) {
            if (decimalNumberDAO.checkExists(newNumber)) {
                return new CoreResponse<Equipment>().error(En_ResultStatus.ALREADY_EXIST_RELATED);
            }
        }
        
	    if ( equipment.getId() == null ) {
            equipment.setCreated( new Date() );
        }
        if ( !equipmentDAO.saveOrUpdate(equipment) ) {
            return new CoreResponse<Equipment>().error(En_ResultStatus.INTERNAL_ERROR);
        }

        updateDecimalNumbers(equipment);

        return new CoreResponse<Equipment>().success(equipment);
    }

    @Override
    public CoreResponse<Integer> getNextAvailableDecimalNumber( AuthToken token, DecimalNumberQuery query ) {
        if ( query == null || query.getNumber() == null
                || query.getNumber().getOrganizationCode() == null
                || query.getNumber().getClassifierCode() == null ) {
            return new CoreResponse<Integer>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Integer regNumber = decimalNumberDAO.getNextAvailableRegNumber(query);
        return new CoreResponse<Integer>().success( regNumber );
    }

    @Override
    public CoreResponse<Integer> getNextAvailableDecimalNumberModification( AuthToken token, DecimalNumberQuery query ) {
        if ( query == null || query.getNumber() == null || query.getNumber().isEmpty() ) {
            return new CoreResponse<Integer>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Integer modification = decimalNumberDAO.getNextAvailableModification(query);
        return new CoreResponse<Integer>().success( modification );
    }

    @Override
    public CoreResponse< Boolean > checkIfExistDecimalNumber( DecimalNumber number ) {
        boolean isExist = decimalNumberDAO.checkExists( number );
        return new CoreResponse<Boolean>().success( isExist );
    }

    @Override
    public CoreResponse<DecimalNumber> findDecimalNumber(AuthToken token, DecimalNumber number) {
        DecimalNumber foundedNumber = decimalNumberDAO.find(number);
        if (foundedNumber == null)
            return new CoreResponse<DecimalNumber>().error(En_ResultStatus.NOT_FOUND);
        return new CoreResponse<DecimalNumber>().success(foundedNumber);
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

        removeLinkedDocuments(token, equipmentId);

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

        List<Long> toRemoveNumberIds = decimalNumberDAO.getDecimalNumberIdsByEquipmentId( equipmentId );
        if ( CollectionUtils.isEmpty(equipment.getDecimalNumbers()) && CollectionUtils.isEmpty(toRemoveNumberIds) ) {
            return true;
        }

        List<DecimalNumber> newNumbers = new ArrayList<>();
        List< DecimalNumber > oldNumbers = new ArrayList<>();
        equipment.getDecimalNumbers().forEach( number -> {
            if ( number.getId() == null ) {
                number.setEntityId(equipmentId);
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

    private void fillDecimalNumbers(List<Equipment> equipments) {
        fillDecimalNumbersImpl(equipments, true);
    }

    private void fillDecimalNumbersWithoutLinkedEquipmentDN(List<Equipment> equipments) {
        fillDecimalNumbersImpl(equipments, false);
    }

    private void fillDecimalNumbersImpl(List<Equipment> equipments, boolean fillAlsoLinkedEquipmentDN) {

        if (equipments == null || equipments.isEmpty()) {
            return;
        }

        Set<Long> equipmentIds = new HashSet<>();

        equipments.stream()
                .filter(Objects::nonNull)
                .peek(equipment -> equipmentIds.add(equipment.getId()))
                .filter(equipment -> fillAlsoLinkedEquipmentDN && equipment.getLinkedEquipmentId() != null)
                .forEach(equipment -> equipmentIds.add(equipment.getLinkedEquipmentId()));

        if (equipmentIds.isEmpty()) {
            return;
        }

        List<DecimalNumber> decimalNumbers = decimalNumberDAO.getDecimalNumbersByEquipmentIds(equipmentIds);
        if (decimalNumbers == null || decimalNumbers.isEmpty()) {
            return;
        }

        decimalNumbers.stream()
                .filter(Objects::nonNull)
                .forEach(decimalNumber -> fillDecimalNumberIntoEquipments(equipments, decimalNumber, fillAlsoLinkedEquipmentDN));
    }

    private void fillDecimalNumberIntoEquipments(List<Equipment> equipments, DecimalNumber decimalNumber, boolean fillAlsoLinkedEquipmentDN) {
        equipments.stream()
                .filter(Objects::nonNull)
                .forEach(equipment -> {
                    if (Objects.equals(decimalNumber.getEntityId(), equipment.getId())) {
                        equipment.addDecimalNumber(decimalNumber);
                    }
                    if (fillAlsoLinkedEquipmentDN && Objects.equals(decimalNumber.getEntityId(), equipment.getLinkedEquipmentId())) {
                        equipment.addLinkedEquipmentDecimalNumber(decimalNumber);
                    }
                });
    }

    private List<DecimalNumber> selectNewNumbers(List<DecimalNumber> decimalNumbers) {
        ArrayList<DecimalNumber> newNumbers = new ArrayList<>();
        for (DecimalNumber decimalNumber : emptyIfNull(decimalNumbers)) {
            if (isNew(decimalNumber)) {
                newNumbers.add(decimalNumber);
            }
        }
        return newNumbers;
    }

    private boolean isNew(DecimalNumber decimalNumber) {
        return (decimalNumber != null && decimalNumber.getId() == null);
    }

    private void removeLinkedDocuments(AuthToken token, Long equipmentId) {

        CoreResponse<List<Document>> documentsResponse = documentService.documentList(token, equipmentId);

        if (documentsResponse.isError()) {
            return;
        }

        List<Document> documents = documentsResponse.getData();

        if (CollectionUtils.isEmpty(documents)) {
            return;
        }

        List<Document> documents2merge = new ArrayList<>();

        for (Document document : documents) {
            if (document.getApproved()) {
                document.setEquipment(null);
                documents2merge.add(document);
                continue;
            }
            documentService.removeDocument(token, document);
        }

        if (CollectionUtils.isNotEmpty(documents2merge)) {
            documentDAO.mergeBatch(documents2merge);
        }
    }
}
