package org.firstinspires.ftc.teamcode.Pratice;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class TestAutonomo extends LinearOpMode {
    DcMotor frontLeft, frontRight, rearRight, rearLeft = null;
    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        rearRight = hardwareMap.get(DcMotor.class, "rearRight");
        rearLeft = hardwareMap.get(DcMotor.class, "rearLeft");

        waitForStart();
        if (opModeIsActive()){
            telemetry.addData("Movimentando-se", "frente");
            frontRight.setPower(1);
            frontLeft.setPower(1);
            rearLeft.setPower(1);
            rearRight.setPower(1);

        }
    }
}
