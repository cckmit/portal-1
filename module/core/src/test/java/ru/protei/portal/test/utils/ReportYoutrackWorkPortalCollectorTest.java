package ru.protei.portal.test.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkPortalInfo;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem;
import ru.protei.portal.core.report.ytwork.ReportYtWorkPortalCollector;

import java.util.*;
import java.util.stream.Stream;

public class ReportYoutrackWorkPortalCollectorTest {
    @Test
    public void testEmpty() {
        ReportYtWorkPortalCollector collector = new ReportYtWorkPortalCollector(
                id -> new ArrayList<>(),
                new Date()
        );

        Map<Long, ReportYtWorkRowItem> data = Stream.<ReportYtWorkPortalInfo>empty()
                .collect(collector);
        Assert.assertNotNull(data);
        Assert.assertEquals(0, data.size());
    }

    @Test
    public void testHomeCompanyTime() {
        Long personId = 7777L;
        Long spentTime = 13L;

        ReportYtWorkPortalInfo info1 = new ReportYtWorkPortalInfo(
                personId, spentTime, null);

        ReportYtWorkPortalCollector collector = new ReportYtWorkPortalCollector(
                id -> new ArrayList<>(),
                new Date()
        );

        Map<Long, ReportYtWorkRowItem> data = Stream.of(info1).collect(collector);

        Assert.assertEquals(1, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(personId);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(spentTime, ytWorkItem.getHomeCompanySpentTime());
        Assert.assertEquals(spentTime, ytWorkItem.getAllTimeSpent());
        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getGuaranteeSpentTime().size());
    }

    @Test
    public void testHomeCompanyTimeTwoComments() {
        Long personId = 7777L;
        Long spentTime1 = 13L;
        Long spentTime2 = 9L;

        ReportYtWorkPortalInfo info1 = new ReportYtWorkPortalInfo(
                personId, spentTime1, null);
        ReportYtWorkPortalInfo info2 = new ReportYtWorkPortalInfo(
                personId, spentTime2, null);

        ReportYtWorkPortalCollector collector = new ReportYtWorkPortalCollector(
                id -> new ArrayList<>(),
                new Date()
        );

        Map<Long, ReportYtWorkRowItem> data = Stream.of(info1, info2).collect(collector);

        Assert.assertEquals(1, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(personId);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(spentTime1 + spentTime2, ytWorkItem.getHomeCompanySpentTime().longValue());
        Assert.assertEquals(spentTime1 + spentTime2, ytWorkItem.getAllTimeSpent().longValue());
        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getGuaranteeSpentTime().size());
    }

    @Test
    public void testHomeCompanyTimeTwoPerson() {
        Long personId1 = 7777L;
        Long personId2 = 7778L;
        Long spentTime1 = 13L;
        Long spentTime2 = 8L;

        ReportYtWorkPortalInfo info1 = new ReportYtWorkPortalInfo(
                personId1, spentTime1, null);
        ReportYtWorkPortalInfo info2 = new ReportYtWorkPortalInfo(
                personId2, spentTime2, null);

        ReportYtWorkPortalCollector collector = new ReportYtWorkPortalCollector(
                id -> new ArrayList<>(),
                new Date()
        );

        Map<Long, ReportYtWorkRowItem> data = Stream.of(info1, info2).collect(collector);

        Assert.assertEquals(2, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(personId1);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(spentTime1, ytWorkItem.getHomeCompanySpentTime());
        Assert.assertEquals(spentTime1, ytWorkItem.getAllTimeSpent());

        ytWorkItem = data.get(personId2);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(spentTime2, ytWorkItem.getHomeCompanySpentTime());
        Assert.assertEquals(spentTime2, ytWorkItem.getAllTimeSpent());
        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getGuaranteeSpentTime().size());
    }

