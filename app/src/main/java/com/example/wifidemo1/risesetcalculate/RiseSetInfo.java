package com.example.wifidemo1.risesetcalculate;

public class RiseSetInfo {
    public static int RS_NORISE = 0x0001;    /* object does not rise as such today */
    public static int RS_NOSET = 0x0002;  /* object does not set as such today */
    public static int RS_NOTRANS = 0x0004;   /* object does not transit as such today */
    public static int RS_CIRCUMPOLAR = 0x0010;    /* object stays up all day today */
    public static int RS_NEVERUP = 0x0020; /* object never up at all today */
    public static int RS_ERROR = 0x1000; /* can't figure out anything! */
    public static int RS_RISERR = (0x0100 | RS_ERROR); /* error computing rise */
    public static int RS_SETERR = (0x0200 | RS_ERROR); /* error computing set */
    public static int RS_TRANSERR = (0x0400 | RS_ERROR); /* error computing transit */


    public int rs_flags;    /* info about what has been computed and any
     * special conditions; see flags, below.
     */
    public double rs_risetm;    /* mjd time of rise today */
    public double rs_riseaz;    /* azimuth of rise, rads E of N */
    public double rs_trantm;    /* mjd time of transit today */
    public double rs_tranalt;    /* altitude of transit, rads up from horizon */
    public double rs_tranaz;    /* azimuth of transit, rads E of N */
    public double rs_settm;    /* mjd time of set today */
    public double rs_setaz;    /* azimuth of set, rads E of N */

    public RiseSetInfo(int rs_flags, double rs_risetm, double rs_riseaz, double rs_trantm, double rs_tranalt, double rs_tranaz, double rs_settm, double rs_setaz) {
        this.rs_flags = rs_flags;
        this.rs_risetm = rs_risetm;
        this.rs_riseaz = rs_riseaz;
        this.rs_trantm = rs_trantm;
        this.rs_tranalt = rs_tranalt;
        this.rs_tranaz = rs_tranaz;
        this.rs_settm = rs_settm;
        this.rs_setaz = rs_setaz;
    }

    @Override
    public String toString() {
        return "RiseSetInfo{" +
                "rs_flags=" + rs_flags +
                ", rs_risetm=" + rs_risetm +
                ", rs_settm=" + rs_settm +
                '}';
    }
}
