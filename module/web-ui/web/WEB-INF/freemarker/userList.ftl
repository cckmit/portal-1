<!DOCTYPE html><html><head><title>Система управления персоналом</title><meta charset="UTF-8"/><link rel="stylesheet" href="/pub/theme/style.css"/><script src="/pub/scripts/jquery-2.1.4.min.js"></script><script>/** my lib **/
function replaceClass(elem, from, to){
    elem.removeClass(from).addClass(to);
    return elem;
}

function log(e){
    console.log(e)
}

function STOP(){
    debugger;
}

</script><script>(function($) {
    $.eventReport = function(selector, root) {
        var s = [];
        $(selector || '*', root).addBack().each(function() {
            // the following line is the only change
            var e = $._data(this, 'events');
            if(!e) return;
            s.push(this.tagName);
            if(this.id) s.push('#', this.id);
            if(this.className) s.push('.', this.className.replace(/ +/g, '.'));
            for(var p in e) {
                var r = e[p],
                        h = r.length - r.delegateCount;
                if(h)
                    s.push('\n', h, ' ', p, ' handler', h > 1 ? 's' : '');
                if(r.delegateCount) {
                    for(var q = 0; q < r.length; q++)
                        if(r[q].selector) s.push('\n', p, ' for ', r[q].selector);
                }
            }
            s.push('\n\n');
        });
        return s.join('');
    }
    $.fn.eventReport = function(selector) {
        return $.eventReport(selector, this);
    }
})(jQuery);

</script><script src="/pub/scripts/highchart/highcharts.js"></script><script src="/pub/scripts/highchart/exporting.js"></script><link rel="stylesheet" href="/pub/theme/lib/daterangepicker.css"/><script src="/pub/scripts/lib/moment.min.js"></script><script src="/pub/scripts/lib/jquery.daterangepicker.js"></script><script src="/pub/scripts/lib/perfect-scrollbar.jquery.min.js"></script><link rel="stylesheet" href="/pub/theme/lib/perfect-scrollbar.min.css"/><script src="/pub/scripts/jquery-ui.min.js"></script><link rel="stylesheet" href="/pub/theme/jquery-ui.min.css"/><script src="/pub/scripts/lib/scrollFixBlock.js"></script><div id="messaging" class="popup_backgroundBlock simpleMessage"><div class="wrapper"><div class="popupBlock"><div class="textMessage"></div></div></div></div><script>function MessageGenerator(){
    var runFlag = false;
    var callQueue = [];
    var popupBlock = $('#messaging');
    var textMessage =  popupBlock.find('.textMessage');
    this.show = function(text){
        callQueue.push(text); //ставим в очередь
        if(runFlag){ // если цикл запущен, то выходим
            return;
        }
        runFlag = true;

        !function showMessage(){
            textMessage.text(callQueue[0]);
            popupBlock.css('display', 'block');
            setTimeout(function () {
                popupBlock.addClass('active');
                setTimeout(function () {
                    popupBlock.removeClass('active')
                    setTimeout(function () {
                        popupBlock.css('display', '')

                        callQueue.shift();
                        if (callQueue.length != 0)
                            showMessage();
                        else
                            runFlag = false

                    }, 200);
                }, 2000);
            }, 200);
        }();
    }
    this.lateCall = function(text){
        return this.show.bind(null, text);
    }
}
var messageGenerator = new MessageGenerator();
</script><script src="/ws/resource/employees.js"></script><script>var MyID = 546;
</script></head><body><div class="global"><div class="header_global"><div class="leftBlock"><div class="company"><span onclick="alert($.eventReport())" class="logo"></span><span class="name">PROTEI</span></div><div id="hideLeftBlock" class="hideBar_button"></div></div><div class="rightBlock"><div class="topicTitle employees"><div class="title">Сотрудники</div></div><div class="currentTask"><span class="case">Freq<span class="id">7823289</span></span><div class="timer"><span class="time"><span class="hours">01</span><span class="timeDelimiter">:</span><span class="minutes">12</span></span><span id="stopCurrentTask" class="stop_button"></span></div></div><div id="runTimeFixer" class="completeWorkSession"><span class="icon-time"></span>Таймфиксер</div><div class="rightSide"><div class="icons"><!--#userProfile.userProfilespan.icon-user--><div id="globalNotifications" class="notifications_button active"></div><div id="globalSettings" class="settings_button"></div><div id="globalHelper" class="helper_button"></div><div id="globalExit" class="exit_button"></div></div></div><div class="rightSide searchBlock"><form class="globalSearch"><input id="globalSearch" placeholder="Поиск по объектам" type="text" autocomplete="off" class="searchInput"/><!--#p4.searchInput(contenteditable="true")span 11
span 22
    span 33--><div class="buttonsBlock"><label class="search_button"><input type="submit" value="Поиск"/></label><!--label.icon-resetButtoninput(type="reset" value="Сброс")
--></div></form></div></div></div><div class="body_global"><div class="leftBlock"><div class="noScrolling"><div class="scrolling"><div class="mainButtons"></div><div class="menuList"><div class="menu"><div class="title">Рабочее место</div><div class="list"><div class="item"><span class="icon-plan"></span>Мой план</div><div class="item"><span class="icon-created"></span>Инспектор</div><div class="item"><span class="icon-activity"></span>Моя активность</div><div class="item"><span class="icon-subscription"></span>Мои подписки</div><div class="item"><span class="icon-favorites"></span>Мои избранные</div></div></div><div class="menu"><div class="title">Компания</div><div class="list"><div class="item"><span class="icon-employees"></span>Сотрудники</div><div class="item"><span class="icon-me"></span>Мой паспорт</div></div></div><div class="menu"><div class="title"><!--span.icon-savedQueries-->Объекты системы</div><div style="max-height: 500px" class="list active"><!--.slideWrapper--><div class="item"><span class="icon-component"></span>Компоненты</div><div class="item"><span class="icon-product"></span>Продукты</div><div class="item"><span class="icon-case"></span>Задачи</div><div class="item"><span class="icon-order"></span>Группы задач</div><div class="item"><span class="icon-order"></span>Заказы</div><div class="item"><span class="icon-project"></span>Проекты</div><div class="item"><span class="icon-organization"></span>Организации</div></div></div><div class="menu"><div class="title"><!--span.icon-savedQueries-->Управление и отчёты</div><div class="list"><!--.slideWrapper--><div class="item"><span class="icon-employees"></span>Сотрудники</div><div class="item"><span class="icon-component"></span>Компоненты</div><div class="item"><span class="icon-product"></span>Продукты</div></div></div></div></div></div><script>$('#hideLeftBlock').click(function(){
    $('.header_global >.leftBlock,.body_global >.leftBlock').addClass('inactive');
    $('.body_global >.leftBlock').click(function func(){
        $('.header_global >.leftBlock,.body_global >.leftBlock').removeClass('inactive');
        $('.body_global >.leftBlock').off()
    })
})
</script></div><div class="rightBlock"><div class="UserList list_showing"><div class="controlButtons"><div class="buttons_group"><div id="showLeaders_but" class="button">Руководство компании</div><div id="showMissing_but" class="button">Отсутствующие</div><div id="showBirthdatPeople_but" class="button">День рождения</div><script>var AJAX = new function(){
    var message = {
        url : null,
        method : "POST",
        data : null,
        dataType : "json",
        success: null,
        error: null
    };
    this.getEmployee = function(data, successHandler, errorHandler){
        message.url = '/api/gate/employees/'+ data +'.json';
        message.data = null;
        message.success = successHandler;
        message.error = errorHandler;
        message.method = "GET";

       $.ajax(message);
    };
    this.getMissingEmployees = function(successHandler, errorHandler){
        message.url = '/api/gate/currentMissingEmployeesIDs.json';
        message.data = null;
        message.success = successHandler;
        message.error = errorHandler;
        $.ajax(message);
    }
    this.getFavoriteEmployees = function(successHandler, errorHandler){
        message.url = '/api/getFavoriteEmployees';
        message.data = null;
        message.success = successHandler;
        message.error = errorHandler;
        $.ajax(message);
    }
    this.getEmployeeAbsences = function(data, successHandler, errorHandler){
        message.url = '/api/gate/employees/'+ data.id +'/absences.json?from='+ data.from +'&till='+ data.till;
        message.data = null;
        message.success = successHandler;
        message.error = errorHandler;
        $.ajax(message);
    }
};


function ActionSequence(){ //последовательность действий при ajax запросе
    var completed;
    var actions = [];
    var ajaxData;
    this.than = function(func){
        if(completed) // если ajax success ф-я закончена, то просто вызываем переданное действие
            func(ajaxData);
        else actions.push(func) // иначе помещаем в массив
        return this;
    }
    this.run = function(data){ // доп String textStatus, jqXHR jqXHR
        while(actions.length != 0){
            actions.shift()(data); // выполняем следующее действие и удаляем его
        }
        ajaxData = data;
        completed = true;
    }
}


function Buttons(){
    var users = employeeGenerator.users;
    var showLeadersButton = $('#showLeaders_but');
    var showMissingButton = $('#showMissing_but');
    var showBirthdayPeopleButton = $('#showBirthdatPeople_but');
    var showStartingWithLetterButton;
    var sortingDirectionButton;

    var lastActiveButton;



    function showingButtonOperation(event){
        if(userProducer){
            userProducer.closeUserPassport();
            userProducer.saveUserPassport();
        }

        //закрыть userPassport

        var elem = $(event.currentTarget)
        users.html('');
        if(elem.hasClass('active')){
            elem.removeClass('active')
            employeeGenerator.generateAllUsers();
        }else{
            if(lastActiveButton)
                lastActiveButton.removeClass('active');
            lastActiveButton = elem;

            elem.addClass('active');
            employeeGenerator.resetIndex();
            var html = '';

            switch (elem[0]){
                case showLeadersButton[0]:
                    for(var i=0, count = 0; i<Employees.length; i++){
                        if(Employees[i].leader){
                            html += employeeGenerator.generate(Employees[i]);
                            count++;
                        }
                    }
                    break;
                case showMissingButton[0]:
                    var missing = employeeGenerator.geMissing();
                    for(var i=0, count = 0; i<Employees.length; i++){
                        if(missing.indexOf(Employees[i].id)>=0){
                            html += employeeGenerator.generate(Employees[i]);
                            count++;
                        }
                    }
                    break;
                case showBirthdayPeopleButton[0]:
                    var today = getToday();
                    for(var i=0, count = 0; i<Employees.length; i++){
                        if(Employees[i].birthday == today){
                            html += employeeGenerator.generate(Employees[i]);
                            count++;
                        }
                    }
                    break;
                case showStartingWithLetterButton[0]:
                    for(var i=0, count = 0; i<Employees.length; i++){
                        if(Employees[i].fio.indexOf(elem.text())==0){
                            html += employeeGenerator.generate(Employees[i]);
                            count++;
                        }
                    }
                    break;
            }

            users.appendEmployees(html);
            employeeGenerator.setEmployeeCount(count);
        }
    }


    showLeadersButton.click(function(event){


        showingButtonOperation(event);

        //var elem = $(event.currentTarget)
        //users.html('');
        //if(elem.hasClass('active')){
        //    elem.removeClass('active')
        //    employeeGenerator.generateAllUsers();
        //}else{
        //    elem.addClass('active');
        //    employeeGenerator.resetIndex();
        //    var html = '';
        //    for(var i=0, count = 0; i<Employees.length; i++){
        //        if(Employees[i].leader){
        //            html += employeeGenerator.generate(Employees[i]);
        //            count++;
        //        }
        //    }
        //    users.append(html);
        //    employeeGenerator.setEmployeeCount(count);
        //}
    });

    showMissingButton.click(function(event){

        showingButtonOperation(event);
        //var elem = $(event.currentTarget)
        //users.html('');
        //if(elem.hasClass('active')){
        //    elem.removeClass('active')
        //    employeeGenerator.generateAllUsers();
        //}else{
        //    elem.addClass('active');
        //    employeeGenerator.resetIndex();
        //    var html = '';
        //    var missing = employeeGenerator.geMissing();
        //    for(var i=0, count = 0; i<Employees.length; i++){
        //        if(missing.indexOf(Employees[i].id)>=0){
        //            html += employeeGenerator.generate(Employees[i]);
        //            count++;
        //        }
        //    }
        //    users.append(html);
        //    employeeGenerator.setEmployeeCount(count);
        //}

    });

    showBirthdayPeopleButton.click(function(event){
        showingButtonOperation(event);
        //var elem = $(event.currentTarget)
        //users.html('');
        //if(elem.hasClass('active')){
        //    elem.removeClass('active')
        //    employeeGenerator.generateAllUsers();
        //}else{
        //    elem.addClass('active');
        //    employeeGenerator.resetIndex();
        //    var html = '';
        //    var today = getToday();
        //    for(var i=0, count = 0; i<Employees.length; i++){
        //        if(Employees[i].birthday == today){
        //            html += employeeGenerator.generate(Employees[i]);
        //            count++;
        //        }
        //    }
        //    users.append(html);
        //    employeeGenerator.setEmployeeCount(count);
        //}
    });
    this.showStartingWithLetter = function(event){
        showStartingWithLetterButton = $(event.currentTarget);
        if(showStartingWithLetterButton.hasClass('inactive'))
            return;
        showingButtonOperation(event);
    }

    //$('#sortingDirection_but').click(function(event){
    //    var elem = $(event.currentTarget);
    //    elem.toggleClass('active');
    //    Employees.reverse();
    //    employeeGenerator.generateAllUsers();
    //});

};


var months = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь']


function getToday(){
    var date = new Date();
    return months[date.getMonth()] + ', ' + date.getDate();
}





</script></div><div id="departmentFilter" class="regulator"><input placeholder="Подразделение" class="inputField"><div class="button sticky empty selected"></div></div><div class="buttons_group"><div class="regulator">Сортировать по<div id="sorting_but" class="button selected">Фамилия</div><div id="sortingDirection_but" class="button empty switcher"><span class="icon-down"></span></div></div></div></div><div id="letters" class="letters scrollFixBlock"><div onclick="showStartingWithLetter(event)" class="button">А</div><div onclick="showStartingWithLetter(event)" class="button">Б</div><div onclick="showStartingWithLetter(event)" class="button">В</div><div onclick="showStartingWithLetter(event)" class="button">Г</div><div onclick="showStartingWithLetter(event)" class="button">Д</div><div onclick="showStartingWithLetter(event)" class="button">Е</div><div onclick="showStartingWithLetter(event)" class="button">Ж</div><div onclick="showStartingWithLetter(event)" class="button">З</div><div onclick="showStartingWithLetter(event)" class="button">И</div><div onclick="showStartingWithLetter(event)" class="button">К</div><div onclick="showStartingWithLetter(event)" class="button">Л</div><div onclick="showStartingWithLetter(event)" class="button">М</div><div onclick="showStartingWithLetter(event)" class="button">Н</div><div onclick="showStartingWithLetter(event)" class="button">О</div><div onclick="showStartingWithLetter(event)" class="button">П</div><div onclick="showStartingWithLetter(event)" class="button">Р</div><div onclick="showStartingWithLetter(event)" class="button">С</div><div onclick="showStartingWithLetter(event)" class="button">Т</div><div onclick="showStartingWithLetter(event)" class="button">У</div><div onclick="showStartingWithLetter(event)" class="button">Ф</div><div onclick="showStartingWithLetter(event)" class="button">Х</div><div onclick="showStartingWithLetter(event)" class="button">Ц</div><div onclick="showStartingWithLetter(event)" class="button">Ч</div><div onclick="showStartingWithLetter(event)" class="button">Ш</div><div onclick="showStartingWithLetter(event)" class="button">Щ</div><div onclick="showStartingWithLetter(event)" class="button">Э</div><div onclick="showStartingWithLetter(event)" class="button">Ю</div><div onclick="showStartingWithLetter(event)" class="button">Я</div></div><script>function LettersToolBar(){
    var lettersBlock = $('#letters');
    var letters = {};

    this.update = function(){
        for(var letter in letters){
            if(CaughtLetters.indexOf(letter)>=0)
                letters[letter].removeClass('inactive')
            else
                letters[letter].addClass('inactive')
        }
    };

    (function(){
        var lArray = ["А","Б","В","Г","Д","Е","Ж","З","И","К","Л","М","Н","О","П","Р","С","Т","У","Ф","Х","Ц","Ч","Ш","Щ","Э","Ю","Я"];
        var letterButtons = lettersBlock.find('.button');

        for(var i=0; i<lArray.length; i++)
            letters[lArray[i]] = letterButtons.eq(i);
    }());
    scrollFixBlock(lettersBlock, 114, 14);
}

