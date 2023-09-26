package at.kugel.zodiac.planet;

import at.kugel.zodiac.util.CalcUtil;
import at.kugel.zodiac.util.XY;

public class PlanetAA0 extends PlanetBasic {
    private static double[][] Array = new double[][] { {
            358.47584D, 35999.0498D, -1.5E-4D, 0.016751D, -4.1E-5D, 0.0D, 1.00000013D, 101.22083D, 1.71918D, 4.5E-4D,
            0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, {
            102.27938D, 149472.515D, 0.0D, 0.205614D, 2.0E-5D, 0.0D, 0.387098D, 28.75375D, 0.37028D, 1.2E-4D,
            47.14594D, 1.1852D, 1.7E-4D, 7.00288D, 0.00186D, -1.0E-5D }, {
            212.60322D, 58517.8039D, 0.00129D, 0.00682D, -4.0E-5D, 0.0D, 0.723332D, 54.38419D, 0.50819D, -0.00139D,
            75.77965D, 0.89985D, 4.1E-4D, 3.39363D, 0.001D, 0.0D }, {
            319.5293D, 19139.8585D, 1.8E-4D, 0.09331D, 9.0E-5D, 0.0D, 1.52369D, 285.43176D, 1.06977D, 1.3E-4D,
            48.78644D, 0.77099D, 0.0D, 1.85033D, -6.8E-4D, 1.0E-5D }, {
            225.32833D, 3034.69202D, 7.2E-4D, 0.04833D, 1.6E-4D, 0.0D, 5.202561D, 273.27754D, 0.59943D, 7.0E-4D,
            99.44338D, 1.01053D, 3.5E-4D, 1.30874D, -0.005696D, 0.0D }, {
            175.46622D, 1221.55147D, -5.0E-4D, 0.05589D, -3.5E-4D, 0.0D, 9.55475D, 338.30777D, 1.08522D, 9.8E-4D,
            112.79039D, 0.873195D, -1.5E-4D, 2.49252D, -0.00392D, -2.0E-5D }, {
            72.64882D, 428.37911D, 8.0E-5D, 0.046344D, -3.0E-5D, 0.0D, 19.21814D, 98.07155D, 0.98577D, -0.00107D,
            73.4771D, 0.49867D, 0.00131D, 0.77246D, 6.3E-4D, 4.0E-5D }, {
            37.73067D, 218.46134D, -7.0E-5D, 0.008997D, 0.0D, 0.0D, 30.10957D, 276.04597D, 0.32564D, 1.4E-4D,
            130.68136D, 1.09894D, 2.49E-4D, 1.77924D, -0.00954D, 0.0D }, {
            229.94722D, 144.91306D, 0.0D, 0.24864D, 0.0D, 0.0D, 39.51774D, 113.52139D, 0.0D, 0.0D,
            108.95444D, 1.39601D, 3.1E-4D, 17.14678D, 0.0D, 0.0D } };

    public String getPlanetName() {
        return "AA0";
    }

    protected void calcPlanets() {
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        double d4 = 0.0D;
        for (byte b = 1; b < 10; b++) {
            double d11 = Array[b - 1][0] + (Array[b - 1][1] + Array[b - 1][2] * this.julianCenturies) * this.julianCenturies;
            double d5 = CalcUtil.RFromD(CalcUtil.Mod360(d11));
            double d6 = Array[b - 1][3] + (Array[b - 1][4] + Array[b - 1][5] * this.julianCenturies) * this.julianCenturies;
            double d9 = d5;
            for (byte b1 = 1; b1 <= 5; b1++)
                d9 = d5 + d6 * Math.sin(d9);
            double d7 = Array[b - 1][6];
            double d8 = CalcUtil.RFromD(d7) * (1.0D - d6 * Math.cos(d9));
            XY xY = CalcUtil.RecToPol(d7 * (Math.cos(d9) - d6), d7 * Math.sin(d9) * Math.sqrt(1.0D - d6 * d6));
            d11 = Array[b - 1][7] + (Array[b - 1][8] + Array[b - 1][9] * this.julianCenturies) * this.julianCenturies;
            double d12 = CalcUtil.DFromR(xY.y) + d11;
            d11 = Array[b - 1][10] + (Array[b - 1][11] + Array[b - 1][12] * this.julianCenturies) * this.julianCenturies;
            double d13 = CalcUtil.Mod360(d12 + d11);
            double d14 = CalcUtil.RFromD(d13);
            d5 = CalcUtil.RFromD(d11);
            d11 = Array[b - 1][13] + (Array[b - 1][14] + Array[b - 1][15] * this.julianCenturies) * this.julianCenturies;
            double d10 = CalcUtil.RFromD(d11);
            d12 = Math.atan(Math.cos(d10) * Math.tan(d14 - d5));
            if (d12 < 0.0D)
                d12 += Math.PI;
            d12 += d5;
            if (Math.abs(d14 - d12) > CalcUtil.RFromD(10.0D))
                d12 -= Math.PI;
            double d15 = CalcUtil.Mod2PI(d12);
            double d16 = Math.atan(Math.sin(d15 - d5) * Math.tan(d10));
            if (b == 1)
                d1 = d15;
            double d17 = d8 * Math.cos(d16) * Math.cos(d15);
            double d18 = d8 * Math.cos(d16) * Math.sin(d15);
            double d19 = d8 * Math.sin(d16);
            if (b == 1) {
                d2 = d17;
                d3 = d18;
                d4 = d19;
            } else {
                d17 -= d2;
                d18 -= d3;
                d19 -= d4;
            }
            xY = CalcUtil.RecToPol(d17, d18);
            if (b == 1) {
                this.planetsR[0] = CalcUtil.Mod2PI(d1 + Math.PI);
            } else {
                this.planetsR[b] = xY.y;
            }
        }
        calculateMoon();
    }

    protected final void calculateMoon() {
        double d3 = this.julianCenturies * this.julianCenturies;
        double d4 = 973563.0D + 1.732564379E9D * this.julianCenturies - 4.0D * d3;
        double d5 = 1012395.0D + 6189.0D * this.julianCenturies;
        double d6 = 933060.0D - 6962911.0D * this.julianCenturies + 7.5D * d3;
        double d7 = 1203586.0D + 1.4648523E7D * this.julianCenturies - 37.0D * d3;
        double d1 = 1262655.0D + 1.602961611E9D * this.julianCenturies - 5.0D * d3;
        double d8 = (d4 - d7) / 3600.0D;
        double d9 = (d4 - d1 - d5) / 3600.0D;
        double d10 = (d4 - d6) / 3600.0D;
        d1 /= 3600.0D;
        double d11 = 2.0D * d1;
        double d2 = 22639.6D * CalcUtil.RSinD(d8) - 4586.4D * CalcUtil.RSinD(d8 - d11) + 2369.9D * CalcUtil.RSinD(d11) + 769.0D * CalcUtil.RSinD(2.0D * d8) - 669.0D * CalcUtil.RSinD(d9) - 411.6D * CalcUtil.RSinD(2.0D * d10) - 212.0D * CalcUtil.RSinD(2.0D * d8 - d11) - 206.0D * CalcUtil.RSinD(d8 + d9 - d11);
        d2 += 192.0D * CalcUtil.RSinD(d8 + d11) - 165.0D * CalcUtil.RSinD(d9 - d11) + 148.0D * CalcUtil.RSinD(d8 - d9) - 125.0D * CalcUtil.RSinD(d1) - 110.0D * CalcUtil.RSinD(d8 + d9) - 55.0D * CalcUtil.RSinD(2.0D * d10 - d11) - 45.0D * CalcUtil.RSinD(d8 + 2.0D * d10) + 40.0D * CalcUtil.RSinD(d8 - 2.0D * d10);
        if (this.siderealOffset != 0.0D) {
            this.planetsR[1] = CalcUtil.Mod2PI(CalcUtil.RFromD((d4 + d2) / 3600.0D) + this.siderealOffset);
        } else {
            this.planetsR[1] = CalcUtil.Mod2PI(CalcUtil.RFromD((d4 + d2) / 3600.0D));
        }
    }
}
