package org.firstinspires.ftc.teamcode.GyroStraight;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

    @Autonomous(name = "Gyro Turn Teste")
    public class GyroTurn extends LinearOpMode {

        DcMotor motorDir;
        DcMotor motorEsq;
        IMU imu;

        // Controle do ângulo
        double anguloContinuo = 0;
        double ultimoYaw = 0;

        @Override
        public void runOpMode() {

            // Configuração dos motores
            motorDir = hardwareMap.get(DcMotor.class, "motorDir");
            motorEsq = hardwareMap.get(DcMotor.class, "motorEsq");

            motorEsq.setDirection(DcMotor.Direction.REVERSE);

            motorDir.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorEsq.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            // Configuração do IMU
            imu = hardwareMap.get(IMU.class, "imu");

            RevHubOrientationOnRobot orientacao =
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
                    );

            imu.initialize(new IMU.Parameters(orientacao));

            imu.resetYaw();

            telemetry.addLine("Gyro Turn pronto");
            telemetry.update();

            waitForStart();

            if (isStopRequested()) {
                return;
            }

            // Inicializa o ângulo
            ultimoYaw = imu.getRobotYawPitchRollAngles()
                    .getYaw(AngleUnit.DEGREES);

            anguloContinuo = 0;

            // Executa somente a curva de 90 graus
            boolean terminou = gyroTurn(90);

            pararMotores();

            telemetry.addData("Resultado", terminou
                    ? "Curva concluida"
                    : "Curva interrompida");

            telemetry.addData("Angulo final", anguloContinuo);
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

            ultimoYaw = yawAtual;

            return anguloContinuo;
        }

        // Controle da curva
        private boolean gyroTurn(double graus) {

            double anguloInicial = atualizarAngulo();

            double anguloAlvo = anguloInicial + graus;

            // Ganhos do controle PD
            double kpGiro = -0.04;
            double kdGiro = 0;

            double anguloAnterior = anguloInicial;

            long tempoAnterior = System.nanoTime();

            int leiturasEstaveis = 0;

            resetRuntime();

            while (opModeIsActive() && getRuntime() < 180) {

                double anguloAtual = atualizarAngulo();

                double erro = anguloAlvo - anguloAtual;

                long tempoAtual = System.nanoTime();

                double deltaTempo =
                        (tempoAtual - tempoAnterior) / 1_000_000_000.0;

                if (deltaTempo <= 0) {
                    deltaTempo = 0.001;
                }

                // Velocidade angular
                double velocidadeAngular =
                        (anguloAtual - anguloAnterior) / deltaTempo;

                // Controle proporcional e derivativo
                double comando =
                        (kpGiro * erro) -
                                (kdGiro * velocidadeAngular);

                comando = Range.clip(comando, -0.8, 0.8);
                if (Math.abs(erro) >= 1 &&
                        Math.abs(comando) < 0.08) {

                    comando = Math.copySign(0.08, comando);
                }

                // Acionamento dos motores
                motorDir.setPower(-comando);
                motorEsq.setPower(comando);

                // Verifica se chegou ao ângulo
                if (Math.abs(erro) < 1 &&
                        Math.abs(velocidadeAngular) < 4) {

                    leiturasEstaveis++;

                    if (leiturasEstaveis > 5) {

                        pararMotores();

                        return true;
                    }

                } else {
                    leiturasEstaveis = 0;
                }

                // Telemetry
                telemetry.addData("Angulo atual", anguloAtual);
                telemetry.addData("Angulo alvo", anguloAlvo);
                telemetry.addData("Erro", erro);
                telemetry.addData("Velocidade angular", velocidadeAngular);
                telemetry.addData("Potencia", comando);
                telemetry.addData("Estabilidade", leiturasEstaveis);

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