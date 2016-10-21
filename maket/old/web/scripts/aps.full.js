/*
* initialize
*/
$(document).ready (function () {
  if (window.name == null || window.name.length == 0 || !window.name.match("^APS_[0-9]+"))
  {
    window.name = "APS_" + new Date ().getTime ();
  }

  $.ajaxSetup({
	headers : { 
		"x-webagent-frameid" : window.name,
		'x-webagent-timezone' : new Date().getTimezoneOffset()
	}
  });

  var ua_screen_res = (screen.width+'x'+screen.height);
  var ua_screen_setup = false;

  if (aps.getCookie('aps_ua_screen') != ua_screen_res) {
	aps.setCookie('aps_ua_screen', ua_screen_res, { expires : 30 });
	ua_screen_setup = true;
  }

  if (aps.emulActionExecute())
	return false;

// if cookie enabled, reload page
  if (ua_screen_setup && aps.getCookie('aps_ua_screen') == ua_screen_res) {
      top.location.reload(true);
      return;
  }
 
  if ($('#ajax_indicator').size() > 0) {
	aps.ajaxIndicator = $('#ajax_indicator');
	aps.ajaxIndicator.data('ajax_hint_def', aps.ajaxIndicator.find('.ajax_hint').html());
    $(document)
    .ajaxStart (aps.requestAjaxIndicator)
    .ajaxStop (aps.hideAjaxIndicator);
  }

  aps.ajaxInit (); 

  aps.processWidgets(document);
  aps.OnPageLoad ();
});


