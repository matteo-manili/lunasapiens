package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public final class HouseCampan extends HouseBasic {
    private final double[] subRange = new double[] { -CalcUtil.RFromD(66.55421111D), 1.5707963267948966D };

    protected void calcHouses() {
        if (this.latR < this.subRange[0] || this.latR > this.subRange[1])
            return;
        double d1 = Math.cos(this.eclipticObliquity);
        double d2 = Math.sin(this.eclipticObliquity) * Math.tan(this.latR);
        double d3 = Math.cos(this.latR);
        for (byte b = 0; b < 12; b++) {
            double d4 = CalcUtil.Mod2PI(0.5235987755982988D * b + 1.5707963267948966D + 1.7E-8D);
            double d5 = Math.atan(Math.tan(d4) * d3);
            if (d5 < 0.0D)
                d5 += Math.PI;
            if (Math.sin(d4) < 0.0D)
                d5 += Math.PI;
            double d6 = CalcUtil.Angle(Math.cos(this.rightAscension + d5) * d1 - Math.sin(d5) * d2, Math.sin(this.rightAscension + d5));
            if (this.siderealOffset != 0.0D) {
                this.housesR[b] = CalcUtil.Mod2PI(d6 + this.siderealOffset);
            } else {
                this.housesR[b] = d6;
            }
        }
    }

    public String getHouseName() {
        return "Campanus";
    }

    public double[] getValidityRange() {
        return this.subRange;
    }
}
