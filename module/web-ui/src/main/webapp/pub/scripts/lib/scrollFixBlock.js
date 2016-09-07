/**
 * Created by bondarenko on 16.06.16.
 */

function scrollFixBlock(sec, topOffset, bottomOffset) {
    var secHeight = sec.outerHeight();
    var secOffset = sec.offset();
    var lastPos = 0;
    var flag = false;
    var flag2 = false;

    $(document).scroll(function (e) {
        e = $(e.currentTarget);
        var scrollTop = e.scrollTop();
        var bodyHeight = $(window).height();

        if(secHeight<bodyHeight){
            if (scrollTop >= topOffset) {
                sec.addClass('fix').css('top', 0)
            } else {
                sec.css('top', '').removeClass('fix');
            }
        }else {

            if (scrollTop > topOffset && scrollTop > lastPos) { // проверка на 78, когда нужно ограничить вверху
                // движение вниз

                var documentHeight = $(document).height();

                if (documentHeight - (scrollTop + bodyHeight) <= bottomOffset) {
                    if (!flag2) {
                        sec.removeClass('fix').removeClass('b0');
                        //sec.css('top', scrollTop - (secHeight - bodyHeight));
                        //sec.addClass('fix').css('bottom', bottomOffset);
                        //sec.css('top', '')
                        //if(documentHeight - (scrollTop + secHeight) < bodyHeight)
                        //    sec.css('top', documentHeight);
                        //flag2 = true;

                        sec.css('top', scrollTop - (secHeight - bodyHeight) - (bottomOffset - (documentHeight - (scrollTop + bodyHeight))));

                        flag = true;
                    }
                } else {

                    if (flag) {
                        flag = false;
                        flag2 = false;
                        secOffset.top = scrollTop;
                        if (scrollTop + secHeight < documentHeight)
                            sec.css('top', scrollTop);
                        sec.removeClass('fix');
                    }
                    else if ((scrollTop + bodyHeight) > (secOffset.top + secHeight)) {
                        sec.css('top', '').addClass('fix b0');
                    }
                }
            } else {
                // движение вверх
                if (scrollTop <= topOffset) {
                    sec.css('top', '').removeClass('fix');
                } else {
                    if (!flag) {
                        sec.removeClass('fix').removeClass('b0');
                        sec.css('top', scrollTop - (secHeight - bodyHeight));
                        flag = true;
                    } else if (scrollTop < parseInt(sec.css('top'))) {
                        sec.css('top', '0').addClass('fix');
                        flag2 = false
                    }
                }
            }
            lastPos = scrollTop
        }
    })
}