package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public final class HousePlacidus extends HouseBasic {
    private final double[] subRange = new double[] { -CalcUtil.RFromD(66.55421111D), CalcUtil.RFromD(66.55421111D) };

    private double CuspPlacidus(double paramDouble1, double paramDouble2, boolean paramBoolean) {
        double d2;
        double d1 = CalcUtil.Mod2PI(this.rightAscension + CalcUtil.RFromD(paramDouble1));
        if (paramBoolean) {
            d2 = 1.0D;
        } else {
            d2 = -1.0D;
        }
        for (byte b = 1; b <= 10; b++) {
            double d = d2 * Math.sin(d1) * Math.tan(this.eclipticObliquity) * Math.tan((this.latR == 0.0D) ? 1.0E-6D : this.latR);
            d = Math.acos(d);
            if (d < 0.0D)
                d += Math.PI;
            if (paramBoolean) {
                d1 = this.rightAscension + Math.PI - d / paramDouble2;
            } else {
                d1 = this.rightAscension + d / paramDouble2;
            }
        }
        double d3 = Math.atan(Math.tan(d1) / Math.cos(this.eclipticObliquity));
        if (d3 < 0.0D)
            d3 += Math.PI;
        if (Math.sin(d1) < 0.0D)
            d3 += Math.PI;
        return d3;
    }

    protected void calcHouses() {
        if (this.latR < this.subRange[0] || this.latR > this.subRange[1])
            return;
        this.housesR[0] = this.ascendant - this.siderealOffset;
        this.housesR[1] = CuspPlacidus(120.0D, 1.5D, true);
        this.housesR[2] = CuspPlacidus(150.0D, 3.0D, true);
        this.housesR[3] = this.midHeaven + Math.PI - this.siderealOffset;
        this.housesR[4] = CuspPlacidus(30.0D, 3.0D, false) + Math.PI;
        this.housesR[5] = CuspPlacidus(60.0D, 1.5D, false) + Math.PI;
        for (byte b = 0; b < 6; b++) {
            this.housesR[b] = CalcUtil.Mod2PI(this.housesR[b] + this.siderealOffset);
            this.housesR[b + 6] = CalcUtil.Mod2PI(this.housesR[b] + Math.PI);
        }
    }

    public String getHouseName() {
        return "Placidus";
    }

    public double[] getValidityRange() {
        return this.subRange;
    }
}
