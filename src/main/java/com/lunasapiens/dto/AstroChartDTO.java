package com.lunasapiens.dto;

import java.util.List;
import java.util.Map;

public class AstroChartDTO {

    private Map<String,Object[]> planets;
    private List<Double> cusps;


    public AstroChartDTO(
            Map<String,Object[]> planets,
            List<Double> cusps
    ){
        this.planets = planets;
        this.cusps = cusps;
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

}