(function( aps, $, undefined ) {
  aps.version = "3.5";
  aps.ajaxIndHD = null;
  aps.ajaxIndicator = null;

  aps.requestAjaxIndicator = function () {
	if (aps.ajaxIndHD == null) {
	  aps.ajaxIndHD = setTimeout(aps.showAjaxIndicator, 500);
    }
  }

  aps.showAjaxIndicator = function () {
	aps.ajaxIndicator.find('.ajax_hint').html(aps.ajaxIndicator.data(aps.ajaxIndicator.data('ajax_hint') ? 'ajax_hint' : 'ajax_hint_def'));

	aps.ajaxIndicator.show ();
	if (aps.ajaxIndicator.attr('fullscreen') == 'yes') {
		aps.ajaxIndicator.height($(document).height());
		aps.ajaxIndicator.width ($(document).width());
	}
  }

  aps.hideAjaxIndicator = function () {
	aps.ajaxIndicator.fadeOut(500);
	aps.ajaxIndicator.removeData ('ajax_hint');
	if (aps.ajaxIndHD) {
      clearTimeout(aps.ajaxIndHD);
    }

    aps.ajaxIndHD = null;
  }


  aps.getAppContextPath = function () {
      var path = window.location.pathname;
      return path.substring (0, path.indexOf("/", 1));	  	
  }

  aps.processWidgets = function (root) {
    $("[apsWidget]", root).each (function () {
	  var obj = $(this);
	  if (obj.prop('_aps_inst'))
		return true;

	  var w = apsWidgets[obj.attr('apsWidget')];
	  if (w != null) {
		w.init (this);
	    obj.prop("_aps_inst", true);		
	  }
    }); 

    $("[hotKey]").each (function() { 
		hotKeysMap [$(this).attr('hotKey')]	= this;
    });

    $('button').attr('type','button');

    aps.processUI_Messages(root, true);
  }

  aps.addWidget = function (widget, def) {
    apsWidgets[widget] = def;
  }

  aps.MessageBox = function (msg) {
    alert (msg);
  }


  aps.joinNodesAsText = function (exp, remove) {
    var text = '';
    exp.each (function () {
    	text += $.trim($(this).html());

	if(remove) {  $(this).remove(); }
    });

    return text;
  };

  aps.processUI_Messages = function (node, remove) {
    var messages = aps.joinNodesAsText ( $(node).find("script[type=\"text/aps-ui-message\"]"), remove );
    if (messages.length > 0) 
      aps.MessageBox (messages);
  }

  aps.addImageList = function (ar) {
	for (var k in ar) {
      var img = new Image();
      img.src = ar[k];
	  cachedImages[k] = img;
    }
  };

  aps.removeItem = function (array, item){
    for(var i in array){
        if(array[i]==item){
            array.splice(i,1);
            break;
        }
    }
  }

  aps.getEventSource = function (e) {
    if (aps.ua.isIE() || aps.ua.isOpera())
      return e.srcElement;
    else
      return e.target;
  }

  aps.regDefValue = function (name, value) {
	  DefValues.push ( { name : name, value : value } );
  };
 
/* return true, if attribute <attrName> in object <obj> is null, or length = 0 */
  aps.isEmptyAttr = function (obj, attrName) {
    var v = obj.getAttribute (attrName);
    return (v == null || v.length == 0);
  }

  aps.frmRegTarget = function (id, target, action, winAttr) {
	targetOptions[id] = { 
		target : target,
		action : action,
		windowAttr : winAttr
	};
  }

  aps.setHotKey = function (hc, obj_id) { hotKeysMap [hc] = aps.ElementById (obj_id); }

  aps.setFocused = function (obj_id) { $(aps.ElementById (obj_id)).focus(); }

  aps.regValidation = function (elm) {
    var id_arr = elm.split (';');
    for (i = 0; i < id_arr.length; i++)
      validElmArray.push (id_arr[i]);
  }
   
  aps.ElementById = function (id) { return document.getElementById (id); }
  
  aps.AfterLoad = function () {
    for (var i=0; i<arrScripts.length; i++) {
      eval (arrScripts [i])
    }
  }

  aps.ExecAfterLoad = function (ScriptText) { arrScripts.push (ScriptText); }

  aps.emulActionExecute = function () {
      var q = window.location.search;

	  if (q == null || q.indexOf('action_ref') < 0) {
		return false;
      }

	  var hid = '';
      var p = q.substring(1).split('&');
      for (var k in p) {
		var opt = p[k].split('=');
		if (opt.length > 0) {
			hid += '<input type="hidden" name="' + opt[0] + '" value="' +
					(opt[1] ? decodeURIComponent(opt[1]):'') + '"/>';
		}
      }

	  $("#x_open_link_form").remove();
      $(document.body).append ("<FORM id=\"x_open_link_form\"" + " action=\"" + window.location.pathname + "\""
				+ " METHOD=\"POST\"><INPUT type=\"hidden\" name=\"frame_id\" value=\"" + window.name + "\">"
				+ hid
				+ "</FORM>");

      $("#x_open_link_form").submit();
	  return true;
  }

  aps.linkOpenEmul = function(event)
  {
    var evtSource = aps.getEventSource (event);
 
    if (!evtSource) 
	  return true;

    if (evtSource.tagName == 'A') 
    {
	/** if it will be a new frame then skip emulation */
      if (event.shiftKey || event.ctrlKey || (evtSource.target != null && evtSource.target.length > 0))
        return true;

	  var frameTarget = evtSource.target != null && evtSource.target.length > 0 ? evtSource.target : event.shiftKey ? '_blank' : null;

      var path = window.location.pathname;
      var context = path.substring (0, path.indexOf("/", 1));
      var thisUrn = window.location.protocol + "//" + window.location.host + context;    
	
      if (evtSource.href.indexOf (thisUrn) != 0)	
  	    return true;

      aps.StopEventBubble (event, true);

	  $("#x_open_link_form").remove();
      $(document.body).append ("<FORM id=\"x_open_link_form\"" + " action=\"" + evtSource.href + "\""
				+ (frameTarget != null ? " target=\""+frameTarget+"\"" : '')
				+ " METHOD=\"POST\"><INPUT type=\"hidden\" name=\"frame_id\" value=\"" + window.name + "\">"
				+ "<INPUT type=\"hidden\" name=\"action_ref\" value=\"navigate\">"
				+ "<INPUT type=\"hidden\" name=\"action_properties\" value=\"" + evtSource.href + "\">"
				+ "</FORM>");

      $("#x_open_link_form").submit();
      return false;
    }

    return true;
  }

  aps.execSingleAction = function(event, ref, prop)
  {
    aps.StopEventBubble (event, true);

    $("#x_open_link_form").remove();
    $(document.body).append ("<FORM id=\"x_open_link_form\" METHOD=\"POST\">"
				+ "<INPUT type=\"hidden\" name=\"frame_id\" value=\"" + window.name + "\">"
				+ "<INPUT type=\"hidden\" name=\"action_ref\" value=\"" + ref + "\">"
				+ "<INPUT type=\"hidden\" name=\"action_properties\" value=\"" + prop + "\"></FORM>");

    $("#x_open_link_form").submit();
    
    return false;
  }

  aps.OnPageLoad = function ()
  {
	$(document.body).keyup(aps.OnBodyKeyPress);

    //$('a').click(aps.linkOpenEmul);

    aps.AfterLoad ();
  }

/* if param p will be sent to server return true, else return false */
  aps.isParamInRequest = function (p)
  {
    if (p == null)
      return false;

    if (new String (p.type).toLowerCase() == 'checkbox' && !p.checked)
      return false;

    if (p.tagName == 'SELECT' && p.selectedIndex < 0)
      return false;
  
    return true;
  }


/* Create hidden input fields set (default state values) */
  aps.createDefaultParams = function (form)
  {
    for (var i = 0; i < DefValues.length; i++)
    {
      var opt = DefValues [i];
      var inp = form.elements[opt.name];

      if (inp == null)
        continue;

      if ($(inp).is('select')) {
	if ($(inp).val() == null)
	  aps.CreateParameter (null, opt.name, opt.value, form, 'aps_defvalue'+i);
      }
      else
      if (inp.length == null)
      {
        if (!aps.isParamInRequest (inp))
        {
          aps.CreateParameter (null, opt.name, opt.value, form, 'aps_defvalue'+i);
        }
      }
      else
      {
        for (var idx = 0; idx < inp.length; idx++)
          if (aps.isParamInRequest (inp[idx])) 
          {
            break;
          }
      
        if (idx == inp.length)
          aps.CreateParameter (null, opt.name, opt.value, form, 'aps_defvalue'+i);
      }
    }
  }


  aps.OnFormSubmit = function (form, lForceSubmit, lIgnoreError)
  {
    ignoreErrors = lIgnoreError != null ? lIgnoreError : false;

    if (form == null)
      return false;

    if (form.elements['aps_time_zone'] != null) {
       form.elements['aps_time_zone'].value = new Date().getTimezoneOffset();
    }

    if ((lForceSubmit == null || !lForceSubmit) && 
	  form.elements ['action_ref'] == null) {
      return false;
    }

    lIgnoreError = (lIgnoreError != null && lIgnoreError);


/*  if (!formSubmitted)
  {
  */
    AllOk = false;

    for (var i=0; i<arrOnSubmitScripts.length; i++)
    {
      var er = typeof(arrOnSubmitScripts [i]) == 'function' ? arrOnSubmitScripts [i]() : eval (arrOnSubmitScripts [i]);
      if (!er && !lIgnoreError)
        return false;
    }


    var widgetErrors = { submit : [], validation : [] };
    $('[apsWidget]', form).each (function() {
	var w = apsWidgets[$(this).attr('apsWidget')];
	if (typeof(w.submit) ==  'function' && !w.submit(this))
	    widgetErrors.submit.push({node: this, widget: w});

	if (typeof(w.validate) == 'function') {
	    var e = { node : this, widget : w, err : w.validate(this) };
	    if (e.err)
		widgetErrors.validation.push(e);
	}
    });

    if (!lIgnoreError && (widgetErrors.submit.length > 0 || widgetErrors.validation.length > 0))
	return false;

    if (!aps.ValidateFormElements (form, lIgnoreError) && !lIgnoreError)
      return false;

    aps.createDefaultParams (form);

    AllOk = true;

/*    formSubmitted = true; */

    return AllOk;
/*  } */

  //  return true;
  }


  aps.FindForm = function (src) {
    if (src == null) return null;
    var parent = src.parentNode;

    while (parent != null && parent.tagName != "FORM") {
      parent = parent.parentNode;
    }

    return parent;
  }


  aps.SetParameter = function (frm, p_name, p_value)
  {
    if (frm == null)
    {
      return false;
    }

    var p = frm.elements [p_name];

    if (p == null)
      return false;

    if (p.length == null)
      p.value = p_value;
    else
    {
      for (i = 0; i < p.length; i++)
        p[i].value = p_value;
    }

    return true;
  }


  aps.IsParameterExists = function (frm, p_name)  {
    return (frm != null && frm.elements[p_name] != null);
  }

  aps.DeleteParameter = function (frm, p_name)
  {
    if (frm == null)
      return false;
    
    return $(frm.elements [p_name]).each(function() { $(this).remove() }).length > 0;
  }

  aps.CreateParameter = function (src, p_name, p_value, frm, id)
  {
    var form = frm;
    var parID = id;

    if (form == null)
      form = aps.FindForm (src);

    if (form == null)
      return false;

    if (parID == null)
      parID = p_name;

    return $("<INPUT TYPE=\"hidden\" NAME=\"" + p_name + "\" ID=\"" + parID + "\" />").appendTo(form).val(p_value).get(0);
  }

  aps.StopEventBubble = function (evt, full)
  {
//    if (IE) {
      evt.cancelBubble = true;

	  if (typeof evt.stopPropagation == 'function')
		 evt.stopPropagation();

	  if (full) {
        evt.returnValue = false;
	    if (typeof evt.preventDefault == 'function')
          evt.preventDefault();
      }
      //evt.cancelBubble = true;
  }


  aps.CheckSubmit = function (evt, form)
  {
    if (!aps.linkOpenEmul (evt))
      return true;	
     
    var t = aps.getEventSource(evt);
	if (t != null && t.tagName=='A')
	  return true;
    
    aps.StopEventBubble (evt);

    if (aps.IsParameterExists (form, 'action_ref'))
    {
      var IVE_Mode = form.getAttribute ('ive');
      var RQI_Mode = form.getAttribute ('rqi');

      if (RQI_Mode == 'true')
        form.action = window.location.pathname;
      else
        form.action = "";


      if (IVE_Mode == 'true')
        aps.CreateParameter (form, 'action_mode', 'ive=true', form);


      var lSubmitOK = aps.SubmitForm (form, (IVE_Mode == 'true'));

      if(!lSubmitOK)
      {
        aps.cleanupFormData (form, true);
      }

      return lSubmitOK;
    }
    else
    {
      var item = null;

      while ((item = aps.NCE_List.pop ()) != null)
        item.close ();
    }

    return false;
  }


  aps.ExecuteAction = function (evt, src, action_ref, action_props)
  {
    var ConfirmMsg = src.getAttribute ('conf_msg');

    if (ConfirmMsg != null && !window.confirm (ConfirmMsg))
      return false;

    form = aps.FindForm (src);

    if (form != null)
    {
      var IVE_Mode = src.getAttribute ('exm_ive');
      var RQI_Mode = src.getAttribute ('exm_rqi');
      var targetId = src.getAttribute ('targ_id');
      var ajax     = src.getAttribute ('jx');
      var jxrel    = src.getAttribute ('jxrel');
      var jxcb     = src.getAttribute ('jxcb');

	  form.eventSource = src;
      form.setAttribute ("ive", IVE_Mode);
      form.setAttribute ("rqi", RQI_Mode);
      form.setAttribute ("targetId", targetId);
      form.setAttribute ("ajxRequest", (ajax == "1") ? 'true' : 'false');
      form.setAttribute ("ajxRequestReload", jxrel);
	  
	  if ($(src).attr('jxTimeout') >= 0) {
		form.setAttribute('jxTimeout',$(src).attr('jxTimeout'));
      }
	  else {
		form.setAttribute('jxTimeout', '');
	  }

	  if (aps.ajaxIndicator) {
		aps.ajaxIndicator.data('ajax_hint', src.getAttribute('ajaxHint'));
	  }

      if (jxcb != null)
	    form.setAttribute ("ajxCallback", jxcb);

      aps.CreateParameter (src, 'action_ref', action_ref, form);
  
      if (action_props != null)
        aps.CreateParameter (src, 'action_properties', action_props, form);
    }
  }

  aps.CommitChange = function (src)
  {
    var conf_msg = src.getAttribute ('conf_msg');

    if (conf_msg != null && conf_msg.length > 0)
    {
      if (!confirm (conf_msg))
      {
        src.value = src.getAttribute ("ivalue");
        return false;
      }
    }

	var form = aps.FindForm (src);
	if (form != null)
		form.eventSource = src;

    aps.SubmitForm (form, true);
  }

  aps.SubmitFormBy = function (src) { 
	var form = aps.FindForm (src);
	if (form != null)
		form.eventSource = src;
	aps.SubmitForm (form, false); 
  }

  aps.SubmitForm = function (f, lIgnoreError) {
    ignoreErrors = lIgnoreError != null ? lIgnoreError : false;
    var form = f;

    if (form != null)
    {
      var success = true;
     
      try 
      {
/*    if (!formSubmitted)
    {*/
        if (!aps.OnFormSubmit (form, true, lIgnoreError))
        {
           form.setAttribute ('targetId', '');
           return false;
        }

        var targetId = form.getAttribute ('targetId');
        var tOpt = targetOptions[targetId];

        if (tOpt != null)
        {
          form.target = tOpt.target;
          form.action = tOpt.action;
        }
        else
        {
       	  var RQI_Mode = form.getAttribute ('rqi');

          if (RQI_Mode != 'true')
            form.action = '';

          form.target = '';
        }

       /*formSubmitted = tOpt == null; */

        aps.CreateParameter (form, 'frame_id', window.name, form);
		//$('input[name=frame_id]', form).val(window.name);

        var ajxRequest = form.getAttribute ('ajxRequest');

        if ((ajxRequest == null) || (ajxRequest != 'true'))
    	   form.submit ();
        else
    	   aps.ajxSubmitForm(form);

        success = true;    
      }
      catch (exc) 
      {
		//alert (exc);
        success = false;
      }
     
      aps.cleanupFormData (form);
     
      return success;
    /* } */
    }

    return false;
  }

  aps.cleanupFormData = function (form, lJustAction) {
     form.setAttribute ('targetId', '');
     form.setAttribute ('action', '');
     form.setAttribute ('ajxRequest', '');
     form.setAttribute ('ajxReload', '');

     if (lJustAction == null)
       aps.DeleteParameter (form, 'frame_id');
       aps.DeleteParameter (form, 'action_ref');
       aps.DeleteParameter (form, 'action_properties');
       aps.DeleteParameter (form, 'action_mode');
  }


  aps.ExecOnFormSubmit = function (s) {
    arrOnSubmitScripts.push (s);
  }

  aps.ValidateFormElements = function (form, lIgnoreError) {
    var elm = form.elements;
    var counter = 0;

    for (i = 0; i < elm.length; i++)
    {
      if (elm [i] == null)
        continue;

      if (elm [i].tagName == 'INPUT')
      {
        counter++;
        var InputFld = elm [i];

        if (InputFld.disabled)
          continue;

        var Mask = InputFld.getAttribute ('vMask');

        if (Mask == null || Mask.length == 0)
          continue;

        var MaskObj = new RegExp (Mask);

        if (!MaskObj.test (InputFld.value))
        {
          if (!lIgnoreError) 
          {
            InputFld.focus ();
            InputFld.select ();

            var Msg = InputFld.getAttribute ('vMsg');
            if (Msg != null && Msg.length > 0)
              aps.MessageBox (Msg);

            return false;
          }
          else
          {
            InputFld.name='';
          }
        }
      }
    }

    return true;
  }


  aps.OnBodyKeyPress = function (evt)
  {
    var src = evt.target;

    if (evt.keyCode == 27)
    {
      aps.mplayer.stop();

      if (src != null && src.tagName != null)
      {
        var tagName  = src.tagName.toLowerCase ();

        if (tagName == 'input' || tagName == 'select' || tagName == 'textarea')
        {
          src.blur ();
        
	      if (tagName == 'input')
	        src.value = src.getAttribute('value');

           return false;
        }
      }
    }


    if (evt.keyCode == 13)
    {
      if (src != null && src.tagName != null)
      {
        var tagName  = src.tagName.toLowerCase ();

        if (tagName == 'select' || tagName == 'textarea')
        {
          src.blur ();      
          return true;
        }
      }
    }


    if (hotKeysMap [evt.keyCode] != null)
    {
      if (IE)
      {
        hotKeysMap [evt.keyCode].click ();
      }
      else
      {
        var element = hotKeysMap [evt.keyCode];
        element.onclick (evt);

        var _form = aps.FindForm (element);
        if (_form != null)
          aps.CheckSubmit (evt, _form);

        return false;
      }
    }
  }


/*
*
* Dimensions & position functions
*
*/

/* construct new Point, define x and y coord properties */
  aps.Point = function (x, y)
  {
    this.x = x != null ? x : 0;
    this.y = y != null ? y : 0;
    
    this.toString = function () {  return "(" + this.x + ", " + this.y + ")"; }
  }

/* rect */
  aps.Rect = function (point, width, height)
  {
    this.origin = point;
    this.width = width;
    this.height = height;
    this.opposite = new aps.Point (point.x + width, point.y + height);

    this.toString = function () {
      return this.origin.toString () + " - " + this.opposite.toString ();
    };

  };

/* return true, if rect1 and rect2 are intersected */
  aps.Rect.intersect = function (rect1, rect2)
  {
    p1 = rect1.origin;
    _p1 = rect1.opposite;

    p2 = rect2.origin;
    _p2 = rect2.opposite;

    if (p1.x >= _p2.x || 
      _p1.x <= p2.x ||
      p1.y >= _p2.y ||
      _p1.y <= p2.y)
     return false;

    return true;
  };


/*
* convert style pixels expression to integer
*/
/*  aps.StylePix2Int = function (strPX) {
    return new Number (strPX.substring (0, strPX.length - 2));
  }*/

  aps.getAbsPointInWindow = function (elm)
  {
    var result = $(elm).offset();
    return new aps.Point(result.left, result.top);
  }

/*
* calculate absolute point for element p

  aps.AbsPoint = function (p)
  {
    if (p == null)
      return new aps.Point (0,0);

    if (p.style.position == "absolute")
      return new aps.Point (0,0);

    var result = $(elm).offset();
    return  new aps.Point(result.left, result.top);
  }
*/

/* return all overlapped input elements for <element> object */
  aps.getOverlappedInputList = function (element) 
  {
    var point = aps.getAbsPointInWindow (element);
    var elmRect = new aps.Rect (point, element.offsetWidth, element.offsetHeight);

    var inpTagList = ["select", "textarea"];

    var result = [];

    for (var i = inpTagList.length; i > 0;)
    {
      var list = document.getElementsByTagName(inpTagList[--i]);

      for (var l = list.length; l > 0;)
      {
        var inp = list[--l];
        var inpPoint = aps.getAbsPointInWindow (inp);

        var inpRect = new aps.Rect (inpPoint, inp.offsetWidth, inp.offsetHeight);

        if (aps.Rect.intersect (elmRect, inpRect) && !aps.ContainsElement (element, inp))
           result [result.length] = inp;
      }
    }

    return result;
  }



/*  aps.AbsTop = function (p)
  {
    if (p == null)
      return 0;

    if (p.currentStyle != null && p.currentStyle.position == "absolute")
      return 0;

    if (p.currentStyle != null && p.currentStyle.position == "relative")
      return 0;

    if (p.offsetParent == document.body)
      return p.offsetTop
    else
      return p.offsetTop + aps.AbsTop (p.offsetParent)
  }

  aps.AbsLeft = function (p)
  {
    if (p == null)
      return 0;

    if (p.currentStyle != null && p.currentStyle.position == "absolute")
      return 0;

    if (p.currentStyle != null && p.currentStyle.position == "relative")
      return 0;

    var ofsParent = p.offsetParent;

    if (ofsParent == document.body)
      return p.offsetLeft;
    else
      return p.offsetLeft + aps.AbsLeft (ofsParent);

    return 0;
  }
*/

/*
* return true if <elm> contains <obj> directly or in sub-childs
*/
  aps.ContainsElement = function (elm, obj)
  {
    if (elm == null || obj == null)
      return false;

    if (elm == obj)
      return true;

    return $.contains(elm, obj);
  }

/** replace it with CSS */
  aps.HighlightImage = function (img, lHighlight) {
    img.src = eval (img.getAttribute (lHighlight ? "hlImg" : "nmImg")).src;
  }

/*
*
* Common controls
*
*/

/*
* CheckBox
*/
  aps.ChbFor = function (element, name)
  {
    var Form = aps.FindForm (element);

    if (Form == null)
      return false;

    var ThisVariant = element.getAttribute ('chbg');
    var lVariant = (ThisVariant != null && ThisVariant.length > 0);

    var P = Form.elements [name];

    if (P == null)
      return false;

    $(P).each (function () {
      var x = this;
      var strId = $(this).attr('id');

      if (strId.indexOf ('_ho') > 0) {
        strId = strId.substring (0, strId.length - 3);
        x = aps.ElementById (strId);
      }	

      if (lVariant && ThisVariant != x.getAttribute ('chbg'))
        return;

      aps.ChbCheck (x, element.checked, false);
    });

    if (element.getAttribute ('commit') == '1')
      return aps.CommitChange (element);

    return true;
  }


  aps.ChbCheckOne = function (element)
  {
    var Form = aps.FindForm (element);
    if (Form == null)
      return false;

    var ThisGroup = element.getAttribute ('chbg');

    var lGroup = (ThisGroup != null && ThisGroup.length > 0);

    if (!lGroup)
      return false;

    var P = Form.elements [element.name];

    if (P == null)
      return false;

    $(P).each (function () {
      if ($(this).attr ('chbg') == ThisGroup)
      {
        if (this == element)
        {
          $(this).val($(this).attr (this.checked ? 'chv' : 'uchv'));
        }
        else
        {
          this.checked='';
          $(this).val ($(this).attr('uchv'));
        }
      }
    });

    if (element.getAttribute ('commit') == '1')
      return aps.CommitChange (element);
  }

  aps.ChbCheck = function (src, checked, lCommit)
  {
    if (src.type != 'checkbox')
      return false;

    var cnfgMsg = src.getAttribute ('cf_msg'); 

    if (cnfgMsg != null && cnfgMsg.length > 0 && !confirm (cnfgMsg))
    {
      src.checked = !src.checked;	
      return false;
    }


    if (checked != null)
      src.checked = checked;

    var stValue = src.checked ? src.getAttribute ('chv') : src.getAttribute ('uchv');
    src.value = stValue; 
    var prm = src.getAttribute ('prm');
    var inp_name = src.name;
  
    if (prm != null && prm.length > 0)
    {
       var h_obj = document.getElementById (src.id + '_ho');
       inp_name = h_obj.name;
       h_obj.value = stValue;
    }

    if ((lCommit == null || lCommit))
    {
      if (src.getAttribute ('commit') == '1')
        return aps.CommitChange (src);
      else
       if (src.getAttribute ('jx') == '1') {
    	  var rl = src.getAttribute ('jxrel');
    	  aps.ajxSubmitInputValue (inp_name, stValue, (rl != null && rl.length > 0 ? rl : null));
    	  return true;
       }  
    }
  }


/*
* Common Input Controls Interface
*/

/*
* evt - event
* element - input control
* args - call parameters, ex: 'len,5,submit;' - submit if length of text == 5
*/
  aps.cmiExec = function (evt, element, args)
  {
    if (args == null || args.length == 0)
      return false;

    var cmdArr = args.split (";");

    for (var i = 0; i < cmdArr.length; i++)
    {
      var cmdArg = cmdArr[i].split(',');

      switch (cmdArg[0])
      {
        case 'len': 
          if (element.value.length == cmdArg [1])
            return aps.SubmitFormBy (element);
        break;
      }
    }

    return false;
  }



/*
*
* Timers and triggers
*
*/

/* last trigger id (unique id generator) */
var trigger_last_id = 0;

/* created triggers registry */
var reg_triggers = [];

/* stop this time trigger */
Trigger.prototype.stop = function ()
{
  if (this.timerId != null && this.timerId > 0)
  {
    if (this.run_once)
      clearTimeout (this.timerId);
    else
      clearInterval (this.timerId);
  }
}


/* start this time trigger */
Trigger.prototype.start = function ()
{
  this.stop ();
  var self = this;
  this.timerId = this.run_once ? 
     setTimeout (function () { self.onTimeOut(); }, this.timeout) 
	: setInterval (function () { self.onTimeOut (); }, this.timeout);
};

/*
* reset this trigger - equals start
* calling this method resets timer and begin timeout counter at 0
*/
Trigger.prototype.reset = function ()
{
  this.start ();
}

/*
* new trigger constructor
* Creates a new trigger, with <onTimeOut> expression, executed after <timeout> ms,
* if no reset function called
*/ 
function Trigger (onTimeOut, timeout, runonce)
{
  this.id = trigger_last_id++;
  this.onTimeOut = onTimeOut;
  this.timeout = timeout;
  this.run_once = (runonce == null || runonce);
  reg_triggers [this.id] = this;
}

  aps.Trigger = function (onTimeOut, timeout, runonce) {
     return new Trigger(onTimeOut, timeout, runonce);
  }

/* this function returns trigger with <id> identificator, or null if not found */
  aps.findTrigger = function (id)
  {
    return reg_triggers [id];
  }

/*
* Sound Player
*/
function MPlayer ()
{}

MPlayer.prototype.checkInit = function ()
{
    if (this.inst != null) 
      return;

    var tmp = document.createElement ("SPAN");
    tmp.id = "SndPlayer";
    tmp.style.margin = "0px";
    tmp.style.padding = "0px";
    tmp.style.position = "absolute";

    document.body.appendChild (tmp);
    this.inst = tmp;
}

MPlayer.prototype.play = function (src)
{
  return this.playURL (src.getAttribute ('mres'), src.getAttribute ('mtype'));
}

MPlayer.prototype.playURL = function (url, mimeType)
{
  if (url == null || url.length == 0)
	return false;

  if (mimeType == null || mimeType.length == 0)
    mimeType = "audio/wav";

  this.checkInit ();
  this.stop ();


 var html = "<embed src=\"" + url + "\" type=\"" + mimeType + "\" autostart=\"true\" play_loop=\"1\" loop=\"false\" width=\"1\" height=\"1\">";
 
 if (!IE || mimeType != "audio/wav") {
	 html = "<audio autoplay=\"1\" id=\"audio_player\">" +
         "<source src=\"" + url + "\" type=\"" + mimeType + "\">" + html + "</audio>";
 }

  $(this.inst).html(html);
  return true;
}


MPlayer.prototype.stop = function ()
{
  this.checkInit ();
  this.inst.innerHTML="";
  return true;    
}


/** utility **/
  aps.setCookie = function (c_name,value,options) {
    if (options === undefined) {
	  options = {};
    }

    var expires = null;
	if (value === null) {                                                                                                                                                             
      options.expires = -1;                                                                                                                                                         
    }   

    if (typeof options.expires === 'number') {
	  expires = new Date();
      expires.setDate(expires.getDate() + options.expires);
    }

    document.cookie = [
				encodeURIComponent(c_name), '=', encodeURIComponent(value),
				expires ? '; expires=' + expires.toUTCString() : '',
				options.path    ? '; path=' + options.path : '',
				options.domain  ? '; domain=' + options.domain : '',
				options.secure  ? '; secure' : ''
			].join('');
  }

  aps.getCookie = function (c_name) {
    var i,x,y,ARRcookies=document.cookie.split(";");
    for (i=0;i<ARRcookies.length;i++)
    {
      x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
      y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
      x=x.replace(/^\s+|\s+$/g,"");
      if (x==c_name)
      {
        return decodeURIComponent(y);
      }
    }
  }

  aps.deleteCookie = function(name, path, domain) {
	aps.setCookie(name, null);
	return true;
  }

  function UserAgent ()
  {
      this.agent_msie = "Microsoft Internet Explorer";
      this.agent_netscape = "Netscape";
      this.agent_mozilla = "Mozilla";
      this.agent_opera = "Opera";
      this.agent_current = navigator.appName;

      this.ua_exp = new Object ();
      this.ua_exp[this.agent_msie] = "MSIE.(\\d+).(\\d+)";
      this.ua_exp[this.agent_mozilla] = "rv:(\\d+).(\\d+.\\d+)";
      this.ua_exp[this.agent_netscape] = "rv:(\\d+).(\\d+.\\d+)";
      this.ua_exp[this.agent_opera] = "Opera/(\\d+).(\\d+)";

      var re = new RegExp(this.ua_exp[this.agent_current]);
      var arr = re.exec(navigator.userAgent);

      this.ver_major = RegExp.$1;
      this.ver_minor = RegExp.$2;
  }

  UserAgent.prototype.test = function (agentName) { return this.agent_current == agentName;  }
  UserAgent.prototype.isIE = function () {  return this.test (this.agent_msie); }
  UserAgent.prototype.isNetscape = function () {  return this.test (this.agent_netscape); }
  UserAgent.prototype.isMozilla = function () {  return this.test (this.agent_mozilla);  }
  UserAgent.prototype.isOpera = function () {  return this.test (this.agent_opera); }


/** 
 members
*/

  aps.mplayer = new MPlayer ();
  aps.ua = new UserAgent ();
  aps.NCE_List = [];

  var cachedImages = {};
  var userAgent = aps.ua;
  var IE = userAgent.isIE ();
  var NETSCAPE = userAgent.isNetscape ();
  var MOZILLA =  userAgent.isMozilla () || NETSCAPE;
  var OPERA = userAgent.isOpera ();

  var arrOnSubmitScripts = [];
  var arrScripts = [];
  var hotKeysMap = {};
  var validElmArray = [];
  var ignoreErrors = false;
  var targetOptions = {};
  var DefValues = [];

  aps.isIgnoreErrors = function () { return ignoreErrors; }

/**
* common widget library
*/
  var apsWidgets = {
	"media.player" : { 
		init : function (domNode) {	$(domNode).click (function () { aps.mplayer.play (this);}); }
	},
	"ui.image_button" : {
		init : function (domNode) { /** I hate this shit, but it still be here for compatibility reasons */
			$(domNode).mouseover(function () { 
				$(this).attr("src", cachedImages[$(this).attr('apsImageAct')].src);
			})
			.mouseout (function () { 
				$(this).attr("src", cachedImages[$(this).attr('apsImage')].src);
			});	
		}
	},
	"media.bgsound" : {
		init : function (domNode) { aps.mplayer.playURL ($(domNode).attr('url')); }
	},
    "ui.text_button" : {
		init : function (domNode) { }
    },
    "aps.frame_context" : {
		init : function (domNode) {
			if ($(domNode).attr('fctx') != 'true') {
				aps.ajxLoad ($(domNode).attr('id'), { complete : function () {$(domNode).attr('fctx', 'true');} });
			}
        }
    },
    "aps.include" : {
	    init : function (domNode) {
			var url = $(domNode).attr('url');
			console.log ('load url: ' + url);
			$(domNode).load(url, function () { aps.processWidgets(domNode); } );
            }
    }, 
    "ui.listbox" : {
	init : function (domNode) {
	    if ($(domNode).attr('name') != 'undefined' && typeof ($(domNode).attr('MULTIPLE')) != 'undefined') {
		
		aps.regDefValue ($(domNode).attr('name'), '');
	    } 
        }
     }
  };
}( window.aps = window.aps || {}, jQuery ));
(function( aps, $, undefined ) {
/** used in 'auto-save' */
  var autoTimeout = null;

//  aps.ajxDefaultTimeout = 30000;

  aps.ajaxEnv = {
		timeout : 30000,
		timeoutMsg : "Timed out waiting for server response"
  };

  aps.ajaxInit = function () {
    if ($('.aps_env_param').length > 0) {
		var envObj = $('.aps_env_param');

		if (envObj.attr('jxTimeout') > 0)
			aps.ajaxEnv.timeout = envObj.attr('jxTimeout');

		if (envObj.attr('jxTimeoutMsg')) {
			aps.ajaxEnv.timeoutMsg = envObj.attr('jxTimeoutMsg'); 
		}
    }
  }

  aps.ajxDebugState = function () { };

  aps.ajaxDefaultHandleError = function (jxr) {
	alert (aps.ajaxEnv.timeoutMsg);
  }

//
// APS-Interface functions
  aps.ajxRequest = function (opt) {
    return new JxSubmitRequest(opt);
  }

  aps.ajxSubmit = function (inp) {
    aps.ajxRequest()
      .data({ request_id : 222 })
      .input(inp)
      .reload(inp.getAttribute ('jxrel'))
      .send();
  }

  aps.ajxSubmitForm = function (form)
  {
    if (form == null)
      return;

    form.setAttribute('ajxRequest', 'false');	
    var eventSource = typeof(form.eventSource) != 'undefined' ? form.eventSource : null;
    if (eventSource != null) {
      $(eventSource).addClass('aps_ajax_ind');
    }

     aps.ajxRequest()
      .reload(form.getAttribute ('ajxRequestReload'))
      .timeout ($(form).attr('jxTimeout') > 0)
      .form (form)
      .callback(form.getAttribute ('ajxCallback'))
      .send (function () {
	if (eventSource)
	    $(eventSource).removeClass('aps_ajax_ind');
      });
  }

  aps.ajxSubmitInputValueEsc = function (name, value, reloads) {
    aps.ajxSubmitInputValue (name, unescape(value), reloads);
  }

  aps.ajxSubmitInputValue = function (name, value, reloads) {
    aps.ajxRequest()
      .data ({ request_id : 222 })
      .value (name, value)
      .reload(reloads)
      .send();
  }

  aps.ajxAutoSave = function (inp, to) {
    if (this.lastval == inp.value)
      return;

    this.lastval = inp.value;

    if (autoTimeout != null)
      clearTimeout (autoTimeout);

    autoTimeout = setTimeout (
	function () { aps.ajxSubmit(inp); }, 
          to == null ? 2000 : to
      );
  }

  aps.ajxReloadNode = function (nodeId) {
    aps.ajxRequest().reloadOne(nodeId).send();
  }


  aps.ajxLoad = function (nodeId, opt) {
    aps.ajxRequest(opt).reloadOne(nodeId).send();
  }


  aps.ajxExecAction = function (act, prop, reloadNodeId) {
    aps.ajxRequest()
      .reloadOne(reloadNodeId)
      .data ({ 
	frame_id : window.name, 
	action_ref : act, 
	action_properties : prop})
      .send();
  }


  aps.ajaxAutoUpdate = function (reloads, pause) {
    if (reloads != null && reloads.length > 0) {
      aps.ajxRequest().reload (reloads).send();

      var handle = setTimeout (
    	    function () { 
		aps.ajaxAutoUpdate(reloads,pause); 
		clearTimeout(handle); 
	    }, pause
      );
    }	  
  }



/** core:: send function **/
  function ajxSendRequest (rq) {
      $(document.body).addClass('aps_page_ajax_ind');

      var headers = { ajax : "1.0", apsRL : (rq.apsRL != null ? rq.apsRL : 'default') };
   
      if (!rq.reloads.isEmpty()) {
         headers.aps_render_tags = rq.reloads.getReloadEnum();
      }

      $.ajax ({
          type : "POST",
          url  : rq.url,
	  data : rq.postData,
	  cache : false,
	  processData : false,
          timeout : rq.timeoutVal,
	  headers : headers,
	  success : function (respData, status, jxr) {
			jxr.complete = true;
			if (typeof(rq.handleResponse) == 'function') {
			    try {   rq.handleResponse (jxr,respData,status); }	    catch (e) {}
			}
	  },
	  error   : function (jxr) {
			if (jxr.complete)
				return;

			if (rq.handleError)
			  rq.handleError (jxr);
			else
			  aps.ajaxDefaultHandleError (jxr);
          },
	  complete : function () {
			$(document.body).removeClass('aps_page_ajax_ind');
			if (typeof(rq.onComplete) === 'function') 
				rq.onComplete();
	  }
      });
  }


//
// 
//
function JxReloads (reloads) {

   this.relarray = [];
   this.relmap = {};

   this.append = function (ra) {
      if (typeof (ra) === 'string') {
	var spl = ra.split(",");
        for (var i in spl) {
	  this.relarray.push(spl[i]);
	  this.relmap[spl[i]] = spl[i];
        } 
      }
      else {	
	for (var k in ra) {
	    this.relarray.push(k);
	    this.relmap[k] = ra[k];
	}
      }
   }

   this.appendOne = function (remote,local) {
     this.relarray.push (remote);
     this.relmap[remote] = (local || remote); 
   }

   this.isFullReload = function () {
     return $.inArray('#body', this.relarray) >= 0;
   }

   this.isEmpty = function () {
     return this.relarray.length == 0;
   }

   this.getReloadEnum = function () {
      return this.relarray.join(',');
   }

   this.localReload = function (relId) {
       return typeof(this.relmap[relId] === 'string') ? this.relmap[relId] : relId;
   }

   if (reloads != null)
     this.append(reloads);

   return this;
}


function JxSubmitRequest (opt) {
  opt = opt || {};
  this.url = opt.url || window.location.pathname;
  this.rhl = opt.handler || new JxFormSubmitResponseLogic({});
  this.cbk = opt.callback || null;
  this.timeoutVal = opt.timeout || aps.ajaxEnv.timeout;
  this.reloads = opt.reloads || new JxReloads();
  this.userOnComplete = opt.complete || null;
  this.apsRL = 'aps';
  this.postData = $.param({t_ : new Date().getTime()});


  this.timeout = function (to) {
    if (to && $.isNumeric(to) && to > 0) {
	this.timeoutVal = to;
    }

    return this;
  }

  this.callback = function (cbx) {
	if ((cbx != null) && (typeof(cbx)==='function' || cbx.length > 0 ))
		this.cbk = cbx;

	return this;
  }

  this.form = function (form) {
     this.postData = $(form).serialize();
     return this;
  }

  this.value = function (n, v) {
    var x = {};  x[n] = v;
    return this.data (x);
  }

  this.data = function (plainObject) {
    this.appendData($.param(plainObject));
    return this;
  }

  this.input = function (htmlInput) {
    var inp = $(htmlInput);
    if (inp.is('select') && typeof(inp.attr('name')) != 'undefined' && inp.val() == null)
	return this.value(inp.attr('name'),'');

    this.appendData(inp.serialize());
    return this;
  }

  this.appendData = function (ustr) {
    if (this.postData != null)
      this.postData += "&";
    else
      this.postData = "";

    this.postData += ustr;
  }

  this.reload = function (r) { 
    this.reloads.append(r); 
    return this; 
  }

  this.reloadOne = function (remote,local) { 
    this.reloads.appendOne(remote,local); 
    return this; 
  }

  this.send = function (completeFunc) {
    if (completeFunc)
        this.userOnComplete = completeFunc;

    ajxSendRequest (this);
    return this;
  }

  var self = this;
  this.handleResponse = function (xreq,respData,status) {
     self.rhl.handleResponse ({apsReq: self, jxreq: xreq, data : respData, status : status});
  }

  this._invokeCallback = function () {
     if (this.cbk != null) {
	return typeof(this.cbk) === 'function' ? this.cbk() : eval(this.cbk);    
     }
  }

  this.onComplete = function () {
     this._invokeCallback();
     if (typeof(this.userOnComplete) === 'function')
	this.userOnComplete();
  }

  return this;
}

// utility
//
function ajxGetChildNodes (obj){
  if (obj.tagName == 'TABLE')
	return obj.rows;

  if (obj.tagName == 'TR')
	return obj.cells;

  return obj.childNodes;
}

var merge_attr = ['align'];

function ajxMergeAttr (d, s) {
	
  for (var idx = 0; idx < merge_attr.length; idx++)
  {
    if (s.getAttribute(merge_attr[idx]) != null)
      d.setAttribute (merge_attr[idx], s.getAttribute (merge_attr[idx]));
  }  

  if (s.className != null)
    d.className = s.className;
}

function ajxWeakMergeContent (to, from)
{
  ajxMergeAttr (to, from);
  var childsTo = ajxGetChildNodes (to);
  var childsFrom = ajxGetChildNodes (from);

  if (childsTo == null || childsFrom == null)
	return;

  for (var i = 0; i < childsTo.length; i++ ){
    var s = childsFrom[i];

    if (s == null)
       return; 

    var d = childsTo [i];

    if (s.tagName == null || d.tagName == null || s.tagName != d.tagName)
	return;

    if (s.hasChildNodes () && d.hasChildNodes ())
	ajxWeakMergeContent (d, s);
    else
        ajxMergeAttr (d, s);
  }
}


function parseResponseUI_Messages (doc) {
    var text = '';
    $(doc).find("aps-ui-message").each (function () {
    	text += $.trim($(this).text());
    });

    return text;
}
/**
*
* Common response handler
*
*/
function JxFormSubmitResponseLogic (p) {
  p = p || {};     

  this.fullReload = p.fullReload != null ? p.fullReload : false;
  this.processNavigation = p.processNavigation != null ? p.processNavigation : true;
  this.displayMessages = p.displayMessages != null ? p.displayMessages : true;

  this.handleResponse = function (p) {	
	var reloads = p.apsReq.reloads;
	var respData = $.type(p.data) === "string" ? $.parseXML(p.data) : p.data;

	if (this.displayMessages) {
          var messages = parseResponseUI_Messages(respData);
          if (messages.length > 0) 
	    aps.MessageBox (messages);
        }

	if (this.processNavigation) {
		var url = $(respData).find("aps-command-redirect").attr('url');
	    	if (url) {
		   top.location = url;
		   return;
		}
	}

	if (this.fullReload || reloads.isFullReload()) {
		top.location = top.location;//.reload(true);
		return;
	}


	$(respData).find("aps-ui-content").each (function () {
	    var reloadId = $(this).attr('reloadId');
	    var tagId = reloads.localReload (reloadId);

	    var dst = $('#'+tagId);
	    var dstNode = dst.length > 0 ? dst.get()[0] : null;

	    if (tagId == '#body' || (dstNode != null && dstNode === document.body)) {
		top.location.reload(true);
		return false;
	    }

	    if (dst.length > 0) {
		var text = $(this).text();

		if (dst.attr('ajxr') == 'weakm') {
		    var node = $('<div/>');
		    node.html(text);
		    ajxWeakMergeContent (dstNode, node.get()[0].firstChild);
		    node = null;
		    return true;
		}
	
		dst.replaceWith(text);
		aps.processWidgets($('#'+tagId).get()[0]);
            }
	});
  }
}

}( window.aps = window.aps || {}, jQuery ));(function( aps, $, undefined ) {

  aps.legacy = aps.legacy || {};

  aps.legacy.Calendar_showHideOverlapped = function (list, isShow)
  {
    for (var i = list.length; i > 0;)
    {
      var toShowHide = list [--i];

      toShowHide.style.visibility = isShow ? 'visible' : 'hidden';
    }
  }

//
// Prepare to submit calendar data
//
  aps.legacy.CLD_PrepareToSubmit = function (a) 
  {
    var cld = typeof(a) === 'string' ? document.getElementById (a) : a;

    if (cld == null)
      return;


    var SelPropID = cld.getAttribute ('sel_prop');
    if (SelPropID == null || SelPropID.length == 0)
      return true;

    var Form = aps.FindForm (cld);
    var SelDays = aps.legacy.CLD_GetDaysElements (cld);

    aps.DeleteParameter (Form, SelPropID);

    if (SelDays == null || SelDays.length == 0)
    {
      aps.CreateParameter (cld, SelPropID, '', Form, ($(cld).attr('id') + "_def"));
    }
    else
    {
      for (i = 0; i < SelDays.length; i++)
      {
        var Day = SelDays [i];
        var p_id = $(cld).attr('id') + "_" + Day.getAttribute ('day');
        aps.CreateParameter (cld, SelPropID, Day.getAttribute ('cl_date'), Form, p_id);
      }
    }

    return true;
  }


//
// 
// function returns tags for days in 'days' array,
// or all selected days if 'days' is null                                                 
//
//
  aps.legacy.CLD_GetDaysElements = function (cld, days)
  {
    var all_days = cld.getElementsByTagName ('TD');
    var s_days = days;

    var result = new Array ();
    var res_index = 0;

    for (i = 0; i < all_days.length; i++)
    {
      var day = all_days [i].getAttribute ("day");

      if (day == null || day.length == 0)
        continue;

      if (s_days != null)
      {
        if (day != s_days [res_index])
          continue;
      }
      else
      {
        if (all_days [i].getAttribute ('selected') != 1) 
          continue;
      }

      result [res_index] = all_days [i];
      res_index++;
    }

    return result;
  }

//
//
//
  aps.legacy._CLD_DoSelectDays = function (cld, DayList, SelMode)
  {
    var SelPropID = cld.getAttribute ('sel_prop');

    if (SelPropID != null && SelPropID.length == 0)
      SelPropID = null;

    var SelStyle = cld.getAttribute ('sel_style');
    var DayStyle = cld.getAttribute ('day_style');

    if (SelMode)
      DayStyle += " " + SelStyle;

    var HolidayStyle = cld.getAttribute ('holiday_style');

    if (SelMode)
      HolidayStyle += " " + SelStyle;

    for (i = 0; i < DayList.length; i++)
    {
      var CurrDay = DayList [i];
      var HD_Sign = CurrDay.getAttribute ('holiday');
      if (HD_Sign != null && HD_Sign.length > 0)
      {
        CurrDay.className = HolidayStyle;
      }
      else
      {
        CurrDay.className = DayStyle;
      }

      if (SelMode)
      {
        CurrDay.setAttribute ('selected', '1');
      }
      else
      {
        CurrDay.removeAttribute ('selected');
      }
    }
  }


//
//
// Function select/unselect days 'Days' for calendar 'cl_id' (identificator)
//
//
  aps.legacy.CLD_SelectDays = function (cl_id, Days)
  {
    var cld = document.getElementById (cl_id);
    if (cld == null)
      return;

    var sAction = cld.getAttribute ('action');

    var arr = Days.split (',');

    if (sAction != null && sAction.length > 0)
    {
       var DayList = aps.legacy.CLD_GetDaysElements (cld, arr);

       var SelSign = DayList[0].getAttribute ('selected');
       var selValue = SelSign == null || SelSign.length == 0;
       return aps.ExecuteAction (window.event, cld, sAction, 'day=' + Days + ';select='+ selValue + ';');
    }

    var lMultiselect = (cld.getAttribute ('multiselect') == 'true');
    var lCommit = (cld.getAttribute ('autocommit') == 'true');


    if (lMultiselect)
    {
      var DayList = aps.legacy.CLD_GetDaysElements (cld, arr);

      var SelMode = false;

      for (i = 0; i < DayList.length; i++)
      {
        var SelSign = DayList [i].getAttribute ('selected');
  
        if (SelSign == null || SelSign.length == 0)
        {
          SelMode = true;
          break;
        }
      }

      aps.legacy._CLD_DoSelectDays (cld, DayList, SelMode);
    }
    else
    {
      if (arr.length > 1)
        return false;

      var SelDays = aps.legacy.CLD_GetDaysElements (cld);
      var SelDay = SelDays [0];

    // unselect
      aps.legacy._CLD_DoSelectDays (cld, SelDays, false);

      var DayList = aps.legacy.CLD_GetDaysElements (cld, arr);

      if (DayList.length > 0)
      {
        var SelSign = DayList [0].getAttribute ('selected');
        aps.legacy._CLD_DoSelectDays (cld, DayList, !(DayList [0] == SelDay || SelSign == 1));
      }
    }

    if (lCommit)
      aps.CommitChange (cld);
  }

  aps.legacy.CLD_UnselectAll = function (cl_id)
  {
    var cld = document.getElementById (cl_id);

    if (cld == null)
      return;

    aps.legacy._CLD_DoSelectDays (cld, CLD_GetDaysElements (cld), false);

    return true;
  }


  aps.legacy.CLD_SetMonthYear = function (cl_id, month, year, clear_selection)
  {
    var cld = document.getElementById (cl_id);

    if (cld == null)
      return;

    var YearPropID = cld.getAttribute ('year_prop');
    var MonthPropID = cld.getAttribute ('month_prop');
    var Form = aps.FindForm (cld);
    var lNeedSubmit = false;

    if (month != null)
    {
      if (MonthPropID != null && MonthPropID.length > 0)
      {
        lNeedSubmit = aps.SetParameter (Form, MonthPropID, month);
      }
    }

    if (year != null)
    {
      if (YearPropID != null && YearPropID.length > 0)
      {
        lNeedSubmit = lNeedSubmit | aps.SetParameter (Form, YearPropID, year);
      }
    }

    if (clear_selection != null && clear_selection)
    {
      aps.legacy.CLD_UnselectAll (cl_id);
    }


    if (lNeedSubmit)
      aps.CommitChange (cld);
  }


//
// Date Picker
//
  aps.legacy.DP_Open = function (dp, cl, hc)
  {
	$(cl).show().position ({my : "left top", at : "left bottom", of : dp});

    aps.legacy.Calendar_showHideOverlapped (aps.getOverlappedInputList (cl), false);

    if (hc != null)
      hc.value = 'true';
  }

  aps.legacy.DP_Close = function (dp, cl, hc)
  {
    aps.legacy.Calendar_showHideOverlapped (aps.getOverlappedInputList (cl), true);
    $(cl).hide();

    if (hc != null)
      hc.value = 'false';
  }

  aps.legacy.DP_ToggleDatePicker = function (dp)
  {
//    var CL_ID = dp.getAttribute ('calendar');
    var theCLD = document.getElementById (dp.getAttribute ('calendar'));

//    var theHC_ID = dp.getAttribute ('dp_hid');
    var theHC = document.getElementById (dp.getAttribute ('dp_hid'));

    if ($(theCLD).is(':hidden'))
      aps.legacy.DP_Open (dp, theCLD, theHC);
    else
      aps.legacy.DP_Close (dp, theCLD, theHC);
  }

  aps.legacy.DP_UpdateState = function (n)
  {
    var dp = typeof(n) === 'string' ? document.getElementById (n) : n;

    if (dp == null)
      return;

    var theHC_ID = dp.getAttribute ('dp_hid');
    var theHC = document.getElementById (theHC_ID);

    if (theHC.value == 'true')
      aps.legacy.DP_ToggleDatePicker (dp);
  }



aps.addWidget ( 'ui.calendar', {
   init : function (domNode) {
   },

   submit : function (domNode) {
      return $(domNode).attr('enableSelect') ? aps.legacy.CLD_PrepareToSubmit(domNode) : true;
   },
   
   destroy : function (domNode) {
   }
});

aps.addWidget ( 'ui.datepicker', {
   init : function (domNode) {
	$(domNode).click (function(){ aps.legacy.DP_ToggleDatePicker(this);});
	aps.legacy.DP_UpdateState(domNode);
   }
});

}( window.aps = window.aps || {}, jQuery ));/** 
  Client Date Class Formatter
  @author GammiBear
  @version 1.0.0.0 build 0 Goblin
  @refactored, Michael, 2013
  */
