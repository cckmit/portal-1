package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.protei.portal.core.model.yt.fields.DateField;
import ru.protei.portal.core.model.yt.fields.Field;
import ru.protei.portal.core.model.yt.fields.StringField;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by admin on 15/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Change {

//    public static class Field {
//        public String name;
//        public String value;
//        public List<String> oldValue;
//        public List<String> newValue;
//    }

    private List<Field> field;

    public List< Field > getField() {
        return field;
    }

    public void setField( List< Field > field ) {
        this.field = field;
    }

    public Optional<Double> getElapsedTime() {
        Optional<String> newValue = getFieldByname("Затраченное время")
                .map( (f)->(((StringField)f).getNewValue()) )
                .filter( (list)->!list.isEmpty() )
                .map( (list)->list.get( 0 ) );

        if ( !newValue.isPresent() ) {
            return Optional.empty();
        }

        Double newDouble = convertToHours( newValue.get() );

        Optional<String> oldValue = getFieldByname("Затраченное время")
                .map( (f)->(((StringField)f).getOldValue()) )
                .filter( (list)->!list.isEmpty() )
                .map( (list)->list.get( 0 ) );

        Double oldDouble = 0.0;
        if ( oldValue.isPresent() ) {
            String oldString = oldValue.get();
            if ( !oldString.equalsIgnoreCase( "?" ) ) {
                if ( !oldString.isEmpty() ) {
                    oldDouble = convertToHours( oldString );
                }
            }
        }

        return Optional.of( newDouble - oldDouble );
    }

    public Optional<String> getUpdaterName() {
        return getFieldByname("updaterName")
                .map( (f)->((StringField)f).getValue() );
    }

    public Optional<Date> getUpdated() {
        return getFieldByname("updated")
                .map( (f)->(((DateField)f).getValue() ) );

    }
    //comment here

    private Optional<Field> getFieldByname( String name ) {
        if (Objects.isNull( field ) ) {
            return Optional.empty();
        }

        return field.stream()
                .filter((f)->f != null )
                .filter((f)->name.equalsIgnoreCase( f.name ) )
                .findFirst();
    }

    private Double convertToHours( String value ) {
        Double hours = 0.0;

        System.out.println("hours(н)="+value);
        int weeksPos = value.indexOf('н');
        if ( weeksPos != -1 ) {
            String weeksAmount = value.substring(0, weeksPos);
            System.out.println("hours, weeksAmount="+weeksAmount);
            hours += Double.parseDouble( weeksAmount )*40;
            value = value.substring(weeksPos+1);
        }

        System.out.println("hours(д)="+value);
        int daysPos = value.indexOf('д');
        if ( daysPos != -1 ) {
            String daysAmount = value.substring(0, daysPos);
            System.out.println("hours, daysAmount="+daysAmount);
            hours += Double.parseDouble( daysAmount )*8;
            value = value.substring(daysPos+1);
        }

        System.out.println("hours(ч)="+value);
        int hoursPos = value.indexOf("ч");
        if ( hoursPos != -1 ) {
            String hoursAmount= value.substring(0, hoursPos);
            System.out.println("hours, hoursAmount="+hoursAmount);
            hours += Double.parseDouble( hoursAmount );
            value = value.substring(hoursPos+1);
        }

        System.out.println("hours(м)="+value);
        int minutesPos = value.indexOf("м");
        if ( minutesPos != -1 ) {
            String minutesAmount= value.substring(0, minutesPos);
            System.out.println("hours, minutesAmount="+minutesAmount);
            hours += Double.parseDouble( minutesAmount)/60.0;
            value = value.substring(minutesPos+1);
        }
        return hours;
    }

    @Override
    public String toString() {
        return "Change{" +
                "field=" + field +
                '}';
    }
}