    @Test
    public void testHomeCompanyTimeTwoPersonTwoComments() {
        Long personId1 = 7777L;
        Long personId2 = 7778L;
        Long spentTime11 = 13L;
        Long spentTime12 = 8L;
        Long spentTime21 = 113L;
        Long spentTime22 = 23L;

        ReportYtWorkPortalInfo info11 = new ReportYtWorkPortalInfo(
                personId1, spentTime11, null);
        ReportYtWorkPortalInfo info12 = new ReportYtWorkPortalInfo(
                personId1, spentTime12, null);
        ReportYtWorkPortalInfo info21 = new ReportYtWorkPortalInfo(
                personId2, spentTime21, null);
        ReportYtWorkPortalInfo info22 = new ReportYtWorkPortalInfo(
                personId2, spentTime22, null);

        ReportYtWorkPortalCollector collector = new ReportYtWorkPortalCollector(
                name -> new ArrayList<>(),
                new Date()
        );

        // arbitrary items seq
        Map<Long, ReportYtWorkRowItem> data = Stream.of(info21, info12, info22, info11).collect(collector);

        Assert.assertEquals(2, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(personId1);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(spentTime11 + spentTime12, ytWorkItem.getHomeCompanySpentTime().longValue());
        Assert.assertEquals(spentTime11 + spentTime12, ytWorkItem.getAllTimeSpent().longValue());

        ytWorkItem = data.get(personId2);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(spentTime21 + spentTime22, ytWorkItem.getHomeCompanySpentTime().longValue());
        Assert.assertEquals(spentTime21 + spentTime22, ytWorkItem.getAllTimeSpent().longValue());
        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getGuaranteeSpentTime().size());
    }