(function( aps, $, undefined ) {
/**
 * Static Section
 */

var digits = [];

for(var i=0;i<100;i++){
    digits[i] = i<10 ? "0" + i : "" + i;
};

var shortWeekdays  = [];
var shortMonths    = [];
var longWeekdays   = [];
var longMonths     = [];

var LOCALE_RU = "ru_RU";
var LOCALE_EN = "en_EN";
var LOCALE_RU_SHORT = "ru";
var LOCALE_EN_SHORT = "en";

shortMonths[LOCALE_EN] = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
longMonths[LOCALE_EN] = ["January","February","March","April","May","June","July","August","September","October","November","December"];
shortWeekdays[LOCALE_EN] = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];
longWeekdays[LOCALE_EN] = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];

shortMonths[LOCALE_EN_SHORT]   = shortMonths[LOCALE_EN];
longMonths[LOCALE_EN_SHORT]    = longMonths[LOCALE_EN];
shortWeekdays[LOCALE_EN_SHORT] = shortWeekdays[LOCALE_EN];
longWeekdays[LOCALE_EN_SHORT]  = longWeekdays[LOCALE_EN];

shortMonths[LOCALE_RU] = ["Янв","Фев","Мар","Апр","Май","Июнь","Июль","Авг","Сен","Окт","Ноя","Дек"];
longMonths[LOCALE_RU] = ["Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"];
shortWeekdays[LOCALE_RU] = ["Вс","Пн","Вт","Ср","Чт","Пт","Сб"];
longWeekdays[LOCALE_RU] = ["Воскресенье","Понедельник","Вторник","Среда","Четверг","Пятница","Суббота"];

