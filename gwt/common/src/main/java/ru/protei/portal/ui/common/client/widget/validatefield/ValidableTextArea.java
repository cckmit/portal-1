package ru.protei.portal.ui.common.client.widget.validatefield;


import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextArea;

/**
 * TextArea c возможностью валидации
 */
public class ValidableTextArea extends TextArea implements HasValidable{

    public ValidableTextArea(){
        super();
        addBlurHandler(blurEvent -> {
            timer.cancel();
            timer.run();
        });
        addKeyPressHandler(keyPressEvent -> {
            timer.cancel();
            timer.schedule(200);
        });
    }

    @Override
    public void setValid(boolean isValid) {
        this.isValid = isValid;
        if(isValid)
            removeStyleName("error");
        else
            addStyleName("error");
    }

    @Override
    public boolean isValid() {
        timer.run();
        return isValid;
    }


    // делает поле псевдовалидным
    @Override
    public void reset(){
        setValid(true);
        isValid = false;
    }

    public void setRegexp( String regexp ){
        this.regexp = RegExp.compile(regexp);
    }


    private RegExp regexp;
    private boolean isValid;

    Timer timer = new Timer() {
        @Override
        public void run() {
            if(regexp == null){
                regexp = RegExp.compile(".*\\S.*"); //"not empty input" by default
            }
            if(regexp.test(getValue()))
                setValid(true);
            else
                setValid(false);
        }
    };
}
