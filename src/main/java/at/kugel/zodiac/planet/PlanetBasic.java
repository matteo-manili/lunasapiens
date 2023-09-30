package at.kugel.zodiac.planet;

import at.kugel.zodiac.util.CalcUtil;

public abstract class PlanetBasic implements PlanetInt {
   protected final int NUMBER_PLANET = 10;

   //protected final String[] NAME_PLANET = new String[] { "Sun", "Moon", "Mercury", "Venus", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto" };
   protected final String[] NAME_PLANET = new String[] { "Sole", "Luna", "Mercurio", "Venere", "Marte", "Giove", "Saturno", "Urano", "Nettuno", "Plutone" };

   protected double julianCenturies;

   protected double siderealOffset;

   protected final double[] planetsR = new double[10];

   public final double[] getPlanetsR() {
      return this.planetsR;
   }

   public final String[] getPlanetsText() {
      return this.NAME_PLANET;
   }

   public String getPlanetName() {
      return "Null";
   }

   public final void setPlanets(double paramDouble1, double paramDouble2) {
      this.julianCenturies = paramDouble1;
      this.siderealOffset = paramDouble2;
      calcPlanets();
      calcAspects();
   }

   protected void calcPlanets() {
      this.planetsR[0] = CalcUtil.RFromD(135.0D);
      this.planetsR[1] = CalcUtil.RFromD(105.0D);
      this.planetsR[2] = CalcUtil.RFromD(75.0D);
      this.planetsR[3] = CalcUtil.RFromD(195.0D);
      this.planetsR[4] = CalcUtil.RFromD(15.0D);
      this.planetsR[5] = CalcUtil.RFromD(255.0D);
      this.planetsR[6] = CalcUtil.RFromD(285.0D);
      this.planetsR[7] = CalcUtil.RFromD(315.0D);
      this.planetsR[8] = CalcUtil.RFromD(345.0D);
      this.planetsR[9] = CalcUtil.RFromD(225.0D);
   }

   protected final void calcAspects() {}

   public final String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("Planets \"");
      stringBuffer.append(getPlanetName());
      stringBuffer.append("\" [");
      for (byte b = 0; b < 10; b++) {
         stringBuffer.append(this.NAME_PLANET[b]);
         stringBuffer.append(':');
         CalcUtil.HMStringFromR(stringBuffer, this.planetsR[b]);
         stringBuffer.append("; ");
         if (b == 2 || b == 6)
            stringBuffer.append('\n');
      }
      stringBuffer.append(']');
      return stringBuffer.toString();
   }
}
