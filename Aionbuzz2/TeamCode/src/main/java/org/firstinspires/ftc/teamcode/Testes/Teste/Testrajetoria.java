package org.firstinspires.ftc.teamcode.Testes.Teste;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

    @Autonomous
    public class Testrajetoria extends LinearOpMode{

        private DcMotor motorEsquerdo;
        private DcMotor motorDireito;
        private IMU imu;

        // SUBSTITUIR pelo valor medido na calibracao!
        // Exemplo ficticio: nao usar sem calibrar.
        private static final double TICKS_POR_METRO = 1000.0;

        private static final double DISTANCIA_METROS = 2.0;

        // Ajustes iniciais para os testes
        private static final double POTENCIA_MAXIMA = 0.40;
        private static final double POTENCIA_MINIMA = 0.16;
        private static final double DISTANCIA_RAMPA = 0.40;

        private static final double KP_HEADING = 0.012;
        private static final double CORRECAO_MAXIMA = 0.20;
        private static final double TOLERANCIA_HEADING = 2.0;

        // Se os encoders diminuirem ao avancar,
        // mudar o sinal correspondente para -1.
        private static final double SINAL_ENCODER_ESQ = 1.0;
        private static final double SINAL_ENCODER_DIR = 1.0;

        @Override
        public void runOpMode() {

            motorEsquerdo = hardwareMap.get(
                    DcMotor.class, "motorEsquerdo"
            );

            motorDireito = hardwareMap.get(
                    DcMotor.class, "motorDireito"
            );

            imu = hardwareMap.get(IMU.class, "imu");

            RevHubOrientationOnRobot orientacaoHub =
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
                    );

            imu.initialize(
                    new IMU.Parameters(orientacaoHub)
            );

            motorEsquerdo.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE
            );

            motorDireito.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE
            );

            telemetry.addLine("Teste de trajetoria Aion");
            telemetry.addLine("Verifique motores e encoders");
            telemetry.addLine("Pressione PLAY para iniciar");
            telemetry.update();

            waitForStart();

            if (isStopRequested()) {
                return;
            }

            // Estabelece o heading inicial como zero.
            imu.resetYaw();

            // Zera os encoders no inicio do movimento.
            motorEsquerdo.setMode(
                    DcMotor.RunMode.STOP_AND_RESET_ENCODER
            );

            motorDireito.setMode(
                    DcMotor.RunMode.STOP_AND_RESET_ENCODER
            );

            motorEsquerdo.setMode(
                    DcMotor.RunMode.RUN_WITHOUT_ENCODER
            );

            motorDireito.setMode(
                    DcMotor.RunMode.RUN_WITHOUT_ENCODER
            );

            int inicioEsq =
                    motorEsquerdo.getCurrentPosition();

            int inicioDir =
                    motorDireito.getCurrentPosition();

            double headingDesejado = 0.0;

            double ticksAlvo =
                    DISTANCIA_METROS * TICKS_POR_METRO;

            ElapsedTime cronometro = new ElapsedTime();
            ElapsedTime cronometroParado = new ElapsedTime();

            double ultimoProgresso = 0.0;
            double distancia = 0.0;

            String resultado = "Movimento em andamento";

            cronometro.reset();
            cronometroParado.reset();

            while (opModeIsActive()
                    && distancia < DISTANCIA_METROS
                    && cronometro.seconds() < 10.0) {

                int ticksEsq =
                        motorEsquerdo.getCurrentPosition() - inicioEsq;

                int ticksDir =
                        motorDireito.getCurrentPosition() - inicioDir;

                double progressoTicks =
                        (ticksEsq * SINAL_ENCODER_ESQ
                                + ticksDir * SINAL_ENCODER_DIR) / 2.0;

                distancia = progressoTicks / TICKS_POR_METRO;

                // Le a orientacao atual.
                double headingAtual =
                        imu.getRobotYawPitchRollAngles()
                                .getYaw(AngleUnit.DEGREES);

                // Calcula o erro angular.
                double erro = headingDesejado - headingAtual;

                // Escolhe o menor caminho angular.
                while (erro > 180) erro -= 360;
                while (erro <= -180) erro += 360;

                // Corrige a direcao proporcionalmente ao erro.
                double correcao = Range.clip(
                        KP_HEADING * erro,
                        -CORRECAO_MAXIMA,
                        CORRECAO_MAXIMA
                );

                // Calcula a desaceleracao perto do destino.
                double restante =
                        DISTANCIA_METROS - distancia;

                double fatorRampa = Math.min(
                        1.0,
                        Math.min(
                                Math.max(0.0, distancia)
                                        / DISTANCIA_RAMPA,
                                restante / DISTANCIA_RAMPA
                        )
                );

                double potenciaBase =
                        POTENCIA_MINIMA
                                + (POTENCIA_MAXIMA - POTENCIA_MINIMA)
                                * fatorRampa;

                // Ajusta os dois lados para corrigir o rumo.
                double potenciaEsq =
                        potenciaBase - correcao;

                double potenciaDir =
                        potenciaBase + correcao;

                potenciaEsq = Range.clip(
                        potenciaEsq, -1.0, 1.0
                );

                potenciaDir = Range.clip(
                        potenciaDir, -1.0, 1.0
                );

                motorEsquerdo.setPower(potenciaEsq);
                motorDireito.setPower(potenciaDir);

                // Detecta se os encoders pararam de progredir.
                if (Math.abs(progressoTicks - ultimoProgresso) >= 5) {
                    ultimoProgresso = progressoTicks;
                    cronometroParado.reset();
                }

                if (cronometroParado.seconds() > 1.5) {
                    resultado = "Parado: sem progresso dos encoders";
                    break;
                }

                telemetry.addData(
                        "Distancia", "%.2f / %.2f m",
                        distancia, DISTANCIA_METROS
                );

                telemetry.addData(
                        "Heading", "%.1f graus", headingAtual
                );

                telemetry.addData(
                        "Erro angular", "%.1f graus", erro
                );

                telemetry.addData(
                        "Potencia esquerda", "%.2f", potenciaEsq
                );

                telemetry.addData(
                        "Potencia direita", "%.2f", potenciaDir
                );

                telemetry.addData(
                        "Ticks esquerdo", ticksEsq
                );

                telemetry.addData(
                        "Ticks direito", ticksDir
                );

                telemetry.addData(
                        "Tempo", "%.1f s", cronometro.seconds()
                );

                telemetry.addData("Estado", resultado);
                telemetry.update();
            }

            motorEsquerdo.setPower(0);
            motorDireito.setPower(0);

            if (opModeIsActive()
                    && distancia >= DISTANCIA_METROS) {
                resultado = "Distancia atingida";
            } else if (opModeIsActive()
                    && cronometro.seconds() >= 10.0) {
                resultado = "Parado: tempo limite";
            }

            telemetry.addData("Resultado", resultado);
            telemetry.addData("Distancia final", "%.2f m", distancia);
            telemetry.update();
        }
    }