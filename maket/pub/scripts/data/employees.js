var CaughtLetters = ["А","Б","В","Г","Д","Е","Ж","З","И","К","Л","М","Н","О","П","Р","С","Т","У","Ф","Х","Ц","Ш","Щ","Ю","Я"];
var Employees = [{id: 1, fio: "Игнатов Игнатий Ильяович" , birthday: "Январь, 4", post: "инженер 2 категории"},{id: 2, fio: "Панфилова Надежда Дамировна" , birthday: "Февраль, 13", post: "ведущий специалист"},{id: 3, leader: true, fio: "Веселова Милица Донатовна" , birthday: "Март, 12", post: "Ведущий менеджер по контролю проектов"},{id: 4, fio: "Котов Святослав Авксентьевич" , birthday: "Апрель, 15", post: "старший бухгалтер"},{id: 5, leader: true, fio: "Васильева Евфросиния Дмитрьевна" , birthday: "Май, 8", post: "Группа тестирования решений для сетей передачи данных"},{id: 6, fio: "Федотов Михаил Иринеевич" , birthday: "Июнь, 28", post: "Группа Интеллектуальные платформы"},{id: 7, fio: "Лазарев Никита Игнатьевич" , birthday: "Июль, 22", post: "Ведущий аналитик"},{id: 8, fio: "Тихонов Демьян Альвианович" , birthday: "Август, 17", post: "Группа разработки программного обеспечения"},{id: 9, fio: "Рябов Юлиан Константинович" , birthday: "Сентябрь, 9", post: "Отдел разработки программного обеспечения №7"},{id: 10, fio: "Сидорова Эмилия Леонидовна" , birthday: "Октябрь, 28", post: "Группа разработки программного обеспечения NGN"},{id: 11, fio: "Колесникова Дарья Всеволодовна" , birthday: "Ноябрь, 4", post: "Группа разработки программного обеспечения"},{id: 12, fio: "Сысоев Григорий Лукьевич" , birthday: "Декабрь, 26", post: "Группа разработки программного обеспечения"},{id: 13, leader: true, fio: "Большаков Юрий Фёдорович" , birthday: "Январь, 13", post: "начальник отдела"},{id: 14, fio: "Гуляева Фаина Протасьевна" , birthday: "Февраль, 30", post: "ПРОТЕЙ СТ"},{id: 15, fio: "Зыков Александр Христофорович" , birthday: "Март, 18", post: "Ведуший программист"},{id: 16, fio: "Федотов Ириней Пантелеймонович" , birthday: "Апрель, 22", post: "Группа разработки программного обеспечения NGN"},{id: 17, fio: "Лаврентьев Фёдор Куприянович" , birthday: "Май, 2", post: "Менеджер проектов"},{id: 18, fio: "Веселова Полина Васильевна" , birthday: "Июнь, 17", post: "инженер-проектировщик"},{id: 19, fio: "Тихонов Варлам Валентинович" , birthday: "Июль, 1", post: "Представительство в Узбекистане"},{id: 20, fio: "Лыткин Мэлс Матвеевич" , birthday: "Август, 4", post: "менеджер региональных проектов"},{id: 21, fio: "Ермакова Клавдия Романовна" , birthday: "Сентябрь, 16", post: "не определено"},{id: 22, fio: "Уварова Евгения Дмитрьевна" , birthday: "Октябрь, 9", post: "Отдел системных администраторов"},{id: 23, fio: "Трофимов Алексей Даниилович" , birthday: "Ноябрь, 17", post: "Инженер-сметчик 1 категории"},{id: 24, fio: "Фомина Людмила Семёновна" , birthday: "Декабрь, 20", post: "не определено"},{id: 25, fio: "Баранова Валентина Владимировна" , birthday: "Январь, 15", post: "Отдел разработки программного обеспечения №6"},{id: 26, fio: "Богданова Раиса Павловна" , birthday: "Февраль, 14", post: "руководитель направления"},{id: 27, fio: "Зыкова Елена Юлиановна" , birthday: "Март, 8", post: "Руководитель общего отдела"},{id: 28, fio: "Афанасьев Анатолий Всеволодович" , birthday: "Апрель, 6", post: "Ведуший программист"},{id: 29, leader: true, fio: "Юдин Созон Тихонович" , birthday: "Май, 20", post: "старший бухгалтер"},{id: 30, fio: "Самойлов Руслан Мэлсович" , birthday: "Июнь, 23", post: "инженер по охране труда"},{id: 31, leader: true, fio: "Копылов Агафон Филатович" , birthday: "Июль, 27", post: "руководитель проектов"},{id: 32, fio: "Петухова Пелагея Александровна" , birthday: "Август, 22", post: "Отдел разработки программного обеспечения №4"},{id: 33, fio: "Гурьев Николай Иринеевич" , birthday: "Сентябрь, 28", post: "Заместитель генерального директора по развитию человеческих ресурсов"},{id: 34, fio: "Куликова Ирина Ростиславовна" , birthday: "Октябрь, 24", post: "инженер-электронщик"},{id: 35, fio: "Силина Прасковья Фёдоровна" , birthday: "Ноябрь, 14", post: "ведущий специалист"},{id: 36, fio: "Иван Васильевна" , birthday: "Декабрь, 14", post: "Руководитель направления СОРМ в сетях передачи данных"},{id: 37, fio: "Егорова Маргарита Демьяновна" , birthday: "Январь, 25", post: "Руководитель общего отдела"},{id: 38, fio: "Жуков Демьян Федотович" , birthday: "Февраль, 21", post: "инженер-аналитик"},{id: 39, fio: "Лукина Оксана Святославовна" , birthday: "Март, 2", post: "Администрация"},{id: 40, fio: "Тихонова Иванна Мэлсовна" , birthday: "Апрель, 30", post: "Ведущий менеджер по контролю проектов"},{id: 41, fio: "Макаров Ефим Арсеньевич" , birthday: "Май, 7", post: "Отдел разработки программного обеспечения №7"},{id: 42, fio: "Морозова Агата Игнатьевна" , birthday: "Июнь, 3", post: " "},{id: 43, fio: "Комаров Куприян Геннадьевич" , birthday: "Июль, 3", post: "Отдел проектирования"},{id: 44, fio: "Кабанов Станислав Кимович" , birthday: "Август, 19", post: "инженер-конструктор"},{id: 45, fio: "Быков Руслан Федотович" , birthday: "Сентябрь, 8", post: "Заместитель IT директора"},{id: 46, fio: "Суханова Евгения Георгьевна" , birthday: "Октябрь, 21", post: "Старший научный сотрудник"},{id: 47, fio: "Игнатьева Фёкла Лукьяновна" , birthday: "Ноябрь, 28", post: "Группа тестирования оборудования NGN"},{id: 48, fio: "Сергеев Григорий Владиславович" , birthday: "Декабрь, 8", post: "Руководитель общего отдела"},{id: 49, fio: "Мясникова Иванна Лукьяновна" , birthday: "Январь, 4", post: "Группа разработки программного обспечения №5"},{id: 50, fio: "Кулаков Вячеслав Русланович" , birthday: "Февраль, 27", post: "Подразделение разработки аппаратного обеспечения"},{id: 51, fio: "Аксёнов Илья Игоревич" , birthday: "Март, 27", post: "Руководитель отдела разработки программного обеспечения"},{id: 52, fio: "Игнатьев Лукий Иринеевич" , birthday: "Апрель, 9", post: "Подразделение разработки аппаратного обеспечения"},{id: 53, fio: "Фёдоров Ростислав Владиславович" , birthday: "Май, 23", post: "проектировщик"},{id: 54, fio: "Родионов Артём Вадимович" , birthday: "Июнь, 25", post: "Внештатный менеджер по продажам"},{id: 55, fio: "Меркушев Юлиан Альвианович" , birthday: "Июль, 29", post: "Ведущий аналитик"},{id: 56, fio: "Шашкова Нина Валерьяновна" , birthday: "Август, 20", post: "администратор баз данных"},{id: 57, fio: "Жуков Святослав Матвеевич" , birthday: "Сентябрь, 21", post: "Ведущий менеджер по контролю проектов"},{id: 58, fio: "Дьячков Святослав Всеволодович" , birthday: "Октябрь, 11", post: "Отдел контроля качества"},{id: 59, fio: "Гусев Митрофан Игнатьевич" , birthday: "Ноябрь, 18", post: "Группа разработки программного обеспечения"},{id: 60, fio: "Зиновьев Артём Антонович" , birthday: "Декабрь, 20", post: "инженер по качеству"},{id: 61, fio: "Некрасов Федот Станиславович" , birthday: "Январь, 18", post: "Заместитель генерального директора по общим вопросам"},{id: 62, fio: "Павлов Артём Макарович" , birthday: "Февраль, 26", post: "Группа разработки программного обеспечения NGN"},{id: 63, fio: "Кириллов Геласий Игнатьевич" , birthday: "Март, 25", post: "Директор по развитию продуктов"},{id: 64, fio: "Доронина Алина Авдеевна" , birthday: "Апрель, 5", post: "руководитель региональных проектов"},{id: 65, fio: "Беляева Агафья Ивановна" , birthday: "Май, 16", post: "Руководитель отдела разработки программного обеспечения"},{id: 66, fio: "Герасимов Станислав Всеволодович" , birthday: "Июнь, 4", post: "Группа разработки программного обеспечения"},{id: 67, fio: "Аксёнова Евдокия Вадимовна" , birthday: "Июль, 3", post: "Директор по развитию продуктов"},{id: 68, fio: "Виноградов Ярослав Альвианович" , birthday: "Август, 21", post: "инженер-сметчик 2 категории"},{id: 69, fio: "Щербакова Глафира Федосеевна" , birthday: "Сентябрь, 8", post: "менеджер региональных проектов"},{id: 70, fio: "Доронин Роман Демьянович" , birthday: "Октябрь, 18", post: "Группа разработки программного обеспечения систем управления"},{id: 71, fio: "Смирнова Элеонора Германновна" , birthday: "Ноябрь, 29", post: "Ведущий научный сотрудник"},{id: 72, fio: "Красильников Андрей Фёдорович" , birthday: "Декабрь, 27", post: "Отдел договоров"},{id: 73, fio: "Трофимова Валерия Игнатьевна" , birthday: "Январь, 22", post: "Группа Центры обслуживания вызовов"},{id: 74, fio: "Медведьев Якун Кондратович" , birthday: "Февраль, 20", post: "Заместитель главного бухгалтера"},{id: 75, fio: "Лебедева Валентина Куприяновна" , birthday: "Март, 26", post: "Группа разработки программного обеспечения"},{id: 76, fio: "Новикова Марина Еремеевна" , birthday: "Апрель, 23", post: "Группа разработки программного обеспечения"},{id: 77, fio: "Коновалов Евсей Максимович" , birthday: "Май, 13", post: "инженер-программист"},{id: 78, fio: "Хохлов Лукий Макарович" , birthday: "Июнь, 24", post: "Заместитель директора по маркетингу"},{id: 79, fio: "Кабанова Евфросиния Парфеньевна" , birthday: "Июль, 30", post: "Отдел менеджмента качества"},{id: 80, fio: "Белякова Анастасия Максимовна" , birthday: "Август, 5", post: "специалист"},{id: 81, fio: "Пестова Анжела Александровна" , birthday: "Сентябрь, 4", post: "руководитель направления"},{id: 82, fio: "Большаков Фёдор Кимович" , birthday: "Октябрь, 18", post: "Подразделение разработки программного обеспечения"},{id: 83, fio: "Цветкова Фёкла Мартыновна" , birthday: "Ноябрь, 16", post: "Группа разработки программного обеспечения"},{id: 84, fio: "Артемьева Феврония Максимовна" , birthday: "Декабрь, 8", post: "Группа разработки программного обспечения №5"},{id: 85, fio: "Дмитриев Павел Николаевич" , birthday: "Январь, 22", post: "Руководитель подразделения"},{id: 86, fio: "Самойлов Альвиан Ярославович" , birthday: "Февраль, 27", post: "Директор по маркетингу и системным исследованиям"},{id: 87, fio: "Ефимов Николай Мэлорович" , birthday: "Март, 9", post: "Отдел системных администраторов"},{id: 88, fio: "Медведьева Таисия Евгеньевна" , birthday: "Апрель, 26", post: "Руководитель общего отдела"},{id: 89, fio: "Горбачёва Алевтина Макаровна" , birthday: "Май, 12", post: "Инженер-сметчик 1 категории"},{id: 90, fio: "Мельников Виктор Мстиславович" , birthday: "Июнь, 26", post: "техник"},{id: 91, fio: "Смирнов Бронислав Пантелеймонович" , birthday: "Июль, 9", post: "Администрация"},{id: 92, fio: "Голубев Роман Мстиславович" , birthday: "Август, 17", post: "Группа разработки программного обеспечения"},{id: 93, fio: "Орехова Василиса Анатольевна" , birthday: "Сентябрь, 5", post: "проектировщик"},{id: 94, fio: "Федотов Николай Игнатьевич" , birthday: "Октябрь, 1", post: "Отдел опытного производства"},{id: 95, fio: "Рожкова Глафира Юлиановна" , birthday: "Ноябрь, 17", post: "Группа разработки программного обеспечения систем управления"},{id: 96, fio: "Горбачёва Венера Дмитрьевна" , birthday: "Декабрь, 7", post: "старший бухгалтер"},{id: 97, fio: "Кошелева Раиса Владимировна" , birthday: "Январь, 11", post: "Группа разработки программного обспечения №5"},{id: 98, fio: "Антонова Полина Михаиловна" , birthday: "Февраль, 21", post: "не определено"},{id: 99, fio: "Мишин Юрий Мэлсович" , birthday: "Март, 24", post: "Менеджер по развитию международного бизнеса"},{id: 100, fio: "Блинова Прасковья Владимировна" , birthday: "Апрель, 25", post: "Руководитель направления Управление трафиком и широкополосные сети"},{id: 101, fio: "Павлова Светлана Серапионовна" , birthday: "Май, 12", post: "Группа разработки программного обеспечения"},{id: 102, fio: "Иван Никитевна" , birthday: "Июнь, 10", post: "главный технолог"},{id: 103, fio: "Трофимова Анжела Руслановна" , birthday: "Июль, 18", post: "Подразделение маркетинга и системных исследований"},{id: 104, fio: "Игнатьева Ольга Мартыновна" , birthday: "Август, 27", post: "старший бухгалтер"},{id: 105, fio: "Беляева Лидия Протасьевна" , birthday: "Сентябрь, 20", post: "ПРОТЕЙ СТ"},{id: 106, fio: "Соловьёв Парфений Протасьевич" , birthday: "Октябрь, 7", post: "Аналитик"},{id: 107, fio: "Емельянов Алексей Аристархович" , birthday: "Ноябрь, 16", post: "Начальник отдела управления проектами"},{id: 108, fio: "Комиссаров Лукьян Еремеевич" , birthday: "Декабрь, 13", post: "Заместитель главы представительства"},{id: 109, fio: "Быков Вадим Евгеньевич" , birthday: "Январь, 8", post: "Технический писатель"},{id: 110, fio: "Баранов Фёдор Святославович" , birthday: "Февраль, 15", post: "Отдел менеджмента качества"},{id: 111, fio: "Котов Иван Борисович" , birthday: "Март, 29", post: "Группа тестирования аппаратного обеспечения"},{id: 112, fio: "Осипов Михаил Максимович" , birthday: "Апрель, 10", post: "Отдел проектирования"},{id: 113, fio: "Пестова Наина Мэлсовна" , birthday: "Май, 12", post: "Группа Центры обслуживания вызовов"},{id: 114, fio: "Комиссаров Руслан Авдеевич" , birthday: "Июнь, 26", post: "Внештатный менеджер по продажам"},{id: 115, fio: "Гаврилов Юрий Витальевич" , birthday: "Июль, 6", post: "Руководитель группы разработки программного обеспечения"},{id: 116, fio: "Лазарева Алевтина Аристарховна" , birthday: "Август, 27", post: "главный технолог"},{id: 117, fio: "Силин Богдан Лукьянович" , birthday: "Сентябрь, 13", post: "руководитель отдела проектирования"},{id: 118, fio: "Комарова Нина Гордеевна" , birthday: "Октябрь, 8", post: "руководитель направления"},{id: 119, fio: "Сафонов Константин Христофорович" , birthday: "Ноябрь, 8", post: "Руководитель группы проектирования"},{id: 120, fio: "Васильев Семён Тихонович" , birthday: "Декабрь, 12", post: "руководитель направления"},{id: 121, fio: "Князева Октябрина Тихоновна" , birthday: "Январь, 26", post: "не определено"},{id: 122, fio: "Зайцев Владимир Мстиславович" , birthday: "Февраль, 1", post: "информатик-дизайнер"},{id: 123, fio: "Зимин Валерий Якунович" , birthday: "Март, 12", post: "Директор по развитию продуктов"},{id: 124, fio: "Колесникова Ольга Степановна" , birthday: "Апрель, 14", post: "Руководитель отдела по работе с персоналом и PR"},{id: 125, fio: "Родионов Лаврентий Созонович" , birthday: "Май, 8", post: "Менеджер по развитию международного бизнеса"},{id: 126, fio: "Шубин Христофор Тихонович" , birthday: "Июнь, 12", post: "Отдел подготовки поставок"},{id: 127, fio: "Шарапов Федосей Дамирович" , birthday: "Июль, 1", post: "Администрация"},{id: 128, fio: "Зуева Алина Никитевна" , birthday: "Август, 16", post: "консультант по техническим вопросам"},{id: 129, fio: "Шарапов Глеб Евгеньевич" , birthday: "Сентябрь, 14", post: " "},{id: 130, fio: "Мишин Евсей Германович" , birthday: "Октябрь, 3", post: "Группа тестирования решений для сетей передачи данных"},{id: 131, fio: "Евдокимов Василий Мэлорович" , birthday: "Ноябрь, 21", post: "Руководитель направления СОРМ в сетях передачи данных"},{id: 132, fio: "Селезнёв Германн Владимирович" , birthday: "Декабрь, 25", post: "Группа разработки программного обеспечения"},{id: 133, fio: "Григорьева Олимпиада Созоновна" , birthday: "Январь, 27", post: "ПРОТЕЙ СТ"},{id: 134, fio: "Захаров Игорь Борисович" , birthday: "Февраль, 10", post: "Заместитель генерального директора по развитию человеческих ресурсов"},{id: 135, fio: "Морозова Евпраксия Эдуардовна" , birthday: "Март, 14", post: "менеджер региональных проектов"},{id: 136, fio: "Уваров Пантелеймон Федотович" , birthday: "Апрель, 30", post: "Подразделение разработки аппаратного обеспечения"},{id: 137, fio: "Ситников Евгений Созонович" , birthday: "Май, 3", post: "Группа разработки программного обеспечения"},{id: 138, fio: "Яковлева Дарья Фёдоровна" , birthday: "Июнь, 2", post: "Руководитель отдела по работе с персоналом и PR"},{id: 139, fio: "Князев Юлиан Ильяович" , birthday: "Июль, 28", post: "информатик-дизайнер"},{id: 140, fio: "Миронов Ким Валерьянович" , birthday: "Август, 9", post: "Инженер-сметчик 1 категории"},{id: 141, fio: "Шубина София Руслановна" , birthday: "Сентябрь, 1", post: "Руководитель подразделения"},{id: 142, fio: "Блинов Семён Дмитрьевич" , birthday: "Октябрь, 20", post: "консультант по техническим вопросам"},{id: 143, fio: "Харитонова Валентина Протасьевна" , birthday: "Ноябрь, 29", post: "Группа тестирования решений для сетей передачи данных"},{id: 144, fio: "Копылова Майя Ефимовна" , birthday: "Декабрь, 1", post: "Отдел управления проектами"},{id: 145, fio: "Шубина Наталья Матвеевна" , birthday: "Январь, 23", post: "начальник отдела"},{id: 146, fio: "Кабанова Зоя Игнатьевна" , birthday: "Февраль, 13", post: "инженер 2 категории"},{id: 147, fio: "Хохлов Филат Брониславович" , birthday: "Март, 16", post: "инженер-электронщик"},{id: 148, fio: "Карпова Вера Геннадьевна" , birthday: "Апрель, 2", post: "Отдел разработки программного обеспечения №6"},{id: 149, fio: "Дмитриев Якун Всеволодович" , birthday: "Май, 8", post: "Ведущий аналитик"},{id: 150, fio: "Корнилова Октябрина Агафоновна" , birthday: "Июнь, 2", post: "IT директор"},{id: 151, fio: "Соловьёва Акулина Денисовна" , birthday: "Июль, 4", post: "Ведущий аналитик"},{id: 152, fio: "Красильникова Фёкла Геласьевна" , birthday: "Август, 21", post: "Группа разработки программного обеспечения"},{id: 153, fio: "Тимофеева Валерия Данииловна" , birthday: "Сентябрь, 13", post: "техник"},{id: 154, fio: "Фёдоров Вячеслав Михаилович" , birthday: "Октябрь, 25", post: "Заместитель генерального директора по общим вопросам"},{id: 155, fio: "Сафонова Нинель Якововна" , birthday: "Ноябрь, 16", post: "Группа разработки программного обеспечения №1"},{id: 156, fio: "Николаев Христофор Артёмович" , birthday: "Декабрь, 8", post: "Инженер-сметчик 1 категории"},{id: 157, fio: "Кононов Аркадий Антонович" , birthday: "Январь, 20", post: "Общий отдел"},{id: 158, fio: "Субботин Станислав Валерьевич" , birthday: "Февраль, 30", post: "Группа разработки программного обеспечения"},{id: 159, fio: "Матвеева Ираида Валентиновна" , birthday: "Март, 19", post: "Отдел разработки программного обеспечения №6"},{id: 160, fio: "Логинова Агафья Кимовна" , birthday: "Апрель, 6", post: "менеджер по маркетингу"},{id: 161, fio: "Евдокимова Анна Куприяновна" , birthday: "Май, 4", post: "ведущий инженер-программист"},{id: 162, fio: "Семёнов Федот Ярославович" , birthday: "Июнь, 9", post: "Группа разработки программного обеспечения"},{id: 163, fio: "Колесникова Любовь Альбертовна" , birthday: "Июль, 12", post: "Общий отдел"},{id: 164, fio: "Евсеева Пелагея Мэлсовна" , birthday: "Август, 7", post: "Группа Ситуационные центры и служба 112"},{id: 165, fio: "Мухин Федосей Сергеевич" , birthday: "Сентябрь, 11", post: "Менеджер по развитию международного бизнеса"},{id: 166, fio: "Панова Зинаида Якововна" , birthday: "Октябрь, 26", post: "Руководитель общего отдела"},{id: 167, fio: "Ефремов Федосей Еремеевич" , birthday: "Ноябрь, 14", post: "Отдел разработки программного обеспечения NGN"},{id: 168, fio: "Зиновьев Святослав Дамирович" , birthday: "Декабрь, 23", post: "Отдел контроля качества"},{id: 169, fio: "Силина Елизавета Лукьевна" , birthday: "Январь, 13", post: "Отдел разработки программного обеспечения СОРМ"},{id: 170, fio: "Пономарёв Лаврентий Максимович" , birthday: "Февраль, 26", post: "Заместитель генерального директора по общим вопросам"},{id: 171, fio: "Владимирова Ульяна Никитевна" , birthday: "Март, 7", post: "ведущий инженер-программист"},{id: 172, fio: "Тимофеев Алексей Матвеевич" , birthday: "Апрель, 6", post: "Ведущий менеджер по контролю проектов"},{id: 173, fio: "Анисимов Пётр Федотович" , birthday: "Май, 21", post: "консультант по техническим вопросам"},{id: 174, fio: "Щербаков Даниил Васильевич" , birthday: "Июнь, 14", post: "инженер"},{id: 175, fio: "Воробьёва Евдокия Брониславовна" , birthday: "Июль, 24", post: "Отдел контроля качества"},{id: 176, fio: "Беляев Всеволод Глебович" , birthday: "Август, 9", post: "Группа тестирования оборудования NGN"},{id: 177, fio: "Семёнова Лидия Вячеславовна" , birthday: "Сентябрь, 25", post: "Специалист по связям с общественностью"},{id: 178, fio: "Жданов Яков Валентинович" , birthday: "Октябрь, 2", post: "Отдел по работе с персоналом и PR"},{id: 179, fio: "Пестов Фрол Гордеевич" , birthday: "Ноябрь, 15", post: "Руководитель направления СОРМ в сетях передачи данных"},{id: 180, fio: "Ермакова Евдокия Аркадьевна" , birthday: "Декабрь, 10", post: "Общий отдел"},{id: 181, fio: "Ершов Ростислав Кимович" , birthday: "Январь, 6", post: "Группа разработки программного обеспечения"},{id: 182, fio: "Бурова Зоя Павловна" , birthday: "Февраль, 1", post: "менеджер по персоналу"},{id: 183, fio: "Ермаков Анатолий Улебович" , birthday: "Март, 3", post: "Группа разработки программного обеспечения"},{id: 184, fio: "Трофимов Роман Кондратович" , birthday: "Апрель, 3", post: "Администрация"},{id: 185, fio: "Гурьев Христофор Мэлсович" , birthday: "Май, 13", post: "Технический директор"},{id: 186, fio: "Стрелкова Анастасия Всеволодовна" , birthday: "Июнь, 21", post: "менеджер по персоналу"},{id: 187, fio: "Никифорова Галина Кимовна" , birthday: "Июль, 9", post: "инженер-конструктор"},{id: 188, fio: "Исаев Донат Ильяович" , birthday: "Август, 19", post: "Технический директор"},{id: 189, fio: "Щукин Григорий Еремеевич" , birthday: "Сентябрь, 10", post: "Отдел системных администраторов"},{id: 190, fio: "Данилова Ксения Максимовна" , birthday: "Октябрь, 24", post: "главный технолог"},{id: 191, fio: "Давыдова Юлия Игоревна" , birthday: "Ноябрь, 18", post: "Руководитель отдела по работе с персоналом и PR"},{id: 192, fio: "Русакова Прасковья Никитевна" , birthday: "Декабрь, 27", post: "Группа разработки программного обеспечения"},{id: 193, fio: "Субботин Улеб Брониславович" , birthday: "Январь, 14", post: "Группа разработки программного обеспечения"},{id: 194, fio: "Шарапова Феврония Алексеевна" , birthday: "Февраль, 3", post: "Группа тестирования аппаратного обеспечения"},{id: 195, fio: "Блинова Евфросиния Авдеевна" , birthday: "Март, 10", post: "Заместитель главы представительства"},{id: 196, fio: "Михайлова Валерия Альбертовна" , birthday: "Апрель, 21", post: "Администрация"},{id: 197, fio: "Афанасьева Фаина Валентиновна" , birthday: "Май, 4", post: "Ведущий специалист"},{id: 198, fio: "Колобова Алина Федосеевна" , birthday: "Июнь, 10", post: "Группа разработки программного обеспечения"},{id: 199, fio: "Мухин Святослав Кимович" , birthday: "Июль, 17", post: "Руководитель группы проектирования"},{id: 200, fio: "Пестова Виктория Протасьевна" , birthday: "Август, 22", post: "Группа разработки программного обеспечения"},{id: 201, fio: "Капустин Антон Валентинович" , birthday: "Сентябрь, 25", post: "ведущий инженер-проектировщик"},{id: 202, fio: "Киселёва Эмилия Константиновна" , birthday: "Октябрь, 29", post: "Руководитель группы разработки программного обеспечения"},{id: 203, fio: "Корнилова Синклитикия Еремеевна" , birthday: "Ноябрь, 27", post: "Менеджер"},{id: 204, fio: "Смирнова Кира Федотовна" , birthday: "Декабрь, 17", post: "Группа тестирования СОРМ"},{id: 205, fio: "Воробьёв Всеволод Александрович" , birthday: "Январь, 24", post: "инженер-электронщик"},{id: 206, fio: "Киселёв Евсей Арсеньевич" , birthday: "Февраль, 5", post: "проектировщик-сметчик"},{id: 207, fio: "Лазарева Варвара Куприяновна" , birthday: "Март, 10", post: "Отдел проектирования"},{id: 208, fio: "Маслова Алина Лукьяновна" , birthday: "Апрель, 25", post: "менеджер по персоналу"},{id: 209, fio: "Антонов Артём Игнатьевич" , birthday: "Май, 1", post: "Аналитик"},{id: 210, fio: "Сазонов Анатолий Максимович" , birthday: "Июнь, 25", post: "Группа тестирования оборудования NGN"},{id: 211, fio: "Исаева Валерия Антониновна" , birthday: "Июль, 4", post: "ассистент менеджера проектов"},{id: 212, fio: "Щукина Ирина Кондратовна" , birthday: "Август, 24", post: "инженер-программист"},{id: 213, fio: "Власова Акулина Христофоровна" , birthday: "Сентябрь, 3", post: "Группа разработки программного обеспечения СОРМ"},{id: 214, fio: "Белозёрова Алина Аркадьевна" , birthday: "Октябрь, 1", post: "Группа разработки программного обеспечения №4"},{id: 215, fio: "Евсеева Глафира Парфеньевна" , birthday: "Ноябрь, 27", post: "Глава представительства"},{id: 216, fio: "Самойлова Лора Донатовна" , birthday: "Декабрь, 26", post: "Не определена"},{id: 217, fio: "Козлов Павел Кимович" , birthday: "Январь, 20", post: "Заместитель генерального директора по экономике и финансам"},{id: 218, fio: "Моисеев Пантелеймон Леонидович" , birthday: "Февраль, 28", post: "Технический директор"},{id: 219, fio: "Полякова Екатерина Антоновна" , birthday: "Март, 19", post: "Группа разработки программного обеспечения"},{id: 220, fio: "Владимирова Агата Лукьяновна" , birthday: "Апрель, 10", post: "Старший научный сотрудник"},{id: 221, fio: "Ефимов Григорий Никитевич" , birthday: "Май, 29", post: "ведущий системный администратор"},{id: 222, fio: "Трофимов Андрей Федотович" , birthday: "Июнь, 17", post: "Группа разработки программного обеспечения систем управления"},{id: 223, fio: "Петров Серапион Христофорович" , birthday: "Июль, 14", post: "Системный администратор"},{id: 224, fio: "Меркушев Матвей Якунович" , birthday: "Август, 26", post: "Группа разработки программного обеспечения"},{id: 225, fio: "Ковалёв Всеволод Антонович" , birthday: "Сентябрь, 10", post: "бухгалтер"},{id: 226, fio: "Шашков Альберт Германнович" , birthday: "Октябрь, 27", post: "Группа разработки программного обеспечения"},{id: 227, fio: "Миронов Аристарх Русланович" , birthday: "Ноябрь, 12", post: "Ведущий научный сотрудник"},{id: 228, fio: "Бобылёва Вероника Митрофановна" , birthday: "Декабрь, 30", post: "Директор по маркетингу и системным исследованиям"},{id: 229, fio: "Архипова Ольга Семёновна" , birthday: "Январь, 29", post: "старший бухгалтер"}];