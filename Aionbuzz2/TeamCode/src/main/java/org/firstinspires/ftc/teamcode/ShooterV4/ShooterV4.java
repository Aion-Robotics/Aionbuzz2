package org.firstinspires.ftc.teamcode.ShooterV4;


import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class ShooterV4 extends OpMode {

        //COLMEIA FRENTE//
        double distanciaA = 120;
        double rpmA = 0;
        double angulacaoA = 0;
        double distanciaB = 90;
        double rpmB = 0;
        double angulacaoB = 0;

        //COLMEIA TRÁS//
        double distanciaX = 120;
        double rpmX = rpmA;
        double angulacaoX = angulacaoA;
        double distanciaY = 90;
        double rpmY = rpmB;
        double angulacaoY = angulacaoB;

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

        ControleAngular controleAngular = new ControleAngular(0.02);

        @Override
        public void init() {
            imu = hardwareMap.get(IMU.class, "imu");

            imu.initialize(new IMU.Parameters(
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD)
            ));
        }

        @Override
        public void loop() {
            if (gamepad1.a) {
                presentSelecionado = "A";
                distanciaSelecionada = distanciaA;
                anguloReferencia = 0;
                rpmSelecionado = rpmA;
                angulacaoSelecionada = angulacaoA;
            }
            else if (gamepad1.b) {
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
            else if (gamepad1.y) {
                presentSelecionado = "Y";
                distanciaSelecionada = distanciaY;
                anguloReferencia = 180;
                rpmSelecionado = rpmY;
                angulacaoSelecionada = angulacaoY;
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
    }