package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public final class HouseEqualMid extends HouseBasic {
    private final double[] subRange = new double[] { -CalcUtil.RFromD(66.55421111D), 1.5707963267948966D };

    protected void calcHouses() {
        if (this.latR < this.subRange[0] || this.latR > this.subRange[1])
            return;
        for (byte b = 0; b < 12; b++)
            this.housesR[b] = CalcUtil.Mod2PI(this.midHeaven + 1.5707963267948966D + 0.5235987755982988D * b + this.siderealOffset);
    }

    public String getHouseName() {
        return "EqualMidheaven";
    }

    public double[] getValidityRange() {
        return this.subRange;
    }
}
