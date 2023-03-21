// Copyright 2008 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.wifidemo1.oksocket.units;

import android.util.Log;

import com.example.wifidemo1.util.Geometry;
import com.example.wifidemo1.util.MathUtil;

/**
 * A simple struct for latitude and longitude.
 * 
 */
public class LatLong {
  private float latitude;
  private float longitude;
  private float altitude;

  @Override
  public String toString() {
    return "LatLong{" +
            "latitude=" + latitude +
            ", longitude=" + longitude +
            ", altitude=" + altitude +
            '}';
  }

  public LatLong(float latitude, float longitude, float altitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.altitude = altitude;
    // Silently enforce reasonable limits
    if (this.latitude > 90f) {
      this.latitude = 90f;
    }
    if (this.latitude < -90f) {
      this.latitude = -90f;
    }
    this.longitude = flooredMod(this.longitude + 180f, 360f) - 180f;
  }

  /**
   * This constructor automatically downcasts the latitude and longitude to
   * floats, so that the previous constructor can be used. It is added as a
   * convenience method, since many of the GPS methods return doubles.
   */
  public LatLong(double latitude, double longitude, double altitude) {
    this((float)latitude, (float) longitude, (float)altitude);
  }

  /**
   * Angular distance between the two points.
   * @param other
   * @return degrees
   */
  public float distanceFrom(LatLong other) {
    // Some misuse of the astronomy math classes
    GeocentricCoordinates otherPnt = GeocentricCoordinates.getInstance(other.longitude, other.latitude);
    GeocentricCoordinates thisPnt = GeocentricCoordinates.getInstance(this.longitude, this.latitude);
    float cosTheta = Geometry.cosineSimilarity(thisPnt, otherPnt);

    Log.e("TAG", "distanceFrom: "+(MathUtil.acos(cosTheta) * 180f / MathUtil.PI) );
    return MathUtil.acos(cosTheta) * 180f / MathUtil.PI;
  }

  public float getLatitude() {
    return latitude;
  }

  public float getLongitude() {
    return longitude;
  }

  public float getAltitude() {
    return altitude;
  }

  public void setLatitude(float latitude){
    this.latitude = latitude;
  }

  public void setLongitude(float longitude){
    this.longitude = longitude;
  }
  /**
   * Returns the 'floored' mod assuming n>0.
   */
  private static float flooredMod(float a, float n){
    return (a < 0 ? a % n + n : a) %n;
  }
}
