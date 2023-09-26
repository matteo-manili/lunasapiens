package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public abstract class HouseBasic implements HouseInt {
    protected static final int NUMBER_HOUSE = 12;

    protected static final String[] NAME_HOUSE = new String[] {
            "AC", "2", "3", "IC", "5", "6", "DC", "8", "9", "MC",
            "11", "12" };

    protected static final int ANBLE_HOUSE_D = 30;

    protected static final double ANGLE_HOUSE_R = 0.5235987755982988D;

    protected static final double rAxis = 23.44578889D;

    protected double[] range = new double[] { -1.5707963267948966D, 1.5707963267948966D };

    protected double latR;

    protected double rightAscension;

    protected double eclipticObliquity;

    protected double siderealOffset;

    protected double ascendant;

    protected double midHeaven;

    protected final double[] housesR = new double[12];

    public final double getAscendant() {
        return this.ascendant;
    }

    public final double getMidHeaven() {
        return this.midHeaven;
    }

    public final double[] getHousesR() {
        return this.housesR;
    }

    public final String[] getHousesText() {
        return NAME_HOUSE;
    }

    public final double getHousesR(int paramInt) {
        return this.housesR[paramInt];
    }

    public final int HouseFromD(double paramDouble) {
        return HouseFromR(CalcUtil.RFromD(paramDouble));
    }

    public final int HouseFromR(double paramDouble) {
        boolean bool = (this.housesR[11] > this.housesR[0]) ? true : false;
        if (bool) {
            if (this.housesR[11] <= paramDouble || paramDouble < this.housesR[0])
                return 11;
        } else if (this.housesR[11] <= paramDouble && paramDouble < this.housesR[0]) {
            return 11;
        }
        for (byte b = 0; b < 11; b++) {
            bool = (this.housesR[b] > this.housesR[b + 1]) ? true : false;
            if (bool) {
                if (this.housesR[b] <= paramDouble || paramDouble < this.housesR[b + 1])
                    return b;
            } else if (this.housesR[b] <= paramDouble && paramDouble < this.housesR[b + 1]) {
                return b;
            }
        }
        throw new RuntimeException("invalid angle in HouseFromR");
    }

    public final void setHouses(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
        this.latR = paramDouble1;
        this.rightAscension = paramDouble2;
        this.eclipticObliquity = paramDouble3;
        this.siderealOffset = paramDouble4;
        calcBasicAngles();
        calcHouses();
    }

    protected final void calcBasicAngles() {
        this.ascendant = CalcUtil.Angle(-Math.sin(this.rightAscension) * Math.cos(this.eclipticObliquity) - Math.tan(this.latR) * Math.sin(this.eclipticObliquity), Math.cos(this.rightAscension));
        if (this.siderealOffset != 0.0D)
            this.ascendant = CalcUtil.Mod2PI(this.ascendant + this.siderealOffset);
        this.midHeaven = CalcUtil.Angle(Math.cos(this.eclipticObliquity), Math.tan(this.rightAscension));
        if (this.siderealOffset != 0.0D)
            this.midHeaven = CalcUtil.Mod2PI(this.midHeaven + this.siderealOffset);
        if (CalcUtil.MinDifference(this.midHeaven, this.ascendant) < 0.0D)
            this.midHeaven = CalcUtil.Mod2PI(this.midHeaven + Math.PI);
    }

    protected void calcHouses() {
        if (this.latR < this.range[0] || this.latR > this.range[1])
            return;
        for (byte b = 0; b < 12; b++)
            this.housesR[b] = b * 0.5235987755982988D;
    }

    public String getHouseName() {
        return "Null";
    }

    public double[] getValidityRange() {
        return this.range;
    }

    public final String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("House \"");
        stringBuffer.append(getHouseName());
        stringBuffer.append("\" [");
        for (byte b = 0; b < 12; b++) {
            stringBuffer.append(NAME_HOUSE[b]);
            stringBuffer.append(':');
            CalcUtil.HMStringFromR(stringBuffer, this.housesR[b]);
            stringBuffer.append("; ");
            if (b == 5)
                stringBuffer.append('\n');
        }
        stringBuffer.append(']');
        return stringBuffer.toString();
    }
}
