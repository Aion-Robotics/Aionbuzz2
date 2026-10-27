package org.firstinspires.ftc.teamcode;

// Importa as classes necessárias para controlar o robô.
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class DriveTrain extends OpMode {

    // Vetor que armazena os quatro motores do robô.
    DcMotor[] motor = new DcMotor[4];

    // Vetor que armazena os dois servos de rotação contínua.
    CRServo[] speedServo = new CRServo[2];

    // Armazena o limite de velocidade do Shooter.
    double limite;

    // Velocidade nominal do motor em RPM.
    final static double rpm = 5990.4;

    // Inicializa o OpMode.
    public void init() {
    }

    // Executa repetidamente durante o OpMode.
    public void loop() {
    }

    // Associa os motores e servos aos seus nomes no hardware.
    public void initializerHardware() {

        // Configura os motores do robô.
        motor[0] = hardwareMap.get(DcMotor.class, "motorDir");
        motor[1] = hardwareMap.get(DcMotor.class, "motorEsq");
        motor[2] = hardwareMap.get(DcMotor.class, "motorIntake");
        motor[3] = hardwareMap.get(DcMotor.class, "motorShooter");

        // Inverte o sentido do motor esquerdo.
        motor[1].setDirection(DcMotor.Direction.REVERSE);

        // Configura os servos de rotação contínua.
        speedServo[0] = hardwareMap.get(CRServo.class, "servoDir");
        speedServo[1] = hardwareMap.get(CRServo.class, "servoEsq");

        // Inverte o sentido do servo direito.
        speedServo[0].setDirection(CRServo.Direction.REVERSE);
    }

    // Calcula a distância percorrida pelas rodas.
    public double distanceMeter(){

        // Armazena a distância de cada roda.
        Double[] distance = new Double[2];

        // Percorre os dois motores de tração.
        for (int i = 0; i < 2; i++){

            // Obtém a posição atual do encoder.
            int ticks = motor[i].getCurrentPosition();

            // Converte os ticks em centímetros usando
            // os ticks por rotação e o diâmetro da roda.
            double cm = (ticks / 537.6) * (9.66 * Math.PI);

            // Armazena a distância calculada.
            distance[i] = cm;
        }

        // Retorna a média das distâncias das duas rodas.
        return (distance[0] + distance[1]) / 2.0;
    }

    // Controla o movimento do robô pelo analógico esquerdo.
    public void moveRobot(){

        // Armazena o comportamento dos motores sem potência.
        DcMotor.ZeroPowerBehavior stateMotor;

        // Obtém o movimento para frente e para trás.
        double stickY = -gamepad1.left_stick_y;

        // Obtém o movimento para a esquerda e para a direita.
        double stickX = gamepad1.left_stick_x;

        // Calcula a potência do motor esquerdo.
        double leftMove = Range.clip(stickY - stickX,-0.4, 0.4);

        // Calcula a potência do motor direito.
        double rightMove = Range.clip(stickY + stickX, -0.4, 0.4);

        // Aplica a potência ao motor direito.
        motor[0].setPower(rightMove);

        // Aplica a potência ao motor esquerdo.
        motor[1].setPower(leftMove);

        // Verifica se o botão RB está pressionado.
        if (gamepad1.right_bumper) {

            // Define o multiplicador do turbo.
            double turbo = 2.5;

            // Multiplica a potência dos motores pelo turbo.
            for(int i = 0; i < 2; i++)
                motor[i].setPower(motor[i].getPower()*turbo);
        }

        // Ativa o freio quando os dois motores estão parados.
        if((leftMove == 0.0) && (rightMove == 0.0))
            stateMotor = DcMotor.ZeroPowerBehavior.BRAKE;

            // Permite que os motores girem livremente em movimento.
        else
            stateMotor = DcMotor.ZeroPowerBehavior.FLOAT;

        // Aplica o comportamento aos dois motores.
        for (int i = 0; i < 2; i++)
            motor[i].setZeroPowerBehavior(stateMotor);
    }

    // Controla o motor do Intake.
    public void energyIntake() {

        // Verifica se o botão LB está pressionado.
        if (gamepad1.left_bumper) {

            // Liga o Intake com 75% de potência.
            motor[2].setPower(0.75);

            // Mostra que o Intake está ligado.
            telemetry.addData("Intake Status", "On");
        } else {

            // Desliga o Intake.
            motor[2].setPower(0.0);

            // Mostra que o Intake está desligado.
            telemetry.addData("Intake Status", "Off");
        }
    }

    // Controla os servos de rotação contínua.
    public void servoCR() {

        // Verifica se o botão LB está pressionado.
        if (gamepad1.left_bumper) {

            // Faz os dois servos girarem com potência máxima.
            for (int i = 0; i < 2; i++)
                speedServo[i].setPower(1.0);
        } else {

            // Para os dois servos.
            for (CRServo servo : speedServo)
                servo.setPower(0.0);
        }
    }

    // Controla a potência do Shooter.
    public void motorShooter() {

        // Seleciona o limite de potência usando o botão A.
        if (gamepad1.a) {
            limite = 0.75;

            // Seleciona o limite de potência usando o botão B.
        } else if (gamepad1.b) {
            limite = 0.9;

            // Seleciona o limite máximo usando o botão Y.
        } else if (gamepad1.y){
            limite = 1;
        }

        // Desliga o Shooter se nenhum desses botões estiver pressionado.
        else{
            motor[3].setPower(0);
        }

        // Calcula a potência final usando o limite selecionado
        // e a intensidade do gatilho direito.
        double valorFinal = limite * gamepad1.right_trigger;

        // Aplica a potência calculada ao Shooter.
        motor[3].setPower(valorFinal);

        // Mostra a velocidade estimada do Shooter em RPM.
        telemetry.addData("Velocidade d'o Shooter:","%.1f RPM", (motor[3].getPower()*rpm));
    }
}