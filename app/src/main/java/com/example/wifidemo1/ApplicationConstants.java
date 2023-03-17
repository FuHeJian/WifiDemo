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

package com.example.wifidemo1;

import com.example.wifidemo1.units.Vector3;

/**
 * A home for the application's few global constants.
 */
public class ApplicationConstants {

    public static final String APP_NAME = "Stardroid";
    /**
     * Default value for 'south' in phone coords when the app starts
     */
    public static final Vector3 INITIAL_SOUTH = new Vector3(0, -1, 0);
    /**
     * Default value for 'down' in phone coords when the app starts
     */
    public static final Vector3 INITIAL_DOWN = new Vector3(0, -1, -9);

    public static final String REVERSE_MAGNETIC_Z_PREFKEY = "reverse_magnetic_z";

    public static final int UPDATE_BATTERY_LIMIT = 30;

    public static final int GPS_STATE_IN_DEVICE_NEW = 1;
    public static final int GPS_STATE_IN_DEVICE_ANOMALY = 0;

    public static final int CONTROL_MODE_USB = 0;
    public static final int CONTROL_MODE_CABLE_RELEASE = 1;

    public static final double EPSINON = 1e-6;

    public static final int SKY_MODE_TYPE_DELAY = 0;
    public static final int SKY_MODE_TYPE_NOR_PANO = 1;
    public static final int SKY_MODE_TYPE_PRO_PANO = 2;

    public static final int PANO_NOR = 0;
    public static final int PANO_PRO = 1;
    public static final int PANO_720 = 2;

    public static final int MAX_STAR_USE_HISTORY_COUNT = 6;

    public static final String HDMI_MODE_VIDEO_URL = "rtsp://192.168.0.1:8554/12";
}
