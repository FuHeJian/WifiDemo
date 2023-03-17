// Copyright 2010 Google Inc.
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

package com.example.wifidemo1.util;


/**
 * Encapsulates interactions with Firebase Analytics, allowing it to be
 * disabled etc.
 *
 * @author John Taylor
 */
public class AnalyticsInterface {
    public static final String DEVICE_SENSORS_ACCELEROMETER = "accel";
    public static final String DEVICE_SENSORS_GYRO = "gyro";
    public static final String DEVICE_SENSORS_MAGNETIC = "mag";
    public static final String DEVICE_SENSORS_ROTATION = "rot";

    public static final String TOGGLED_MANUAL_MODE_LABEL = "toggled_manual_mode_ev";
    public static final String MENU_ITEM_EVENT_VALUE = "menu_item";
    public static final String TOGGLED_NIGHT_MODE_LABEL = "night_mode";
    public static final String SEARCH_REQUESTED_LABEL = "search_requested";
    public static final String SETTINGS_OPENED_LABEL = "settings_opened";
    public static final String HELP_OPENED_LABEL = "help_opened";
    public static final String CALIBRATION_OPENED_LABEL = "calibration_opened";
    public static final String TIME_TRAVEL_OPENED_LABEL = "time_travel_opened";
    public static final String GALLERY_OPENED_LABEL = "gallery_opened";
    public static final String TOS_OPENED_LABEL = "TOS_opened";
    public static final String DIAGNOSTICS_OPENED_LABEL = "diagnostics_opened";
}