shortMonths[LOCALE_RU_SHORT]   = shortMonths[LOCALE_RU];
longMonths[LOCALE_RU_SHORT]    =longMonths[LOCALE_RU];
shortWeekdays[LOCALE_RU_SHORT] = shortWeekdays[LOCALE_RU];
longWeekdays[LOCALE_RU_SHORT]  = longWeekdays[LOCALE_RU];


var evalExpressions = [];
//Time Eval
evalExpressions["hh"  ] = "'+this.digits[this.evalDate.getUTCHours()]+'";
evalExpressions["mm"  ] = "'+this.digits[this.evalDate.getUTCMinutes()]+'";
evalExpressions["ss"  ] = "'+this.digits[this.evalDate.getUTCSeconds()]+'";
//Day Eval
evalExpressions["dd"  ] = "'+this.digits[this.evalDate.getUTCDate()]+'";
evalExpressions["d"   ] = "'+this.evalDate.getUTCDate()+'";
//Year Eval 
evalExpressions["y"  ] = "'+digits[this.evalDate.getUTCFullYear()%100]+'";
evalExpressions["yy"] = "'+this.evalDate.getUTCFullYear()+'";
//Month Eval
evalExpressions["M"   ] = "'+this.evalDate.getUTCMonth()+'";
evalExpressions["MM"  ] = "'+this.digits[this.evalDate.getUTCMonth()]+'";
evalExpressions["MMM" ] = "'+this.shortMonths[this.GetLocale()][this.evalDate.getUTCMonth()]+'";
evalExpressions["MMMM"] = "'+this.longMonths[this.GetLocale()][this.evalDate.getUTCMonth()]+'";
//Week Dayzzz
evalExpressions["w"   ] = "'+this.evalDate.getUTCDay()+'";
evalExpressions["ww"  ] = "'+this.digits[this.evalDate.getUTCDay()]+'";
evalExpressions["www" ] = "'+this.shortWeekdays[this.GetLocale()][this.evalDate.getUTCDay()]+'";
evalExpressions["wwww"] = "'+this.longWeekdays[this.GetLocale()][this.evalDate.getUTCDay()]+'";
//Misc Feachures
evalExpressions["T"   ] = "'+this.evalDate.getTimezoneOffset()+'";
evalExpressions["G"   ] = "'+this.evalDate.getTimezoneOffset()+'";
evalExpressions["U"   ] = "'+this.evalDate.getTimezoneOffset()+'";
var FORMAT_FUN_HEAD = "return '";
var FORMAT_FUN_TAIL = "';";


