package org.firstinspires.ftc.teamcode.mechanics;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

@TeleOp
public class Motor extends OpMode {
    DcMotor motorEsq = null;
    double leftStick;
    double rightStick;

    @Override
    public void init(){
        motorEsq = hardwareMap.get(DcMotor.class, "motorEsq");
        telemetry.addData("Motor Status", "Registered");
    }
    @Override
    public void loop(){
        leftStick = -gamepad1.left_stick_y;
        rightStick = gamepad1.left_stick_x;

        double leftMove = Range.clip(leftStick + rightStick, -0.75, 0.75);
        double rightMove = Range.clip(leftStick - rightStick,-0.75,0.75);

        motorEsq.setPower(leftMove);

    }
}