var lettersToolBar = new LettersToolBar();




function showStartingWithLetter(event){
    buttons.showStartingWithLetter(event);
}

</script><div class="users"></div><script>function EmployeeGenerator(){
    this.users = $('.users');
    this.users.appendEmployees = function(html){
        if(html)
            this.append(html);
        else
            this.append('<div class="EmptyBlock"><div class="wrapper">Не найдено</div></div>');
    }
    var index = 1;
    var favorites = [];
    var missing = [];
    var currentEmployeeCount = Employees.length;

    this.generate = function(employee){
        var employeeHtml = '<div id="' + employee.id + '" class="user' + (employee.leader ? ' leader' : '') + (missing.indexOf(employee.id)>-1 ? ' inactive' : '') + '" onclick="activateUserPassport(event)" data-id="' + index + '">';
        employeeHtml += '<div class="wrapper">';
        employeeHtml += '<div class="ava user' + employee.id + '"></div>';
        employeeHtml += '<div class="wrapper">';
        employeeHtml += '<div class="post">' + employee.post + '</div>';
        employeeHtml += '<div class="name">' + employee.fio + '</div>';
        employeeHtml += '<div class="birthday">' + employee.birthday + (employee.birthday == getToday()?'<span class="today">Сегодня день рождения!</span>':'') + '</div>';
        employeeHtml += '</div>';
        employeeHtml += '<div class="menu_button" onclick="showUserMenu(event)"></div>';
        employeeHtml += '<div class="favorite_button' + (favorites.indexOf(employee.id)>-1 ? ' active' : '') + '" onclick="switchFavorite(event)"></div>';
        employeeHtml += '</div>';
        employeeHtml += '</div>';
        index++;
        return employeeHtml
    }

    this.resetIndex = function(){
        index = 1;
    }
    this.setFavorites = function(mass){
        favorites = mass
    }
    this.setMissing = function(mass){
        missing = mass;
    }
    this.showMissingOnCurrentEmployess = function(){
        for(var i=0;i<missing.length;i++){
            this.users.children('[id='+ missing[i] +']').addClass('inactive');
        }
    }
    this.addMissing = function(id){
        missing.push(id)
    }

    this.geMissing = function(){
        return missing;
    }

    this.getEmployeeCount = function(){
        return currentEmployeeCount;
    }
    this.setEmployeeCount = function(number){
        currentEmployeeCount = number;
    }

    this.generateAllUsers = function(){
        index = 1;
        currentEmployeeCount = Employees.length;
        this.users.html('');
        var html = '';
        for(var i=0; i<Employees.length; i++){
            html += this.generate(Employees[i]);
        }
        this.users.appendEmployees(html);
    }


    function addFavorite(id){
        favorites.push(id)
    }
    function deleteFavorite(id) {
        var pos = favorites.indexOf(id);
        if(pos>=0)
            delete favorites[pos];
    }
    this.switchFavorite = function(elem){
        var userId = elem.parent().parent().attr('id');
        if(elem.hasClass('active')){
            elem.removeClass('active');
            deleteFavorite(+userId)
            messageGenerator.show('Сотрудник удалён из числа избранных');
        }else{
            elem.addClass('active');
            addFavorite(+userId)
            messageGenerator.show('Сотрудник добавлен в число избранных');
        }
    }

}




function Sorting(){
    var reverseSoring = false;
    function stringSorting(a, b){
        var c,d;
        for(var i=0; i <Math.min(a.length, b.length); i++){
            if(a[i] == "ё") c = "е"
            else c = a[i]
            if(b[i] == "ё") d = "е"
            else d = b[i]

            if(c > d) return 1;
            else if(c < d) return -1;
        }
        return 0
    }

    function birthdaySorting(a, b){
        a = a.birthday.split(', ')
        b = b.birthday.split(', ')

        var c = months.indexOf(a[0])
        var d = months.indexOf(b[0])

        if(c==d){
            return a[1] - b[1];
        }
        else return c-d;
    }

    function idSorting(a, b){
        return a.id -  b.id;
    }
    this.by = function(sortKey){
        switch(sortKey){
            case "Фамилия":
                Employees.sort(function(a,b){
                    return stringSorting(a.fio, b.fio)
                });
                break;
            case "Должность":
                Employees.sort(function(a,b){
                    return stringSorting(a.post, b.post)
                });
                break;
            case "Дата рождения":
                Employees.sort(birthdaySorting);
                break;
            case "Id":
                Employees.sort(idSorting);
                break;
            default:
                return;
        }
        if(reverseSoring)
            Employees.reverse();
        employeeGenerator.generateAllUsers();
    }

    $('#sortingDirection_but').click(function(event){
        $(event.currentTarget).toggleClass('active');
        Employees.reverse();
        reverseSoring = !reverseSoring;
        employeeGenerator.generateAllUsers();
    });
}



var employeeGenerator = new EmployeeGenerator();
var sorting = new Sorting();
employeeGenerator.setMissing(window['missingEmployeesIDs']);
//employeeGenerator.setFavorites(favorites);

//                            AJAX.getMissingEmployees(
//                                function(data){
//                                    employeeGenerator.setMissing(data);
//                                },
//                                messageGenerator.lateCall("Ошибка при получении отсутствующих сотрудников")
//                            );
//                            AJAX.getFavoriteEmployees(
//                                function(data){
//                                    employeeGenerator.setFavorites(data);
//                                },
//                                messageGenerator.lateCall("Ошибка при получении избранных сотрудников")
//                            );

var buttons;
$(document).ready(function () {
    employeeGenerator.generateAllUsers();
    buttons = new Buttons();
    lettersToolBar.update();


    //setInterval(function(){
    //    AJAX.getMissingEmployees(
    //        function(data){
    //            employeeGenerator.setMissing(data);
    //            employeeGenerator.showMissingOnCurrentEmployess();
    //        },
    //        messageGenerator.lateCall("Ошибка при получении отсутствующих сотрудников")
    //    );
    //}, 15 * 60 * 1000);
});






function switchFavorite(event){
    event.stopPropagation();
    employeeGenerator.switchFavorite($(event.currentTarget));
}
//                            function sortBy(sortKey){
//                                employeeGenerator.sortBy(sortKey);
//                            }

</script><div class="UserPassport"><div class="head"><div class="tabs"><div class="tab active">Профиль</div><div class="tab">План</div><div class="tab">Инспектор</div><div class="tab">Активность</div><div class="tab">Подписки</div><div class="tab">Права доступа</div><div class="tab">Отчёт</div></div></div><div class="mainInfo"><div class="sectionTitle">Информация</div></div><div class="absences"><div class="sectionTitle">Отсутствия</div><div class="controlButtons"><!--.button.empty.grayCspan.icon-help
--><!--.buttons_group--><div id="addAbsenceButton" class="button greenC"><span class="icon-add"></span>Добавить отсутствие</div><div id="editAbsenceButton" class="button blueC">Просмотрщик отсутствий</div></div><div id="absenceDiagram" class="timingDiagram"><div class="Loader"></div><div class="left_button"></div><table class="diagram"><thead><tr class="headers"></tr></thead><tbody><tr class="absenceLines"></tr></tbody></table><div class="right_button"></div></div></div></div><script>Date.prototype.getWeekNumber = function () {
    var d = new Date(this.getTime());
    d.setHours(0, 0, 0);
    d.setDate(d.getDate() + 4 - (d.getDay() || 7));
    var yearStart = new Date(d.getFullYear(), 0, 1);
    return Math.ceil(( ( (d - yearStart) / 86400000) + 1) / 7);
}
Date.prototype.getCorrectedDay = function () {
    var d = this.getDay() - 1;
    return d==-1?6:d;
}
Date.prototype.getUTCTime = function(){
    return this.getTime() - this.getTimezoneOffset() * 60000;
}
String.escapeSymbols = {
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': '&quot;',
    "'": '&#39;',
    "/": '&#x2F;'
};
String.prototype.escapeHTML = function(){
    return this.replace(/[&<>"'\/]/g, function (s) {
      return String.escapeSymbols[s];
    });
}

