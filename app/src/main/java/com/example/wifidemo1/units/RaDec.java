// Copyright 2008 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.wifidemo1.units;


import com.example.wifidemo1.util.Geometry;
import com.example.wifidemo1.util.MathUtil;

import java.util.Date;

public class RaDec {
    public float ra;        // In degrees
    public float dec;       // In degrees

    public RaDec(float ra, float dec) {
        this.ra = ra;
        this.dec = dec;
    }

    @Override
    public String toString() {
        return "RA: " + ra + ":" + "Dec: " + dec;
    }

    public static RaDec calculateRaDecDist(HeliocentricCoordinates coords) {
        // find the RA and DEC from the rectangular equatorial coords
        float ra = Geometry.mod2pi(MathUtil.atan2(coords.y, coords.x)) * Geometry.RADIANS_TO_DEGREES;
        float dec = MathUtil.atan(coords.z / MathUtil.sqrt(coords.x * coords.x + coords.y * coords.y)) * Geometry.RADIANS_TO_DEGREES;

        return new RaDec(ra, dec);
    }


    public static RaDec getInstance(GeocentricCoordinates coords) {
        float raRad = MathUtil.atan2(coords.y, coords.x);
        if (raRad < 0)
            raRad += MathUtil.TWO_PI;

        float decRad = MathUtil.atan2(coords.z, MathUtil.sqrt(coords.x * coords.x + coords.y * coords.y));
        return new RaDec(raRad * Geometry.RADIANS_TO_DEGREES, decRad * Geometry.RADIANS_TO_DEGREES);
    }


}
