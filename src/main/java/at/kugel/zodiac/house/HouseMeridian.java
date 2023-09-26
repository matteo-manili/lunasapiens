package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public final class HouseMeridian extends HouseBasic {
    protected void calcHouses() {
        if (this.latR < this.range[0] || this.latR > this.range[1])
            return;
        double d = Math.cos(this.eclipticObliquity);
        for (byte b = 0; b < 12; b++) {
            double d1 = 0.5235987755982988D * b + 1.5707963267948966D;
            double d2 = CalcUtil.Angle(Math.cos(this.rightAscension + d1) * d, Math.sin(this.rightAscension + d1));
            if (this.siderealOffset != 0.0D) {
                this.housesR[b] = CalcUtil.Mod2PI(d2 + this.siderealOffset);
            } else {
                this.housesR[b] = d2;
            }
        }
    }

    public String getHouseName() {
        return "Meridian";
    }

    public double[] getValidityRange() {
        return this.range;
    }
}
