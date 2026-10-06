package org.firstinspires.ftc.teamcode.Testes;

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
        if (gamepad1.left_trigger_pressed){
            motor.setPower(1.0);
        } else if (gamepad1.left_bumper) {
            motor.setPower(1.0*limit2);
        }
        else
            motor.setPower(0);

    }
}
