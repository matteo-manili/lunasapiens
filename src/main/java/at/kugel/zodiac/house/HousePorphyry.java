package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public final class HousePorphyry extends HouseBasic {
    private final double[] subRange = new double[] { -CalcUtil.RFromD(66.55421111D), 1.5707963267948966D };

    protected void calcHouses() {
        if (this.latR < this.subRange[0] || this.latR > this.subRange[1])
            return;
        this.housesR[0] = CalcUtil.Mod2PI(this.ascendant + this.siderealOffset);
        double d = CalcUtil.Mod2PI(Math.PI + this.midHeaven - this.ascendant) / 3.0D;
        this.housesR[1] = CalcUtil.Mod2PI(this.ascendant + d + this.siderealOffset);
        this.housesR[2] = CalcUtil.Mod2PI(this.ascendant + 2.0D * d + this.siderealOffset);
        this.housesR[3] = CalcUtil.Mod2PI(this.ascendant + 3.0D * d + this.siderealOffset);
        d = CalcUtil.Mod2PI(this.ascendant - this.midHeaven) / 3.0D;
        this.housesR[4] = CalcUtil.Mod2PI(Math.PI + this.midHeaven + d + this.siderealOffset);
        this.housesR[5] = CalcUtil.Mod2PI(Math.PI + this.midHeaven + 2.0D * d + this.siderealOffset);
        for (byte b = 0; b < 6; b++)
            this.housesR[b + 6] = CalcUtil.Mod2PI(this.housesR[b] + Math.PI);
    }

    public String getHouseName() {
        return "Porphyry";
    }

    public double[] getValidityRange() {
        return this.subRange;
    }
}