aps.time_format_res = {
  digits : digits,
  shortMonths : shortMonths,
  longMonths  : longMonths,
  shortWeekdays : shortWeekdays,
  longWeekdays  : longWeekdays 
};


function DateFormat_StaticInit(){  // nothing for now....
};
DateFormat_StaticInit(); 
/**
 * Class Declaration ...
 */
DateFormat = function(){ this.InitFormat(); };

/**
   Class init function
   @since 1.0.0.0 build 0 Goblin
 */
DateFormat.prototype.InitFormat = function(){   
   this.evalDate = null;
   this.formatImplementation = null;
   this.SetFormat("hh:mm:ss"); 
   this.locale = LOCALE_RU;  

   for (var k in aps.time_format_res) 
     this[k] = aps.time_format_res[k];
};
/**
   Sets the Date Format
   @since 1.0.0.0 build 0 Goblin
 */
DateFormat.prototype.SetFormat = function(theFormat){
   this.format = theFormat;
   this.buildEvalFunction(theFormat);
   
};
/**
 * this function builds EvaluationExpression...
 */ 
DateFormat.prototype.buildEvalFunction = function(theFormat){
   if(theFormat!=null){
      var str = new String(theFormat);
 	  var re = /(.*)(\W|^)(hh|mm|ss|d|dd|M|MM|MMM|MMMM|y|yy|w|ww|www|wwww|U|G|T)(\W|$)(.*)/;
 	  while (re.exec(str) != null) {
		  str = RegExp.$1 + RegExp.$2 + evalExpressions[RegExp.$3] + RegExp.$4 + RegExp.$5;
	  }	  
	  str = FORMAT_FUN_HEAD+str+FORMAT_FUN_TAIL;
	  this.formatImplementation = new Function(str);	  
   }
};

/**
   Gets the Date Format
   @since 1.0.0.0 build 0 Goblin
 */
DateFormat.prototype.GetFormat = function(){
  return this.format; 
};


/**
   Formats incoming date with given pattern 
   @since 1.0.0.0 build 0 Goblin
 */

DateFormat.prototype.Format = function(theDate){
    this.evalDate = theDate;
    var rez = this.formatImplementation!=null ? this.formatImplementation() :
                                                theDate.toUTCString();   
    this.evalDate = null;
    return rez;
};

/**
   Gets the Locale
   @since 1.0.0.0 build 0 Goblin
 */
DateFormat.prototype.GetLocale = function(){
  return this.locale; 
};
/**
   Gets the Locale
   @since 1.0.0.0 build 0 Goblin
 */
DateFormat.prototype.SetLocale = function(theLocale){
    this.locale = theLocale; 
};

aps.DateFormat = DateFormat;

}( window.aps = window.aps || {}, jQuery ));/** 
  Client Clock Class
  @author GammiBear
  @version 1.0.0.0 build 0 Goblin
  @refactored, Michael, 2013
  */
(function( aps, $, undefined ) {
/**
  Clock Registry - for callbacks support
  @since 1.0.0.0 build 0 Goblin  
 */
//var theClockRegistry = [];//Clock Registry - for callbacks support
/** 
  Default update interval in msecs 
  @since 1.0.0.0 build 0 Goblin  
 */
var DEF_CLOCK_TIMEOUT = 1000; //msecs

aps.addWidget (
 "ui.clock", {
    init : function (domNode) {
	  var qn = $(domNode);
	  var clock = new ClientClock(qn.attr('id'),qn.attr('utcOffset'),qn.attr('timeFormat'),qn.attr('id'));
	  
      clock.GetFormatter().SetLocale(qn.attr('locale') || LOCALE_RU);

	  if (qn.attr('run'))
	    clock.RegisterOnTimer();
    }
 });

/**
  Clock Constructor
  @since 1.0.0.0 build 0 Goblin
 */
ClientClock = function(theClockID,nOffset,theFormat,theTargetID){   
    this.InitClock(theClockID,nOffset,theFormat,theTargetID);
};
/**
   Class init function
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.InitClock = function(theClockID,nOffset,theFormat,theTargetID){
  // theClockRegistry[theClockID] = this;
   this.clockID = theClockID;
   this.offset = Math.round(nOffset);
   this.displayFormat = theFormat;
   this.nTimerID = -1;
   this.nClockTimeOut = DEF_CLOCK_TIMEOUT;
   this.targetID = theTargetID;
   this.dateFormatter = new aps.DateFormat();
   if(theFormat!=null){
       this.dateFormatter.SetFormat(theFormat); 
   }
};

/**
   Draws clock html (????) -  don't use now....
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.Paint = function(){
};
/**
   Register update timer....
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.RegisterOnTimer = function() {
   if(this.nTimerID<0){
      var self = this;
      this.nTimerID = window.setInterval(function() { self.TimerCallback(); }, this.nClockTimeOut);
   }
};
/**
   Unregister Clock update timer for this instance....
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.DropTimer = function(){
   if(this.nTimerID>=0){
       window.clearInterval(this.nTimerID);
       this.nTimerID = -1;
   }
};
/**
   instance timer callback
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.TimerCallback = function(){
    this.UpdateTarget();   
};
/**
   Corrects current time
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.AdjustTime = function(theTime){
    theTime.setTime(theTime.getTime()+this.offset);
    return theTime;
};
/**
   Formats given time 
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.FormatTime = function(theTime){
    return this.dateFormatter.Format(theTime);
};
/**
   produces clock value ....
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.MakeClockString = function(){
    var now = new Date();//get curretn date
    //do something to convert time
    now = this.AdjustTime(now);
    //do time formatting & return....
    return this.FormatTime(now);
};
/**
   Shows clock value on target
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.DisplayOnTarget=function(theTarget,theClockStr){
   //window.status = theClockStr;
   theTarget.innerHTML = theClockStr;
};
/**
   updates target [if exists]
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.UpdateTarget = function(){
    var obj = aps.ElementById(this.targetID);
    if(obj!=null){
        var theClockStr = this.MakeClockString();
        this.DisplayOnTarget(obj,theClockStr);
    }
	else
	  console.log ("Object with id=["+this.targetID+"] not found. clockID={"+this.clockID+"}");
};

/**
   Sets time display format
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.SetFormat =  function(theFormat){
    this.dateFormatter.SetFormat(theFormat);
};
/**
   Gets time display format
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.GetFormat =  function(){
    this.dateFormatter.GetFormat();
};

/**
   Gets formatter
   @since 1.0.0.0 build 0 Goblin
 */
ClientClock.prototype.GetFormatter =  function(){
    return this.dateFormatter;
};

 aps.ClientClock = ClientClock;
}( window.aps = window.aps || {}, jQuery ));function GetCookieX_SH(sName){
	var aCookie = document.cookie.split("; ");
	for (var i=0; i < aCookie.length; i++){
		var aCrumb = aCookie[i].split("=");
		if (sName == aCrumb[0]) 
			return unescape(aCrumb[1]);
	}
	return null;
}

function SetCookieX_SH(strKey,strValue){
    document.cookie = strKey+" = "+escape(strValue);
}

function getScrollXY() {
  var scrOfX = 0, scrOfY = 0;

  if (document.documentElement || MOZILLA) {
    scrOfY = document.documentElement.scrollTop;
    scrOfX = document.documentElement.scrollLeft;
  }
  else if( typeof( window.pageYOffset ) == 'number' ) {
    scrOfY = window.pageYOffset;
    scrOfX = window.pageXOffset;
  } else  {
    scrOfY = document.body.scrollTop;
    scrOfX = document.body.scrollLeft;    
  }

  return [ scrOfX, scrOfY ];
}


function setScrollXY (scrOfX, scrOfY) {

  if(document.documentElement || MOZILLA) {
    document.documentElement.scrollTop = scrOfY;
    document.documentElement.scrollLeft = scrOfX;
  }
  else if( typeof( window.pageYOffset ) == 'number' ) {
    window.pageYOffset = scrOfY;
    window.pageXOffset = scrOfX;
  } 
  else {
    document.body.scrollTop = scrOfY;
    document.body.scrollLeft = scrOfX;
  } 
}

function HoldScrollObj(obj,theHoldID) {
	
	if(obj!=null){
		var res = getScrollXY ();

		var top =  res[1];  //obj.scrollTop;
		var left = res[0];  //obj.scrollLeft;	

		SetCookieX_SH('ScrollHolda_'+theHoldID+'_T',top);
		SetCookieX_SH('ScrollHolda_'+theHoldID+'_L',left);
	}

	return true;
}

