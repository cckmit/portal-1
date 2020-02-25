package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextBoxBase;
import ru.protei.portal.ui.common.client.events.InputEvent;

/**
 * Created by bondarenko on 08.11.16.
 */
abstract class ValidableTextBoxBase extends TextBoxBase implements HasValidable{

    ValidableTextBoxBase(Element elem){
        super(elem);
        addDomHandler(event -> validationTimer.schedule(50), InputEvent.getType());
    }

    @Override
    public void setValid(boolean isValid) {
        if(isValid)
            removeStyleName( REQUIRED_STYLE_NAME );
        else
            addStyleName( REQUIRED_STYLE_NAME );
    }

    @Override
    public void setValue( String value ) {
        super.setValue( value );
        validateValue();
    }

    @Override
    public boolean isValid() {
        return regexp.test( getValue() );
    }

    public void setPlaceholder(String placeholder ) {
        getElement().setAttribute("placeholder", placeholder);
    }

    public void setRegexp( String regexp ){
        this.regexp = RegExp.compile(regexp);
    }

    public void setNotNull( boolean value ) {
        this.isNotNull = value;
    }

    private void validateValue() {
        boolean isValid = (!isNotNull && getValue().isEmpty()) || isValid();
        setValid( isValid );
    }

    Timer validationTimer = new Timer(){
        @Override
        public void run() {
            validateValue();
        }
    };

    private RegExp regexp = RegExp.compile( "\\S+" );
    private boolean isNotNull = true;
    private static final String REQUIRED_STYLE_NAME="required";
}
