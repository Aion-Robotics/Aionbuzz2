package org.firstinspires.ftc.teamcode.Testes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class Servo extends OpMode {
    DcMotor aion = null;
    private double Limitemax = 0.31;

    @Override
    public void init(){
        aion = hardwareMap.get(DcMotor.class, "aion");
    }
    @Override
    public void loop(){
        aion.setPower(-gamepad1.left_stick_y*Limitemax);
    }
}