ALTER view "Resource".VIEW_Worker
  as select Tm_Person.nID,
    Tm_Person.dtCreation,
    Tm_Person.strCreator,
    Tm_Person.strClient,
    Tm_Person.strClientIP,
    Tm_Person.strLastName,
    Tm_Person.strFirstName,
    Tm_Person.strPatronymic,
	"Resource".FUNC_GetFIO(strLastName,strFirstName,strPatronymic) as strFIO,
	string("Resource".FUNC_GetFIO(strLastName,strFirstName,strPatronymic), if (dRetire is not null or lRetired = 1) then if nSexID = 1 then ' (уволен)' else ' (уволена)' endif endif ) as strWorkerFIO,
    string("Resource".FUNC_GetFullFIO(strLastName, strFirstName, strPatronymic), if (dRetire is not null or lRetired = 1) then if nSexID = 1 then ' (уволен)' else ' (уволена)' endif endif ) as strWorkerFullFIO,
    Tm_Person.dtBirthday,
    Tm_Person.strPassportInfo,
    Tm_Position.strValue as strPosition,
    Tm_Person.strInfo,
    Tm_PersonPROTEI_Extension.strIP_Address,
    Tm_PersonPROTEI_Extension.strE_Mail,
    substring(Tm_PersonPROTEI_Extension.strE_Mail,0,charindex('@',Tm_PersonPROTEI_Extension.strE_Mail)-1) as strLogin,
    ifnull(Tm_Person.nSYS_MutexID,0,1) as lIsMutexed,
    Tm_Person.nSexID,
	dRetire
	//number(*) as nNumber

	from "Resource".Tm_PersonPROTEI_Extension key join("Resource".Tm_Person left outer join "Resource".Tm_Company) left outer join "OK".Tm_Position
	where (Tm_Company.nID = 1) and ((Tm_PersonPROTEI_Extension.strE_Mail is not null) or (strLastName = 'коллектив'));

alter table "Resource".Tm_Person add strDepartment Dm_DictionaryValue2;

comment on column "Resource".Tm_Person.strDepartment is 'Отдел компании';

update "Resource".Tm_Person set strDepartment = (
select d.strDescription
from "Resource".Tm_PersonPROTEI_Extension pe
left outer join "OK".Tm_Department d on d.nID = pe.nDepartmentID
where "Resource".Tm_Person.nID = pe.nID);