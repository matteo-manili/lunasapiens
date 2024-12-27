package com.lunasapiens.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AstroChartDTO {

    private Map<String, Object[]> planets;
    private List<Integer> cusps;


    public AstroChartDTO(Map<String, Object[]> planets, List<Integer> cusps) {
        this.planets = planets;
        this.cusps = cusps;
    }

    public Map<String, Object[]> getPlanets() {
        return planets;
    }
    public void setPlanets(Map<String, Object[]> planets) {
        this.planets = planets;
    }

    public List<Integer> getCusps() {
        return cusps;
    }
    public void setCusps(List<Integer> cusps) {
        this.cusps = cusps;
    }
}