function DiagramProducer(){

    var today;
    var absenceDiagram = $('#absenceDiagram');
    var diagram = absenceDiagram.children('.diagram');
    var diagramThead = diagram.children('thead');//
    var diagramTbody = diagram.children('tbody');//
    var days = ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'];
    var months = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'];
    var absenceReasons = ["Командировка", "Отпуск", "Болезнь", "Личные дела", "Местная командировка", "Учеба", "Больничный лист", "Гостевой пропуск", "Ночные работы", "Отпуск за свой счёт"]
    var changeFlag = true;
    var todayExistsFlag = false;
    var newAbsences;
    var todayCell;
    var daysBefore; //  кол-во дней, которые были добавлены до today в диаграмму при первом построении

    var diagramFrom; // дата начала диагрыммы
    var diagramTill; // дата конца диагрыммы
    var commonDayCount; // кол-во дней в диаграмме (diagramTill - diagramFrom)

    var currentStartPoint; // дата С которой будут добавлены новые отсутствия
    var currentEndPoint; // // дата ДО которой будут добавлены новые отсутствия

    var schedules = {}; //сохранённые расписания

    var absenceTooltip = $('#absenceTooltip');
    var absenceInfo = $('#absenceInfo');
    var absencePopupBuilder;

    var diagramLoader = absenceDiagram.children('.Loader'); // лоадер

    function AbsencePopupBuilder(){
        var lastScheduleToday;
        var lastAbsenceLine;

        var mainInfo = absenceInfo.children('.info');
        var scheduleInfo = absenceInfo.children('.schedule');
        var commentInfo = absenceInfo.children('.comment');
        var absenceDates = mainInfo.children('.date');
        var dates = {
            from:{
                date: absenceDates.eq(0).children('.dateNumber'),
                month: absenceDates.eq(0).find('.month'),
                time: absenceDates.eq(0).find('.time')
            },
            till:{
                date: absenceDates.eq(1).children('.dateNumber'),
                month: absenceDates.eq(1).find('.month'),
                time: absenceDates.eq(1).find('.time')
            }
        }
        var absenceReason = mainInfo.children('.absenceReason');
        var commentText = commentInfo.children('.text');
        var headerWeekTd = scheduleInfo.find('.headers').children('td:first-child'); // пустой td в headers, который нужно либо показывать , либо скрывать в зависимости от кол-ва недель
        var weekSelector = scheduleInfo.find('.week');
        var weeks = [weekSelector.eq(0).children('td'), weekSelector.eq(1).children('td')];


        function twoValuedNumber(number){
            return number<10?'0'+number:number;
        }

        function setAbsenceDate(date, dateObject) {
            var hoursAndMinutes = twoValuedNumber(dateObject.getUTCHours()) + ':' + twoValuedNumber(dateObject.getUTCMinutes());
            var timeLimit = date=='from'?'00:00':'23:59';
            dates[date].time.text(hoursAndMinutes == timeLimit ? '' : hoursAndMinutes);
            dates[date].date.text(twoValuedNumber(dateObject.getUTCDate()));
            dates[date].month.text(months[dateObject.getUTCMonth()]);
        }
        function buildScheduleTable(week, schedule){
            for(var i=1; i<8; i++){
                var day = week.eq(i).attr('data-day');
                week.eq(i).text( schedule.hasOwnProperty(day) ? schedule[day].replace('-',' — ') : '' );
            }
        }
        function showScheduleBlock(schedule, absenceLine, leftOffset){
            var parentCellDate = absenceLine.parent().attr('data-date').split('.');
            var absenceDateFrom = new Date(+parentCellDate[2], +parentCellDate[1], +parentCellDate[0]);

            var width = parseInt(absenceLine.css('width'));
            var marginLeft = parseInt(absenceLine.css('margin-left'));

            if(schedule.length == 1){ // одна неделя
                headerWeekTd.hide();

                weeks[0].eq(0).hide(); //скрываем 2 колонки с чётностью недель
                weeks[1].eq(0).hide();

                buildScheduleTable(weeks[0], schedule);
            }else{ // две недели
                headerWeekTd.show();

                weeks[0].eq(0).show();
                weeks[1].eq(0).show();

                buildScheduleTable(weeks[0], schedule['чёт']);
                buildScheduleTable(weeks[1], schedule['нечёт']);
            }

            if(lastScheduleToday)
                lastScheduleToday.removeClass('today');

            var dayOffset = Math.floor((leftOffset + marginLeft)/192);
            var todayDay = absenceDateFrom.getCorrectedDay() + dayOffset;

            if(schedule.length==1 || absenceDateFrom.getWeekNumber() % 2 == 0){ //чёт, либо одна неделя
                lastScheduleToday = weeks[0].eq(todayDay);
            }else{
                lastScheduleToday = weeks[1].eq(todayDay); // нечёт
            }

            lastScheduleToday.addClass('today');
        }



        this.showScheduleInfo = function(id, timeFrom){
            //var schedule = diagramProducer.getScheduleByTimeFrom(timeFrom);

            diagramProducer.downloadAbsences(from, till, function(data){
                if(data["absences"]){

                }else{
                    // пусто
                }
            })



            if(schedule.length == 1){ // одна неделя
                headerWeekTd.hide();

                weeks[0].eq(0).hide(); //скрываем 2 колонки с чётностью недель
                weeks[1].eq(0).hide();

                buildScheduleTable(weeks[0], schedule);
            }else{ // две недели
                headerWeekTd.show();

                weeks[0].eq(0).show();
                weeks[1].eq(0).show();

                buildScheduleTable(weeks[0], schedule['чёт']);
                buildScheduleTable(weeks[1], schedule['нечёт']);
            }

            mainInfo.hide();
            commentInfo.hide();

            absenceInfo.addClass('whiteScheduleBlock');
            absenceInfo.show();
            event.stopPropagation();

            setPopupPosition(event, absenceInfo, 58, 28); // 58 = toLeft/toRight offset + row.width/2
            bindDocumentKillingClick(absenceInfo, absenceInfo, function(){
                absenceInfo.hide();
                mainInfo.show();
                absenceInfo.removeClass('whiteScheduleBlock');
            })
        }

        this.showAbsenceInfo = function(event, timeFrom, timeTill, absenceId, comment){
            var absenceLine = $(event.currentTarget);
            if(lastAbsenceLine)
                lastAbsenceLine.removeClass('active');

            var dateFrom = new Date(timeFrom * 1000);
            var dateTill = new Date(timeTill * 1000);

            setAbsenceDate('from', dateFrom);
            setAbsenceDate('till', dateTill);

            absenceReason[0].className = 'absenceReason reason' + absenceId;
            absenceReason.text(absenceReasons[absenceId - 1]);

            if(absenceId==6){ //если учёба , значит расписание
                var schedule = diagramProducer.getScheduleByTimeFrom(timeFrom);

                var leftOffset = event.offsetX || event.clientX - absenceLine.offset().left;
                leftOffset = leftOffset<0?0:leftOffset

                showScheduleBlock(schedule, absenceLine, leftOffset)
                scheduleInfo.show();
            }else
                scheduleInfo.hide();

            if(comment){
                commentInfo.show();
                commentText.text(comment);
            }else
                commentInfo.hide();

            var pointerWidth = 58; // toLeft/toRight offset + row.width/2
            setPopupPosition(event, absenceInfo, pointerWidth, 28);

            absenceLine.addClass('active');
            lastAbsenceLine = absenceLine;

            event.stopPropagation();
            bindDocumentKillingClick(absenceInfo, absenceLine, function(){
                absenceInfo.hide();
                absenceLine.removeClass('active');
            })

            absenceInfo.show();
            absenceTooltip.hide();

        }
    }

    function Schedule(schedule, absenceFrom, absenceTill, lineGenerator){

        var diagramFrom = currentStartPoint; // дата начала диагрыммы
        var diagramTill = currentEndPoint; // дата конца диагрыммы
        var diagramDayCount = (+currentEndPoint - +currentStartPoint) / 1000 / 60 / 60 / 24; //количество дней в строящемся участке

        var absenceStartPos; // позиция, с которой начнётся итерация в lines
        var diagramStartPos; // позиция, с которой начнётся итерация диаграммы
        var diagramEndPos; // позиция, на которой закончится итерация диаграммы


        var lines = [];
        var count = 0; //количество дней в расписании (7 или 14)
        var twoWeeks = []; //заполняется только если в расписании задействованы 2 недели

        this.buildOneScheduleCycle = function(absences){

            var week
            var weekParity

            if (absences.hasOwnProperty("чёт")) {
                weekParity = +(absenceFrom.getWeekNumber() % 2 == 0); // 0 - нечёт, 1 - чёт
                twoWeeks = [absences['нечёт'], absences['чёт']];
                week = twoWeeks[weekParity];
                count = 14;
            } else {
                week = absences;
                count = 7;
            }


            var todayDay = 1; //начинаем с понедельника

            for (var i = 0; i < count; i++) {

                if (todayDay >= 7) {
                    todayDay = 0; // вс
                    if (twoWeeks) {
                        weekParity = +!weekParity; // меняем чётность на противопололожную
                        week = twoWeeks[weekParity];
                    }
                }

                if (week.hasOwnProperty(days[todayDay])) {
                    var dayName = days[todayDay]
                    var times = week[dayName].split('-')
                    var from = times[0].split(':'); //hours and minutes
                    var till = times[1];

                    var marginLeft = ((from[0] * 4) + (from[1] / 4)) * 2
                    var width = 0 - marginLeft;

                    var thisDayIndex = lines.push(marginLeft) - 1;

                    while (true) {
                        if (till == '23:59') {
                            width += 192;
                            if ((i + 1 < count) && week.hasOwnProperty(days[todayDay + 1]) && week[days[todayDay + 1]].indexOf('00:00') == 0) { // если есть следующий день и он начинается с 00:00, то продолжаем линию
                                todayDay++
                                i++;
                                till = week[days[todayDay]].substr(6); // till следующего дня
                                lines.push(true); // занят полностью
                            } else
                                break;
                        } else {
                            till = till.split(':');
                            width += ((till[0] * 4) + (till[1] / 4)) * 2;
                            break;
                        }
                    }
                    lines[thisDayIndex] += ';' + width;

                } else
                    lines.push(false);
                todayDay++;
            }
        }



        this.getStartPosition = function(diff){
            if (diff <= 0) { // если расписание началось раньше начала диаграммы
                absenceStartPos = diagramFrom.getCorrectedDay();
                diagramStartPos = 0;

                if (twoWeeks) {
                    if (diagramFrom.getWeekNumber() % 2 != absenceFrom.getWeekNumber() % 2) { //если чётности начальной недели диаграммы и начальной недели расписания отличаются
                        absenceStartPos+=7;
                    }
                }

            } else { // если расписание началось позже начала диаграммы
                absenceStartPos = (diagramFrom.getCorrectedDay()+diff) % 7;
                diagramStartPos = diff;
            }
        }

        this.getEndPosition = function(diff){
            if (diff > 0) { // конец расписания выходит за рамки диаграммы
                diagramEndPos = diagramDayCount
            } else { // расписание закончится раньше диаграммы или в один день
                diagramEndPos = diagramDayCount - Math.abs(Math.ceil(diff));
            }
        }


        this.duplicateLines = function(){
            //тирражируем неделю расписания или 2 недели (в зависимости от наличия чётности) на всё кол-во дней

            var dayCount = count - absenceStartPos + diagramStartPos; // кол-во уже занятных дней

            while (dayCount < diagramEndPos) {
                for (var i = 0; i < count; i++)
                    lines.push(lines[i]);
                dayCount += count;
            }
        }

        this.boundAbove = function(diff, aboveTop){
            var dayNumber = absenceStartPos % 7;

            if(dayNumber != 0 && lines[absenceStartPos]){

                var occupiedDays = 0;
                while (dayNumber>=0) {
                    if (lines[dayNumber] === true) { // если занят целый день, который занят под одну большую линию
                        occupiedDays++;
                        dayNumber--;

                    } else if (lines[dayNumber]) { // если неполный день
                        var firstAbsenceDay = lines[dayNumber];
                        firstAbsenceDay = firstAbsenceDay.split(';');
                        var marginLeft = +firstAbsenceDay[0];
                        var width = +firstAbsenceDay[1];

                        if(diff>0 && occupiedDays>0){ //начало расписания приходится на true день
                            width = width - (192 - marginLeft)
                            lines[absenceStartPos] = (occupiedDays-1) * -192 + ';' + width;

                        }else if(aboveTop && diff<=0){ //начала диаграммы перекрывает одно из отсутствий
                            if (width + marginLeft >= 192) {
                                lines[absenceStartPos] = occupiedDays * -192 + marginLeft + ';' + width;
                            }
                        }
                        break;

                    }else
                        break;
                }

            }
        }



        this.boundBottom = function(diff, bellowBottom){

            if(!bellowBottom && diff> 0)
                var absenceTillDay = diagramTill.getCorrectedDay();
            else
                absenceTillDay = absenceTill.getCorrectedDay();

            // ограничение снизу, имеет смысл если последнее отсутствие выходит за рамки конечного диапазона расписания, такое расписание надо обрезать
            //if(Math.abs(Math.ceil(diff))<=7 && absenceTillDay != 4){ // 4 - это пятница, т.к на сб и вс нельзя сделать отсутствие
            if(Math.ceil(diff)<=7 && absenceTillDay != 4){ // без abs

                absenceTillDay = lines.length + absenceTillDay;
                if(twoWeeks && absenceFrom.getWeekNumber()%2 == 0) {
                    absenceTillDay-=14;
                }else {
                    absenceTillDay-=7;
                }
                var occupiedDays = 0;
                while (absenceTillDay>0) {
                    if (lines[absenceTillDay] === true) { // если занят целый день, который занят под одну большую линию
                        occupiedDays++;
                        absenceTillDay--;
                    } else if (lines[absenceTillDay]) { // если неполный день
                        var lastAbsenceDay = lines[absenceTillDay];
                        lastAbsenceDay = lastAbsenceDay.split(';');
                        var marginLeft = +lastAbsenceDay[0];
                        var width = +lastAbsenceDay[1];


                        if(!bellowBottom && diff > 0 && marginLeft + width > 192 * (occupiedDays+1)){
                            lines[absenceTillDay] = false;
                        }else if (width + marginLeft >= 192) {
                            var conjecturalWidth = (192 - marginLeft) + 192*occupiedDays
                            lines[absenceTillDay] = marginLeft + ';' + (conjecturalWidth>width?width:conjecturalWidth);
                        }
                        break;

                    }else
                        break;
                }

            }
        }


        this.getAbsenceLines = function(aboveTop, bellowBottom){

            this.buildOneScheduleCycle(schedule);

            var diffFrom = (+absenceFrom - +diagramFrom) / 1000 / 60 / 60 / 24; // отношение между первым днём расписания и первым днём диаграммы, в днях
            this.getStartPosition(diffFrom);

            var diffTill = (+absenceTill - +diagramTill) / 1000 / 60 / 60 / 24; // отношение между последним днём расписания и последним днём диаграммы, в днях
            this.getEndPosition(diffTill);

            this.duplicateLines();

            this.boundAbove(diffFrom, aboveTop);
            this.boundBottom(diffTill, bellowBottom);

            var absences = {}

            var diagramStartCell = new Date(+diagramFrom)
            var prevI = 0
            for (var i = diagramStartPos; i < diagramEndPos; i++) {
                if (lines[absenceStartPos] && lines[absenceStartPos] !== true) {
                    diagramStartCell.setDate(diagramStartCell.getDate() + i - prevI);

                    var line = lines[absenceStartPos].split(';')
                    var marginLeft =  line[0];
                    var width =  line[1];

                    absences[diagramStartCell.getDate() + '.' + diagramStartCell.getMonth() + '.' + (diagramStartCell.getFullYear() - 2000)] = lineGenerator(width, marginLeft)

                    prevI = i;
                }
                absenceStartPos++
            }

            return absences;
        }
    }



    this.buildDays = function(dayCount){

        var dDate = currentStartPoint.getDate(); // date Date
        var dDay = currentStartPoint.getDay(); // date Day
        var dMonth = currentStartPoint.getMonth(); //date Month
        var dYear = currentStartPoint.getFullYear() - 2000; //date Year

        var daysInMonth = new Date(currentStartPoint.getFullYear(), currentStartPoint.getMonth() + 1, 0).getDate();
        var diff = daysInMonth - dDate + 1;

        var htmlHeaders = '';
        var htmlLines = '';

        for (var i = 0; i < dayCount; i++) {
            htmlHeaders += '<td';

            if (diff-- == 0) {
                dDate = 1
                diff = new Date(currentStartPoint.getFullYear(), currentStartPoint.getMonth() + 2, 0).getDate() - 1;
                dMonth++
            }

            //*******************  установить today и/или newMonth
            if(dDate == 1 || (!todayExistsFlag && dDate==today.getDate())){
                htmlHeaders += ' class="';
                if(!todayExistsFlag && dDate==today.getDate()){
                    htmlHeaders +='today';
                    todayExistsFlag = true; // устанавливаем только один раз
                }

                if(dDate==1)
                    htmlHeaders += ' newMonth">'+ months[dMonth % 12] +' ';
                else
                    htmlHeaders += '">'
            }else{
                htmlHeaders += '>';
            }
            //*******************


            htmlHeaders += dDate +', '+ days[dDay++ % 7] +'</td>';
            var dataDate = dDate +'.'+ dMonth +'.'+ dYear
            htmlLines += '<td data-date="'+ dataDate +'">';
            if(newAbsences.hasOwnProperty(dataDate))
                htmlLines += newAbsences[dataDate];
            htmlLines += '</td>';
            dDate++;
        }

        return [htmlHeaders, htmlLines];
    }




    this.buildLine = function(timeFrom, timeTill, comment, absenceId, width, marginLeft){
        return '<div onclick="showAbsenceInfo(event,'+ timeFrom +','+ timeTill +','+ absenceId +','+ (comment?'\''+ comment.escapeHTML() +'\'':'null') +')" style="width:'+ width +'px; margin-left:'+ marginLeft +'px" data-title="'+ absenceReasons[absenceId - 1] +'" class="line reason'+ absenceId +'"></div>';
    }

    this.downloadAbsences = function(timeFrom, timeTill, successAction){
        AJAX.getEmployeeAbsences(
            {
                id: userProducer.getOpenedUserId(),
                from: +timeFrom,
                till: +timeTill
                //from: +timeFrom  + 10800000,
                //till: +timeTill  + 10800000
            },
            successAction,
            messageGenerator.lateCall("Ошибка! Не удалось подгрузить отсутствия")
        );
    }

    this.buildLines = function(absences, aboveTop,  bellowBottom){
        //var openedUser = window["Employee" + userProducer.getOpenedUserId()];
        //if(!openedUser["absenceFlag"]){
        //    absences = openedUser["absences"];
        //    openedUser.absenceFlag = true;
        //}




        var absences2 = jQuery.extend(true, {}, absences);
        for(var a in absences2){
            absences2[a].dateFrom = new Date(absences2[a].dateFrom);
            absences2[a].dateTill = new Date(absences2[a].dateTill);
        }
        log(absences2);
        log("======================================");






        function addAbsence(cellDate, line){
            if(newAbsences.hasOwnProperty(cellDate))
                newAbsences[cellDate] += line
            else
                newAbsences[cellDate] = line;
        }

        for(var i=0; i<absences.length; i++){
            var dateFrom = (absences[i]['dateFrom'] + 10800000)/1000;
            var dateTill = (absences[i]['dateTill'] + 10800000)/1000;

            if(dateTill*1000 < currentStartPoint && dateTill*1000 < currentEndPoint)
                continue; // отсутствие полностью выходит за рамки диаграммы

            var cellDate;

            if(absences[i]['schedule']){

                if(!schedules.hasOwnProperty(dateFrom))
                    schedules[dateFrom] = absences[i]['schedule']; // сохраняем расписание, чтобы можно было потом обратиться к нему при вызове showScheduleData

                var schedule = new Schedule(
                    absences[i]['schedule'],
                    new Date((dateFrom - 10800) * 1000),
                    new Date((dateTill - 10800) * 1000),
                    this.buildLine.bind(null,
                            dateFrom,
                            dateTill,
                            absences[i]['comment'],
                            absences[i].reason
                    )
                );

                var scheduleAbsenceLines = schedule.getAbsenceLines(aboveTop, bellowBottom);
                for(cellDate in scheduleAbsenceLines){
                    addAbsence(cellDate, scheduleAbsenceLines[cellDate]);
                }


            }else {
                var from = new Date(dateFrom * 1000);

                var width = (dateTill - dateFrom) / 60 / 60 * 4 * 2; // в пикселах
                //STOP();
                var marginLeft = ((from.getUTCHours() * 4) + (from.getUTCMinutes() / 15)) * 2; // в пикселах
                //if(marginLeft != 0)
                //    marginLeft = ((from.getHours() * 4) + (from.getMinutes() / 4)) * 2; // в пикселах

                var diffTill = dateTill - currentEndPoint.getUTCTime()/1000;
                if(!bellowBottom && diffTill>0){
                    continue; // удаляем нижнее отсутствие
                }

                var diffFrom = dateFrom - currentStartPoint.getUTCTime()/1000;
                if(aboveTop && diffFrom<0){ //если отсутствие началось раньше диаграммы. добавляем верхнее отсутствие
                    diffFrom = Math.abs(diffFrom) / 60 / 60 * 4 * 2; //в пикселах
                    cellDate = currentStartPoint.getDate() + '.' + currentStartPoint.getMonth() + '.' + (currentStartPoint.getFullYear() - 2000);
                    marginLeft = -diffFrom;
                }else
                    cellDate = from.getUTCDate() + '.' + from.getUTCMonth() + '.' + (from.getUTCFullYear() - 2000);


                var line = this.buildLine(
                        dateFrom,
                        dateTill,
                        absences[i]['comment'],
                        absences[i].reason,
                        width, //width
                        marginLeft //marginLeft
                );

                addAbsence(cellDate, line)

            }
        }

    }

    this.build = function(dayCount){
        today = new Date();
        daysBefore = 14 + today.getDay() - 1; // + 14 дней + дней до понедельника
        var emoloyeeAbsences = window["Employee" + userProducer.getOpenedUserId()].absences;

        if(changeFlag){ //если при прошлом открытии паспорта ячейки добавлялись, то очищаем всё и строем всё заново
            commonDayCount = 0;

            diagramFrom = new Date(today.getFullYear(), today.getMonth(), today.getDate() - daysBefore);
            diagramTill = new Date(+diagramFrom); // copy

            diagramThead.children('.headers').remove();
            diagramTbody.children('.absenceLines').remove();

            diagramThead.append('<tr class="headers"></tr>');
            diagramTbody.append('<tr class="absenceLines"></tr>');

            todayExistsFlag = false;
            this.completeCellsAfter(dayCount, true, true, emoloyeeAbsences)
            todayCell = diagramThead.find('.today');

            changeFlag = false;

        }else{ // иначе стрираем только линии отсутсвия
            diagramTbody.children('.absenceLines').find('.line').remove();
            newAbsences = {};

            if(emoloyeeAbsences) {
                this.buildLines(emoloyeeAbsences, true, true);
                var absenceLines = diagramTbody.children('.absenceLines')
                for (var absence in newAbsences) {
                    absenceLines.find('[data-date="' + absence + '"]').append(newAbsences[absence]);
                }
            }

        }

        var timeOffset = ((today.getHours()*4) + Math.floor(today.getMinutes()/15)) *2;
        todayCell.css('margin-left', timeOffset);

    }

    this.diagramToCenter = function(){ //центрирует диаграмму так, чтобы текущий день был посередине
        diagram.css('margin-left', daysBefore * -192 + absenceDiagram.width()/2 - 96); // 192 = ширина ячейки, 96 = 192/2
    }

    this.completeCellsAfter = function(dayCount, aboveTop, bellowBottom, absences){
        commonDayCount += dayCount;
        newAbsences = {};

        currentStartPoint = new Date(+diagramTill);
        diagramTill.setDate(diagramTill.getDate() + dayCount);
        currentEndPoint = diagramTill;


        log("currentStartPoint"+ currentStartPoint);
        log("currentEndPoint"+ currentEndPoint);

        var seq = new ActionSequence();
        seq.than(
            function(data){
                var absences = data.absences;

                if(absences != null)
                    this.buildLines(absences, aboveTop, bellowBottom);

                var html = this.buildDays(dayCount);
                diagramThead.children('.headers').append(html[0]);
                diagramTbody.children('.absenceLines').append(html[1]);

                changeFlag = true;
            }.bind(this)
        );

        if(absences !== undefined)
            seq.run({'absences': absences});
        else
            this.downloadAbsences(currentStartPoint, currentEndPoint, seq.run);
        return seq;
    }


    this.completeCellsBefore = function(dayCount, aboveTop, bellowBottom){
        commonDayCount += dayCount;
        newAbsences = {};

        currentEndPoint = new Date(+diagramFrom);
        diagramFrom.setDate(diagramFrom.getDate() - dayCount);
        currentStartPoint = diagramFrom;


        log("currentStartPoint"+ currentStartPoint);
        log("currentEndPoint"+ currentEndPoint);

        var seq = new ActionSequence();
        seq.than(
            function(data){
                var absences = data.absences;

                if(absences != null)
                    this.buildLines(absences, aboveTop, bellowBottom);

                var html = this.buildDays(dayCount);
                diagramThead.children('.headers').prepend(html[0]);
                diagramTbody.children('.absenceLines').prepend(html[1]);

                changeFlag = true;
            }.bind(this)
        );

        this.downloadAbsences(currentStartPoint, currentEndPoint, seq.run);
        return seq;
    }


    this.getScheduleByTimeFrom = function(timeFrom){
        return schedules[timeFrom];
    }


    this.showAbsenceInfoPopup = function(event, timeFrom, timeTill, absenceId, comment){
        if (!absencePopupBuilder)
            absencePopupBuilder = new AbsencePopupBuilder();
        absencePopupBuilder.showAbsenceInfo(event, timeFrom, timeTill, absenceId, comment);
    }

    function trackAbsenceLineHover(){
       absenceDiagram.mouseover(function(event){
            var elem = $(event.target);

            if(elem.hasClass('line') && !elem.hasClass('active')) {
                absenceTooltip.text(elem.attr('data-title'))
                var elemOffset = elem.offset();
                var tooltipHalfWidth = absenceTooltip.outerWidth() / 2;
                absenceTooltip.css('top', elemOffset.top + 28).css('left', event.clientX - tooltipHalfWidth);
                absenceTooltip.show();


                var moveTooltip
                elem.mousemove(moveTooltip = function(event){
                    absenceTooltip.css('left', event.clientX - tooltipHalfWidth);
                });

                elem.mouseleave(function func() {
                    absenceTooltip.hide();
                    elem.off('mouseleave', func).off('mousemove', moveTooltip);
                });
            }

        });
    }

    function activateDiagramNavigation() {
        function navigateDiagramGantt(event) {
            var offsetNumber = 20; // начальное смещение
            var offset = parseInt(diagram.css('margin-left'));
            var breakTimeoutFlag;
            var timeoutId;

            // скрываем попапы, если они открыты
            absenceTooltip.hide();
            absenceInfo.hide();

            $(document.body).on('mouseup', function func(event) {
                breakTimeoutFlag = true;
                $(event.currentTarget).off('mouseup', func);
            });
            changeOffset();
            timeoutId = setInterval(changeOffset, 100);
            function changeOffset() {
                if (tryCompleteCell(offset)){
                    clearInterval(timeoutId);
                    return;
                }
                diagram.css('margin-left', offset);

                // увеличиваем на четверть текущего смещения
                offsetNumber += offsetNumber > 100 ? 0 : (offsetNumber / 4); // где 100 максимальное смещение пикселей каждые 100 мс

                offset += offsetNumber * event.data.direction;
                if (breakTimeoutFlag) clearInterval(timeoutId);
            }
        }

        function tryCompleteCell(offset) {
            if (offset >= 0) {
                diagramLoader.addClass('active');
                diagram.css('transition', 'none');
                diagramProducer.completeCellsBefore(14, true, false).than(
                    function () {
                        STOP();
                        diagram.css('margin-left', 14 * -192);
                        setTimeout(function () {
                            diagramLoader.removeClass('active');
                            diagram.css('transition', '');
                        }, 800);
                    }
                );
                return true;
            } else if (offset <= (diagram.width() * -1) + absenceDiagram.width()) {
                diagramLoader.addClass('active');
                diagramProducer.completeCellsAfter(14, false, true).than(
                    function() {
                        setTimeout(function () {
                            diagramLoader.removeClass('active');
                        }, 800);
                    }
                );
                return true;
            }
            return false;
        }

        function navigateDiagramGanttOnWheel(event) {
            var offset = parseInt(diagram.css('margin-left')) + (200 * (event.originalEvent.deltaY > 0 ? -1 : 1)); // 200 смещение в пикселях при прокрутке

            // скрываем попапы
            absenceTooltip.hide();
            absenceInfo.hide();

            if (tryCompleteCell(offset))
                return
            diagram.css('margin-left', offset);
            event.preventDefault();
        }

        absenceDiagram.children('.left_button').on('mousedown', null, {direction: 1}, navigateDiagramGantt);
        absenceDiagram.children('.right_button').on('mousedown', null, {direction: -1}, navigateDiagramGantt);
        diagram.on('wheel', navigateDiagramGanttOnWheel);
    }

    trackAbsenceLineHover();
    activateDiagramNavigation();

}
</script><!--.footer.btn ещё 10
.btn ещё 20
.btn ещё 30






