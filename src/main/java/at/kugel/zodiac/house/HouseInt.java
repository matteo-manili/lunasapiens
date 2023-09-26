package at.kugel.zodiac.house;

public interface HouseInt {
    double[] getHousesR();

    String[] getHousesText();

    String getHouseName();

    double[] getValidityRange();

    int HouseFromD(double paramDouble);

    int HouseFromR(double paramDouble);

    void setHouses(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
}
