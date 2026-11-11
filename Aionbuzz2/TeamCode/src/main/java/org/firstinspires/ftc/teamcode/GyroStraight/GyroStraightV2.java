package org.firstinspires.ftc.teamcode.GyroStraight;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

    @Autonomous(name = "Gyro Straight 150cm")
    public class GyroStraightV2 extends LinearOpMode {

        private DcMotor motorDir;
        private DcMotor motorEsq;
        private IMU imu;
        private double anguloContinuo = 0;
        private double ultimoYaw = 0;

        // Dados das rodas e encoders
        private double ticksPorVolta = 537.6;
        private double diametroRoda = 9.6;
        private double circunferencia =
                Math.PI * diametroRoda;

        @Override
        public void runOpMode() {

            // Configuração dos motores
            motorDir = hardwareMap.get(
                    DcMotor.class, "motorDir");

            motorEsq = hardwareMap.get(
                    DcMotor.class, "motorEsq");

            motorEsq.setDirection(
                    DcMotor.Direction.REVERSE);

            motorDir.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE);

            motorEsq.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE);

            // Configuração dos encoders
            motorDir.setMode(
                    DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            motorEsq.setMode(
                    DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            motorDir.setMode(
                    DcMotor.RunMode.RUN_USING_ENCODER);

            motorEsq.setMode(
                    DcMotor.RunMode.RUN_USING_ENCODER);

            // Configuração do IMU
            imu = hardwareMap.get(IMU.class, "imu");

            RevHubOrientationOnRobot orientacao =
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
                    );

            imu.initialize(
                    new IMU.Parameters(orientacao));

            imu.resetYaw();

            telemetry.addLine("Gyro Straight 150cm");
            telemetry.addLine("Aguardando inicio...");
            telemetry.update();

            waitForStart();

            if (isStopRequested()) {
                pararMotores();
                return;
            }

            // Executa o deslocamento de 150 cm
            gyroStraight(150);

            pararMotores();

            telemetry.addLine("Deslocamento finalizado");
            telemetry.update();

            sleep(2000);
        }

        // Movimento em linha reta usando encoder e IMU
        private void gyroStraight(double distanciaCm) {

            // Converte a distância em ticks
            double ticksAlvo =
                    (distanciaCm / circunferencia)
                            * ticksPorVolta;

            // Parâmetro de correção angular
            double kp = -0.012;

            while (opModeIsActive()) {

                // Lê os encoders
                double ticksDir =
                        Math.abs(motorDir.getCurrentPosition());

                double ticksEsq =
                        Math.abs(motorEsq.getCurrentPosition());

                // Calcula a média dos encoders
                double ticksPercorridos =
                        (ticksDir + ticksEsq) / 2.0;

                // Converte ticks em centímetros
                double distanciaPercorrida =
                        (ticksPercorridos / ticksPorVolta)
                                * circunferencia;

                // Calcula quanto falta percorrer
                double distanciaRestante =
                        distanciaCm - distanciaPercorrida;

                // Para quando atingir a distância desejada
                if (distanciaPercorrida >= distanciaCm) {
                    break;
                }

                // Lê o ângulo do IMU
                double angulo =
                        imu.getRobotYawPitchRollAngles()
                                .getYaw(AngleUnit.DEGREES);

                // Calcula a correção angular
                double correcao = angulo * kp;

                correcao = Range.clip(
                        correcao, -0.15, 0.15);

                // Reduz a velocidade ao se aproximar do alvo
                double velocidade = Range.clip(
                        0.15 + (distanciaRestante / 30.0) * 0.15,
                        0.15,
                        0.30
                );

                // Aplica a correção nos motores
                double potenciaDir =
                        velocidade - correcao;

                double potenciaEsq =
                        velocidade + correcao;

                motorDir.setPower(
                        Range.clip(potenciaDir, -1, 1));

                motorEsq.setPower(
                        Range.clip(potenciaEsq, -1, 1));

                // Telemetria
                telemetry.addData(
                        "Distancia percorrida",
                        distanciaPercorrida);

                telemetry.addData(
                        "Distancia restante",
                        distanciaRestante);

                telemetry.addData(
                        "Ticks direita", ticksDir);

                telemetry.addData(
                        "Ticks esquerda", ticksEsq);

                telemetry.addData(
                        "Angulo IMU", angulo);

                telemetry.addData(
                        "Correcao", correcao);

                telemetry.addData(
                        "Potencia direita", potenciaDir);

                telemetry.addData(
                        "Potencia esquerda", potenciaEsq);

                telemetry.update();

                idle();
            }

            pararMotores();
        }

        // Para os dois motores
        private void pararMotores() {

            motorDir.setPower(0);
            motorEsq.setPower(0);
        }
    }