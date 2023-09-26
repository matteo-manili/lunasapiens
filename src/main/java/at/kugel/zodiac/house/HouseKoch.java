package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public final class HouseKoch extends HouseBasic {
    private final double[] subRange = new double[] { -CalcUtil.RFromD(66.55421111D), CalcUtil.RFromD(66.55421111D) };

    protected void calcHouses() {
        if (this.latR < this.subRange[0] || this.latR > this.subRange[1])
            return;
        double d1 = Math.asin(Math.sin(this.rightAscension) * Math.tan(this.latR) * Math.tan(this.eclipticObliquity));
        double d2 = Math.cos(this.eclipticObliquity);
        double d3 = Math.sin(this.eclipticObliquity) * Math.tan(this.latR);
        for (byte b = 0; b < 12; b++) {
            double d4;
            double d6;
            double d7 = CalcUtil.Mod2PI(0.5235987755982988D * b + 1.5707963267948966D);
            if (d7 >= Math.PI) {
                d6 = -1.0D;
                d4 = d7 / 1.5707963267948966D - 3.0D;
            } else {
                d6 = 1.0D;
                d4 = d7 / 1.5707963267948966D - 1.0D;
            }
            double d5 = CalcUtil.Mod2PI(this.rightAscension + d7 + d4 * d1);
            double d8 = CalcUtil.Angle(Math.cos(d5) * d2 - d6 * d3, Math.sin(d5));
            if (this.siderealOffset != 0.0D) {
                this.housesR[b] = CalcUtil.Mod2PI(d8 + this.siderealOffset);
            } else {
                this.housesR[b] = d8;
            }
        }
    }

    public String getHouseName() {
        return "Koch";
    }

    public double[] getValidityRange() {
        return this.subRange;
    }
}
