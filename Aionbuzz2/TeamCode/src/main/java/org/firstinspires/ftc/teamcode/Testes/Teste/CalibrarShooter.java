package org.firstinspires.ftc.teamcode.Testes.Teste;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class CalibrarShooter extends OpMode {

    private DcMotorEx motorShooter;

    @Override
    public void init() {

        motorShooter = hardwareMap.get(
                DcMotorEx.class,
                "motorShooter"
        );

        // Zera a contagem do encoder
        motorShooter.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER
        );

        // Permite ler o encoder sem usar o controlador
        // de velocidade do motor
        motorShooter.setMode(
                DcMotor.RunMode.RUN_WITHOUT_ENCODER
        );

        telemetry.addLine("Shooter calibrado!");
        telemetry.addLine("Gire o eixo manualmente.");
        telemetry.update();
    }

    @Override
    public void loop() {

        // Lê a posição atual do encoder
        int ticks = motorShooter.getCurrentPosition();

        // Mostra a leitura na Driver Station
        telemetry.addData("Ticks registrados", ticks);
        telemetry.addData(
                "Ticks por volta",
                "Divida o resultado por 10"
        );

        telemetry.update();
    }

    @Override
    public void stop() {
        motorShooter.setPower(0);
    }
}