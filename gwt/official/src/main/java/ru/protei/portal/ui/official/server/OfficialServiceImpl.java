package ru.protei.portal.ui.official.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by serebryakov on 22/08/17.
 */
@Service("OfficialService")
public class OfficialServiceImpl implements ru.protei.portal.ui.common.client.service.OfficialService {

    @Override
    public Official getOfficial(long id) {
        for (Official official : officialList) {
            if (official.getId() == id) {
                return official;
            }
        }
        return null;
    }

    @Override
    public Map<String, List<Official>> getOfficialsByRegions(OfficialQuery query) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< Map< String, List< Official > > > response = officialService.listOfficialsByRegions( descriptor.makeAuthToken(), query );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
    }

//    @Override

    @Override
    public Map<String, List<OfficialMember>> getOfficialMembersByProducts(Long id) {
        Official official = getOfficial(id);
        return createMembersByRegionsMap(official);
    }



    @Override
    public OfficialMember getOfficialMember(Long id) {
        for (Official official: officialList) {
            List<OfficialMember> members = official.getMembers();
            for (OfficialMember member: members) {
                if (member.getId() == id) {
                    return member;
                }
            }
        }
        return null;
    }

    @Override
    public void saveOfficialMember(OfficialMember officialMember) {
        for (Official official: officialList) {
            List<OfficialMember> members = official.getMembers();
            for (OfficialMember member: members) {
                if (member.getId() == officialMember.getId()) {
                    int index = members.indexOf(member);
                    members.set(index, officialMember);
                }
            }
        }
    }

    @Override
    public void initMembers() {
//        Official official1 = new Official();
//        official1.setCreated(new Date(2017, 12, 17));
//        official1.setProduct(new DevUnit("SORM", 1l));
//        official1.setNumberEmployees("12");
//        official1.setInfo("Оперативно разыскные мероприятия проводимые республикой Казахстан при содействии сил внеземного происхождения");
//        official1.setAttachmentExists(true);
//        official1.setRegion(new EntityOption("Астраханская область", 2l));
//        official1.setId(1l);
//
//        Official official2 = new Official();
//        official2.setCreated(new Date(2016, 05, 11));
//        official2.setProduct(new EntityOption("DPI", 3l));
//        official2.setNumberEmployees("55");
//        official2.setInfo("Эффективные технологии анализа трафика");
//        official2.setAttachmentExists(true);
//        official2.setRegion(new EntityOption("Камчатский край", 4l));
//        official2.setId(2l);
//
//        OfficialMember member1 = new OfficialMember();
//        member1.setLastName("Морозов");
//        member1.setFirstName("Евгений");
//        member1.setSecondName("Юрьевич");
//        member1.setPosition("Директор");
//        member1.setCompany("НТЦ Протей");
//        member1.setAmplua("Любит коньяк 12-летней выдержки, обедает в ресторане \"Какой-то\", 2 дочери, разведен. В общении - дружелюбен, любит разговоры о рыбалке");
//        member1.setRelations("Артемьев А.С, Сергеев А.А, Иванов А.Н, Арсеньев В.Н, Козлов М.Л, Максимов М.М, Шелестов Г.А");
//        member1.setId(1l);
//
//        OfficialMember member2 = new OfficialMember();
//        member2.setLastName("Григорьев");
//        member2.setFirstName("Геннадий");
//        member2.setSecondName("Иванович");
//        member2.setPosition("Заместитель");
//        member2.setCompany("НТЦ Буравчик");
//        member2.setAmplua("Решает управленческие вопросы. Обращаться только в крайнем случае.");
//        member2.setRelations("Артемьев А.С, Сергеев А.А, Иванов А.Н, Арсеньев В.Н, Козлов М.Л, Максимов М.М, Шелестов Г.А");
//        member2.setId(2l);
//
//        OfficialMember member3 = new OfficialMember();
//        member3.setLastName("Серебряков");
//        member3.setFirstName("Евгений");
//        member3.setSecondName("Дмитриевич");
//        member3.setPosition("Менеджер");
//        member3.setCompany("Гугл");
//        member3.setAmplua("Любит коньяк 12-летней выдержки, обедает в ресторане \"Какой-то\", 2 дочери, разведен. В общении - дружелюбен, любит разговоры о рыбалке");
//        member3.setRelations("Артемьев А.С, Сергеев А.А, Иванов А.Н, Арсеньев В.Н, Козлов М.Л, Максимов М.М, Шелестов Г.А");
//        member3.setId(3l);
//
//        OfficialMember member4 = new OfficialMember();
//        member4.setLastName("Песков");
//        member4.setFirstName("Дмитрий");
//        member4.setSecondName("Иванович");
//        member4.setPosition("Младший менеджер");
//        member4.setCompany("Яндекс");
//        member4.setAmplua("Решает управленческие вопросы. Обращаться только в крайнем случае.");
//        member4.setRelations("Артемьев А.С, Сергеев А.А, Иванов А.Н, Арсеньев В.Н, Козлов М.Л, Максимов М.М, Шелестов Г.А");
//        member4.setId(4l);
//
//        OfficialMember member5 = new OfficialMember();
//        member5.setLastName("Иванов");
//        member5.setFirstName("Иванов");
//        member5.setSecondName("Иванович");
//        member5.setPosition("Заместитель");
//        member5.setCompany("Яндекс");
//        member5.setAmplua("Хороший человек");
//        member5.setRelations("Артемьев А.С, Сергеев А.А, Иванов А.Н, Арсеньев В.Н, Козлов М.Л, Максимов М.М, Шелестов Г.А");
//        member5.setId(5l);
//
//        members.add(member1);
//        members.add(member2);
//        members.add(member3);
//        members.add(member4);
//        members.add(member5);
//
//        official1.setMembers(Arrays.asList(members.get(0), members.get(1)));
//        official2.setMembers(Arrays.asList(members.get(2), members.get(3), members.get(4)));
//
//        officialList.add(official1);
//        officialList.add(official2);
//
//
//        List<Official> firstList = new ArrayList<>();
//        firstList.add(official1);
//        List<Official> secondList = new ArrayList<>();
//        secondList.add(official2);
//        officialsByRegions.put("Астраханская область", firstList);
//        officialsByRegions.put("Камчатский край", secondList);

    }

    private Map<String, List<OfficialMember>> createMembersByRegionsMap(Official official) {
        Map<String, List<OfficialMember>> membersByRegionsMap = new HashMap<>();

        List<OfficialMember> members = official.getMembers();
        Set<String> companies = new HashSet<>();
        for (OfficialMember member : members) {
            companies.add(member.getCompany());
        }
        for (String company : companies) {
            List<OfficialMember> newMembers = new ArrayList<>();
            for (OfficialMember member : members) {
                if (member.getCompany().equals(company)) {
                    newMembers.add(member);
                }
            }
            membersByRegionsMap.put(company, newMembers);
        }

        return membersByRegionsMap;
    }

    Map<String, List<Official>> officialsByRegions = new HashMap<>();

    private List<Official> officialList = new ArrayList<>();
    private List<OfficialMember> members = new ArrayList<>();

    @Autowired
    private ru.protei.portal.core.service.OfficialService officialService;
    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;

}
