/*
 * Федеральные округа
 */
insert into location (ID, TYPE_ID, name, code) values (1, 1, 'Центральный Федеральный Округ','30', 'ЦФО');
insert into location (ID, TYPE_ID, name, code) values (2, 1, 'Северо-Западный Федеральный Округ','СЗФО');
insert into location (ID, TYPE_ID, name, code) values (3, 1, 'Южный Федеральный Округ','ЮФО');
insert into location (ID, TYPE_ID, name, code) values (4, 1, 'Северо-Кавказский Округ','СКФО');
insert into location (ID, TYPE_ID, name, code) values (5, 1, 'Приволжский Федеральный Округ','ПФО');
insert into location (ID, TYPE_ID, name, code) values (6, 1, 'Уральский Федеральный Округ','УФО');
insert into location (ID, TYPE_ID, name, code) values (7, 1, 'Сибирский Федеральный Округ','СФО');
insert into location (ID, TYPE_ID, name, code) values (8, 1, 'Дальневосточный Федеральный Округ','ДВФО');
insert into location (ID, TYPE_ID, name, code) values (9, 1, 'Крымский Федеральный Округ','КФО');

/*
 * Регионы
 */
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Алтайский край','22', 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Амурская область','28', 8, '8');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Архангельская область','29', 2, '2');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Астраханская область','30', 3, '3' );
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Белгородская область','31', 1,'1' );
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Брянская область','32', 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Владимирская область', '33',1 , '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Волгоградская область','34' , 3, '3');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Вологодская область','35' , 2, '2');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Воронежская область', '36', 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Еврейская автономная область', '37', 8, '8');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Забайкальский край', '75', 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Ивановская область','37' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Иркутская область','38' , 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Кабардино-Балкарская республика','07' , 4, '4');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Калининградская область','39' , 2, '2');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Калужская область','40' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Камчатский край','41' , 8, '8');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Карачаево-Черкесская республика','09' , 4, '4');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Кемеровская область','42' , 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Кировская область','43' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Костромская область','44' , 1,'1' );
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Краснодарский край','23' , 3, '3');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Красноярский край','24' , 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Курганская область','45' , 6, '6');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Курская область','46' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Ленинградская область','47' , 2, '2');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Липецкая область','48' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Магаданская область','49' , 8, '8');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Москва','7' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Московская область','50' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Мурманская область','51' , 2, '2');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Ненецкий автономный округ','83' , 2, '2');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Нижегородская область','52' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Новгородская область','53' , 2, '2');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Новосибирская область','54' , 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Омская область','55' , 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Оренбургская область','56' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Орловская область','57' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Пензенская область','58' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Пермский край','59' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Приморский край','25' , 8, '8');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Псковская область','60' , 2, '2');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Адыгея','01' , 3, '3');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Алтай','04' , 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Башкортостан','02' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Бурятия','03' , 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Дагестан','05' , 4, '4');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Ингушетия','06' , 4, '4');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Калмыкия','08' , 3, '3');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Карелия','10' , 2, '2');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Коми','11' , 2, '2');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Крым','91' , 3, '3');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Марий Эл','12' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Мордовия','13' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Саха (Якутия)','14' , 8, '8');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Северная Осетия — Алания','15' , 4, '4');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Татарстан','16' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Тыва','17' , 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Республика Хакасия','19' , 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Ростовская область','61' , 3, '3');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Рязанская область','62' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Самарская область','63' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Санкт-Петербург','78' , 2, '2');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Саратовская область','64' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Сахалинская область','65' , 8, '8');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Свердловская область','66' , 6, '6');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Севастополь','92' , 3, '3');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Смоленская область','67' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Ставропольский край','26' , 4, '4');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Тамбовская область','68' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Тверская область','69' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Томская область','70' , 7, '7');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Тульская область','71' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Тюменская область','72' , 6, '6');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Удмуртская республика','18' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Ульяновская область','73' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Хабаровский край','27' , 8, '8');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Ханты-Мансийский автономный округ - Югра','86' , 6, '6');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Челябинская область','74' , 6, '6');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Чеченская республика','20' , 4, '4');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Чувашская республика','21' , 5, '5');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Чукотский автономный округ','87' , 8, '8');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Ямало-Ненецкий автономный округ','89' , 6, '6');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Ярославская область','76' , 1, '1');
insert into location (TYPE_ID, name, code, PARENT_ID, path) values (2, 'Байконур','94' , 3, '3');
