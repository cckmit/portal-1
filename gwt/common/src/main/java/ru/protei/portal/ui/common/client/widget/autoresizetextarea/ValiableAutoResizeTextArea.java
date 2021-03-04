package ru.protei.portal.ui.common.client.widget.autoresizetextarea;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class ValiableAutoResizeTextArea extends AutoResizeTextArea implements HasValidable {

    public ValiableAutoResizeTextArea() {
        super();
        addDomHandler(event -> validationTimer.schedule(50), InputEvent.getType());
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        validateValue();
    }

    @Override
    public void setValid(boolean isValid) {
        if(isValid)
            removeStyleName( REQUIRED_STYLE_NAME );
        else
            addStyleName( REQUIRED_STYLE_NAME );
    }

    @Override
    public boolean isValid() {
        String valueString = getText();
        boolean isRegexMatched = regexp.test(valueString);

        return (!isNotNull && isEmpty(valueString)) || isRegexMatched;
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