--></div></div></div><div id="absenceTooltip" class="tooltipPopup_block"></div><div id="absenceInfo" class="absencePopup_block"><div class="info"><div class="date"><div class="dateNumber"></div><div class="monthTime"><div class="month"></div><div class="time"></div></div></div>—<div class="date"><div class="dateNumber"></div><div class="monthTime"><div class="month"></div><div class="time"></div></div></div><div class="absenceReason"></div><div class="rightSide"><span class="delete_button"></span><span class="edit_button"></span><span class="duplicate_button"></span></div></div><div class="schedule"><table><tr class="headers"><td></td><td class="day mo">Пн</td><td class="day tu">Вт</td><td class="day we">Ср</td><td class="day th">Чт</td><td class="day fr">Пт</td></tr><tr class="week"><td class="weekParity">Чётная неделя</td><td data-day="Пн" class="day"></td><td data-day="Вт" class="day"></td><td data-day="Ср" class="day"></td><td data-day="Чт" class="day"></td><td data-day="Пт" class="day"></td></tr><tr class="week"><td class="weekParity">Нечётная неделя</td><td data-day="Пн" class="day"></td><td data-day="Вт" class="day"></td><td data-day="Ср" class="day"></td><td data-day="Чт" class="day"></td><td data-day="Пт" class="day"></td></tr></table></div><div class="comment"><span class="icon-comment"></span><span class="text"></span></div></div><div id="absenceViewer" class="popup_backgroundBlock"><div class="wrapper"><div class="popupBlock"><div class="popup absenceViewer"><div class="leftBlock"><div class="datePicker"><div class="datePickerWrap"></div></div></div><div class="rightBlock"><div class="head">Просмотрщик отсутствий<div class="rightSide"><div id="clsAbsenceViewerBut1" class="close_button"></div></div></div><div class="body"><div class="controlButtons"><span id="dtAv" class="dateTime"><span class="dateTimeBy">дата отсутствия</span><span class="from"><span id="tAv1" class="date">23.05.16</span></span>—<span class="to"><span id="tAv2" class="date">23.05.16</span></span></span><div class="buttons_group"><div id="absenceReasonFilter" class="button selected">Все типы отсутствий</div></div><div class="rightSide"><div class="button greenC"><span class="icon-add"></span>Новое отсутствие</div></div></div><div class="AbsenceList"><table class="absences"><tr class="headers"><td class="control">Управление</td><td class="dateCreation">Дата создания</td><td class="dateUpdate">Дата обновления</td><td class="creator">Создатель</td><td class="absenceRange">Время отсутствия</td><td class="type">Тип отсутствия</td><td class="comment">Комментарий</td></tr><tbody><tr class="absence"><td class="control"><span class="edit_button"></span><span class="duplicate_button"></span><span class="delete_button"></span></td><td class="dateCreation"><div class="dateTime">12.09.12 12:30</div></td><td class="dateUpdate"><div class="dateTime">12.09.12 12:30</div></td><td class="creator me">Я</td><td class="absenceRange"><div class="from"><div class="dateNumber">18</div><div class="monthTime"><div class="month">Май</div><div class="time">13:00</div></div></div><div class="to"><div class="dateNumber">01</div><div class="monthTime"><div class="month">Сентябрь</div><div class="time">13:00</div></div></div></td><td class="type"><span class="typeName reason6">Расписание<div id="showSchedule_but" class="more_button"></div></span></td><td class="comment"><div class="comment_button"></div></td></tr></tbody></table><!--.footer.btn ещё 5
.btn ещё 10
.btn ещё 15
--></div><div class="EmptyBlock"><div class="wrapper">По вашему запросу ничего не найдено</div></div></div><div class="footer"><div class="button greenC">Сохранить</div><div id="clsAbsenceViewerBut2" class="button grayC">Отмена</div></div></div></div><!--.popup.absenceEditing--><!----><!--    .body--><!--        .t_wrapper--><!--            .tc_wrapper.datePickerBlock--><!--                .datePicker--><!--                    #2date-rangeWrap--><!--            .tc_wrapper.mainBlock--><!--                .head Новое отсутствие--><!--                    .rightSide--><!--                        #clsAbsenceViewerBut1.close_button--><!--                .body--><!--                    .mainInfo--><!--                        span#2twoRange.dateTime--><!--                            span.from--><!--                                span#2date-range1.date 23.05.16--><!--                                span#amount.time с начала дня--><!--                            |—--><!--                            span.to--><!--                                span#2date-range2.date 23.05.16--><!--                                span#amount2.time до конца дня--><!----><!--                        .rightSide--><!--                            #absenceReason_but.absenceReason_button.reason2 Расписание--><!----><!--                    .timePicker--><!----><!--                        .twoDays--><!--                            .slider.toRight--><!--                                #slider-range-max--><!--                            .slider.toLeft--><!--                                #slider-range-min--><!--                        .oneDay--><!--                            .slider--><!--                                #slider-range--><!----><!--                    .comment--><!--                        textarea.text(placeholder="Комментарий")--><!----><!----><!--            #scheduleBlock.tc_wrapper.scheduleBlock--><!--                .dimension_wrapper--><!--                    .schedule--><!--                        .head Расписание--><!--                            .rightSide--><!--                                .weekParity_button 2нед.--><!--                        .body--><!--                            table--><!--                                thead--><!--                                    tr.headers--><!--                                        td.day--><!--                                        td.week Чётная неделя--><!--                                        td.week Нечётная неделя--><!--                                tbody--><!--                                    +loop(5)--><!--                                        tr--><!--                                            td.day Пн--><!--                                            td.week--><!--                                                span.time--><!--                                                    span.from 12:00--><!--                                                    | —--><!--                                                    span.to 18:00--><!--                                                span.delete_button--><!----><!--                                            td.week--><!--                                                .add_button--><!----><!----><!----><!----><!--    script.--><!--        $(function () {--><!--            $('#2twoRange').dateRangePicker(--><!--                {--><!--                    singleMonth: true,--><!--                    inline: true,--><!--                    format: 'DD.MM.YY',--><!--                    showShortcuts: false,--><!--                    showTopbar: false,--><!--                    //startDate: "27.10.15",--><!--                    startOfWeek: "monday",--><!--                    //                    showTopbar: false,--><!--                    separator: '  ',--><!--                    getValue: function () {--><!--                        //                        if ($('#date-range200').val() && $('#date-range201').val() )--><!--                        //  				return $('#date-range200').val() + ' to ' + $('#date-range201').val();--><!--                        //                            return '';--><!--                        //                        else--><!--                        return '';--><!--                    },--><!--                    setValue: function (s, s1, s2) {--><!--                        $('#2date-range1').html(s1);--><!--                        $('#2date-range2').html(s2);--><!--                    },--><!--                    alwaysOpen: true,--><!--                    container: '#2date-rangeWrap'--><!--                }--><!--            );--><!--            //$('#2twoRange').data('dateRangePicker').setDateRange('23-05-16', '23-05-16');--><!----><!----><!--            function generateTime(elem, e, ui){--><!----><!--                var hours = Math.floor(ui.value / 60);--><!--                var minutes = ui.value - (hours * 60);--><!----><!--                if (hours == 0 && minutes==0) {--><!--                    elem[0].html( 'с начала дня' );--><!--                    return;--><!--                }--><!--                else if(hours==24){--><!--                    elem[0].html( 'до конца дня' );--><!--                    return--><!--                }--><!----><!--                if (hours < 10) hours = '0' + hours;--><!--                if (minutes < 10) minutes = '0' + minutes;--><!----><!--                elem[0].html( hours + ':' + minutes );--><!----><!----><!--            }--><!----><!----><!--            $( "#slider-range-max" ).slider({--><!--                range: "max",--><!--                min: 0,--><!--                max: 1425,--><!--                step: 15,--><!--                slide: generateTime.bind(null, [$("#amount")])--><!--            });--><!----><!----><!--            $( "#slider-range-min" ).slider({--><!--                range: "min",--><!--                min: 15,--><!--                max: 1440,--><!--                step: 15,--><!--                value: 1440,--><!--                slide: generateTime.bind(null, [$("#amount2")])--><!--            });--><!----><!--            $( "#slider-range" ).slider({--><!--                range: true,--><!--                min: 0,--><!--                max: 1440,--><!--                step: 15,--><!--                values: [0,1440],--><!--                slide: function(e, ui) {--><!--                    generateTime([$("#amount")], null, {value: ui.values[0]});--><!--                    generateTime([$("#amount2")], null, {value: ui.values[1]});--><!--                }--><!--            });--><!----><!----><!----><!--            $('#absenceReason_but').click(function(event){--><!--                var absenceEditing = $('#absenceViewer').find('.absenceEditing');--><!--                if(absenceEditing.hasClass('schedule_showing')){--><!--                    $('#absenceViewer').find('.scheduleBlock').removeClass('active');--><!--                    absenceEditing.removeClass('schedule_showing');--><!--                }--><!--                else {--><!--                    absenceEditing.addClass('schedule_showing');--><!--                    setTimeout(function(){--><!--                        $('#absenceViewer').find('.scheduleBlock').addClass('active');--><!--                    }, 300);--><!--                }--><!--            });--><!----><!----><!--        });--><!----><!--    .footer--><!--        .button.greenC Сохранить--><!--        #clsAbsenceViewerBut2.button.grayC Отмена-->



