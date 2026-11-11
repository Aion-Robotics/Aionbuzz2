package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "AutonomoCorrigido")
public class Autonomo extends LinearOpMode {

    private DcMotor motorDir;
    private DcMotor motorEsq;
    private IMU imu;

    // Dados do drivetrain
    private double ticksPorVolta = 537.6;
    private double diametroRoda = 9.6;
    private double circunferenciaRoda = Math.PI * diametroRoda;

    // Controle de direção
    private double kpDirecao = 0.012;

    // Controle de giro
    private double kpGiro = 0.012;
    private double kdGiro = 0.0015;

    // Controle do ângulo
    private double anguloContinuo = 0;
    private double ultimoYaw = 0;

    @Override
    public void runOpMode() {

        motorDir = hardwareMap.get(DcMotor.class, "motorDir");
        motorEsq = hardwareMap.get(DcMotor.class, "motorEsq");

        imu = hardwareMap.get(IMU.class, "imu");

        motorEsq.setDirection(DcMotor.Direction.REVERSE);

        motorDir.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorEsq.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        RevHubOrientationOnRobot orientacao =
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
                );

        imu.initialize(new IMU.Parameters(orientacao));

        imu.resetYaw();

        ultimoYaw = imu.getRobotYawPitchRollAngles()
                .getYaw(AngleUnit.DEGREES);

        anguloContinuo = 0;

        motorDir.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorEsq.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorDir.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorEsq.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addLine("Drivetrain pronto");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        // ETAPA 1: andar 100 cm
        boolean chegou = moverDistancia(110);

        pararMotores();

        if (!chegou || isStopRequested()) {
            telemetry.addLine("Deslocamento interrompido");
            telemetry.update();
            return;
        }

        sleep(3000);

        // ETAPA 2: capturar o ângulo atual
        double anguloInicial = atualizarAngulo();

        // ETAPA 3: girar 180 graus a partir da posição atual
        boolean girou = girarPara(anguloInicial + 180.0);

        pararMotores();

        if (!girou) {
            telemetry.addLine("Giro não concluído");
            telemetry.update();
            return;
        }

        telemetry.addLine("Autonomo finalizado");
        telemetry.update();
    }

    // Atualiza o ângulo continuamente
    private double atualizarAngulo() {

        double yawAtual = imu.getRobotYawPitchRollAngles()
                .getYaw(AngleUnit.DEGREES);

        double delta = yawAtual - ultimoYaw;


        while (delta > 180) {
            delta -= 360;
        }

        while (delta < -180) {
            delta += 360;
        }
        anguloContinuo += delta;

        // Guarda a leitura atual para a próxima atualização
        ultimoYaw = yawAtual;

        return anguloContinuo;
    }

    // Deslocamento medido pelos encoders
    private boolean moverDistancia(double distanciaCm) {

        motorDir.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorEsq.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorDir.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorEsq.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double alvoTicks =
                (distanciaCm / circunferenciaRoda) * ticksPorVolta;

        // Captura a direção atual como referência
        double alvoAngulo = atualizarAngulo();

        resetRuntime();

        while (opModeIsActive() && getRuntime() < 10) {

            double ticksDir = Math.abs(motorDir.getCurrentPosition());
            double ticksEsq = Math.abs(motorEsq.getCurrentPosition());

            double ticksAndados = (ticksDir + ticksEsq) / 2.0;

            double restante = alvoTicks - ticksAndados;

            if (restante <= 5) {
                pararMotores();
                return true;
            }

            double progresso = ticksAndados / alvoTicks;

            // Acelera gradualmente e reduz perto do destino
            double potencia = 0.15 + (progresso * 0.30);

            if (progresso > 0.75) {
                potencia = 0.15 + ((1.0 - progresso) * 0.60);
            }

            potencia = Range.clip(potencia, 0.15, 0.45);

            double anguloAtual = atualizarAngulo();

            double erroAngulo = alvoAngulo - anguloAtual;

            double correcao = erroAngulo * kpDirecao;

            correcao = Range.clip(correcao, -0.20, 0.20);

            // Corrige a direção do robô
            double potenciaDir = potencia - correcao;
            double potenciaEsq = potencia + correcao;

            motorDir.setPower(potenciaDir);
            motorEsq.setPower(potenciaEsq);

            telemetry.addData("Distancia", ticksAndados);
            telemetry.addData("Alvo ticks", alvoTicks);
            telemetry.addData("Angulo atual", anguloAtual);
            telemetry.addData("Angulo referencia", alvoAngulo);
            telemetry.addData("Erro direcao", erroAngulo);
            telemetry.update();
        }

        pararMotores();
        return false;
    }

    // Giro controlado pelo IMU
    private boolean girarPara(double anguloAlvo) {

        double anguloAnterior = atualizarAngulo();

        long tempoAnterior = System.nanoTime();

        int leiturasEstaveis = 0;

        resetRuntime();

        while (opModeIsActive() && getRuntime() < 8) {

            double anguloAtual = atualizarAngulo();

            double erro = anguloAlvo - anguloAtual;

            long tempoAtual = System.nanoTime();

            double deltaTempo =
                    (tempoAtual - tempoAnterior) / 1_000_000_000.0;

            if (deltaTempo <= 0) {
                deltaTempo = 0.001;
            }

            double velocidadeAngular =
                    (anguloAtual - anguloAnterior) / deltaTempo;

            double comando =
                    (kpGiro * erro) - (kdGiro * velocidadeAngular);

            comando = Range.clip(comando, -0.30, 0.30);

            // Evita que o giro fique fraco demais antes de chegar
            if (Math.abs(erro) > 4 && Math.abs(comando) < 0.07) {
                comando = Math.signum(erro) * 0.07;
            }

            // Sentido invertido conforme o resultado do seu teste
            motorDir.setPower(-comando);
            motorEsq.setPower(comando);

            if (Math.abs(erro) < 1.5 &&
                    Math.abs(velocidadeAngular) < 4) {

                leiturasEstaveis++;

                if (leiturasEstaveis >= 5) {
                    pararMotores();
                    return true;
                }

            } else {
                leiturasEstaveis = 0;
            }

            telemetry.addData("Angulo atual", anguloAtual);
            telemetry.addData("Angulo alvo", anguloAlvo);
            telemetry.addData("Erro", erro);
            telemetry.addData("Velocidade angular", velocidadeAngular);
            telemetry.addData("Potencia do giro", comando);
            telemetry.update();

            anguloAnterior = anguloAtual;
            tempoAnterior = tempoAtual;
        }

        pararMotores();
        return false;
    }

    private void pararMotores() {

        motorDir.setPower(0);
        motorEsq.setPower(0);
    }
}