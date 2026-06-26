package com.lunasapiens.dto;

import java.util.List;
import java.util.Map;

public class AstroChartDTO {

    private Map<String,Object[]> planets;
    private List<Double> cusps;

    private double ascendente;
    private double medioCielo;

    public AstroChartDTO(
            Map<String,Object[]> planets,
            List<Double> cusps
    ){
        this.planets = planets;
        this.cusps = cusps;

        this.ascendente = cusps.get(0);
        this.medioCielo = cusps.get(9);
    }


    public Map<String, Object[]> getPlanets() {
        return planets;
    }

    public void setPlanets(Map<String, Object[]> planets) {
        this.planets = planets;
    }

    public List<Double> getCusps() {
        return cusps;
    }

    public void setCusps(List<Double> cusps) {
        this.cusps = cusps;
    }

    public double getAscendente() {
        return ascendente;
    }

    public void setAscendente(double ascendente) {
        this.ascendente = ascendente;
    }

    public double getMedioCielo() {
        return medioCielo;
    }

    public void setMedioCielo(double medioCielo) {
        this.medioCielo = medioCielo;
    }
}
