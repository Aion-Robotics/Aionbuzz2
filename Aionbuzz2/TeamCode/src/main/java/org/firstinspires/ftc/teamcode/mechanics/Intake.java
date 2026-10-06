package org.firstinspires.ftc.teamcode.mechanics;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class Intake extends OpMode {

    DcMotor aion = null;

    double limite = 0.25;

    @Override
    public void init() {
        aion = hardwareMap.get(DcMotor.class, "aion");
    }

    @Override
public void loop(){

    if (gamepad1.a)
    }
}
