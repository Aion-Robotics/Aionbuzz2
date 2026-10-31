package org.firstinspires.ftc.teamcode.Shooter;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class ShooterV3 extends OpMode {

   private Servo servoAngulacao;
        private IMU imu;

        private HoodController hood;

        private double anguloCalculado = 66.0;

        private GateController gate;

        @Override
        public void init() {

            Servo servoTrava = hardwareMap.get(Servo.class,"servoTrava");
            gate = new GateController(servoTrava);

            servoAngulacao = hardwareMap.get(
                    Servo.class, "servoAngulacao");

            imu = hardwareMap.get(
                    IMU.class, "imu");

            hood = new HoodController(servoAngulacao);

            telemetry.addLine("AION SHOOTER V3");
            telemetry.addLine("Modo: simulacao da hood");
            telemetry.update();
        }

        @Override
        public void loop() {

            double pitch = imu.getRobotYawPitchRollAngles()
                    .getPitch(AngleUnit.DEGREES);

            double roll = imu.getRobotYawPitchRollAngles()
                    .getRoll(AngleUnit.DEGREES);

            double velocidade =
                    ShooterBallistics.calcularVelocidade(
                            ShooterConfig.distanciaAlvo,
                            ShooterConfig.alturaLancamento,
                            ShooterConfig.alturaAlvo,
                            anguloCalculado
                    );

            telemetry.addLine("=== AION SHOOTER ===");

            telemetry.addData("Distancia",
                    ShooterConfig.distanciaAlvo);

            telemetry.addData("Angulo teorico",
                    anguloCalculado);

            telemetry.addData("Velocidade teorica",
                    Double.isNaN(velocidade)
                            ? "Trajetoria invalida"
                            : String.format("%.2f m/s", velocidade));

            telemetry.addData("Pitch IMU", pitch);
            telemetry.addData("Roll IMU", roll);

            telemetry.addData("Angulo hood solicitado",
                    hood.getAnguloDesejado());

            telemetry.addLine("Servo: aguardando calibracao");
            telemetry.addLine("Motor shooter: desativado");

            telemetry.update();
        }
    }