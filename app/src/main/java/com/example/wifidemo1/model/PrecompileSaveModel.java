package com.example.wifidemo1.model;


import com.litesuits.orm.db.annotation.Table;
import com.example.wifidemo1.model.precompile.CameraParameter;
import com.example.wifidemo1.model.precompile.PhotoModel;
import com.example.wifidemo1.model.precompile.TriaxialParameter;

import java.util.ArrayList;

@Table("PrecompileSaveModel")
public class PrecompileSaveModel extends BaseModel {
    public ArrayList<CameraParameter> cameraParameters;
    public ArrayList<PhotoModel> photoModels;
    public ArrayList<TriaxialParameter> triaxialParameters;
    public long appointmentTime;
    public boolean isAppointment;
}
