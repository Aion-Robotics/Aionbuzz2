package org.firstinspires.ftc.teamcode.Shooter.ShooterV4;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class ShooterV4 extends OpMode {

        //Distancia 1//
        double angulacaoB = 0;
        double distanciaB = 90;
        double rpmB = 0;


        //Distancia 2//
        double angulacaoX = 0;
        double distanciaX = 0;
        double rpmX = 0;


        double distanciaSelecionada = 0.0;
        double rpmSelecionado = 0;
        double angulacaoSelecionada = 0;

        //Orientação do DRIVETRAIN//
        double anguloReferencia = 0;


        //Identificação do preset//
        String presentSelecionado = "NENHUM";

        //IMU//
        double anguloAtual = 0;
        double erroAngular = 0;
        IMU imu;

        //CAPUZ//
        double denteServo = 19;
        double denteCapuz = 16;
        double relacaoEngrenagem = denteServo/denteCapuz;
        double posicaoMinima = 0.15;
        double posicaoMaxima = 0.85;
        double anguloServoMinimo = 0;
        double angulaServoMaximo = 180;

        //HARDWARE//
        Servo servoAngulacao;


        ControleAngular controleAngular = new ControleAngular(0.02);

        @Override
        public void init() {
            imu = hardwareMap.get(IMU.class, "imu");

            imu.initialize(new IMU.Parameters(
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD)
            ));
            servoAngulacao = hardwareMap.get(Servo.class, "servoAngulacao");
        }

        @Override
        public void loop() {

            if (gamepad1.b) {
                presentSelecionado = "B";
                distanciaSelecionada = distanciaB;
                anguloReferencia = 0;
                rpmSelecionado = rpmB;
                angulacaoSelecionada = angulacaoB;
            }
            else if (gamepad1.x) {
                presentSelecionado = "X";
                distanciaSelecionada = distanciaX;
                anguloReferencia = 180;
                rpmSelecionado = rpmX;
                angulacaoSelecionada = angulacaoX;
            }

            else {
                presentSelecionado = "NENHUM";
                distanciaSelecionada = 0;
                anguloReferencia = 0;
                rpmSelecionado = 0;
                angulacaoSelecionada = 0;
            }
            //IMU//
            anguloAtual = imu.getRobotYawPitchRollAngles()
                    .getYaw(AngleUnit.DEGREES);

            erroAngular = controleAngular.getErroAngular();

            //CONTROLE ANGULAR//
            controleAngular.calcular(
                    anguloAtual,
                    anguloReferencia
            );

            //TELEMETRIA//
            telemetry.addData("Preset", presentSelecionado);
            telemetry.addData("Distância", distanciaSelecionada);
            telemetry.addData("Referência IMU", anguloReferencia);
            telemetry.addData("Angulo atual", anguloAtual);
            telemetry.addData("RPM selecionado", rpmSelecionado);
            telemetry.addData("Angulação selecionada", angulacaoSelecionada);
            telemetry.addData("Erro angular", erroAngular);
            telemetry.addData("Resposta proporcional", controleAngular.getResposta());
            telemetry.update();
        }
        public double calcularPosicaoServo(double anguloCapuz){
            double anguloServo = anguloCapuz/relacaoEngrenagem;
            double posicao = posicaoMinima + (anguloServo - anguloServoMinimo)*
                    (posicaoMaxima - posicaoMinima) /
                    (angulaServoMaximo - anguloServoMinimo);
            return Range.clip(posicao, posicaoMinima, posicaoMaxima);
        }
    }