package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public final class HouseRegio extends HouseBasic {
    private final double[] subRange = new double[] { -CalcUtil.RFromD(66.55421111D), 1.5707963267948966D };

    protected void calcHouses() {
        if (this.latR < this.subRange[0] || this.latR > this.subRange[1])
            return;
        double d1 = Math.cos(this.eclipticObliquity);
        double d2 = Math.tan(this.latR) * Math.sin(this.eclipticObliquity);
        for (byte b = 0; b < 12; b++) {
            double d3 = 0.5235987755982988D * b + 1.5707963267948966D;
            double d4 = CalcUtil.Angle(Math.cos(this.rightAscension + d3) * d1 - Math.sin(d3) * d2, Math.sin(this.rightAscension + d3));
            if (this.siderealOffset != 0.0D) {
                this.housesR[b] = CalcUtil.Mod2PI(d4 + this.siderealOffset);
            } else {
                this.housesR[b] = d4;
            }
        }
    }

    public String getHouseName() {
        return "Regiomontanus";
    }

    public double[] getValidityRange() {
        return this.subRange;
    }
}
