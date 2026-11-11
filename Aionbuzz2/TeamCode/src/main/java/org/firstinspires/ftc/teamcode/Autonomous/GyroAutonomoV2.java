package org.firstinspires.ftc.teamcode.Autonomous;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Autonomous
public class GyroAutonomoV2 extends LinearOpMode{


        private DcMotor motorDir;
        private DcMotor motorEsq;
        private IMU imu;

        // Controle do angulo continuo
        private double anguloContinuo = 0;
        private double ultimoYaw = 0;

        // Encoders e roda
        private double ticksPorVolta = 537.6;
        private double diametroRoda = 9.6;
        private double circunferencia = Math.PI * diametroRoda;

        @Override
        public void runOpMode() {

            // Configuracao dos motores
            motorDir = hardwareMap.get(DcMotor.class, "motorDir");
            motorEsq = hardwareMap.get(DcMotor.class, "motorEsq");

            motorEsq.setDirection(DcMotor.Direction.REVERSE);

            motorDir.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE);

            motorEsq.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE);

            // Configuracao do IMU
            imu = hardwareMap.get(IMU.class, "imu");

            RevHubOrientationOnRobot orientacao =
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
                    );

            imu.initialize(new IMU.Parameters(orientacao));

            imu.resetYaw();

            telemetry.addLine("Gyro Autonomo V2 inicializado");
            telemetry.addLine("Aguardando inicio...");
            telemetry.update();

            waitForStart();

            if (isStopRequested()) {
                return;
            }

            // Inicializa o angulo continuo
            ultimoYaw = imu.getRobotYawPitchRollAngles()
                    .getYaw(AngleUnit.DEGREES);

            anguloContinuo = 0;

            // ETAPA 1: ANDAR 150 CM
            telemetry.addLine("Etapa 1: deslocamento de 150 cm");
            telemetry.update();

            boolean primeiroDeslocamento = gyroStraight(150);

            pararMotores();

            if (!primeiroDeslocamento || !opModeIsActive()) {
                finalizar("Primeiro deslocamento nao concluido");
                return;
            }

            // Pequena pausa antes da curva
            sleep(300);

            if (!opModeIsActive()) {
                pararMotores();
                return;
            }

            // ETAPA 2: CURVA DE 90 GRAUS COM A V2
            telemetry.addLine("Etapa 2: curva de 90 graus");
            telemetry.update();

            boolean terminouCurva = gyroTurn(90);

            pararMotores();

            if (!terminouCurva || !opModeIsActive()) {
                finalizar("Curva nao concluida");
                return;
            }

            // Aguarda a estabilizacao antes de andar novamente
            sleep(500);

            if (!opModeIsActive()) {
                pararMotores();
                return;
            }

            // ETAPA 3: ANDAR MAIS 150 CM
            telemetry.addLine("Etapa 3: segundo deslocamento de 150 cm");
            telemetry.update();

            boolean segundoDeslocamento = gyroStraight(150);

            pararMotores();

            if (!segundoDeslocamento || !opModeIsActive()) {
                finalizar("Segundo deslocamento nao concluido");
                return;
            }

            // FINALIZACAO
            pararMotores();

            telemetry.addLine("AUTONOMO FINALIZADO");
            telemetry.addData("Angulo final", anguloContinuo);
            telemetry.update();
        }

        // ATUALIZACAO DO ANGULO CONTINUO
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

            // Reinicia os encoders para este deslocamento
            motorDir.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motorEsq.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            motorDir.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motorEsq.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // Guarda a direcao atual como referencia
            double anguloReferencia = atualizarAngulo();

            double distanciaPercorrida = 0;
            double distanciaRestante = distanciaCm;

            resetRuntime();

            while (opModeIsActive() && getRuntime() < 30) {

                // Leitura dos encoders
                int ticksDir = motorDir.getCurrentPosition();
                int ticksEsq = motorEsq.getCurrentPosition();

                double ticksPercorridos =
                        (Math.abs(ticksDir) + Math.abs(ticksEsq)) / 2.0;

                distanciaPercorrida =
                        (ticksPercorridos / ticksPorVolta) * circunferencia;

                distanciaRestante = distanciaCm - distanciaPercorrida;

                // Verifica se chegou aos 150 cm
                if (distanciaPercorrida >= distanciaCm) {
                    break;
                }

                // Leitura do IMU
                double anguloAtual = atualizarAngulo();

                double desvio = anguloAtual - anguloReferencia;

                // Correcao original do deslocamento
                double kp = -0.012;

                double correcao = desvio * kp;

                correcao = Range.clip(correcao, -0.15, 0.15);

                // Desaceleracao nos ultimos 30 cm
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
                telemetry.addData("Tempo", getRuntime());

                telemetry.update();

                idle();
            }

            pararMotores();

            return opModeIsActive()
                    && distanciaPercorrida >= distanciaCm;
        }

        // GYRO TURN V2 - PARAMETROS ORIGINAIS
        private boolean gyroTurn(double graus) {

            double anguloInicial = atualizarAngulo();

            double anguloAlvo = anguloInicial + graus;

            // Parametros originais da V2
            double kpGiro = 0.016;
            double kdGiro = 0.00;

            // Filtro da velocidade angular
            double velocidadeAngularFiltrada = 0;

            // Valores da leitura anterior
            double anguloAnterior = anguloInicial;

            long tempoAnterior = System.nanoTime();

            // Contador de estabilidade
            int leiturasEstaveis = 0;

            resetRuntime();

            while (opModeIsActive() && getRuntime() < 12) {

                // Leitura do angulo
                double anguloAtual = atualizarAngulo();

                // Calcula o erro angular
                double erro = anguloAlvo - anguloAtual;

                // Calcula o tempo entre leituras
                long tempoAtual = System.nanoTime();

                double deltaTempo =
                        (tempoAtual - tempoAnterior)
                                / 1_000_000_000.0;

                if (deltaTempo <= 0) {
                    deltaTempo = 0.001;
                }

                // Calcula a velocidade angular
                double velocidadeAngular =
                        (anguloAtual - anguloAnterior) / deltaTempo;

                // Filtro original da V2
                velocidadeAngularFiltrada =
                        0.8 * velocidadeAngularFiltrada
                                + 0.4 * velocidadeAngular;

                // Controlador PD original
                double comando =
                        (kpGiro * erro)
                                - (kdGiro * velocidadeAngularFiltrada);

                // Limite de potencia original
                comando = Range.clip(comando, -0.35, 0.35);

                // Acionamento dos motores original
                motorDir.setPower(-comando);
                motorEsq.setPower(comando);

                // Verifica a estabilidade
                if (Math.abs(erro) < 1 &&
                        Math.abs(velocidadeAngularFiltrada) < 4) {

                    leiturasEstaveis++;

                    if (leiturasEstaveis >= 5) {

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

                telemetry.addData(
                        "Velocidade bruta", velocidadeAngular);

                telemetry.addData(
                        "Velocidade filtrada",
                        velocidadeAngularFiltrada);

                telemetry.addData("Potencia", comando);

                telemetry.addData(
                        "Leituras estaveis", leiturasEstaveis);

                telemetry.addData("Tempo", getRuntime());

                telemetry.update();

                // Atualiza os valores anteriores
                anguloAnterior = anguloAtual;
                tempoAnterior = tempoAtual;

                idle();
            }

            // Interrompe os motores
            pararMotores();

            return false;
        }

        // FINALIZACAO COM MENSAGEM
        private void finalizar(String mensagem) {

            pararMotores();

            telemetry.addLine(mensagem);
            telemetry.addLine("Motores parados");
            telemetry.update();
        }

        // PARADA DOS MOTORES
        private void pararMotores() {

            motorDir.setPower(0);
            motorEsq.setPower(0);

            motorDir.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE);

            motorEsq.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }