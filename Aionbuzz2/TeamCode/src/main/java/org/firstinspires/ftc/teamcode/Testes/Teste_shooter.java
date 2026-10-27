package org.firstinspires.ftc.teamcode.Testes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class Teste_shooter extends OpMode {
    DcMotor motorShooter = null;
    double limite = 0.75;

    double rpm = 5990.4;

    @Override
    public void init() {
        motorShooter = hardwareMap.get(DcMotor.class, "motorShooter");
    }

    @Override
    public void loop() {
        if (gamepad1.a) {
            limite = 0.75;
        } else if (gamepad1.b) {
            limite = 0.9;
        } else if (gamepad1.y){
            limite = 1;
        }
        else{
            motorShooter.setPower(0);
        }

        double valorfinal = limite * gamepad1.right_trigger;

        motorShooter.setPower(valorfinal);

        telemetry.addData("Velocidade do Shooter:","%.1f RPM", (valorfinal*rpm));
    }
}