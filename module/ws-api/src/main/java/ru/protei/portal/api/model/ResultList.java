package ru.protei.portal.api.model;

import ru.protei.portal.api.struct.Result;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="results")
public class ResultList {
    private List<Result> results = new ArrayList<>();

    public  ResultList() {}

    @XmlElement(name="result")
    public List<Result> getResults() {
        return results;
    }

    public void append (Result result) {
        this.results.add(result);
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ResultList{");
        for (Result result : results) {
            stringBuilder.append(result);
        }

        return stringBuilder.toString();
    }
}