    @Test
    public void testContract() {
        Long personId = 7777L;
        Long spentTime = 13L;
        Long platformId = 1L;
        String contractProject = "contract1";

        ReportYtWorkPortalInfo info1 = new ReportYtWorkPortalInfo(
                personId, spentTime, platformId);

        Contract contract = new Contract();
        contract.setNumber(contractProject);
        contract.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 20, 0, 0, 0).getTime());

        ReportYtWorkPortalCollector collector = new ReportYtWorkPortalCollector(
                id -> Arrays.asList(contract),
                new GregorianCalendar(2021, Calendar.JULY, 19, 0, 0, 0).getTime()
        );

        Map<Long, ReportYtWorkRowItem> data = Stream.of(info1).collect(collector);

        Assert.assertEquals(1, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(personId);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(1, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals(spentTime, ytWorkItem.getContractSpentTime().get(contractProject));
        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getGuaranteeSpentTime().size());
        Assert.assertEquals(spentTime, ytWorkItem.getAllTimeSpent());
    }

    @Test
    public void testTwoContract() {
        Long personId = 7777L;
        Long spentTime1 = 12L;
        Long spentTime2 = 14L;
        Long platformId = 1L;
        String contractName1 = "contract1";
        String contractName2 = "contract2";

        ReportYtWorkPortalInfo info1 = new ReportYtWorkPortalInfo(
                personId, spentTime1, platformId);

        ReportYtWorkPortalInfo info2 = new ReportYtWorkPortalInfo(
                personId, spentTime2, platformId);

        Contract contract1 = new Contract();
        contract1.setNumber(contractName1);
        contract1.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 20, 0, 0, 0).getTime());

        Contract contract2 = new Contract();
        contract2.setNumber(contractName2);
        contract2.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 21, 0, 0, 0).getTime());

        ReportYtWorkPortalCollector collector = new ReportYtWorkPortalCollector(
                id -> Arrays.asList(contract1, contract2),
                new GregorianCalendar(2021, Calendar.JULY, 19, 0, 0, 0).getTime()
        );

        Map<Long, ReportYtWorkRowItem> data = Stream.of(info2, info1).collect(collector);

        Assert.assertEquals(1, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(personId);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(2, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals((spentTime1 + spentTime2) / 2 , ytWorkItem.getContractSpentTime().get(contractName1).longValue());
        Assert.assertEquals((spentTime1 + spentTime2) / 2 , ytWorkItem.getContractSpentTime().get(contractName2).longValue());
        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getGuaranteeSpentTime().size());
        Assert.assertEquals(spentTime1 + spentTime2, ytWorkItem.getAllTimeSpent().longValue());
    }

    @Test
    public void testContractAndGuaranty() {
        Long personId = 7777L;
        Long spentTime1 = 12L;
        Long spentTime2 = 14L;
        Long platformId = 1L;
        String contractName1 = "contract1";
        String contractName2 = "contract2";

        ReportYtWorkPortalInfo info1 = new ReportYtWorkPortalInfo(
                personId, spentTime1, platformId);

        ReportYtWorkPortalInfo info2 = new ReportYtWorkPortalInfo(
                personId, spentTime2, platformId);

        Contract contract = new Contract();
        contract.setNumber(contractName1);
        contract.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 20, 0, 0, 0).getTime());

        Contract guaranty = new Contract();
        guaranty.setNumber(contractName2);
        guaranty.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 17, 0, 0, 0).getTime());

        ReportYtWorkPortalCollector collector = new ReportYtWorkPortalCollector(
                id -> Arrays.asList(contract, guaranty),
                new GregorianCalendar(2021, Calendar.JULY, 19, 0, 0, 0).getTime()
        );

        Map<Long, ReportYtWorkRowItem> data = Stream.of(info2, info1).collect(collector);

        Assert.assertEquals(1, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(personId);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(1, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals((spentTime1 + spentTime2), ytWorkItem.getContractSpentTime().get(contractName1).longValue());
        Assert.assertNull(ytWorkItem.getContractSpentTime().get(contractName2));
        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getGuaranteeSpentTime().size());
        Assert.assertEquals(spentTime1 + spentTime2, ytWorkItem.getAllTimeSpent().longValue());
    }

    @Test
    public void testTwoGuaranty() {
        Long personId = 7777L;
        Long spentTime1 = 12L;
        Long spentTime2 = 14L;
        Long platformId = 1L;
        String contractName1 = "contract1";
        String contractName2 = "contract2";

        ReportYtWorkPortalInfo info1 = new ReportYtWorkPortalInfo(
                personId, spentTime1, platformId);

        ReportYtWorkPortalInfo info2 = new ReportYtWorkPortalInfo(
                personId, spentTime2, platformId);

        Contract guaranty1 = new Contract();
        guaranty1.setNumber(contractName1);
        guaranty1.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 18, 0, 0, 0).getTime());

        Contract guaranty2 = new Contract();
        guaranty2.setNumber(contractName2);
        guaranty2.setDateValid(new GregorianCalendar(2021, Calendar.JULY, 17, 0, 0, 0).getTime());

        ReportYtWorkPortalCollector collector = new ReportYtWorkPortalCollector(
                id -> Arrays.asList(guaranty1, guaranty2),
                new GregorianCalendar(2021, Calendar.JULY, 19, 0, 0, 0).getTime()
        );

        Map<Long, ReportYtWorkRowItem> data = Stream.of(info2, info1).collect(collector);

        Assert.assertEquals(1, data.size());
        ReportYtWorkRowItem ytWorkItem = data.get(personId);

        Assert.assertNotNull(ytWorkItem);
        Assert.assertEquals(2, ytWorkItem.getGuaranteeSpentTime().size());
        Assert.assertEquals((spentTime1 + spentTime2) / 2 , ytWorkItem.getGuaranteeSpentTime().get(contractName1).longValue());
        Assert.assertEquals((spentTime1 + spentTime2) / 2 , ytWorkItem.getGuaranteeSpentTime().get(contractName2).longValue());
        Assert.assertEquals(0, ytWorkItem.getNiokrSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getNmaSpentTime().size());
        Assert.assertEquals(0, ytWorkItem.getContractSpentTime().size());
        Assert.assertEquals(spentTime1 + spentTime2, ytWorkItem.getAllTimeSpent().longValue());
    }
}
