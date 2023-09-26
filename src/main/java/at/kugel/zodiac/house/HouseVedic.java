package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public final class HouseVedic extends HouseBasic {
    protected void calcHouses() {
        if (this.latR < this.range[0] || this.latR > this.range[1])
            return;
        for (byte b = 0; b < 12; b++)
            this.housesR[b] = CalcUtil.Mod2PI(this.ascendant - 0.2617993877991494D + 0.5235987755982988D * b + this.siderealOffset);
    }

    public String getHouseName() {
        return "Vedic";
    }

    public double[] getValidityRange() {
        return this.range;
    }
}
