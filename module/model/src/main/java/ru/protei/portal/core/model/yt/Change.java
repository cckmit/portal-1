package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.protei.portal.core.model.yt.fields.Fields;
import ru.protei.portal.core.model.yt.fields.change.ChangeField;
import ru.protei.portal.core.model.yt.fields.change.DateChangeField;
import ru.protei.portal.core.model.yt.fields.change.StringChangeField;

import java.util.Date;
import java.util.List;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Change {

    private List<ChangeField> field;
    private List<Comment> comment;

    public List<ChangeField> getField() {
        return field;
    }

    public void setField(List<ChangeField> field ) {
        this.field = field;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

    private <T extends ChangeField> T getField(String name) {
        if (field == null || name == null) {
            return null;
        }

        ChangeField fieldValue = field.stream()
                .filter(Objects::nonNull)
                .filter((field) -> name.equals(field.getName()))
                .findFirst()
                .orElse(null);

        if (fieldValue == null)
            return null;
        return (T) fieldValue;
    }

    public Date getUpdated() {
        DateChangeField dateChangeField = getField(Fields.updated);
        if (dateChangeField == null)
            return null;
        return dateChangeField.getValue();
    }

    public String getUpdaterName() {
        StringChangeField stringChangeField = getField(Fields.updaterName);
        if (stringChangeField == null)
            return null;
        return stringChangeField.getValue();
    }

//    public Optional<Double> getElapsedTime() {
//        Optional<String> newValue = getFieldByname("Затраченное время")
//                .map( (f)->(((Str)f).getNewValue()) )
//                .filter( (list)->!list.isEmpty() )
//                .map( (list)->list.get( 0 ) );
//
//        if ( !newValue.isPresent() ) {
//            return Optional.empty();
//        }
//
//        Double newDouble = convertToHours( newValue.get() );
//
//        Optional<String> oldValue = getFieldByname("Затраченное время")
//                .map( (f)->(((StringIssueField)f).getOldValue()) )
//                .filter( (list)->!list.isEmpty() )
//                .map( (list)->list.get( 0 ) );
//
//        Double oldDouble = 0.0;
//        if ( oldValue.isPresent() ) {
//            String oldString = oldValue.get();
//            if ( !oldString.equalsIgnoreCase( "?" ) ) {
//                if ( !oldString.isEmpty() ) {
//                    oldDouble = convertToHours( oldString );
//                }
//            }
//        }
//
//        return Optional.of( newDouble - oldDouble );
//    }


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
                ", comment=" + comment +
                '}';
    }
}
