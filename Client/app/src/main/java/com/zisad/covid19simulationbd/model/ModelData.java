package com.zisad.covid19simulationbd.model;

public class ModelData {
    private double population;
    private int initial_infected, incubation_period, infection_period, model;
    private float r0value;
    private float r1value;
    private String start_date;
    private String intervention_date;

    public double getPopulation() {
        return population;
    }

    public void setPopulation(double population) {
        this.population = population;
    }

    public int getInitial_infected() {
        return initial_infected;
    }

    public void setInitial_infected(int initial_infected) {
        this.initial_infected = initial_infected;
    }

    public float getR0value() {
        return r0value;
    }

    public void setR0value(float r0value) {
        this.r0value = r0value;
    }

    public float getR1value() {
        return r1value;
    }

    public void setR1value(float r1value) {
        this.r1value = r1value;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getIntervention_date() {
        return intervention_date;
    }

    public void setIntervention_date(String intervention_date) {
        this.intervention_date = intervention_date;
    }

    public int getIncubation_period() {
        return incubation_period;
    }

    public void setIncubation_period(int incubation_period) {
        this.incubation_period = incubation_period;
    }

    public int getInfection_period() {
        return infection_period;
    }

    public void setInfection_period(int infection_period) {
        this.infection_period = infection_period;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }
}
