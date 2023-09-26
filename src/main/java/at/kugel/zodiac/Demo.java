package at.kugel.zodiac;

import at.kugel.zodiac.house.HouseInt;
import at.kugel.zodiac.house.HousePlacidus;
import at.kugel.zodiac.planet.PlanetAA0;
import at.kugel.zodiac.planet.PlanetInt;

public class Demo {
   public static void main(String[] paramArrayOfString) {
      TextHoroscop textHoroscop = new TextHoroscop();
      textHoroscop.setPlanet((PlanetInt)new PlanetAA0());
      textHoroscop.setHouse((HouseInt)new HousePlacidus());
      textHoroscop.setTime(1, 12, 1953, 20.0D);
      textHoroscop.setLocationDegree(-16.283333333333335D, 48.06666666666667D);
      textHoroscop.calcValues();
      System.out.println(textHoroscop.toString());
   }
}