</div></div></div><script>function setPopupPosition(event, popup, innerOffset, outerTopOffset){
    var width = popup.outerWidth();

    if(event.clientX + width > $(document).width())
        popup.removeClass('toLeft').addClass('toRight').css('left', (event.clientX - width + innerOffset));
    else
        popup.removeClass('toRight').addClass('toLeft').css('left', event.clientX - innerOffset);

    popup.css('top', $(event.currentTarget).offset().top + outerTopOffset);
}

function bindDocumentKillingClick(freeElem1, freeElem2, handler){
    $(document.body).click(
        function func(event) {
            var elem = event.target;
            if (freeElem1.has(elem).length <= 0 && freeElem2.has(elem).length <= 0) {
                handler();
                $(document.body).off('click', func);
            }
        }
    );
}


function showAbsenceInfo(event, timeFrom, timeTill, absenceId, comment){
    //var elem = $(event.currentTarget);
    diagramProducer.showAbsenceInfoPopup(event, timeFrom, timeTill, absenceId, comment);
}


function activateUserPassport(event) {
    if (!userProducer)
        userProducer = new UserProducer();

    userProducer.showUserFor($(event.currentTarget));
}



var userProducer;

function UserProducer(){
    var expectsOpening = false;
    var userPassport = $('.UserPassport');
    var lastActiveUserPassport;
    var mainInfo = userPassport.children('.mainInfo');
    var currentOpenUserId;
    var insertionOrder = [
        {name: "post", handler: function(data){
            return '<td>Должность</td><td><span class="link" onclick="fastSearch(\'Должность:'+ data +'\')">'+ data +'</span></td>';
        }},
        {name: "department", handler: function(data){
            return '<td>Подразделение</td><td><span class="link" onclick="fastSearch(\'Подразделение:'+ data +'\')">'+ data +'</span></td>';
        }},
        {name: "birthday", handler: function(data){
            return '<td>День рождения</td><td>'+ data +'</td>';
        }},
        {name: "ip", handler: function(data){
            var html = '<td>IP</td><td>';
            if($.isArray(data)){
                html+= data[0];
                for(var i=1; i<data.length; i++)
                    html+= '<br>' + data[i];
            }else
                html += data;
            return html + '</td>';
        }},
        {name: "workPhone", handler: function(data){
            return '<td>Телефон рабочий</td><td>'+ data +'</td>';
        }},
        {name: "mobilePhone", handler: function(data){
            return '<td>Телефон мобильный</td><td>'+ data +'</td>';
        }},
        {name: "homePhone", handler: function(data){
            return '<td>Телефон домашний</td><td>'+ data +'</td>';
        }},
        {name: "email", handler: function(data){
            return '<td>Email</td><td><a href="mailto:'+ data +'" class="link">'+ data +'</a></td>';
        }},
        {name: "icq", handler: function(data){
            return '<td>ICQ</td><td>'+ data +'</td>';
        }},
        {name: "jid", handler: function(data){
            return '<td>JID</td><td>'+ data +'</td>';
        }},
        {name: "fax", handler: function(data){
            return '<td>Fax</td><td>'+ data +'</td>';
        }},
        {name: "homeFax", handler: function(data){
            return '<td>Fax домашний</td><td>'+ data +'</td>';
        }}
    ]

    this.saveUserPassport = function(){
        $(document.body).append(userPassport);
    }

    function buildUserPassport(employeeData){
        var html = '';

        for(var propIndex=i=0; i<insertionOrder.length; i++){
            var propName = insertionOrder[i].name;
            if(employeeData.hasOwnProperty(propName)
                    && employeeData[propName] != null
                    && employeeData[propName] != "") {
                if(propIndex % 4 == 0)
                    html += '<table class="userData">';

                html += '<tr class="' + propName + '">' + insertionOrder[i].handler(employeeData[propName]) + '</tr>';

                propIndex++;
                if(propIndex % 4 == 0)
                    html += '</table>';
            }
        }

        if(propIndex % 4 != 0);
        html += '</table>';

        mainInfo.html('');
        mainInfo.append(html);
    }


    function downloadEmployeeData(userBlock, successHandler, errorHandler){
        var id = userBlock.attr('id');
        var employee = 'Employee' + id;
        if(window[employee])
            successHandler();
        else{

            AJAX.getEmployee(
                id,
                function(data){
                    window[employee] = data;
                    var addData = userBlock.children('.wrapper').children('.wrapper');
                    window[employee].post = addData.children('.post').text();
                    //window[employee].birthday = addData.children('.birthday').text();
                    window[employee].birthday = addData.children('.birthday')[0].firstChild.data
                    buildUserPassport(window[employee]);
                    successHandler();
                },
                errorHandler
            );

            //var link = document.createElement('script');
            //link.onload = function(){
            //    var addData = userBlock.children('.wrapper').children('.wrapper');
            //    window[employee].post = addData.children('.post').text();
            //    window[employee].birthday = addData.children('.birthday').text();
            //    buildUserPassport(window[employee]);
            //    successHandler();
            //}
            //link.onerror = errorHandler;
            //link.src = '/pm/web/scripts/data/'+ employee +'.js';
            //document.body.appendChild(link);
            //document.body.removeChild(link);
        }
    }


    this.closeUserPassport = function(){
        if(lastActiveUserPassport)
            lastActiveUserPassport.removeClass('active');
        userPassport.slideUp().hide();
        setTimeout(userPassport.show, 300);
    }


    this.getOpenedUserId = function(){
        return currentOpenUserId;
    }


    this.showUserFor = function(userBlock){
        if(expectsOpening)
            return;

        var id = userBlock.data('id');
        currentOpenUserId = userBlock.attr('id');

        var passportsInRow = $(document.body).width()<1600 ? 2 : 3; //mediaQuery
        var prevUserPassportId = Math.ceil(id/passportsInRow)*passportsInRow; // id профиля, перед которым нужно вставить orgPassport

        if(prevUserPassportId > employeeGenerator.getEmployeeCount())
            prevUserPassportId = employeeGenerator.getEmployeeCount()

        var prevUserPassport = $('.user[data-id="'+ prevUserPassportId +'"');

        var timeDelay;
        if(lastActiveUserPassport) {
            userPassport.slideUp(); // предудущий
            if (userBlock.hasClass('active')) { // закрываем тот же, что и открыли
                lastActiveUserPassport.removeClass('active');
                lastActiveUserPassport = null;
                return;
            }
            lastActiveUserPassport.removeClass('active');
            timeDelay = 400; // время на то, чтобы закрыть предыдущий паспорт
        }else{
            timeDelay = 0;
        }

        expectsOpening = true;
        setTimeout(
            function(){
                downloadEmployeeData(userBlock,
                    function(){
                        userBlock.addClass('active')
                        prevUserPassport.after(userPassport);
                        userPassport.slideDown()

                        if(!diagramProducer)
                            diagramProducer = new DiagramProducer();
                        diagramProducer.build(60);
                        diagramProducer.diagramToCenter();

                        lastActiveUserPassport = userBlock;
                        expectsOpening = false;
                    },
                    function(){
                        messageGenerator.show("Ошибка при загрузке данных")
                        expectsOpening = false;
                    }
                )
            },
        timeDelay);

    }

}




</script><script>function AbsenceViewer(){
    var absenceViewer = $('#absenceViewer');
    var dateTime = absenceViewer.find('#dtAv');
    var absenceList = absenceViewer.find('.AbsenceList');
    var absenceListTbody = absenceList.find('tbody');
    var months = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'];
    var absenceReasons = ["Командировка", "Отпуск", "Болезнь", "Личные дела", "Местная командировка", "Учеба", "Больничный лист", "Гостевой пропуск", "Ночные работы", "Отпуск за свой счёт"]
    var emptyBlock = absenceList.next('.EmptyBlock');
    dateTime.dateRangePicker(
        {
            singleMonth: true,
            inline: true,
            format: 'DD.MM.YY',
            showShortcuts: false,
            showTopbar: false,
            startOfWeek: "monday",
            separator: '  ',
            getValue: function () {
                return '';
            },
            setValue: function (s, s1, s2) {
                dateTime.find('#tAv1').html(s1);
                dateTime.find('#tAv2').html(s2);
            },
            alwaysOpen: true,
            container: absenceViewer.find('.datePickerWrap')
        }
    );


    function downloadAbsences(from, till, id){
        AJAX.getEmployeeAbsences(
            {
                id: id,
                from: +from,
                till: +till
                //from: +timeFrom  + 10800000,
                //till: +timeTill  + 10800000
            },
            successAction,
            messageGenerator.lateCall("Ошибка! Не удалось подгрузить отсутствия")
        );
    }



    function getTwoNumberDatePart(number){
        return number<10?'0'+number:number;
    }

    function getDateString(date){
        return getTwoNumberDatePart(date.getDate()) +'.'+ getTwoNumberDatePart(date.getMonth()) +'.'+ date.getFullYear() +' '+ getTwoNumberDatePart(date.getHours()) +':'+ getTwoNumberDatePart(date.getMinutes());
    }


    function getDateRangeString(date, initiallyTime){
        var hm = getTwoNumberDatePart(date.getHours()) +':'+ getTwoNumberDatePart(date.getMinutes());
        return '<div class="dateNumber">' + getTwoNumberDatePart(date.getDate()) +
            '<div class="monthTime">' +
            '<div class="month">'+ months[date.getMonth()] +'</div>' +
             (hm != initiallyTime?'<div class="time">'+ hm +'</div>':'') +
            '</div>' +
            '</div>';
    }

    function buildAbsence(absence){

        var html = '<tr>' +
                '<td class="control">' +
                '<span class="edit_button inactive"></span>' +
                '<span class="duplicate_button inactive"></span>' +
                '<span class="delete_button inactive"></span>' +
                '</td>'


        html += '<td class="dateCreation">' +
                '<div class="dateTime">' +
                getDateString(new Date(absence.dtCreation)) +
                '</div>' +
                '</td>';

        html += '<td class="dateUpdate">' +
                '<div class="dateTime"></div>' +
                getDateString(new Date(absence.dtUpdate)) +
                '</div>' +
                '</td>';

        if(absence.creatorId == MyID)
            html += '<td class="creator me">Я</td>'
        else
            html += '<td class="creator"><span class="link">'+ absence.creator +'</span></td>'


        html += '<td class="absenceRange">' +
                '<div class="from">' +
                getDateRangeString(new Date(absence.dateFrom), '00:00') +
                '</div>' +
                '</td>';

        html += '<td class="absenceRange">' +
                '<div class="to">' +
                getDateRangeString(new Date(absence.dateTill), '23:59') +
                '</div>' +
                '</td>';



        html += '<td class="type">' +
                '<span class="typeName reason'+ absenceReasons[absence.reason] +'">' +
                (absence.reason == 6?'<div class="more_button" onclick="showSchedule(event, '+ absence.schedule +')"></div>':'') +
                '</span>' +
                '</td>';

        html += '<td class="comment">' +
                (absence.comment?'<div class="comment_button" onclick="showAbsenceComment(event, '+ absence.comment +')"></div>':'') +
                '</td>';

        html += '</tr>';

        return html;
    }

    this.show = function(id){
        var till = new Date();
        var from = new Date(+till)
        from.setDate(from.getDate() - 7);

        dateTime.data('dateRangePicker').setDateRange(from, till);
        //downloadAbsences(from, till, id);


        var absences = window['Employee'+id].absences;
        if(absences.length>0){
            emptyBlock.hide();
            absenceList.show();
            var html = '';
            for(var i=0; i<absences.length; i++)
                html += buildAbsence(absences[i]);
            absenceListTbody.html('html');
        }else{
            absenceListTbody.html('');
            absenceList.hide();
            emptyBlock.show();
        }

        absenceViewer.show();
        setTimeout(function () {
            absenceViewer.addClass('active')
        }, 200);


        $('#clsAbsenceViewerBut1, #clsAbsenceViewerBut2').click(
            function func(event) {
                absenceViewer.removeClass('active')
                setTimeout(function () {
                    absenceViewer.hide();
                    //absenceViewer.css('display', '')
                }, 200);
                $(event.target).off('click', func);
            }
        );


    }


}


var absenceViewer;
$('#editAbsenceButton').click(function () {
    STOP();
    if(!absenceViewer)
        absenceViewer = new AbsenceViewer();

    absenceViewer.show(userProducer.getOpenedUserId());
});

</script><div id="selectList" class="selectList"><div class="wrapper"></div></div><script>var departmentFilter = $('#departmentFilter');
var selectList = $('#selectList');
var selectListWrap = selectList.children('.wrapper');


var departments = [
    "1инженер-программист",
    "2инженер-программист",
    "3инженер-программист",
    "4инженер-программист",
    "5инженер-программист",
    "6инженер-программист",
    "7инженер-программист",
    "11инженер-программист",
    "11инженер-программист",
    "11инженер-программист",
    "11инженер-программист",
    "11инженер-программист",
    "11инженер-программист",
    "11инженер-программист",
    "11инженер-программист"
]













function BinderSelectList(){


    this.hi; //hover index
    this.items;
    this.visibleItemsIndex;

    var t = this;


    this.initialize = function (){
        t.items = selectListWrap.find('.item');
        t.visibleItemsIndex = [];
        for (var i = 0; i < t.items.length; i++)
            t.visibleItemsIndex[i] = i;
    }

    function makeItemHover(i){
        t.hi = i;
        t.items.eq(i).addClass('hover');
    }

    function activeSelectList(elem, dataList){
        selectList.css('min-width', elem.outerWidth());
        var elemOffset = elem.offset();
        selectList.css('left', elemOffset.left).css('top', elemOffset.top + 28 + 4); // 4 = visual padding
        elem.addClass('checked')
        selectList.show();

        selectListWrap.html('');
        var html = '';
        for(var i = 0; i<dataList.length; i++){
            html += '<div class="item show">'+ dataList[i] +'</div>';
        }
        selectListWrap.append(html);
    }

    function closeSelectList(elem){
        elem.removeClass('checked')
        selectList.hide();
    }

    function keyDownTracking(event){
        var elemOffset;
        var marginTop
        var elem

        switch (event.which) {
            case 13: // enter

                log('ENTER')

                break;
            case 38: // up

                if (t.hi != 0) {
                    t.items.eq(t.visibleItemsIndex[t.hi]).removeClass('hover');
                    t.hi--
                    t.items.eq(t.visibleItemsIndex[t.hi]).addClass('hover');
                    elem = t.items.eq(t.visibleItemsIndex[t.hi]);

                    elemOffset = elem.position();
                    marginTop = parseInt(selectListWrap.css('margin-top'));

                    if (marginTop != 0 && elemOffset.top+29 < 173) {
                        selectListWrap.css('margin-top', marginTop + 29);
                    }
                }
                break;
            case 40: // down

                if(t.hi<t.visibleItemsIndex.length-1){
                    t.items.eq(t.visibleItemsIndex[t.hi]).removeClass('hover');
                    t.hi++
                    t.items.eq(t.visibleItemsIndex[t.hi]).addClass('hover');
                    elem = t.items.eq(t.visibleItemsIndex[t.hi]);

                    elemOffset = elem.position();
                    marginTop = parseInt( selectListWrap.css('margin-top') );

                    var wrapHeight = selectListWrap.height();

                    if(elemOffset.top >= 173 && (wrapHeight+marginTop-319) > 0){
                        selectListWrap.css('margin-top', marginTop - 29);
                    }
                }

                break;
            default:
                return; // exit this handler for other keys
        }
        event.preventDefault();
    }


    this.bindSimpleSelect = function(selector, dataList, handler){

        var documentClick;

        function close(){
            closeSelectList(selector);
            //$(document).off('keydown', keyDownTracking).off('click', documentClick);
            $(document).off('click', documentClick);
        }

        selector.click(function(){
            if(selector.hasClass('checked')){
                close();
            }else{
                activeSelectList(selector, dataList);
                t.initialize();
                makeItemHover(dataList.indexOf(selector.text()));

                $(document).click(
                    documentClick = function(event){
                        var elem = event.target;
                        if(selectList.has(elem).length<=0 && !selector.is(elem)){
                            close();
                        }else if(selectList.has(elem).length>0 && !$(elem).hasClass('hover')){
                            handler(elem.textContent);
                            selector.text(elem.textContent)
                            close();
                        }
                    }
                );
            }
        });
    }

    this.bindComboSelect = function(selector, dataList){

        var documentClick;
        var changeInputField;
        var input = selector.children('.inputField');
        var button = selector.children('.button');
        var onWheel

        var t = this;



        function close(){
            closeSelectList(selector);
            $(document).off('keydown', keyDownTracking).off('click', documentClick);
            input.off('input', changeInputField);
            selectList.off('wheel', onWheel);
        }


        input.focus(function(event){

            if (selector.hasClass('checked')) {
                close();
                return;
            }

            activeSelectList(selector, dataList);
            t.initialize();
            makeItemHover(0)

            input.on('input', changeInputField = function (e){
                var text = $(e.currentTarget).val();
                t.items.eq(t.visibleItemsIndex[t.hi]).removeClass('hover');

                t.visibleItemsIndex = [];
                $.each(t.items, function(i, e){
                    e = $(e);
                    if(e.text().toLowerCase().indexOf(text)>=0){
                        e.addClass('show');
                        t.visibleItemsIndex.push(i);
                    }else{
                        e.removeClass('show');
                    }
                });

                t.hi = 0;
                t.items.eq(t.visibleItemsIndex[0]).addClass('hover');
                selectListWrap.css('margin-top', 0)
            });



            onWheel = function(event) {

                var offset = parseInt(selectListWrap.css('margin-top')) + (29 * (event.originalEvent.deltaY > 0 ? -1 : 1)); // 200 смещение в пикселях при прокрутке

                if(offset>0)
                    offset = 0
                else if(offset < selectList.height() - selectListWrap.height()){
                    event.preventDefault();
                    return
                }

                selectListWrap.css('margin-top', offset);
                event.preventDefault();
            }



            selectList.on('wheel', onWheel);

            $(document).keydown(keyDownTracking).click(
                documentClick = function(event){
                    var elem = event.target;
                    if(selectList.has(elem).length<=0 && selector.has(elem).length<=0){
                        close();
                    }
                }
            );





        }).focusout(close);

        button.click(function func(){
            if(selector.hasClass('checked')){
                close();
                return
            }
            input.focus();
        });


    }


    this.setComboSelect = function(){
        this.comboFlag = true;
        return this;
    }


    this.resetComboSelect = function(){
        this.comboFlag = false;
        return this;
    }

}

