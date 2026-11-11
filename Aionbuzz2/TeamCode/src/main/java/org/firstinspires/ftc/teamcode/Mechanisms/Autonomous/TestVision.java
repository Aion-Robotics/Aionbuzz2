package org.firstinspires.ftc.teamcode.Mechanisms.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.*;

public class TestVision extends OpMode {
    AprilTagProcessor aprilTag;
    VisionPortal visionPortal;
    WebcamName webcam;

    @Override
    public void init() {
        initVision();
    }

    @Override
    public void loop() {
        updateTelemetry();
    }

    public void initVision(){
        // Registra a Webcam utilizada
        webcam = hardwareMap.get(WebcamName.class, "Webcam_1");

        // Configura o processamento das AprilTags
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();
        visionPortal = VisionPortal.easyCreateWithDefaults(webcam, aprilTag);
    }
    public void updateTelemetry(){
       List<AprilTagDetection> detections = aprilTag.getDetections();
       telemetry.addData("AprilTags Detected", detections.size());

       for (AprilTagDetection detection : detections){
           if (detection instanceof AprilTagSingleDetection){
               AprilTagSingleDetection singleDetect = (AprilTagSingleDetection) detection;

               if (singleDetect.metadata != null){
                   telemetry.addData("ID: %d", "Type: %s", singleDetect.id, singleDetect.metadata.name);
               }
           }
       }
    }
}
