use portal;

# Переходим на идентификаторы. В базе автоматически будут заменены строковые значения енамов на айди.
update case_filter set params = REGEXP_REPLACE(params, 'importances', 'importanceIds') where params like "%importances%";