var binderSelectList = new BinderSelectList();

binderSelectList.bindComboSelect(departmentFilter, departments);
binderSelectList.bindSimpleSelect($('#sorting_but'), [
    "Id",
    "Фамилия",
    "Должность",
    "Дата рождения"
], sorting.by);
binderSelectList.bindSimpleSelect($('#absenceReasonFilter'), [
    "Все типы отсутствий",
    "Командировка",
    "Отпуск",
    "Болезнь",
    "Личные дела",
    "Местная командировка",
    "Учёба",
    "Больничный лист",
    "Гостевой пропуск",
    "Ночные работы",
    "Отпуск за свой счёт"
], sorting.by);

</script><div id="userMenuPopup" class="menu_popup"><div class="menu"><div class="item">Отправить email</div><div class="item">Добавить в избранные</div><div class="item">Добавить отсутствие</div></div></div><script>function showUserMenu(event){
    var elem = $(event.currentTarget);
    var popup = $('#userMenuPopup');

    if(elem.hasClass('active')){
        elem.removeClass('active');
        popup.hide();
    }else {
        elem.addClass('active');
        var diff = event.clientX - elem.offset().left;
        setPopupPosition(event, popup, 18, 24);
        popup.show();
        bindDocumentKillingClick(popup, popup, function () {
            popup.hide();
            elem.removeClass('active');
        });
    }
    event.stopPropagation();
}




var diagramProducer;

</script><div id="timeFixer" class="popup_backgroundBlock"><div class="wrapper"><div class="popupBlock"><div class="popup timeFixer"><span id="twoRange"><input id="date-range1" type="hidden" value="23.05.16"/><input id="date-range2" type="hidden" value="23.05.16"/></span><div class="datePicker"><div id="date-rangeWrap"></div></div><div class="body"><div class="section active"><div class="head"><span class="header">Зафиксированные задачи<div class="count">4</div></span><div class="rightSide"><div class="button selected">По дате фиксации</div><div class="button empty"><span class="icon-sortBy"></span></div><div class="buttons_group"><input placeholder="Поиск" class="inputField"/><div class="button empty blueC sticky"><span class="icon-search"></span></div></div></div></div><!--#container.diagramContainer(style="width: 1063px; height: 276px")--><div class="t_wrapper"><div class="tc_wrapper large"><div id="container" class="diagramContainer"></div></div><div class="tc_wrapper"><div class="commonFixTime"><div class="date">23.05.16 - 23.05.16</div><div class="wrapper"><span>Затрачено</span><div class="time">06 : 29</div></div></div></div></div><!--table(style="width: 100%")--><!--    tr--><!--        td(style="width: 100%;")--><!--            //#container.diagramContainer(style="width: 1063px; height: 176px")--><!--        td--><script>function activateTimeFixerDiagram() {
    Highcharts.setOptions({
        lang: {
            resetZoom: "Сбросить масштаб"
        }
    });
    $('#container').highcharts({
        chart: {
            //type: 'column',
            type: 'areaspline',
            zoomType: 'x',
            panning: true,
            panKey: 'shift',
            resetZoomButton: {
                position: {
                    x: 0,
                    y: 0
                },
                theme: {
                    fill: 'white',
                    stroke: '#bec5ce',
                    r: 3,
                    style: {
                        color: '#314157'
                    },
                    states: {
                        hover: {
                            stroke: '#acb7c6',
                            fill: '#f5f7f8'
                        }
                    }
                }
            },
        },
        exporting: false,
        title: false,
        xAxis: {
            type: 'datetime'
        },
        yAxis: {
            title: false,
            gridLineColor: '#e8e8e8',
        },
        legend: {
            enabled: false
        },
        tooltip: {
            shared: true,
            crosshairs: true,
        },
        credits: {
            enabled: false
        },

        plotOptions: {
            areaspline: {
                fillColor: {
                    linearGradient: [0, 0, 0, 350],
                    stops: [
                        [0, '#5F9EDC'],
                        [1, '#4978ba']
                    ]
                },
                marker: {
                    radius: 3,
                    enabled: true
                },
                lineWidth: 1,
                states: {
                    hover: {
                        lineWidth: 1
                    }
                },
            }
        },

        series: [{
            color: '#5481c1',
            data: [
                [Date.UTC(2016, 4, 25), 7.28],
                [Date.UTC(2016, 4, 26), 6.25],
                [Date.UTC(2016, 4, 27), 6],
                {
                    x: Date.UTC(2016, 4, 28),
                    y: 7.28,
                    color: 'red'
                },
                [Date.UTC(2016, 4, 29), 0],
                [Date.UTC(2016, 4, 30), 0],
                [Date.UTC(2016, 4, 31), 8],
            ]
        }]
    });
}



</script><div class="CaseList"><table class="cases"><tr class="headers"><td colspan="2" class="type number">Тип и номер задачи</td><td class="name">Название</td><td class="timeSpent">Затрачено</td><td class="timeEstimate">Оценка</td><td class="timeLeft">Осталось</td><td class="timeFix">Время</td><td class="status">Статутс</td></tr><tbody><tr class="case"><td class="type"><div class="relativeBlock"><div class="timer_button"></div><div class="typeName">Bug</div></div></td><td class="number"><div class="id">32158</div><div class="criticality critical"></div></td><td class="name"><div class="relativeBlock"><span class="productTag"><span class="productName">Portal Protei</span><span class="productBranch">Base</span><span class="productVersion">1</span></span><div class="description">Здесь будет описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание</div><div class="right_block"><div class="markers"><div class="marker type1">Маркер №1</div><div class="marker type2">Мой маркер</div></div><span class="menu_button"></span></div></div></td><td class="timeSpent"><div class="time">28 : 00</div></td><td class="timeEstimate"><div class="time">49 : 00</div></td><td class="timeLeft"><div class="time">21 : 00</div></td><td class="timeFix"><div class="time">02 : 35</div></td><td class="status"><div class="relativeBlock"><div class="statusName">status</div><div class="favorite_button"></div></div></td></tr><tr class="case"><td class="type"><div class="relativeBlock"><div class="timer_button"></div><div class="typeName">Task</div></div></td><td class="number"><div class="id">124532</div><div class="criticality"></div></td><td class="name"><div class="relativeBlock"><div class="description">Здесь будет описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание</div><div class="right_block"><span class="menu_button"></span></div></div></td><td class="timeSpent"><div class="time">28 : 00</div></td><td class="timeEstimate"><div class="time">49 : 00</div></td><td class="timeLeft"><div class="time">21 : 00</div></td><td class="timeFix"><div class="time">02 : 35</div></td><td class="status"><div class="relativeBlock"><div class="statusName"><span class="link">status</span></div><div class="favorite_button"></div></div></td></tr><tr class="case"><td class="type"><div class="relativeBlock"><div class="timer_button"></div><div class="typeName">Task</div></div></td><td class="number"><div class="id">124532</div><div class="criticality"></div></td><td class="name"><div class="relativeBlock"><div class="description">Здесь будет описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание</div><div class="right_block"><span class="menu_button"></span></div></div></td><td class="timeSpent"><div class="time">28 : 00</div></td><td class="timeEstimate"><div class="time">49 : 00</div></td><td class="timeLeft"><div class="time">21 : 00</div></td><td class="timeFix"><div class="time">02 : 35</div></td><td class="status"><div class="relativeBlock"><div class="statusName">status</div><div class="favorite_button"></div></div></td></tr><tr class="case"><td class="type"><div class="relativeBlock"><div class="timer_button"></div><div class="typeName">Freq</div></div></td><td class="number"><div class="id">32158</div><div class="criticality cosmetic"></div></td><td class="name"><div class="relativeBlock"><div class="description">Здесь будет описание описание описание описание описание описание описание</div><div class="right_block"><span class="menu_button"></span></div></div></td><td class="timeSpent"><div class="time">28 : 00</div></td><td class="timeEstimate"><div class="time">49 : 00</div></td><td class="timeLeft"><div class="time">21 : 00</div></td><td class="timeFix"><div class="time">02 : 35</div></td><td class="status"><div class="relativeBlock"><div class="statusName">status</div><div class="favorite_button"></div></div></td></tr></tbody></table></div></div><div class="section active"><div class="head"><span class="header">Возможные задачи</span><div class="rightSide"><div class="button selected">По дате создания</div><div class="button empty"><span class="icon-sortBy"></span></div><div class="buttons_group"><input placeholder="Поиск" class="inputField"/><div class="button empty blueC sticky"><span class="icon-search"></span></div></div></div></div><div class="CaseList"><table class="cases"><tr class="headers"><td colspan="2" class="type number">Тип и номер задачи</td><td class="name">Название</td><td class="timeSpent">Затрачено</td><td class="timeEstimate">Оценка</td><td class="timeLeft">Осталось</td><td class="timeFix">Время</td><td class="status">Статутс</td></tr><tbody><tr class="case"><td class="type"><div class="relativeBlock"><div class="timer_button notMine"></div><div class="typeName">Bug</div></div></td><td class="number"><div class="id">32158</div><div class="criticality critical"></div></td><td class="name"><div class="relativeBlock"><span class="productTag"><span class="productName">Portal Protei</span><span class="productBranch">Base</span><span class="productVersion">1</span></span><div class="description">Здесь будет описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание</div><div class="right_block"><span class="menu_button"></span></div></div></td><td class="timeSpent"><div class="time empty"></div></td><td class="timeEstimate"><div class="time empty"></div></td><td class="timeLeft"><div class="time empty"></div></td><td class="timeFix"><div class="timeFix_button"></div></td><td class="status"><div class="relativeBlock"><div class="statusName"><span class="link">status</span></div><div class="favorite_button"></div></div></td></tr><tr class="case"><td class="type"><div class="relativeBlock"><div class="timer_button"></div><div class="typeName">Freq</div></div></td><td class="number"><div class="id">32158</div><div class="criticality important"></div></td><td class="name"><div class="relativeBlock"><span class="productTag"><span class="productName">Portal Protei</span><span class="productBranch">Base</span><span class="productVersion">1</span></span><div class="description">Здесь будет описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание</div><div class="right_block"><span class="menu_button"></span></div></div></td><td class="timeSpent"><div class="time">28 : 00</div></td><td class="timeEstimate"><div class="time">49 : 00</div></td><td class="timeLeft"><div class="time">21 : 00</div></td><td class="timeFix"><div class="timeFix_button"></div></td><td class="status"><div class="relativeBlock"><div class="statusName"><span class="link">status</span></div><div class="favorite_button"></div></div></td></tr><tr class="case"><td class="type"><div class="relativeBlock"><div class="timer_button performed"></div><div class="typeName">Task</div></div></td><td class="number"><div class="id">32158</div><div class="criticality"></div></td><td class="name"><div class="relativeBlock"><div class="description">Здесь будет описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание</div><div class="right_block"><span class="menu_button"></span></div></div></td><td class="timeSpent"><div class="time">28 : 00</div></td><td class="timeEstimate"><div class="time empty"></div></td><td class="timeLeft"><div class="time">21 : 00</div></td><td class="timeFix"><div class="timeFix_button"></div></td><td class="status"><div class="relativeBlock"><div class="statusName"><span class="link">status</span></div><div class="favorite_button"></div></div></td></tr><tr class="case"><td class="type"><div class="relativeBlock"><div class="timer_button performed"></div><div class="typeName">Task</div></div></td><td class="number"><div class="id">32158</div><div class="criticality"></div></td><td class="name"><div class="relativeBlock"><div class="description">Здесь будет описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание</div><div class="right_block"><div class="markers"><div class="marker type3">Мой личный маркер</div></div><span class="menu_button"></span></div></div></td><td class="timeSpent"><div class="time">28 : 00</div></td><td class="timeEstimate"><div class="time">49 : 00</div></td><td class="timeLeft"><div class="time">21 : 00</div></td><td class="timeFix"><div class="timeFix_button"></div></td><td class="status"><div class="relativeBlock"><div class="statusName"><span class="link">status</span></div><div class="favorite_button"></div></div></td></tr><tr class="case"><td class="type"><div class="relativeBlock"><div class="timer_button notMine"></div><div class="typeName">Treq</div></div></td><td class="number"><div class="id">32158</div><div class="criticality cosmetic"></div></td><td class="name"><div class="relativeBlock"><div class="description">Здесь будет описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание</div><div class="right_block"><span class="menu_button"></span></div></div></td><td class="timeSpent"><div class="time empty"></div></td><td class="timeEstimate"><div class="time empty"></div></td><td class="timeLeft"><div class="time empty"></div></td><td class="timeFix"><div class="timeFix_button"></div></td><td class="status"><div class="relativeBlock"><div class="statusName"><span class="link">status</span></div><div class="favorite_button"></div></div></td></tr><tr class="case closed"><td class="type"><div class="relativeBlock"><div class="timer_button notMine"></div><div class="typeName">Freq</div></div></td><td class="number"><div class="id">32158</div><div class="criticality"></div></td><td class="name"><div class="relativeBlock"><span class="productTag"><span class="productName">Portal Protei</span><span class="productBranch">Base</span><span class="productVersion">1</span></span><div class="description">Здесь будет описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание описание</div><div class="right_block"><span class="menu_button"></span></div></div></td><td class="timeSpent"><div class="time empty"></div></td><td class="timeEstimate"><div class="time empty"></div></td><td class="timeLeft"><div class="time empty"></div></td><td class="timeFix"><div class="timeFix_button"></div></td><td class="status"><div class="relativeBlock"><div class="statusName"><span class="link">closed</span></div><div class="favorite_button"></div></div></td></tr></tbody></table></div><div class="footer"><div class="btn">ещё 5</div><div class="btn">ещё 10</div><div class="btn">показать всё</div></div></div></div><div class="footer"><div class="button greenC">Сохранить</div><div id="closeTimeFixer" class="button grayC">Отмена</div></div></div></div></div></div><script>$(function () {
    $('#twoRange').dateRangePicker(
        {
            singleMonth: true,
            inline: true,
            format: 'DD.MM.YY',
            showShortcuts: false,
            showTopbar: false,
            //startDate: "27.10.15",
            startOfWeek: "monday",
            //                    showTopbar: false,
            separator: '  ',
            getValue: function () {
                //                        if ($('#date-range200').val() && $('#date-range201').val() )
                //  				return $('#date-range200').val() + ' to ' + $('#date-range201').val();
                //                            return '';
                //                        else
                return '';
            },
            setValue: function (s, s1, s2) {
                $('#date-range1').val(s1);
                $('#date-range2').val(s2);
            },
            alwaysOpen: true,
            container: '#date-rangeWrap'
        }
    );
    $('#twoRange').data('dateRangePicker').setDateRange('23-05-16', '23-05-16');
});