function HoldScroll(theObjID){
  	var obj = aps.ElementById(theObjID);
  	return HoldScrollObj(obj,theObjID);
}

function RestScrollObj(obj,theHoldID){
	if(obj!=null){
		var top = GetCookieX_SH('ScrollHolda_'+theHoldID+'_T');
		var left = GetCookieX_SH('ScrollHolda_'+theHoldID+'_L');

		setScrollXY (left == null ? 0 : left, top == null ? 0 : top);	
	}
	return true;
}

function RestScroll(theObjID){
  	var obj = aps.ElementById(theObjID);
  	return RestScrollObj(obj,theObjID);
}

function doScrollHold(theObjectID){
	RestScroll(theObjectID);
	aps.ExecOnFormSubmit("HoldScroll('"+theObjectID+"');");
}

function holdBodyScroll(theBodyHoldID){
    return HoldScrollObj(document.body,theBodyHoldID);
}
function restBodyScroll(theBodyHoldID){
	return RestScrollObj(document.body,theBodyHoldID);
}

function doBodyScrollHold(theBodyID){
	var theID = theBodyID!=null ? theBodyID : 'BODY';	
	restBodyScroll(theID);
	aps.ExecOnFormSubmit("holdBodyScroll('"+theID+"');");
}
(function( aps, $, undefined ) {

  aps.DynLoad_LoadNode = function (toNodeID, urlPath)
  {
     $('#'+toNodeID).load (urlPath);
  }

  aps.DynLoad_EnableFor = function (id, tagID, time)
  {
    var url = window.location.pathname + "?exclusive_mode=tagID=" + tagID;
    var dynTrigger = new aps.Trigger (function () { aps.DynLoad_LoadNode(id, url); }, time, false);
    dynTrigger.reset ();
  }

  aps.DynLoad_ExecAction = function (actId, actProp, elmId, tagID)
  {
    var url = window.location.pathname + "?exclusive_mode=tagID=" + tagID;
    url += "&&action_ref=" + actId;
    if (actProp != null)
      url += "&&action_properties=" + actProp;

    aps.DynLoad_LoadNode (elmId, url);
  }


aps.addWidget (
 "ui.dynload", {
   init : function (domNode) {
    var o = $(domNode);
    aps.DynLoad_EnableFor (o.attr('id'), o.attr('id'),o.attr('reloadTime'));
   }
 }); 

}( window.aps = window.aps || {}, jQuery ));(function( aps, $, undefined ) {

var inpSearchContext = {};

function createPrefetchStr (flt, form, append) {
   if (flt == null) return '';
   if (append == null) append = '';

   var result = '';
   while (flt.length > 0)
   {
     var re = new RegExp ("([^\\$]*)(\\$\\$[a-zA-Z0-9]+)");         	
     var arr = re.exec (flt); 
     if (arr == null) {
       result += append; 
       result += flt;
       flt = '';
     } else {   
        var left = RegExp.$1;
        var rpl = RegExp.$2;
	    var inpValue = form.elements[rpl.substring(2)].value;
        result += left;

	    if (inpValue != null && inpValue.length > 0) result += inpValue;
          result += append; 
        flt = RegExp.rightContext;
    }
  }

  return result;
}

function InputSearch (id, listShow, defListValue, limit, editId, listBoxId, lSubmit, delay, fltExpr)
{
  this.m_lListShow = listShow;
  this.m_strDefaultListValue = defListValue;
  this.m_nLimit = limit;
  this.m_strEditId = editId;
  this.m_strListBoxId = listBoxId;
  this.m_Data = [];
  this.m_Delay = delay != null ? delay : 1000;
  this.m_lSubmit = lSubmit;
  this.m_strFltExpr = fltExpr;

  inpSearchContext[id] = this;

  aps.ExecOnFormSubmit ("aps.inpsBeforeSubmit('" + id + "')");

  return this;
}


InputSearch.prototype.close = function ()
{
  if (!this.m_lListShow) {
    var lb = this.getListBox ();
    lb.style.display = 'none';
  }
}

InputSearch.prototype.getEdit = function ()
{
  if (this.m_Edit == null) {
    this.m_Edit = document.getElementById (this.m_strEditId);
  }

  return this.m_Edit;
}

InputSearch.prototype.getThisForm = function () {
  return aps.FindForm (this.getEdit ());
}

InputSearch.prototype.getListBox = function ()
{
  if (this.m_ListBox == null) {
    this.m_ListBox = document.getElementById (this.m_strListBoxId);
  }

  return this.m_ListBox;
}


InputSearch.prototype.add = function (text, value)
{
  this.m_Data.push ({text : text, value : value});
}

InputSearch.prototype.getFilterText = function ()
{
  return this.getEdit ().value.toLowerCase ();
}

//
// old name = make_search
InputSearch.prototype.proceedFiltering = function ()
{
  var nCounter = 0;

  var str_text = this.getFilterText ();
  var list = this.m_Data;
  var listbox = this.getListBox ();
  listbox.value = this.m_strListDefaultValue;

  if (aps.ua.isMozilla())
    listbox.options.length = this.m_nLimit;

  var RegExpString = createPrefetchStr (this.m_strFltExpr, this.getThisForm());
  var FltRegEx = new RegExp ("^" + RegExpString, "i");

  for (i = 0; i < list.length; i++)
  {
    var currOption = list[i];

    var itemValue = currOption.text.toLowerCase()
    var lMatch = FltRegEx.test (itemValue) && (itemValue.indexOf (str_text) >= 0);

    if (lMatch)
    {
      if (listbox.options[nCounter] == null)
      {
        var oOption = document.createElement ("OPTION");
        listbox.options[nCounter]=oOption;
      }
      else
      {
        listbox.options[nCounter].selected = false;
        listbox.options[nCounter].defaultSelected = false;
      }

      listbox.options[nCounter].innerHTML = currOption.text;
      listbox.options[nCounter].value = currOption.value;
      nCounter++;
    }

    if (!this.m_lListShow && nCounter > this.m_nLimit)
      break;
  }

  listbox.options.length = nCounter;
  return nCounter;
}


function doTimerProceed (ctxId,nolimit)
{
  var ctx = inpSearchContext [ctxId];

  if (ctx.m_lListShow)
  {
    ctx.proceedFiltering ();// makeSearch (ctx.edit.value, ctx.list, ctx.listbox, ctx.list.length);
  }
  else
  {
    var limit = ctx.m_nLimit; // edit.getAttribute ('searchLimit');

    var nFound = ctx.proceedFiltering ();// makeSearch (ctx.edit.value, ctx.list, ctx.listbox, limit);

    var listbox = ctx.getListBox ();
    var edit = ctx.getEdit ();

    if (nFound > 0 && (nFound <= limit || nolimit))
    {
      //AdjustToLeft (listbox, edit);
      //AdjustToBottom (listbox, edit);
//      listbox.style.position='absolute';
//      listbox.style.display='';
      
	  $(listbox).css('position','absolute').show().position ({ my : "left top", at : "left bottom", of : edit});

      if (listbox.className == null || listbox.className.length == 0)
        listbox.style.width = edit.offsetWidth;

      aps.NCE_List.push(ctx);
    }
    else
    {
      listbox.style.position='absolute';
      listbox.style.display='none';
      aps.removeItem (aps.NCE_List, ctx);
    }
  }
}

aps.InputSearch = InputSearch;

aps.editSearch = function (evt, inpsId) // edit, sel_id, list)
{
  var inps = inpSearchContext [inpsId];

  var trgId = inps.triggerId; 

  var trg = trgId != null ? aps.findTrigger (trgId) : null;

  var listbox = inps.getListBox ();
  var edit = inps.getEdit ();

  var nolimit = evt.keyCode == 40 && listbox.style.display == 'none';

  if (trg == null)
  {
     var timeout = inps.m_Delay;
     trg = new aps.Trigger (function () { doTimerProceed (inpsId, nolimit); }, timeout);
     inps.triggerId = trg.id;
  }


  if (evt.keyCode == 40)
  {
     if (listbox.style.display != 'none')
     {
//        trg.stop ();
       listbox.focus ();
       return;
     }
  }


  if (evt.keyCode == 13 && inps.m_lSubmit)
  {
    //alert ('that\'s it');
    aps.StopEventBubble (evt);
    trg.stop ();
    return aps.SubmitFormBy (edit);
  }


  if (edit.getAttribute ('lastValue') == edit.value)
  {
    trg.stop ();
    return;
  }

  trg.reset ();
}


aps.searchLB_Change = function (evt, inpsId)
{
  if (evt.keyCode == 13 && inpsId != null)
  {
    var inps = inpSearchContext [inpsId];
    aps.StopEventBubble (evt);
    aps.searchCommit (inpsId);

    if (inps.m_SelActionID != null) 
    {
	  aps.CommitChange (inps.getListBox ()); // ignoring errors!
    }	
  }
}


aps.searchCommit = function (inpsId)
{
  var inps = inpSearchContext [inpsId];
  //if (inps.m_lListShow)
  //   return false;

  var listbox = inps.getListBox ();

  if (listbox.selectedIndex < 0)
  {
    return false;
  }

  var value = listbox.options[listbox.selectedIndex].innerHTML;

  var DefValue = inps.m_strDefaultListValue; //listbox.getAttribute ('defValue');


  if (DefValue != null && DefValue.length > 0)
  {
    var DefName = listbox.name;
    var DefElement = aps.ElementById (DefName + '_id');

    if (DefElement != null)
    {
      DefElement.parentNode.removeChild (DefElement);
    }
  }


  if (!inps.m_lListShow)
  {
    var edit = inps.getEdit (); // document.getElementById (edit_id);

    edit.setAttribute ('lastValue', value);
    edit.value = value;

    listbox.style.position='absolute';
    listbox.style.display='none';

    aps.removeItem (aps.NCE_List, inps);

    edit.focus ();

    if (inps.m_SelActionID != null)
	return aps.ExecuteAction (window.event, listbox, inps.m_SelActionID, inps.m_SelActionProp);
    
    if (inps.m_lSubmit)
      return aps.SubmitFormBy (edit);
  }
  else
  {
     if (inps.m_SelActionID != null)
 	return aps.ExecuteAction (window.event, listbox, inps.m_SelActionID, inps.m_SelActionProp);

     if (inps.m_lSubmit)	
      return aps.SubmitFormBy (listbox);	
  }
}


aps.inpsBeforeSubmit = function (inpsId)
{
  var inps = inpSearchContext [inpsId];

  var listbox = inps.getListBox (); //document.getElementById (lbId);
  var editValue = inps.getEdit ().value;

  if (editValue != null)
	editValue = editValue.toLowerCase ();

  //
  // special check for equality of text in edit-box and one of the list items 
  //
  // Test if not list show (popup-mode) and listbox has no selected option,
  // and of cource edit-box value is not null
  // 
  if (!inps.m_lListShow && listbox.selectedIndex < 0 && editValue != null)
  {
    for (var i = 0; i < listbox.options.length; i++)
    {
	var opt = listbox.options[i];
	if (opt.text.toLowerCase() == editValue.toLowerCase ())
	{
	  // yes, we found it
	  opt.selected = true;
	  return true;
	}
    }
  }


  if (listbox.selectedIndex < 0)
  {
    var DefValue = inps.m_strDefaultListValue; //listbox.getAttribute ('defValue');

    if (DefValue != null && DefValue.length > 0)
    {

//     alert ('init default value ' + DefValue);

      var DefName = listbox.name;
      var DefElement = aps.ElementById (DefName + '_id');
      var Form = aps.FindForm (listbox);

      if (DefElement == null)
      {
        aps.CreateParameter (listbox, DefName, DefValue, Form, DefName+'_id');
      }
      else
        DefElement.value = DefValue;
    }
  }

  return true;
}
}( window.aps = window.aps || {}, jQuery ));/** 
  Ip Editor Class
  @author GammiBear
  @version 1.0.0.0 build 0 Goblin
  @refactored, Michael, 2013
  */
