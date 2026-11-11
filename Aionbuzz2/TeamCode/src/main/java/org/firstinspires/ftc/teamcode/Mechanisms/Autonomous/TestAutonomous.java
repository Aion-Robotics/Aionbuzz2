package org.firstinspires.ftc.teamcode.Mechanisms.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

@Disabled
@Autonomous
public class TestAutonomous extends LinearOpMode {
    DcMotor[] motor = new DcMotor[2];
    IMU imu = null;

    // Drive Train
    public static final double ticksPerTurn = 537.6;
    public static final double wheelDiameter = 9.6;
    public static final double wheelCircumference = (wheelDiameter * Math.PI);

    @Override
    public void runOpMode() {
        motor[0] = hardwareMap.get(DcMotor.class, "motorDir");
        motor[1] = hardwareMap.get(DcMotor.class, "motorEsq");

        for (int i = 0; i < 2; i++){
            motor[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}