$('#runTimeFixer').click(function () {
    var popupBlock = $('#timeFixer');
    popupBlock.css('display', 'block');
    setTimeout(function () {
        popupBlock.addClass('active'),
        activateTimeFixerDiagram();
    }, 200);
    $('#closeTimeFixer').click(
        function func(event) {
            popupBlock.removeClass('active')
            setTimeout(function () {
                popupBlock.css('display', '')
            }, 200);
            $(event.target).off('click', func);
        }
    );
});



</script><div class="globalSearch_popup"><div class="t_wrapper"><div class="tc_wrapper systemObject active"><div class="relativeBlock"><div class="title">Объекты системы</div><div style="overflow: hidden" class="items"><div class="main"><div class="item more show">0Компонента</div><div class="item more show">1Компонента</div><div class="item more show">2Компонента</div><div class="item more show">3Компонента</div><div class="item more show">4Компонента</div><div class="item more show">5Компонента</div><div class="item more show">6Компонента</div><div class="item more show">7Компонента</div><div class="item more show">8Компонента</div><div class="item more show">Продукт</div><div class="item more show">Задача</div><div class="item more show">План</div><div class="item more show">Группа задач</div><div class="item more show">Заказ</div><div class="item more show">Проект</div><div class="item more show">Организация</div><div data-title="employee" class="item more show hover">Сотрудник</div></div></div></div></div><div class="tc_wrapper objectProperties"><div class="relativeBlock"><div class="title">Параметры объекта</div><div class="items"><div class="additional"><!--.common(data-additional="another,exception")--><div data-title="another" class="item back"><span class="keyWord">,</span><div class="description">другое_значение</div></div><div data-title="exception" class="item back"><span class="keyWord">-</span><div class="description">исключение</div></div><div data-title="range" class="item back"><span class="keyWord">..</span><div class="description">диапазон</div></div></div><div class="main"></div><!--.item.show.more Фильтр--><!--.item.show.single Избранный--><!--.item.show.multi.more Отсутсвие--><!--.item.show.more.back(data-additional="another") Пол--><!--.item.show.more.free.back ФИО--><!--.item.show.more.back Должность--><!--.item.show.more.back Подразделение--><!--.item.show.more.free.back(data-additional="another,exception,range") День_рождения--><!--.item.show Телефон_рабочий--><!--.item.show Телефон_домашний--><!--.item.show--><!--.item.show.more.free.back Email--><!--.item.show IP--><!--.item.show JID-->

</div></div></div><div class="tc_wrapper propertyValues"><div class="title">Варианты значений</div><div class="items"><div class="additional"><div data-title="another" class="item back"><span class="keyWord">,</span><div class="description">другое_значение</div></div><div data-title="exception" class="item back"><span class="keyWord">-</span><div class="description">исключение</div></div><div data-title="range" class="item back"><span class="keyWord">..</span><div class="description">диапазон</div></div></div><div class="main"></div></div></div><div class="tc_wrapper propertyValues"><div class="relativeBlock"><div class="title">Сохранённые фильтры</div><div class="items"><div class="additonal"><div data-title="another" class="item back"><span class="keyWord">,</span><div class="description">другое_значение</div></div><div data-title="exception" class="item back"><span class="keyWord">-</span><div class="description">исключение</div></div><div data-title="range" class="item back"><span class="keyWord">..</span><div class="description">диапазон</div></div></div><div class="main"></div></div></div></div></div></div><script>var allItems = {
    employee: {
        items: {
            mobile: {
                name: 'Телефон_мобильный',
                show: true,
            },
            late: {
                name: 'Отсутствие',
                show: true,
                multi: true,
                items: {
                    date: {
                        name: 'Дата',
                        show: true,
                        back: true,
                        free: true,
                        additional: 'another,exception,range',
                        items: [
                            '<div class="item show">{Сегодня}</div>',
                            '<div class="item show">{Вчера}</div>'
                        ]
                    },
                    reason: {
                        name: 'Причина',
                        show: true,
                        back: true,
                        items: [
                            '<div class="item show">Личные дела</div>',
                            '<div class="item show">Личные дела</div>',
                            '<div class="item show">Личные дела</div>',
                            '<div class="item show">Личные дела</div>',
                            '<div class="item show">Личные дела</div>',
                            '<div class="item show">Болезнь</div>'
                        ]
                    }
                }
            },
            sex: {
                name: 'Пол',
                show: true,
                back: true,
                additional: 'another',
                items: [
                    '<div class="item show">Мужской</div>',
                    '<div class="item show">Женский</div>'
                ]
            },
            email: {
                name: 'Email',
                show: true,
                free: true,
                back: true,
                items: [
                    '<div class="item show">1a@mail.ru</div>',
                    '<div class="item show">2a@mail.ru</div>',
                    '<div class="item show">3a@mail.ru</div>',
                    '<div class="item show">4a@mail.ru</div>',
                    '<div class="item show">5a@mail.ru</div>',
                    '<div class="item show">6a@mail.ru</div>',
                    '<div class="item show">7a@mail.ru</div>',
                    '<div class="item show">8a@mail.ru</div>'
                ]
            },
            post: {
                name: 'Должность',
                show: true,
                back: true,
                items: [
                    '<div class="item show">0Инженер-программист</div>',
                    '<div class="item show">1Инженер-программист</div>',
                    '<div class="item show">2Инженер-программист</div>',
                    '<div class="item show">3Инженер-программист</div>',
                    '<div class="item show">4Инженер-программист</div>',
                    '<div class="item show">5Инженер-программист</div>',
                    '<div class="item show">6Инженер-программист</div>',
                    '<div class="item show">7Инженер-программист</div>'
                ]
            },
            department: {
                name: 'Подразделение',
                show: true,
                back: true,
                items: [
                    '<div class="item show">0</div>',
                    '<div class="item show">1</div>',
                    '<div class="item show">2</div>',
                    '<div class="item show">3</div>',
                    '<div class="item show">4</div>',
                    '<div class="item show">5</div>',
                    '<div class="item show">6</div>',
                    '<div class="item show">7</div>'
                ]
            }
        }
    }
}


</script><script>!function() {


    var globalSearch = $('#globalSearch');
    var popupBlock = $('.globalSearch_popup');
    var searchInputOffset = globalSearch.offset();

    //var searchWidth = globalSearch.outerWidth();
    var searchWidth = 179;


    var sections = [];
    var asi = 0; // индекс активной секции


    var permittedItems // разрешённые
    var visibleItems; // показанные
    var activeItem; // активный item
    var hi; // индекс hover item

    var savedParams = [];
    var multipleParam = (function(){
        return savedParams; // для сохранения savedParams при записи в multipleParam (единственный способо, это сделать замыкание)
    }());
    var api = 0; // индекс активного параметра


    var inputT = "";

    function makeFirstItemSelected(items){
        items.eq(0).addClass('hover');
        hi = 0;
    }

    function showSection(i){
        sections[i].section.css('display', 'table-cell').addClass('active');
    }


    function isAdditionalItem(i){
        if(i.parent().hasClass('additional')) return true;
    }

    function activateSection(i){
        if(!sections[i]){
            sections[i] = {
                section: sections[sections.length-1].section.next()
            };
        }



        if(sections[i-1].link) {

            var items = sections[i].section.find('.items .main');
            items.html(""); //очищаем предыдущие items

            var innerItems = sections[i-1].link.items

            if (innerItems) {
                var html = "";

                if ($.isArray(innerItems)) {
                    html = innerItems.join("");
                } else {
                    for (var title in innerItems) {
                        var itemNode = innerItems[title];
                        var item = '<div data-title="'+ title +'" class="item ';
                        itemNode['back'] && (item += 'back ');
                        itemNode['multi'] && (item += 'multi ');
                        itemNode['show'] && (item += 'show ');
                        itemNode['items'] && (item += 'more ');
                        itemNode['free'] && (item += 'free ');
                        item += '" ';
                        itemNode['additional'] && (item += 'data-additional="' + itemNode['additional'] + '"');
                        item += '>' + itemNode.name + '</div>';
                        html += item;
                    }
                }
                items.append(html)

            } else {
                log('загрузить')
            }
            sections[asi].items = items.find('.item')

        }else
            showAllItems(sections[asi].items);

        permittedItems = visibleItems = sections[asi].items;



        makeFirstItemSelected(visibleItems)
        showSection(i)

    }


    function additionalActivate(dataAdditional){

        //var items = sections[aisection].find('.item');
        //items.filter('.active').removeClass('active');
        //activeItem.removeClass('active')
        activeItem.removeClass('active')
        //activeItem = null;
        //oldItems = items;

        //var dataAdditional = visibleItems.eq(hi).attr('data-additional');
        //var addItems;
        //if(dataAdditional)
        //    addItems = dataAdditional.split(',');
        //else
        //    addItems = sections[aisection].find('.common').attr('data-additional').split(',')

        var addItems = dataAdditional.split(',');

        visibleItems = $([]);

        var additionalSection = sections[asi].section.find('.additional');
        additionalSection.show();

        additionalSection.find('.item').each(function(i,e){
            e = $(e);

            for (var i = 0; i < addItems.length; i++) {
                if (e.attr('data-title') == addItems[i]) {
                    e.addClass('show')
                    if(activeItem.hasClass('more'))
                        e.addClass('more')
                    else
                        e.removeClass('more');
                    visibleItems = visibleItems.add(e);
                    return;
                }
            }
            e.removeClass('show');
        })

        log(sections[asi].items)
        showAllItems(sections[asi].items);
        visibleItems = visibleItems.add(sections[asi].items);


        permittedItems = visibleItems;
        hi = 0;
        visibleItems.eq(0).addClass('hover');
    }

    function moveToBack(){

        visibleItems.eq(hi).removeClass('hover');
        sections[asi].section.removeClass('active').css('display', 'none');
        asi--;
        sections[asi].section.addClass('active')
        popupBlock.css('left', searchInputOffset.left - searchWidth * asi);

    }

    function showAllItems(collection){
        collection.each(function(i, e){
            $(e).addClass('show')
        });
    }


    function saveParam(param){
        //param = param || "";
        //param = getItemKeyWord(visibleItems.eq(ai)) + '"';

        var dataAdditional = activeItem.attr('data-additional');

        if(dataAdditional)
            if(dataAdditional==""){
                visibleItems = permittedItems;

                showAllItems(visibleItems)

                if(activeItem.hasClass('multi'))
                    param += ") ";
                else
                    param += " ";

                activeItem.removeClass('active').addClass('hover');
                activeItem = null;
                hi = visibleItems.filter('hover').index();
            }else
                additionalActivate(dataAdditional)
        else{
            additionalActivate('another,exception');
        }

        inputT += param
        globalSearch.val(inputT);
        multipleParam[api] += param.trim();
    }


    function getItemKeyWord(e){

        e = $(e);
        var keyWordTag = e.children('.keyWord');
        if(keyWordTag.length) return keyWordTag.text()
        return e.text();

    }



    globalSearch.focus(function () {

        //multipleParam = savedParams;


        globalSearch.on('input', function (event) {

            var inputText = globalSearch.val();

            if(inputText.charAt(inputText.length-1) == ' '){
                visibleItems.each(function(i,e){
                    $(e).removeClass('show')
                });


                permittedItems = sections[asi].items;
                showAllItems(permittedItems);
            }

            if(visibleItems.eq(hi).hasClass('active') && inputText.charAt(inputText.length-1) == '"'){

                var joinedParams = multipleParam.join(" ")
                var param =  inputText.substr(joinedParams.length) + '"';


                if(visibleItems.eq(hi).attr('data-additional'))
                    additionalActivate()
                else{
                    visibleItems.eq(hi).removeClass('active').addClass('hover')
                    param += " ";
                }

                globalSearch.val(multipleParam.join(" ") + param)
                multipleParam[multipleParam.length - 1] += param.trim();
                return;
            }

            var text;
            if(!multipleParam[0]){
                text = inputText;
            }else{
                var quotePos = inputText.lastIndexOf('"');
                var spacePos = inputText.lastIndexOf(' ');
                var fBracket = inputText.lastIndexOf('(');
                var lBracket = inputText.lastIndexOf(')');
                var maxPos = Math.max(quotePos, spacePos, fBracket, lBracket);
                text = inputText.substr(maxPos + 1);


            }


            //var firstItemFlag;
            //showingItemIndexes = [];
            visibleItems.eq(hi).removeClass('hover')


            visibleItems = $($.grep(permittedItems, function(e, i){

                e = $(e);
                if(e.text().toLowerCase().indexOf(text) == 0){

                    e.addClass('show');
                    //if(!firstItemFlag){
                    //    activeItems.eq(ai).removeClass('hover');
                    //    ai = e.index();
                    //    e.addClass('hover');
                    //    firstItemFlag = true;
                    //}
                    return true;

                }else{
                    e.removeClass('show');
                    return false;
                }
            }));

            makeFirstItemSelected(visibleItems)
            sections[asi].section.css('margin-top', 0)


        });




        sections[0] = {
            section: popupBlock.find('.tc_wrapper.active'),
            link: allItems
        };
        popupBlock.css('display', 'block').css('left', searchInputOffset.left);
        permittedItems = visibleItems = sections[0].section.find('.item')
        hi = visibleItems.filter('.hover').index();

        showSection(0)


        var elem = visibleItems.eq(hi);
        var elemOffset = elem.position();
        var bottom = elemOffset.top + 33;

        if (bottom > 424) {
            sections[asi].section.find('.main').css('margin-top', (bottom - 424) * -1);
        }



        setTimeout(function(){
            popupBlock.addClass('active');


            $(document).keydown(function (event) {
                switch (event.which) {
                    case 16: // shift

                        break;
                    case 38: // up

                        if(hi != 0){
                            visibleItems.eq(hi).removeClass('hover');
                            hi--;
                            visibleItems.eq(hi).addClass('hover');


                            var elem = visibleItems.eq(hi);
                            var elemOffset = elem.position();
                            var marginTop = parseInt( sections[asi].section.find('.main').css('margin-top') );
                            var top = elemOffset.top;
                            if (marginTop != 0 && top < 28) {
                                //log(1)
                                sections[asi].section.find('.main').css('margin-top', marginTop+33);
                            }




                        }
                        break;
                    case 9: // tab

                        if(visibleItems.length == 0 && activeItem.hasClass('free')){
                            var joinedParams = multipleParam.join(" ")
                            var param = globalSearch.val().substr(joinedParams.length) + '"'
                            if(activeItem.hasClass('back')) {
                                moveToBack();
                            }
                            saveParam(param);
                        }


                        else if(visibleItems.length == 0 && !activeItem.hasClass('free')){

                            log('НЕ ДОПУСКАЕТСЯ СВОБОДНЫЙ ВВОД')

                        }

                        else {

                            //activeItem = $('#datePicker');


                            if (visibleItems.filter('.active').length>0) {
                                var joinedParams = multipleParam.join(" ")
                                var param = globalSearch.val().substr(joinedParams.length) + '"';









                                //if (activeItem.attr('data-additional'))
                                //    additionalActivate()

                                var dataAdditional = activeItem.attr('data-additional');
                                if(!dataAdditional || dataAdditional != "") {
                                    if (!dataAdditional)
                                        dataAdditional = 'another,exception';
                                    additionalActivate(dataAdditional)
                                }else {
                                    activeItem.removeClass('active').addClass('hover')
                                    hi = visibleItems.filter('active').index();
                                    log(visibleItems.has(activeItem));
                                    activeItem = null;
                                    param += " ";
                                }

                                globalSearch.val(multipleParam.join(" ") + param)
                                multipleParam[multipleParam.length - 1] += param.trim();
                            } else {

                                if (visibleItems.eq(hi).hasClass('more')) {

                                    sections[asi].section.removeClass('active')
                                    var param = getItemKeyWord(visibleItems.eq(hi));


                                    activeItem = visibleItems.eq(hi);
                                    activeItem.removeClass('hover').addClass('active');



                                    if(asi == 0){
                                        param += " ";
                                        sections[0].link = allItems[activeItem.attr('data-title')];

                                    }else{
                                        if (visibleItems.eq(hi).hasClass('multi')){
                                            param = " " + param + "(";
                                            api++;
                                            multipleParam[api] = [];
                                            multipleParam = multipleParam[api];
                                            api = 0;
                                        }
                                        else
                                            param += '"';
                                            //param += '"';
                                        sections[asi].link = sections[asi-1].link.items[activeItem.attr('data-title')];
                                    }

                                    if(multipleParam[api])
                                        if(isAdditionalItem(activeItem)){
                                            multipleParam[api] += param; // += для additional параметров
                                        }else {
                                            api++
                                            multipleParam[api] = param;
                                            param = " " + param;
                                        }
                                     else
                                        multipleParam[api] = param.trim();



                                    inputT += param;

                                    if(asi==0) {
                                        //globalSearch.val(param + " ");
                                        api++;
                                    }else{

                                    }
                                        //globalSearch.val(multipleParam.join(" "));

                                    globalSearch.val(inputT)



                                    asi++
                                    popupBlock.css('left', searchInputOffset.left - searchWidth * asi);

                                    activateSection(asi)

                                } else {
                                    if (activeItem.hasClass('back')) {

                                        moveToBack();
                                        saveParam(getItemKeyWord(visibleItems.eq(hi)) + '"');

                                    } else {
                                        if(activeItem.hasClass('single')){
                                            api++;
                                            multipleParam[api] = getItemKeyWord(activeItem);
                                            api++;
                                            globalSearch.val(multipleParam.join(" "))
                                        }else {
                                            activeItem = visibleItems.eq(hi);
                                            activeItem.removeClass('hover').addClass('active');
                                            api++
                                            multipleParam[api] = getItemKeyWord(activeItem) + '"';
                                            globalSearch.val(multipleParam.join(" "))
                                        }
                                    }

                                }


                            }
                        }



                        break;
                    case 40: // down

                        if(hi < visibleItems.length-1){
                            visibleItems.eq(hi).removeClass('hover')
                            hi++;
                            visibleItems.eq(hi).addClass('hover')


                            var elem = visibleItems.eq(hi);
                            var elemOffset = elem.position();
                            var marginTop = parseInt( sections[asi].section.find('.main').css('margin-top') );
                            var bottom = elemOffset.top + 33 + (marginTop*-1);
                            if (elemOffset.top+33 >=424 && bottom > 424) {
                                sections[asi].section.find('.main').css('margin-top', (bottom - 424) * -1);
                            }

                        }

                        break;
                    default:
                        return; // exit this handler for other keys
                }
                event.preventDefault();
            });




            $(document.body).click(
                function func(event){
                    var elem = event.target;
                    if(popupBlock.has(elem).length<=0){
                        popupBlock.removeClass('active')
                        setTimeout(function(){popupBlock.css('display', '')}, 200);
                        globalSearch.removeClass('active')
                        $(document.body).off('click', func);
                    }
                }
            );
        }, 200);

    });
}();

