package com.example.model;

import java.util.List;

public class Bot {
    private int level;
    private List<Integer> times;
    private List<Integer> errors;

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Integer> getTimes() {
        return this.times;
    }

    public void setTimes(List<Integer> times) {
        this.times = times;
    }

    public List<Integer> getErrors() {
        return this.errors;
    }

    public void setErrors(List<Integer> errors) {
        this.errors = errors;
    }

    public int findTimeByDiff(int difficulty) {
        return times.get(difficulty);
    }

    public int findErrorByDiff(int difficulty) {
        return errors.get(difficulty);
    }
}
