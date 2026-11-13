package org.firstinspires.ftc.teamcode.GyroStraight;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


    @Autonomous
    public class GyroTurnV2 extends LinearOpMode {

        private DcMotor motorDir;
        private DcMotor motorEsq;
        private IMU imu;

        // Controle do ângulo contínuo
        private double anguloContinuo = 0;
        private double ultimoYaw = 0;

        @Override
        public void runOpMode() {

            // Configuração dos motores
            motorDir = hardwareMap.get(DcMotor.class, "motorDir");
            motorEsq = hardwareMap.get(DcMotor.class, "motorEsq");

            motorEsq.setDirection(DcMotor.Direction.REVERSE);

            motorDir.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE);

            motorEsq.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE);

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

            telemetry.addLine("GyroTurnTeste inicializado");
            telemetry.addLine("Aguardando inicio...");
            telemetry.update();

            waitForStart();

            if (isStopRequested()) {
                return;
            }

            // Inicializa a leitura do ângulo
            ultimoYaw = imu.getRobotYawPitchRollAngles()
                    .getYaw(AngleUnit.DEGREES);

            anguloContinuo = 0;

            // Executa somente uma curva de 90 graus
            boolean sucesso = gyroTurn(90);

            pararMotores();

            telemetry.addData("Resultado", sucesso ? "Curva concluida" : "Tempo esgotado");
            telemetry.addData("Angulo final", anguloContinuo);
            telemetry.update();

            sleep(2000);
        }

        // Atualiza o ângulo acumulado do robô
        private double atualizarAngulo() {

            double yawAtual = imu.getRobotYawPitchRollAngles()
                    .getYaw(AngleUnit.DEGREES);

            double delta = yawAtual - ultimoYaw;

            // Corrige a passagem de +180 para -180 e vice-versa
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

        // Controlador PD para realizar a curva
        private boolean gyroTurn(double graus) {

            double anguloInicial = atualizarAngulo();

            double anguloAlvo = anguloInicial + graus;

            // Parâmetros do controlador
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

                // Leitura do ângulo
                double anguloAtual = atualizarAngulo();

                // Calcula o erro angular
                double erro = anguloAlvo - anguloAtual;

                // Calcula o tempo entre leituras
                long tempoAtual = System.nanoTime();

                double deltaTempo = (tempoAtual - tempoAnterior) / 1_000_000_000.0;

                if (deltaTempo <= 0) {
                    deltaTempo = 0.001;
                }

                // Calcula a velocidade angular
                double velocidadeAngular = (anguloAtual - anguloAnterior) / deltaTempo;

                // Filtra a velocidade
                velocidadeAngularFiltrada = 0.8 * velocidadeAngularFiltrada + 0.4 * velocidadeAngular;

                // Controlador PD
                double comando = (kpGiro * erro) - (kdGiro * velocidadeAngularFiltrada);

                // Limita a potência
                comando = Range.clip(comando, -0.35, 0.35);

                // Comanda os motores
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
                telemetry.addData("Velocidade bruta", velocidadeAngular);
                telemetry.addData("Velocidade filtrada", velocidadeAngularFiltrada);
                telemetry.addData("Potencia", comando);
                telemetry.addData("Leituras estaveis", leiturasEstaveis);
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

        // Para os motores
        private void pararMotores() {

            motorDir.setPower(0);
            motorEsq.setPower(0);

            motorDir.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE);

            motorEsq.setZeroPowerBehavior(
                    DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }