package org.firstinspires.ftc.teamcode.Autonomous;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
@Autonomous
public class AutonomoGyroV1 extends LinearOpMode{

        DcMotor motorDir;
        DcMotor motorEsq;
        IMU imu;

        // Controle do angulo
        double anguloContinuo = 0;
        double ultimoYaw = 0;

        // Encoders e roda
        double ticksPorVolta = 537.6;
        double diametroRoda = 9.6;
        double circunferencia = Math.PI * diametroRoda;

        @Override
        public void runOpMode() {

            // Configuracao dos motores
            motorDir = hardwareMap.get(DcMotor.class, "motorDir");
            motorEsq = hardwareMap.get(DcMotor.class, "motorEsq");

            motorEsq.setDirection(DcMotor.Direction.REVERSE);

            motorDir.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorEsq.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            // Configuracao do IMU
            imu = hardwareMap.get(IMU.class, "imu");

            RevHubOrientationOnRobot orientacao =
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                            RevHubOrientationOnRobot.UsbFacingDirection.UP
                    );

            imu.initialize(new IMU.Parameters(orientacao));

            imu.resetYaw();

            telemetry.addLine("Gyro Autonomo pronto");
            telemetry.update();

            waitForStart();

            if (isStopRequested()) {
                return;
            }

            // Inicializa a leitura continua do IMU
            ultimoYaw = imu.getRobotYawPitchRollAngles()
                    .getYaw(AngleUnit.DEGREES);

            anguloContinuo = 0;

            // ETAPA 1: ANDAR 150 CM
            boolean primeiroDeslocamento = gyroStraight(150);

            if (!primeiroDeslocamento || !opModeIsActive()) {
                pararMotores();
                return;
            }
            sleep(10000);

            // ETAPA 2: FAZER CURVA DE 90 GRAUS
            boolean terminouCurva = gyroTurn(90);

            if (!terminouCurva || !opModeIsActive()) {
                pararMotores();
                return;
            }
            sleep(10000);

            // ETAPA 3: ANDAR MAIS 150 CM
            boolean segundoDeslocamento = gyroStraight(150);

            pararMotores();

            telemetry.addData("Primeiro deslocamento", primeiroDeslocamento);
            telemetry.addData("Curva", terminouCurva);
            telemetry.addData("Segundo deslocamento", segundoDeslocamento);
            telemetry.addData("Angulo final", anguloContinuo);
            telemetry.addLine("Autonomo finalizado");
            telemetry.update();
        }

        // Atualiza o angulo continuamente
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

        // DESLOCAMENTO COM ENCODERS E CORRECAO DO IMU
        private boolean gyroStraight(double distanciaCm) {

            // Reinicia os encoders para medir este trecho
            motorDir.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motorEsq.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            motorDir.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motorEsq.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // Guarda a direcao atual como referencia
            double anguloReferencia = atualizarAngulo();

            double distanciaPercorrida = 0;
            double distanciaRestante = distanciaCm;

            resetRuntime();

            while (opModeIsActive() && getRuntime() < 90) {

                // Leitura dos encoders
                int ticksDir = motorDir.getCurrentPosition();
                int ticksEsq = motorEsq.getCurrentPosition();

                double ticksPercorridos =
                        (Math.abs(ticksDir) + Math.abs(ticksEsq)) / 2.0;

                distanciaPercorrida =
                        (ticksPercorridos / ticksPorVolta) * circunferencia;

                distanciaRestante = distanciaCm - distanciaPercorrida;

                // Para quando atingir a distancia
                if (distanciaPercorrida >= distanciaCm) {
                    break;
                }

                // Leitura do IMU
                double anguloAtual = atualizarAngulo();

                // Diferenca entre direcao atual e direcao desejada
                double desvio = anguloAtual - anguloReferencia;

                // Mantem os valores da correcao que ja funcionaram
                double kp = -0.018;

                double correcao = desvio * kp;

                correcao = Range.clip(correcao, -0.15, 0.15);

                // Reduz a velocidade nos ultimos 30 cm
                double velocidade = Range.clip(
                        0.15 + (distanciaRestante / 30.0) * 0.15,
                        0.15,
                        0.30
                );

                double potenciaDir = velocidade - correcao;
                double potenciaEsq = velocidade + correcao;

                motorDir.setPower(Range.clip(potenciaDir, -1, 1));
                motorEsq.setPower(Range.clip(potenciaEsq, -1, 1));

                // Telemetria
                telemetry.addData("Distancia percorrida", distanciaPercorrida);
                telemetry.addData("Distancia restante", distanciaRestante);
                telemetry.addData("Ticks direita", ticksDir);
                telemetry.addData("Ticks esquerda", ticksEsq);
                telemetry.addData("Angulo atual", anguloAtual);
                telemetry.addData("Angulo referencia", anguloReferencia);
                telemetry.addData("Desvio", desvio);
                telemetry.addData("Correcao", correcao);
                telemetry.addData("Potencia direita", potenciaDir);
                telemetry.addData("Potencia esquerda", potenciaEsq);

                telemetry.update();

                idle();
            }

            pararMotores();

            return opModeIsActive()
                    && distanciaPercorrida >= distanciaCm;
        }

        // CURVA: PARAMETROS MANTIDOS DA SUA PROGRAMACAO
        private boolean gyroTurn(double graus) {

            double anguloInicial = atualizarAngulo();

            double anguloAlvo = anguloInicial + graus;

            // Ganhos originais
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

                // Controle proporcional e derivativo original
                double comando =
                        (kpGiro * erro) -
                                (kdGiro * velocidadeAngular);

                comando = Range.clip(comando, -0.8, 0.8);

                if (Math.abs(erro) >= 1 &&
                        Math.abs(comando) < 0.08) {

                    comando = Math.copySign(0.08, comando);
                }

                // Acionamento dos motores original
                motorDir.setPower(-comando);
                motorEsq.setPower(comando);

                // Verifica se chegou ao angulo
                if (Math.abs(erro) < 1 &&
                        Math.abs(velocidadeAngular) < 4) {

                    leiturasEstaveis++;

                    if (leiturasEstaveis > 8) {

                        pararMotores();

                        return true;
                    }

                } else {
                    leiturasEstaveis = 0;
                }

                // Telemetria
                telemetry.addData("Angulo atual", anguloAtual);
                telemetry.addData("Angulo alvo", anguloAlvo);
                telemetry.addData("Erro", erro);
                telemetry.addData("Velocidade angular", velocidadeAngular);
                telemetry.addData("Potencia", comando);
                telemetry.addData("Estabilidade", leiturasEstaveis);

                telemetry.update();

                anguloAnterior = anguloAtual;
                tempoAnterior = tempoAtual;

                idle();
            }

            pararMotores();

            return false;
        }

        private void pararMotores() {

            motorDir.setPower(0);
            motorEsq.setPower(0);
            motorDir.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorEsq.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }