package ru.protei.portal.test.enterprise1c;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.*;
import ru.protei.portal.core.client.enterprise1c.api.Api1C;
import ru.protei.portal.core.model.enterprise1c.dto.Contract1C;
import ru.protei.portal.core.model.enterprise1c.dto.Contractor1C;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.winter.core.CoreConfigurationContext;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CoreConfigurationContext.class, IntegrationTestsConfiguration.class})
public class TestApi1C extends BaseServiceTest {
    @Autowired
    Api1C api1C;

    /**
     *  Создавать контрагентов можно только для одной из двух компаний.
     *  По компании определяем, в какую базу будем лезть
     */
    static final String mainHomeCompanyName = CrmConstants.Company.MAIN_HOME_COMPANY_NAME;
    static final String homeCompanyName = CrmConstants.Company.PROTEI_ST_HOME_COMPANY_NAME;


    @Test
    public void testContractor1C(){
        /**
         *  Создаем контрагента. В ответ получаем полностью заполненного из 1С.
         *  Полей там очень много, но мы лишние игнорим (@JsonIgnoreProperties(ignoreUnknown = true))
         */
        Result<Contractor1C> contractorResult = api1C.saveContractor(makeContractor1C(), mainHomeCompanyName);

        assertNotNull(contractorResult);
        assertTrue(contractorResult.isOk());
        assertNotNull(contractorResult.getData());

        Contractor1C createdContractor1C = contractorResult.getData();

        /**
         *  Получаем контрагента. Запрос посылаем с полностью заполненным контрагентом, чтобы проверить правильно ли строится фильтр
         */
        Result<List<Contractor1C>> contractorsResult = api1C.getContractors(createdContractor1C, mainHomeCompanyName);

        assertNotNull(contractorsResult);
        assertTrue(contractorsResult.isOk());
        assertNotNull(contractorsResult.getData());

        List<Contractor1C> contractors = contractorsResult.getData();
        Contractor1C contractor1C = contractors.get(0);

        assertNotNull(contractor1C);
        assertEquals(contractor1C.getRefKey(), createdContractor1C.getRefKey());
    }

    @Test
    public void testContract1C() {
        Result<List<Contractor1C>> contractorsResult = api1C.getContractors(makeContractor1C(), mainHomeCompanyName);

        assertNotNull(contractorsResult);
        assertTrue(contractorsResult.isOk());
        assertNotNull(contractorsResult.getData());

        List<Contractor1C> contractors = contractorsResult.getData();
        Contractor1C contractor1C = contractors.get(0);

        /**
         *  Создаем договор. В ответ получаем объект из 1С с заполненным ref_key
         *  Лишние поля игнорим (@JsonIgnoreProperties(ignoreUnknown = true))
         */
        Result<Contract1C> contract1CResult = api1C.saveContract(
                makeContract1C("b/n", contractor1C.getRefKey()),
                mainHomeCompanyName);

        assertNotNull(contract1CResult);
        assertTrue(contract1CResult.isOk());
        assertNotNull(contract1CResult.getData());

        Contract1C savedContract1C = contract1CResult.getData();

        /**
         *  Получаем договор по ref_key
         */
        Result<Contract1C> contractResult = api1C.getContract(savedContract1C, mainHomeCompanyName);

        assertNotNull(contractResult);
        assertTrue(contractResult.isOk());
        assertNotNull(contractResult.getData());

        Contract1C contract1C = contractResult.getData();

        assertNotNull(contract1C.getRefKey());
    }

    /**
     *  Заполняем контрагента. Нижеуказанные поля обязательные
     *  ИНН должно быть корректным с точки зрения налоговой (алгоритм проверки указан в родительской задаче PORTAL-1256)
     *  Код страны регистрации - ключ сущности из 1С. На UI мы тоже будем доставать ключи и именно в таком виде засовывать в апи
     */
    private Contractor1C makeContractor1C(){
        Contractor1C contractor1C = new Contractor1C();
        contractor1C.setKpp("777777888");
        contractor1C.setInn("7801126047");
        contractor1C.setFullName("ООО Тест Портала13");
        contractor1C.setName("ООО Тест Портала13");
        contractor1C.setRegistrationCountryKey("042a9171-7bc0-11e8-80cb-ac1f6b010113");
        return contractor1C;
    }

    /**
     *  Заполняем контрагента. Нижеуказанные поля обязательные
     *  ИНН должно быть корректным с точки зрения налоговой (алгоритм проверки указан в родительской задаче PORTAL-1256)
     *  Код страны регистрации - ключ сущности из 1С. На UI мы тоже будем доставать ключи и именно в таком виде засовывать в апи
     */
    private Contract1C makeContract1C(String number, String contractorKey) {
        Contract1C contract1C = new Contract1C();
        contract1C.setNumber(number);
        contract1C.setContractorKey(contractorKey);
        contract1C.setDateSigning(new Date());
        return contract1C;
    }
}