</script><div class="notifications"><div class="head"><div class="button grayC active"><span class="icon-settings"></span>Типы уведомлений</div><div class="rightSide"><div class="regulator"><form><input type="text" placeholder="Поиск" class="inputField"/><span class="filters_button"></span><label class="hideSource"><input type="submit"/><div class="button empty sticky blueC"><span class="icon-search"></span></div></label></form></div></div></div><div class="propertiesBlock"><div class="property"><div class="marker status active"></div>Статус</div><div class="property"><div class="marker comments active"></div>Комментарии</div><div class="property"><div class="marker"></div>Приоритет</div><div class="property"><div class="marker fixTime active"></div>Фиксация времени</div><div class="property"><div class="marker"></div>Документы</div><div class="property"><div class="marker files active"></div>Вложения</div><div class="property"><div class="marker"></div>Связи</div><div class="property"><div class="marker"></div>Сроки и этапы</div><div class="property"><div class="marker executors active"></div>Исполнители</div></div><div class="body"><div class="date"><div class="dateTitle">Сегодня, 15.04.16</div><div class="notes"><div class="note plan notRead"><div class="head"><div class="time">12:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title"><span class="case mr">Bug<span class="id">13432</span></span>был добавлен в ваш план<span class="by ml"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note comment notRead"><div class="head"><div class="time">11:45</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">Вам ответили на комментарий в<span class="case mlr">Bug<span class="id">13432</span></span><span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note comment notRead"><div class="head"><div class="time">11:42</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">Вам ответили на комментарий в<span class="case mlr">Bug<span class="id">13432</span></span><span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note late"><div class="head"><div class="time">11:40</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title"><span class="employee mr">Фамилия И.О.</span>будет отсутствовать<span class="date mlr">16.04.16</span>по причине<span class="lateReason ml">Болезнь</span></span></div></div></div></div></div><div class="date"><div class="dateTitle">Вчера, 14.04.16</div><div class="notes"><div class="note plan"><div class="head"><div class="time">12:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">У вас новая неподтверждённая задача<span class="case ml">Freq<span class="id">13432</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">11:45</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Freq<span class="id">213432</span></span>появился новый этап<span class="by ml"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">11:42</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Task<span class="id">134324</span></span>сменился статус на<span class="status mlr">paused</span><span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note plan"><div class="head"><div class="time">11:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">У вас новая неподтверждённая задача<span class="case ml">Freq<span class="id">13432</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">10:12</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Freq<span class="id">134324</span></span>появился новый этап<span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note plan"><div class="head"><div class="time">10:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">Порядок задач в вашем плане был изменён<span class="by ml"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note component"><div class="head"><div class="time">08:15</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В компоненте<span class="component mlr">.Component_A</span>появилась новая версия<span class="version mlr">4.32</span><span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note plan"><div class="head"><div class="time">12:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">У вас новая неподтверждённая задача<span class="case ml">Freq<span class="id">13432</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">11:45</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Freq<span class="id">213432</span></span>появился новый этап<span class="by ml"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">11:42</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Task<span class="id">134324</span></span>сменился статус на<span class="status mlr">paused</span><span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note plan"><div class="head"><div class="time">11:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">У вас новая неподтверждённая задача<span class="case ml">Freq<span class="id">13432</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">10:12</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Freq<span class="id">134324</span></span>появился новый этап<span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note plan"><div class="head"><div class="time">10:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">Порядок задач в вашем плане был изменён<span class="by ml"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note component"><div class="head"><div class="time">08:15</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В компоненте<span class="component mlr">.Component_A</span>появилась новая версия<span class="version mlr">4.32</span><span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note plan"><div class="head"><div class="time">12:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">У вас новая неподтверждённая задача<span class="case ml">Freq<span class="id">13432</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">11:45</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Freq<span class="id">213432</span></span>появился новый этап<span class="by ml"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">11:42</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Task<span class="id">134324</span></span>сменился статус на<span class="status mlr">paused</span><span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note plan"><div class="head"><div class="time">11:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">У вас новая неподтверждённая задача<span class="case ml">Freq<span class="id">13432</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">10:12</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Freq<span class="id">134324</span></span>появился новый этап<span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note plan"><div class="head"><div class="time">10:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">Порядок задач в вашем плане был изменён<span class="by ml"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note component"><div class="head"><div class="time">08:15</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В компоненте<span class="component mlr">.Component_A</span>появилась новая версия<span class="version mlr">4.32</span><span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note plan"><div class="head"><div class="time">12:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">У вас новая неподтверждённая задача<span class="case ml">Freq<span class="id">13432</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">11:45</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Freq<span class="id">213432</span></span>появился новый этап<span class="by ml"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">11:42</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Task<span class="id">134324</span></span>сменился статус на<span class="status mlr">paused</span><span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note plan"><div class="head"><div class="time">11:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">У вас новая неподтверждённая задача<span class="case ml">Freq<span class="id">13432</span></span></span></div></div></div><div class="note case"><div class="head"><div class="time">10:12</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В<span class="case mlr">Freq<span class="id">134324</span></span>появился новый этап<span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note plan"><div class="head"><div class="time">10:00</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">Порядок задач в вашем плане был изменён<span class="by ml"><span class="employee">Фамилия И.О.</span></span></span></div></div></div><div class="note component"><div class="head"><div class="time">08:15</div></div><div class="content"><div class="head"><span class="marker"></span><span class="title">В компоненте<span class="component mlr">.Component_A</span>появилась новая версия<span class="version mlr">4.32</span><span class="by"><span class="employee">Фамилия И.О.</span></span></span></div></div></div></div></div></div><div class="footer"><div class="btn">ещё 10</div><div class="btn">ещё 30</div><div class="btn">ещё 1 день</div><div class="btn">ещё 3 дня</div><div class="btn">ещё неделя</div></div></div><script>function fixPositioning(elem) {
    if ($(window).scrollTop() > 59) {
        if(elem.css("top") != 0) {
            //elem.addClass("fix");
            elem.css('top', 0);
        }
    } else {
        elem.css('top', 59 - $(window).scrollTop());
        //elem.removeClass("fix");
    }
};



!function() {
    var notificationsIconElem = $('#globalNotifications');
    var popupBlock = $('.notifications');
    var fixPopupBlock = fixPositioning.bind(null, popupBlock);

    notificationsIconElem.click(function () {
        //if(notificationsIconElem.hasClass('active')) return;
        notificationsIconElem.addClass('active');

        $(window).scroll(fixPopupBlock);

        popupBlock.css('display', 'block');
        setTimeout(function(){
            popupBlock.addClass('active');
            //popupBlock.addClass('noAnimation');
            fixPopupBlock();
            $(document.body).click(
                    function func(event){
                        var elem = event.target;
                        if(popupBlock.has(elem).length<=0){
                            popupBlock.removeClass('active')
                            setTimeout(function(){popupBlock.css('display', '')}, 200);
                            notificationsIconElem.removeClass('active')
                            $(document.body).off('click', func);
                            $(window).off('scroll', fixPopupBlock);
                        }
                    }
            );
        }, 200);

    });
}();</script><div id="taskTimeFixer" class="popup_backgroundBlock"><div class="wrapper"><div class="popupBlock"><div class="popup caseTimeFix"><div class="head"><div class="caseType"><span class="title">Фиксация времени</span><span class="content">Freq (Запрос на доработку)<span class="caseId">1234566</span></span></div><div class="status"><span class="title">Статус</span><span class="content">active 13%</span></div></div><div class="body"><div class="caseReportElection"><div class="dateStart"><div class="header">Время начала</div><div class="content">Сегодня, 08:20</div></div><div class="dateStop"><div class="header">Время фиксации</div><div class="content">Сегодня, 13:20</div></div><div class="timeSpent"><div class="header">Время затрачено</div><div class="content"><span class="time">05 : 00</span><span class="icon-edit"></span></div></div><div class="timeLeft"><div class="header">Время осталось</div><div class="content"><span class="time">13 : 00</span><span class="icon-edit"></span></div></div><div class="timeEstimate"><div class="header">Оценка</div><div class="content"><span class="time">28 : 00</span><span class="icon-edit"></span></div></div></div><div class="commitComponents"><div class="title">Укажите над какими компонентами вы работали:<!--.rightSide--><!--    .button Редактировать компоненты--><!--    .button.greenColor Показать все--></div><div class="components"><table class="wrapper"><tr><td class="component"><div class="checkbox"></div><div class="componentName">LoooongComponentName_A</div></td><td class="component"><div class="checkbox"></div><div class="componentName">Component_B</div></td><td class="component"><div class="checkbox"></div><div class="componentName">Component_C</div></td><td class="component"><div class="checkbox"></div><div class="componentName">Component_D</div></td></tr><tr><td class="component"><div class="checkbox"></div><div class="componentName">Component_A</div></td><td class="component"><div class="checkbox"></div><div class="componentName">Component_B</div></td></tr></table></div></div><div class="commentBlock"><!--.icon-edit--><textarea type="text" placeholder="Комментарий" class="inputField"></textarea></div></div><div class="footer"><div class="buttons"><div class="leftSide"><div id="replayTimer" class="button grayC">Продолжить</div></div><div class="button blueC">Сбросить</div><div class="button greenC">Зафиксировать</div><!--.button.sticky.additional и выбрать другую задачу-->
</div></div></div></div></div></div><script>$('#stopCurrentTask').click(function(){
    var popupBlock = $('#taskTimeFixer');
    popupBlock.css('display', 'block');
    setTimeout(function(){
        popupBlock.addClass('active')
    }, 200);

    $('#replayTimer').click(
        function func(event){
            popupBlock.removeClass('active')
            setTimeout(function(){popupBlock.css('display', '')}, 200);
            $(event.target).off('click', func);
        }
    );

});

</script><div class="popupBlack"><div class="user"><span class="name">Бондаренко Артём Александрович</span><div class="rightSide"><span class="icon-settings"></span></div></div><div class="buttons"><div class="button blueC">Мой пасспорт</div><div class="button grayC">Мои отсутствия</div><div class="button empty sticky grayC"><span class="icon-plus"></span></div><div class="button grayC">Мои подписки</div><div class="button grayC">Моя активность</div></div><div class="work"><div class="case"><div class="animationWrapper"><div class="timerButton active"><span class="icon-right"></span></div><div class="timeSpent"><div class="hours">02</div><div class="timeDelimiter">:</div><div class="minutes">17</div></div></div><div class="caseType">Bug</div><div class="caseId">32158</div><div class="caseCriticality critical"></div><div class="description">Здесь будет описание описание описание описание описание описание описание</div></div></div><div class="bottomButtons"><div class="button">Завершить рабочий период</div></div></div><script>!function() {
    var userIconElem = $('#userProfile');
    var popupBlock = $('.popupBlack');

    userIconElem.click(function () {
        if(userIconElem.hasClass('active')) return;
        userIconElem.addClass('active');
        popupBlock.css('display', 'block');
        setTimeout(function(){
            popupBlock.addClass('active');
            $(document.body).click(
                function func(event){
                    var elem = event.target;
                    if(popupBlock.has(elem).length<=0){
                        popupBlock.removeClass('active')
                        setTimeout(function(){popupBlock.css('display', '')}, 200);
                        userIconElem.removeClass('active')
                        $(document.body).off('click', func);
                    }
                }
            );
        }, 200);

    });
}();

</script></div></body></html>