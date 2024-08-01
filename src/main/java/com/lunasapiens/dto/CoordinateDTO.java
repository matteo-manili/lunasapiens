package com.lunasapiens.dto;



public class CoordinateDTO {

    private String cityName;
    private String regioneName;
    private String statoName;
    private String statoCode;

    // Costruttore senza parametri
    public CoordinateDTO() { }

    // Costruttore con parametri
    public CoordinateDTO(String cityName, String regioneName, String statoName, String statoCode) {
        this.cityName = cityName;
        this.regioneName = regioneName;
        this.statoName = statoName;
        this.statoCode = statoCode;
    }

    // Getter e Setter
    public String getCityName() {
        return cityName;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getRegioneName() {
        return regioneName;
    }
    public void setRegioneName(String regioneName) {
        this.regioneName = regioneName;
    }

    public String getStatoName() {
        return statoName;
    }
    public void setStatoName(String statoName) {
        this.statoName = statoName;
    }

    public String getStatoCode() {
        return statoCode;
    }
    public void setStatoCode(String statoCode) {
        this.statoCode = statoCode;
    }



    // Metodo toString
    @Override
    public String toString() {
        return "CoordinateDTO{" +
                ", cityName='" + cityName + '\'' +
                ", regioneName='" + regioneName + '\'' +
                ", statoName='" + statoName + '\'' +
                ", statoCode='" + statoCode + '\'' +
                '}';
    }
}

