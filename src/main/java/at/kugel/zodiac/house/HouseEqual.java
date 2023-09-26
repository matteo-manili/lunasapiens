package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public final class HouseEqual extends HouseBasic {
    protected void calcHouses() {
        if (this.latR < this.range[0] || this.latR > this.range[1])
            return;
        for (byte b = 0; b < 12; b++)
            this.housesR[b] = CalcUtil.Mod2PI(this.ascendant + 0.5235987755982988D * b + this.siderealOffset);
    }

    public String getHouseName() {
        return "Equal";
    }

    public double[] getValidityRange() {
        return this.range;
    }
}
