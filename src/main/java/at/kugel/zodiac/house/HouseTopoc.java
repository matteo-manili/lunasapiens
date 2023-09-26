package at.kugel.zodiac.house;

import at.kugel.zodiac.util.CalcUtil;

public final class HouseTopoc extends HouseBasic {
    private final double[] subRange = new double[] { -CalcUtil.RFromD(66.55421111D), 1.5707963267948966D };

    private double CuspTopocentric(double paramDouble1, double paramDouble2) {
        double d1 = CalcUtil.Mod2PI(this.rightAscension + CalcUtil.RFromD(paramDouble1));
        double d2 = Math.atan(Math.tan(paramDouble2) / Math.cos(d1));
        double d3 = Math.atan(Math.cos(d2) * Math.tan(d1) / Math.cos(d2 + this.eclipticObliquity));
        if (d3 < 0.0D)
            d3 += Math.PI;
        if (Math.sin(d1) < 0.0D)
            d3 += Math.PI;
        return d3;
    }

    protected void calcHouses() {
        if (this.latR < this.subRange[0] || this.latR > this.subRange[1])
            return;
        double d1 = Math.tan(this.latR);
        double d2 = Math.atan(d1 / 3.0D);
        double d3 = Math.atan(d1 / 1.5D);
        this.housesR[0] = CuspTopocentric(90.0D, this.latR);
        this.housesR[1] = CuspTopocentric(120.0D, d3);
        this.housesR[2] = CuspTopocentric(150.0D, d2);
        this.housesR[3] = this.midHeaven + Math.PI - this.siderealOffset;
        this.housesR[4] = CuspTopocentric(30.0D, d2) + Math.PI;
        this.housesR[5] = CuspTopocentric(60.0D, d3) + Math.PI;
        for (byte b = 0; b < 6; b++) {
            this.housesR[b] = CalcUtil.Mod2PI(this.housesR[b] + this.siderealOffset);
            this.housesR[b + 6] = CalcUtil.Mod2PI(this.housesR[b] + Math.PI);
        }
    }

    public String getHouseName() {
        return "Topocentric";
    }

    public double[] getValidityRange() {
        return this.subRange;
    }
}
