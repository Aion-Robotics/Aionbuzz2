package org.firstinspires.ftc.teamcode.Testes;

import android.annotation.TargetApi;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class Teste_intake extends OpMode {
    DcMotor motor = null;
    private final double limit1 = 0.25;
    private final double limit2 = 0.5;

    @Override
    public void init(){
        motor = hardwareMap.get(DcMotor.class, "motor");
    }
    @Override
    public void loop(){
        if (gamepad1.a){
            motor.setPower(1*limit1);
        } else if (gamepad1.b) {
            motor.setPower(1*limit2);
        }
        else
            motor.setPower(0);

    }
}
