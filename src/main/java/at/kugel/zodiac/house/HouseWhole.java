package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public final class HouseWhole extends HouseBasic {
    protected void calcHouses() {
        if (this.latR < this.range[0] || this.latR > this.range[1])
            return;
        int i = CalcUtil.ZFromR(this.ascendant, 3) - 1;
        for (byte b = 0; b < 12; b++)
            this.housesR[b] = CalcUtil.Mod2PI((i + b) * 0.5235987755982988D + this.siderealOffset);
    }

    public String getHouseName() {
        return "Whole";
    }

    public double[] getValidityRange() {
        return this.range;
    }
}
