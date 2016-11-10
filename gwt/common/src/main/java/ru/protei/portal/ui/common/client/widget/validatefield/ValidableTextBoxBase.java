package ru.protei.portal.ui.common.client.widget.validatefield;

import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextBoxBase;

/**
 * Created by bondarenko on 08.11.16.
 */
abstract public class ValidableTextBoxBase extends TextBoxBase implements HasValidable{

    ValidableTextBoxBase(Element elem){
        super(elem);
        addBlurHandler(blurEvent -> {
            validationTimer.cancel();
            validationTimer.run();
        });
        addKeyPressHandler(keyPressEvent -> {
            validationTimer.cancel();
            validationTimer.schedule(200);
        });
    }


    @Override
    public void setValid(boolean isValid) {
        if(isValid)
            removeStyleName( ERROR_STYLE_NAME );
        else
            addStyleName( ERROR_STYLE_NAME );
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

    public void setRegexp( String regexp ){
        this.regexp = RegExp.compile(regexp);
    }

    public void setNotNull( boolean value ) {
        if ( value ) {
            this.regexp = notEmptyStringRegexp;
        }
    }

    private void validateValue() {
        if ( regexp == null ) {
            throw new NullPointerException("Validation condition is not installed");
        }
        setValid( isValid() );
    }

    Timer validationTimer = new Timer(){
        @Override
        public void run() {
            validateValue();
        }
    };

    private RegExp regexp = null;
    private RegExp notEmptyStringRegexp = RegExp.compile("\\S+");
    private static final String ERROR_STYLE_NAME="error";
}