(function( aps, $, undefined ) {

var IP_VERSION_4 = 4; 
var IP_VERSION_6 = 6;
var STAR_VALUE = 888; 
var EMPTY_VALUE = 444;

var IPE_DEFAULT_DELAY = 250;
var IPE_DEFAULT_GOOD_CLASS = 'IPE_TEXT';
var IPE_DEFAULT_BAD_CLASS = 'IPE_TEXT_BAD';
var IPE_DEAULT_DELIM_CHAR = '.';

aps.IpEditorConst = {
 IP_VERSION_4 : IP_VERSION_4, 
 IP_VERSION_6 : IP_VERSION_6,
 STAR_VALUE  : STAR_VALUE,
 EMPTY_VALUE : EMPTY_VALUE,
 IPE_DEFAULT_DELAY : IPE_DEFAULT_DELAY,
 IPE_DEFAULT_GOOD_CLASS : IPE_DEFAULT_GOOD_CLASS,
 IPE_DEFAULT_BAD_CLASS : IPE_DEFAULT_BAD_CLASS,
 IPE_DEAULT_DELIM_CHAR : IPE_DEAULT_DELIM_CHAR 
};

/**
  Editor Registry
  @version 1.0.0.0 build 0 Goblin
 */
var theEditorRegistry = [];


/**
  IpEditor Constructor
  @since 1.0.0.0 build 0 Goblin
 */  
IpEditor = function(theEditorID,theIp,nIpVersion,theGoodClass,theBadClass,nBugDelay,nDelimChar,lStarAllow,theTargetID){
  this.init(theEditorID,theIp,nIpVersion,theGoodClass,theBadClass,nBugDelay,nDelimChar,lStarAllow,theTargetID);
};

/**
   IpEditor init function
   @since 1.0.0.0 build 0 Goblin
 */
IpEditor.prototype.init = function(theEditorID,theIp,nIpVersion,theGoodClass,theBadClass,nBugDelay,nDelimChar,lStarAllow,theTargetID){
    theEditorRegistry[theEditorID] = this;
    this.m_nIpVersion = nIpVersion;
    this.m_theValues =  new Array();
    this.m_theBadClassName = theBadClass;
    this.m_nBugDelay = nBugDelay;
    this.m_theGoodClassName = theGoodClass;
    this.m_nDelimChar = nDelimChar;
    this.m_lStarAllow = lStarAllow;
    this.m_theTargetID = theTargetID;
    this.m_theWrongInputMessage = null;
    var nCount = this.m_nIpVersion == IP_VERSION_4 ? 4 : 6;    
    this.values = new Array();
    for(var i=0;i<nCount;i++){      
       this.values[i] = theIp[i];
    }
};


/**
   Callback Function
   @since 1.0.0.0 build 0 Goblin
 */
IpEditor.prototype.valueChanged = function(nItemIndex,theElm,theNewValue){
   var numbers = /^\d{1,3}$/;
   var star    = /^\d{0,2}\*{1,3}\d{0,2}$/;
   if(this.m_lStarAllow && star.test(theNewValue)){
      theElm.value = '*';
      this.values[nItemIndex] = STAR_VALUE;
      this.displayNoBugFor(theElm);
      return;
   }
   if(numbers.test(theNewValue)){
      nValue = parseInt(theNewValue);
      if(nValue>=0 && nValue<=255){//All Yo!!!
          this.values[nItemIndex] = nValue;
          this.displayNoBugFor(theElm);
          return;
      }
   }
   if(theNewValue.length>0){
       switch(this.values[nItemIndex]){
           case EMPTY_VALUE:
               theElm.value = '';
               break;
           case STAR_VALUE :
               theElm.value = '*';
               break;
           default:
               theElm.value = this.values[nItemIndex];               
       } 
       this.displayBugFor(theElm,this.values[nItemIndex]!=EMPTY_VALUE);
   }
   else{
       this.values[nItemIndex] = EMPTY_VALUE;
       this.displayBugFor(theElm,false);
   }
   
};
/**
   error mark function
   @since 1.0.0.0 build 0 Goblin
 */
IpEditor.prototype.displayBugFor = function(theElm,lRestore){
   theElm.className = this.m_theBadClassName;
   if (lRestore) {
     $(theElm).delay(this.m_nBugDelay).removeClass(this.m_theBadClassName).addClass(this.m_theGoodClassName);
   }
//   if(lRestore){
//      window.setTimeout('IpEditor_RestClass("'+theElm.id+'","'+this.m_theGoodClassName+'");',this.m_nBugDelay);
//   }
};

/**
   OK mark function
   @since 1.0.0.0 build 0 Goblin
 */
IpEditor.prototype.displayNoBugFor = function(theElm){
   theElm.className = this.m_theGoodClassName;
};

IpEditor.prototype.isValid = function() {
   var nCount = this.m_nIpVersion == IP_VERSION_4 ? 4 : 6;    
   for(var i=0;i<nCount;i++){
       if(this.values[i]==EMPTY_VALUE){
           return false;
       }       
   }
   return true;
};
IpEditor.prototype.submitValue = function() {
    var obj = aps.ElementById(this.m_theTargetID);
    if(obj!=null){
        var theRezult = '';
        var nCount = this.m_nIpVersion == IP_VERSION_4 ? 4 : 6;    
        for(var i=0;i<nCount;i++){
            theRezult+=(i==0? '' : this.m_nDelimChar)+(this.values[i]==STAR_VALUE ? '*' :this.values[i]);
        }  
        obj.value = theRezult;                            
    }
    return true;
};
//----[Callbacks]-------------------------------------
//function IpEditor_RestClass(theItemID,theClassID){
//   var obj = ElementById(theItemID);
//   obj.className = theClassID;
//};

aps.IpEditor = IpEditor;

//----[HOOKS]-----------------------------------------
aps.IpEditor_KeyUpHook = function (elm,theEditorID,nItemIndex){
   var theEdObj =  theEditorRegistry[theEditorID];
   if(theEdObj!=null){
       theEdObj.valueChanged(nItemIndex,elm,elm.value);
   }   
};
aps.IpEditor_FormSubmitHook = function (theEditorID){
    var theEdObj =  theEditorRegistry[theEditorID];
    if(theEdObj!=null){
        var form = aps.FindForm(aps.ElementById(theEdObj.m_theTargetID));
        if(form!=null){
          var IVE_Mode = form.getAttribute ('ive');
          if(IVE_Mode == 'true') {
              return true;
          }
        }
        if(theEdObj.isValid()){
            return theEdObj.submitValue();
        }
        else{
            if(theEdObj.m_theWrongInputMessage!=null){
               aps.MessageBox (theEdObj.m_theWrongInputMessage);
            }
            return false;
        }
    }
    return true;
};


aps.addWidget (
 "ui.ip-editor", {
   init : function (domNode) {
      var o = $(domNode);
	
      var e = new aps.IpEditor(
		    o.attr('editorId'), 
		    o.attr('iparr').split(','),
		    o.attr('ipver') == '6' ? aps.IpEditorConst.IP_VERSION_6 : aps.IpEditorConst.IP_VERSION_4,
		    o.attr('esClass') || aps.IpEditorConst.IPE_DEFAULT_GOOD_CLASS,
		    o.attr('esWrong') || aps.IpEditorConst.IPE_DEFAULT_BAD_CLASS,
		    o.attr('delayWrongVal'),
		    o.attr('delim'),
		    o.attr('ipstars') == 'true',
		    o.attr('edlink'));

      if (o.attr('ivInputMsg'))
	    e.m_theWrongInputMessage = o.attr('ivInputMsg');
      
      var edId = o.attr('editorId');	

      o.next().find('input[ipItem]').each(function() {
	$(this).keyup(function() {aps.IpEditor_KeyUpHook(this, edId, $(this).attr('ipItem'));});
	$(this).blur(function() {aps.IpEditor_KeyUpHook(this,edId, $(this).attr('ipItem')); });
      });
   },


   submit : function (domNode) {
	return aps.IpEditor_FormSubmitHook($(domNode).attr('editorId'));
   },

   destroy : function (domNode) {
	//
   }
 });


}( window.aps = window.aps || {}, jQuery ));(function( aps, $, undefined ) {
//
//
  aps.menu = aps.menu || {};

  function _HideMenu (menu)
  {
    var pMenu = menu.parentMenu;

    if (pMenu != null)
    {
      $(menu).hide(); //.style.display='none';
      pMenu.subMenu = null;
      menu.parentMenu = null;
      return true;
    }

    return false;
  }

  aps.menu.ShowSubMenu = function (src, menuID, subMenuID, hAlign)
  {
    var subMenu = document.getElementById (subMenuID);
    var menu = document.getElementById (menuID);

    if (menu.subMenu != null)
      _HideMenu (menu.subMenu);

    subMenu.style.position='absolute';
    subMenu.parentMenu=menu;
    menu.subMenu=subMenu;

    if (hAlign)
    {
      $(subMenu).show().position({my:"left top", at : "right top", of : src});
    }
    else
    {
      $(subMenu).show().position({my : "left top", at : "left bottom", of : src});
    }

//    Menu_showHideOverlapped (aps.getOverlappedInputList (subMenu), false);
  }

  aps.menu.HideSubMenu = function (evt, menuID, check)
  {
    if (menuID == null)
      return;

    var menu = document.getElementById (menuID);

    if (menu == null)
      return;

    if (check != null && check)
    {
      var target = evt.relatedTarget;   //aps.ua.isMozilla () ? evt.relatedTarget : evt.toElement;

      if (menu.parentMenu == target)
         return;

      if (aps.ContainsElement (menu, target))
         return;

      var sub = menu.subMenu;

      while (sub != null)
      {
        if (aps.ContainsElement (sub, target))
          return;

          sub = sub.subMenu;
      }
    }

    var pMenu = menu.parentMenu;

    if (_HideMenu (menu))
    {
      aps.menu.HideSubMenu (evt, pMenu.id, check);
    }
  }

  aps.menu.HideSubMenuFor = function (menuID)
  {
    var menu = document.getElementById (menuID);

    if (menu != null && menu.subMenu != null)
      _HideMenu (menu.subMenu);
  }

  aps.addWidget ("ui.mainmenu", {
	init : function (domNode) {
		var xmenu = $(domNode);
		xmenu.mouseout (function (event) {
			if (event.target == this && this.subMenu) 
				_HideMenu (this.subMenu);
		});

		$("td[menuitem]", domNode).each (function() {
			var x = $(this);
			x.mouseover(function(event) {
			  if (x.attr('subMenu'))
				aps.menu.ShowSubMenu (this, xmenu.attr('id'), x.attr('subMenu'));
			  else
				aps.menu.HideSubMenuFor (xmenu.attr('id'));
			});
			x.click (function (evt) {
				if (x.attr('actionRef')) {
					// store selected node path into hidden input
					$('#' + xmenu.attr('selProperty')).val(x.attr('treeNodeId'));
					return aps.ExecuteAction (evt,this,x.attr('actionRef'),x.attr('actionProp'));
				}
				else
				if (x.attr('actionResult')) {
					return aps.ExecuteAction (evt,this,x.attr('actionResult'));
				}
			});
		});
    }
  });

  aps.addWidget ("ui.popupmenu", {
	init : function (domNode) {
		var xmenu = $(domNode);
		
		xmenu.mouseout(function (evt) { 
			aps.menu.HideSubMenu (evt, xmenu.attr('id'), true);
		});

		$("td[menuitem]", domNode).each (function() {
			var x = $(this);

			x.mouseover (function (evt) {
				if (x.attr('subMenu'))
				  aps.menu.ShowSubMenu (this, xmenu.attr('id'), x.attr('subMenu'), true);
				else
				  aps.menu.HideSubMenuFor (xmenu.attr('id'));
			});
			if (x.attr('subMenu')) {
				x.mouseout (function (evt) {
				  aps.menu.HideSubMenu (evt,x.attr('subMenu'),true);
				});
			};

			x.click (function (evt) {
				if (x.attr('actionRef')) {
					// store selected node path into hidden input
					$('#' + xmenu.attr('selProperty')).val(x.attr('treeNodeId'));
					return aps.ExecuteAction (evt,this,x.attr('actionRef'),x.attr('actionProp'));
				}
				else
				if (x.attr('actionResult')) {
					return aps.ExecuteAction (evt,this,x.attr('actionResult'));
				}
			});
		});
    }
  });
  
}( window.aps = window.aps || {}, jQuery ));/** @refactored, Michael, 2013 **/
(function( aps, $, undefined ) {
var PIT_TEXT   = 0;
var PIT_WSPACE = 1;
var PIT_EDITOR = 2;

aps.PIT = {
  PIT_TEXT : PIT_TEXT,
  PIT_WSPACE : PIT_WSPACE,
  PIT_EDITOR : PIT_EDITOR
};

var thePatternInputRegistry = [];

/** 
  Pattern Input Class
  @author GammiBear
  @version 1.0.0.0 build 0 Goblin
  */
 
function PatternInput(theInputID,theUplinkID,theGoodClassName,theBadClassName,nBugDelay,lDisabled){  
   this.m_theInputID = theInputID;
   thePatternInputRegistry[this.m_theInputID] = this;
   this.m_theUplinkID = theUplinkID;
   this.m_thePartsInfo = [];
   this.m_theWrongInputMessage  = null;
   this.m_theGoodClassName = theGoodClassName;
   this.m_theBadClassName = theBadClassName;
   this.m_nBugDelay = nBugDelay; 
   this.m_lDisabled = lDisabled==null ? false : lDisabled;
};

/**
   error mark function
   @since 1.0.0.0 build 0 Goblin
 */
PatternInput.prototype.displayBugFor = function(theElm,lRestore){
   theElm.className = this.m_theBadClassName;
   if (lRestore) {
     $(theElm).delay(this.m_nBugDelay).removeClass(this.m_theBadClassName).addClass(this.m_theGoodClassName);
   }
/*
   if(lRestore){
      window.setTimeout('PattenInput_RestClass("'+theElm.id+'","'+this.m_theGoodClassName+'");',this.m_nBugDelay);
   }*/
};

/**
   OK mark function
   @since 1.0.0.0 build 0 Goblin
 */
PatternInput.prototype.displayNoBugFor = function(theElm){
   theElm.className = this.m_theGoodClassName;
};

PatternInput.prototype.putPatternPart =  function(nItemIndex,theValue,theTargetID,theMask,nType){
    this.m_thePartsInfo[nItemIndex] = new PatternInputElement(this,nItemIndex,theValue,theTargetID,theMask,nType);
};

PatternInput.prototype.isDisabled = function() {
	return this.m_lDisabled;
};

PatternInput.prototype.isValid = function() {
    for(var Q in this.m_thePartsInfo){
       if(!this.m_thePartsInfo[Q].isValid()){
           return false;
       }
    }
    return true;
};

PatternInput.prototype.checkValid = function() {
    if(this.isDisabled()){
        return true;
    }
    var lRezult = true;
    var lIntermediate ;
    for(var Q in this.m_thePartsInfo){
       lIntermediate = this.m_thePartsInfo[Q].checkValid();	
       lRezult = lRezult && lIntermediate;
    }
    return lRezult;
};

PatternInput.prototype.submitValue = function() {
    var obj = aps.ElementById(this.m_theUplinkID);
    var rezult = '';
    if(obj!=null){
        for(var Q in this.m_thePartsInfo){
            var theItem = this.m_thePartsInfo[Q];
            if(theItem.m_nType!=PIT_TEXT){
                rezult+=theItem.m_theValue;
            }
        }
        obj.value = rezult; 
    }
    return true;
};

PatternInput.prototype.valueChanged = function(nItemIndex,theElm,theNewValue){
    var obj = this.m_thePartsInfo[nItemIndex];
    if(obj!=null){
        obj.valueChanged(theElm,theNewValue);
    }
};

aps.PatternInput = PatternInput;

//----[HOOKS]--------------------------
aps.PattenInput_KeyUpHook = function (theEditorID,theElm,nIndex,nType){
   var theInputObj =  thePatternInputRegistry[theEditorID];
   if(theInputObj!=null){       
       theInputObj.valueChanged(nIndex,theElm,theElm.value);
   }    
};

aps.PattenInput_FormSubmitHook = function (theEditorID){
    var theInputObj =  thePatternInputRegistry[theEditorID];
    if(theInputObj!=null){
    	if(theInputObj.isDisabled()){
    		return true;
    	}
        var form = aps.FindForm(aps.ElementById(theInputObj.m_theUplinkID));
        if(form!=null){
          var IVE_Mode = form.getAttribute ('ive');
          if(IVE_Mode == 'true' || aps.isIgnoreErrors()) {
              return theInputObj.submitValue();
          }
        }
        if(theInputObj.isValid()){
            return theInputObj.submitValue();
        }
        else{
            if(theInputObj.m_theWrongInputMessage!=null){
               alert(theInputObj.m_theWrongInputMessage);
            }
            return false;
        }
    }
    return true;
};


/** 
  Pattern Input Element Class
  @author GammiBear
  @version 1.0.0.0 build 0 Goblin
  */

PatternInputElement = function(theParent,nIndex,theValue,theTargetID,theMask,nType){
   this.m_nIndex = nIndex;
   this.m_theValue =  theValue;
   this.m_theTargetID = theTargetID;
   this.m_theMask = nType==PIT_EDITOR ? new RegExp(theMask,'i') : null;
   this.m_nType = nType;
   this.m_lInvalid = false;
   this.m_theParent = theParent;
};

PatternInputElement.prototype.isValid = function(){      
   return !this.m_lInvalid;
};

PatternInputElement.prototype.checkValid = function(){      
   if(this.m_nType != PIT_EDITOR) {
       return true;
   }
   var obj = aps.ElementById(this.m_theTargetID);
   if(obj!=null){
      return  this.valueChanged(obj,obj.value); 
   }
   return true;
};
  
PatternInputElement.prototype.valueChanged = function(theElm,theNewValue){
   if(this.m_nType != PIT_EDITOR) {
       return true;
   }
   this.m_theValue = theNewValue;
   if(this.m_theMask.test(theNewValue)){ 
       this.m_theParent.displayNoBugFor(theElm);       
       this.m_lInvalid = false;
   }
   else{   
       //theElm.value = this.m_theValue;
       this.m_theParent.displayBugFor(theElm,false);
       this.m_lInvalid = true;
   }
};  
}( window.aps = window.aps || {}, jQuery ));(function( aps, $, undefined ) {

  aps.ttree = {};

function loadNode (tagID, toNodeID, nodePath)
{
  var loadURL = window.location.pathname + "?exclusive_mode=tagID=" + tagID + ";fromNodePath="+nodePath + ";expanded=true;";

  $.ajax ({
      url      : loadURL,
      cache    : false,
      context  : document.getElementById(toNodeID),
      dataType : "html",
	  success  : function (data) {     
		$(this.cells[1]).html(data);
      },
      complete : function () {
        $(this).attr ('nodeLoaded', 'true');
        $(this).css('display', '');
      }
  });
}

function loadExpNode (tagID, toNodeID, nodePath)
{
  var loadURL = window.location.pathname + "?exclusive_mode=tagID=" + tagID + ";fromNodePath="+nodePath + ";expanded=false;";   

  $.ajax ({
      url      : loadURL,
      cache    : false,
      context  : document.getElementById(toNodeID),
      dataType : "html",
      complete : function () {
        $(this).css('display', 'none');
      }
  });
}

function checkContains (elm, obj) {  return elm === obj || $.contains (elm, obj); }

function treeViewGetNodeInfo (evt, treeview, node)
{
  var Elm = node;
  var EventSrc = evt != null ? aps.getEventSource(evt) : null;

/** WTF this means ? */
  if (Elm == null)
  {
    Elm = EventSrc; 

    if (Elm.tagName == 'INPUT')
      return null;

    while (Elm != null && (Elm.nodeType != 1 || aps.isEmptyAttr (Elm, 'tn')))
    {
      Elm = Elm.parentNode;
    }
  }

  if (Elm == null || Elm.nodeType != 1 || aps.isEmptyAttr (Elm, 'tn'))
    return null;

  var result = {
    node : Elm,
    subNodeId : Elm.getAttribute ('tsb'),
    isFolder  : !aps.isEmptyAttr (Elm, 'tsb'),
    treeview  : treeview
  };

  //var cArr = Elm.cells; //childNodes;

  $(Elm.cells).each (function () {
	if ($(this).attr('nc') == '1') {
	  result.controlNode = this;
      if (EventSrc && checkContains (this, EventSrc))
		result.eventNode = this;
    }
    else if ($(this).attr('nct') == '1') {
	  result.contentNode = this;
 	  if (EventSrc != null && checkContains (this, EventSrc))
	    result.eventNode = this;
    }	  
  });

  return result;
}


function treeViewExpColNode (NodeInfo)
{
// var textRange = document.body.createTextRange ();
// textRange.moveToElementText (divNode);
// textRange.select ();

 var SubNode = document.getElementById (NodeInfo.subNodeId);

 var expText = NodeInfo.treeview.getAttribute ('expText');
 var colText = NodeInfo.treeview.getAttribute ('colText');

 if (expText == null || expText.length == 0)
   expText = '';

 if (colText == null || colText.length == 0)
   colText = '';

 var MyNodeExpanded;

  if (SubNode != null)
    MyNodeExpanded = SubNode.style.display != 'none';
  else
    MyNodeExpanded = NodeInfo.node.getAttribute ('tsb') == '-';

 if (MyNodeExpanded && NodeInfo.eventNode == NodeInfo.contentNode)
    return;

 if (NodeInfo.treeview.getAttribute ('dynLoad') == 'true')
 {
   if (MyNodeExpanded)
     //SubNode.setAttribute ('nodeLoaded', 'true');
	   loadExpNode (NodeInfo.treeview.id, NodeInfo.subNodeId, NodeInfo.node.getAttribute ('np'));
   else
   if (SubNode.getAttribute ('nodeLoaded') != 'true')
   {
     if (!aps.IsParameterExists (aps.FindForm (NodeInfo.treeview), 'action_ref'))
       loadNode (NodeInfo.treeview.id, NodeInfo.subNodeId, NodeInfo.node.getAttribute ('np'));
   }
 }

  var Form = aps.FindForm (NodeInfo.treeview);

  var State = Form != null ? document.getElementById (NodeInfo.node.id + '_state') : null;
  if (State != null)
    State.parentNode.removeChild (State);


  if (MyNodeExpanded)
  {
    if (Form != null)
      aps.CreateParameter (null, 
		     NodeInfo.treeview.getAttribute ('colPaths'),
		     NodeInfo.node.getAttribute ('np'),
		     Form,
		     NodeInfo.node.id + '_state');

    if (SubNode != null)
      SubNode.style.display = 'none';
    else
      NodeInfo.node.setAttribute ('tsb', '+');

    $(NodeInfo.controlNode).text(colText);
  }
  else
  {
    if (Form != null)
      aps.CreateParameter (null, 
		     NodeInfo.treeview.getAttribute ('expPaths'),
		     NodeInfo.node.getAttribute ('np'),
		     Form,
		     NodeInfo.node.id + '_state');

    if (SubNode != null)
      SubNode.style.display = '';
    else
      NodeInfo.node.setAttribute ('tsb', '-');

    $(NodeInfo.controlNode).text (expText);
  }
}


function treeViewSelectNode (NodeInfo)
{
  var origClass = NodeInfo.contentNode.getAttribute ('origClass');

  var slClass = NodeInfo.treeview.getAttribute ('nodeSelClass');

  var oldSelNode = document.getElementById (NodeInfo.treeview.getAttribute ('selNodeId'));

  if (oldSelNode != null) 
  {
    var oldNodeInfo = treeViewGetNodeInfo (null, NodeInfo.treeview, oldSelNode);

    if (oldNodeInfo != null)
    {
      oldNodeInfo.node.removeAttribute ('nodeSelected');
      treeViewNormalizeNode (oldNodeInfo);
    }
  }

  if (origClass != null && origClass.length > 0)
  {
    NodeInfo.contentNode.className = slClass + " " + origClass;
  }
  else
    NodeInfo.contentNode.className = slClass;

  var StateID = NodeInfo.treeview.getAttribute ("selPath");

  var Form = aps.FindForm (NodeInfo.treeview);

  if (Form != null)
  {
    if (Form.elements[StateID] == null)
      aps.CreateParameter (null, 
		     StateID,
		     NodeInfo.node.getAttribute ('np'),
		     Form,
		     StateID);
    else
      aps.SetParameter (Form, StateID, NodeInfo.node.getAttribute ('np'));
  }


  NodeInfo.treeview.setAttribute ('selNodeId', NodeInfo.node.id);
  NodeInfo.node.setAttribute ('nodeSelected', 'true');
}

function treeViewHighlightNode (NodeInfo)
{
  if (NodeInfo.node.getAttribute ('nodeSelected') == 'true')
    return;

  var origClass = NodeInfo.contentNode.className;
  var hlClass = NodeInfo.treeview.getAttribute ('nodeHlClass');

  if (origClass != null && origClass.length > 0)
  {
    NodeInfo.contentNode.setAttribute ('origClass', origClass);
    NodeInfo.contentNode.className = origClass + ' ' + hlClass;
  }
  else
    NodeInfo.contentNode.className = hlClass;
}

function treeViewNormalizeNode (NodeInfo)
{
  if (NodeInfo.node.getAttribute ('nodeSelected') == 'true')
    return;

  var origClass = NodeInfo.contentNode.getAttribute ('origClass');

  if (origClass != null)
    NodeInfo.contentNode.className = origClass;
  else
    NodeInfo.contentNode.className = '';
}

/**
*
* EXT API
*
*/
  aps.ttree.treeViewClick = function (evt, treeview)
  {
    var NodeInfo = treeViewGetNodeInfo (evt, treeview);
    if (NodeInfo == null)
      return;

    treeViewSelectNode (NodeInfo);

    if (NodeInfo.isFolder)
      treeViewExpColNode (NodeInfo);

    var actionID = treeview.getAttribute ('selectAction');
    var actionProp = treeview.getAttribute ('selectActionProp');

    if (actionID != null && actionID.length > 0 && NodeInfo.eventNode == NodeInfo.contentNode)
      aps.ExecuteAction (evt, treeview, actionID, actionProp);
    else
    if (treeview.getAttribute ('commit') == 'true' && NodeInfo.eventNode == NodeInfo.contentNode)
    {
      if (treeview.getAttribute ('ive') == 'true')
  		aps.SubmitForm (aps.FindForm (treeview), true);
  	  else
	    aps.SubmitFormBy (treeview);
    }
  }


  aps.ttree.treeViewOver = function (evt, treeview)
  {
    var NodeInfo = treeViewGetNodeInfo (evt, treeview);

    if (NodeInfo == null)
     return;

    if (NodeInfo.contentNode != null)
    {
      treeViewHighlightNode (NodeInfo);
    }
  }

  aps.ttree.treeViewOut = function (evt, treeview)
  {
    var NodeInfo = treeViewGetNodeInfo (evt, treeview);

    if (NodeInfo == null)
     return;

    if (NodeInfo.contentNode != null)
    {
      treeViewNormalizeNode (NodeInfo);
    }
  }
}(window.aps = window.aps || {}, jQuery ));(function( aps, $, undefined ) {
aps.tree = {
  treeExpNode : function (parentNode, node) {
    var ctrlImg = eval (parentNode.getAttribute ("expImg"));
    var fldImg = eval (parentNode.getAttribute ("foImg"));

    parentNode.cells [0].firstChild.src = ctrlImg.src;
    parentNode.cells [1].firstChild.src = fldImg.src;

    aps.CreateParameter (parentNode, 
			parentNode.getAttribute ('expProp'),
			parentNode.getAttribute ('nodePath'),
			null,
			parentNode.getAttribute ('stID'));

    node.style.display = '';
  },

  treeColNode : function (parentNode, node) {
    var ctrlImg = eval (parentNode.getAttribute ("colImg"));
    var fldImg = eval (parentNode.getAttribute ("fcImg"));

    parentNode.cells [0].firstChild.src = ctrlImg.src;
    parentNode.cells [1].firstChild.src = fldImg.src;

    var stateCtrl = document.getElementById (parentNode.getAttribute ("stID"));

    stateCtrl.parentNode.removeChild (stateCtrl);

    node.style.display = 'none';
  },

  treeCHS : function (pNodeID, cNodeID)
  {
    prNode = document.getElementById (pNodeID);
    chNode = document.getElementById (cNodeID);

    if (chNode.style.display == 'none')
      aps.tree.treeExpNode (prNode, chNode);
    else
      aps.tree.treeColNode (prNode, chNode);
  }
};
}( window.aps = window.aps || {}, jQuery ));