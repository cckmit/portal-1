create view "Resource".view_person_contact_data as
select p.nID, p.nPersonID,pp.strValue strProp,c.strValue strCategory,p.strValue
from "resource".tm_person2property p
join "resource".tm_category c on (p.nCategoryID=c.nID)
join "resource".tm_personproperty pp on (pp.nID=p.nPropertyID);
