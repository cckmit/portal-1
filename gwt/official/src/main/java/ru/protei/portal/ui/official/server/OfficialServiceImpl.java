package ru.protei.portal.ui.official.server;

import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.ent.OfficialMember;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.service.OfficialService;

import java.util.*;

/**
 * Created by serebryakov on 22/08/17.
 */
@Service( "OfficialService" )
public class OfficialServiceImpl implements OfficialService {

    @Override
    public List<Official> getOfficialList() {
        List<Official> officials = new ArrayList<>();
        Official official1 = new Official();
        official1.setCreated(new Date(2017, 12, 17));
        official1.setProductName("SORM");
        official1.setEmployeesNumber("12");
        official1.setInfo("Разыскные мероприятие");
        official1.setAttachmentExists(true);
        official1.setRegion(new EntityOption("Нижегородская область", 1l));

        Official official2 = new Official();
        official2.setCreated(new Date(2016, 05, 11));
        official2.setProductName("DPI");
        official2.setEmployeesNumber("55");
        official2.setInfo("Анализ трафика");
        official2.setAttachmentExists(true);
        official1.setRegion(new EntityOption("Камчатский край", 2l));

        officials.add(official1);
        officials.add(official2);

        return officials;
    }

    @Override
    public Official getOfficial(long id) {
        for (Official official: officialList) {
            if (official.getId() == id) {
                return official;
            }
        }
        return null;
    }

    @Override
    public Long getOfficialCount() {
        return null;
    }

    @Override
    public Map<String, List<Official>> getOfficialsByRegions() {
        List<Official> officials = new ArrayList<>();
        Official official1 = new Official();
        official1.setCreated(new Date(2017, 12, 17));
        official1.setProductName("SORM");
        official1.setEmployeesNumber("12");
        official1.setInfo("Оперативно разыскные мероприятия проводимые республикой Казахстан при содействии сил внеземного происхождения");
        official1.setAttachmentExists(true);
        official1.setRegion(new EntityOption("Астраханская область", 1l));
        official1.setId(1l);

        Official official2 = new Official();
        official2.setCreated(new Date(2016, 05, 11));
        official2.setProductName("DPI");
        official2.setEmployeesNumber("55");
        official2.setInfo("Эффективные технологии анализа трафика");
        official2.setAttachmentExists(true);
        official2.setRegion(new EntityOption("Камчатский край", 2l));
        official2.setId(2l);

        officials.add(official1);
        officials.add(official2);
        officialList = officials;

        List<Official> firstList = new ArrayList<>();
        firstList.add(official1);

        List<Official> secondList = new ArrayList<>();
        secondList.add(official2);

        Map<String, List<Official>> officialsByRegions = new HashMap<>();
        officialsByRegions.put("Астраханская область", firstList);
        officialsByRegions.put("Камчатский край", secondList);

        return officialsByRegions;
    }

    @Override
    public Map<String, List<OfficialMember>> getOfficialMembersByProducts() {
        Map<String, List<OfficialMember>> result = new HashMap<>();
        String company1 = "НТЦ Протей";
        String company2 = "НТЦ Буравчик";
        OfficialMember member1 = new OfficialMember();
        Person person1 = new Person();
        person1.setFirstName("Морозов Евгений Юрьевич");
        person1.setPosition("Директор");
        Person person2 = new Person();
        person2.setFirstName("Григорьев Геннадий Иванович");
        person2.setPosition("Заместитель");
        Person person3 = new Person();
        person3.setFirstName("Серебряков Евгений Дмитриевич");
        person3.setPosition("Менеджер");
        Person person4 = new Person();
        person4.setFirstName("Песков Дмитрий Иванович");
        person4.setPosition("Младший менеджер");
        member1.setMember(person1);
        member1.setAmplua("Любит коньяк 12-летней выдержки, обедает в ресторане \"Какой-то\", 2 дочери, разведен. В общении - дружелюбен, любит разговоры о рыбалке");
        member1.setRelations("Артемьев А.С, Сергеев А.А, Иванов А.Н, Арсеньев В.Н, Козлов М.Л, Максимов М.М, Шелестов Г.А");
        OfficialMember member2 = new OfficialMember();
        member2.setMember(person2);
        member2.setAmplua("Решает управленческие вопросы. Обращаться только в крайнем случае.");
        member2.setRelations("Артемьев А.С, Сергеев А.А, Иванов А.Н, Арсеньев В.Н, Козлов М.Л, Максимов М.М, Шелестов Г.А");
        OfficialMember member3 = new OfficialMember();
        member3.setMember(person3);
        member3.setAmplua("Любит коньяк 12-летней выдержки, обедает в ресторане \"Какой-то\", 2 дочери, разведен. В общении - дружелюбен, любит разговоры о рыбалке");
        member3.setRelations("Артемьев А.С, Сергеев А.А, Иванов А.Н, Арсеньев В.Н, Козлов М.Л, Максимов М.М, Шелестов Г.А");
        OfficialMember member4 = new OfficialMember();
        member4.setMember(person4);
        member4.setAmplua("Решает управленческие вопросы. Обращаться только в крайнем случае.");
        member4.setRelations("Артемьев А.С, Сергеев А.А, Иванов А.Н, Арсеньев В.Н, Козлов М.Л, Максимов М.М, Шелестов Г.А");
        result.put(company1, Arrays.asList(member1, member2));
        result.put(company2, Arrays.asList(member3, member4));

        return result;
    }


    private List<Official> officialList;
}
