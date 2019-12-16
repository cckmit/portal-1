package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.DecimalNumberDAO;
import ru.protei.portal.core.model.dao.DocumentDAO;
import ru.protei.portal.core.model.dao.EquipmentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.api.struct.Result.error;

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
    public Result<SearchResult<Equipment>> getEquipments( AuthToken token, EquipmentQuery query) {

        SearchResult<Equipment> sr = equipmentDAO.getSearchResult(query);

        fillDecimalNumbers(sr.getResults());

        return ok(sr);
    }

    @Override
    public Result< List< EquipmentShortView > > shortViewList( AuthToken token, EquipmentQuery query ) {

        SearchResult<Equipment> sr = equipmentDAO.getSearchResult(query);

        fillDecimalNumbersWithoutLinkedEquipmentDN(sr.getResults());

        List<EquipmentShortView> result = sr.getResults().stream()
                .map(EquipmentShortView::fromEquipment)
                .collect(Collectors.toList());

        return ok(result);
    }

    @Override
    public Result<Equipment> getEquipment( AuthToken token, long id) {

        Equipment equipment = equipmentDAO.get(id);
        jdbcManyRelationsHelper.fill(equipment, "decimalNumbers");
        jdbcManyRelationsHelper.fill(equipment, "linkedEquipmentDecimalNumbers");

        return equipment != null ? ok( equipment)
                : error( En_ResultStatus.NOT_FOUND);
    }

    @Override
    public Result<List<DecimalNumber>> getDecimalNumbersOfEquipment( AuthToken token, long id) {

        List<DecimalNumber> numbers = decimalNumberDAO.getDecimalNumbersByEquipmentId(id);

        if (numbers == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        return ok(numbers);
    }

    @Override
    @Transactional
    public Result<Equipment> saveEquipment( AuthToken token, Equipment equipment ) {
        if (StringUtils.isBlank(equipment.getName())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (equipment.getProjectId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if ( CollectionUtils.isEmpty( equipment.getDecimalNumbers() ) ) {
            return error(En_ResultStatus.INCORRECT_PARAMS );
        }

        for (DecimalNumber newNumber : selectNewNumbers(equipment.getDecimalNumbers())) {
            if (decimalNumberDAO.checkExists(newNumber)) {
                return error(En_ResultStatus.ALREADY_EXIST_RELATED);
            }
        }
        
	    if ( equipment.getId() == null ) {
            equipment.setCreated( new Date() );
        }
        if ( !equipmentDAO.saveOrUpdate(equipment) ) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        updateDecimalNumbers(equipment);

        return ok(equipment);
    }

    @Override
    public Result<Integer> getNextAvailableDecimalNumber( AuthToken token, DecimalNumberQuery query ) {
        if ( query == null || query.getNumber() == null
                || query.getNumber().getOrganizationCode() == null
                || query.getNumber().getClassifierCode() == null ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Integer regNumber = decimalNumberDAO.getNextAvailableRegNumber(query);
        return ok(regNumber );
    }

    @Override
    public Result<Integer> getNextAvailableDecimalNumberModification( AuthToken token, DecimalNumberQuery query ) {
        if ( query == null || query.getNumber() == null || query.getNumber().isEmpty() ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Integer modification = decimalNumberDAO.getNextAvailableModification(query);
        return ok(modification );
    }

    @Override
    public Result< Boolean > checkIfExistDecimalNumber( DecimalNumber number ) {
        boolean isExist = decimalNumberDAO.checkExists( number );
        return ok(isExist );
    }

    @Override
    public Result<DecimalNumber> findDecimalNumber( AuthToken token, DecimalNumber number) {
        DecimalNumber foundedNumber = decimalNumberDAO.find(number);
        if (foundedNumber == null)
            return error(En_ResultStatus.NOT_FOUND);
        return ok(foundedNumber);
    }

    @Override
    public Result<Long> copyEquipment( AuthToken token, Long equipmentId, String newName, Long authorId ) {
        if (equipmentId == null || newName == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Equipment equipment = equipmentDAO.get(equipmentId);
        if (equipment == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        Equipment newEquipment = new Equipment(equipment);
        newEquipment.setAuthorId( authorId );
        newEquipment.setCreated( new Date() );
        newEquipment.setName( newName );

        Long newId = equipmentDAO.persist(newEquipment);
        if (newId == null) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        return ok(newId );
    }

    @Override
    public Result<Boolean> removeEquipment(AuthToken token, Long equipmentId, Person person) {

        if (equipmentId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        removeLinkedDocuments(token, equipmentId, person);

        Boolean removeStatus = equipmentDAO.removeByKey(equipmentId);
        return ok(removeStatus );
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

    private void removeLinkedDocuments(AuthToken token, Long equipmentId, Person person) {

        Result<List<Document>> documentsResponse = documentService.documentList(token, equipmentId);

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
            Result<Long> result = documentService.removeDocument(token, document.getId(), document.getProjectId(), person);
            if (result.isError()) {
                log.error("removeLinkedDocuments(): failed to remove document | status={}", result.getStatus());
            }
        }

        if (CollectionUtils.isNotEmpty(documents2merge)) {
            documentDAO.mergeBatch(documents2merge);
        }
    }
}
