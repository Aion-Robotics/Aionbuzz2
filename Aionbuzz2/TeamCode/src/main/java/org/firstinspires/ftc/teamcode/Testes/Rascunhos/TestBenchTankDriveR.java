package org.firstinspires.ftc.teamcode.Testes.Rascunhos;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;


public class TestBenchTankDriveR extends OpMode {
    DcMotor[] motor = new DcMotor[2];

        public void initializerHardware(){
            motor[0] = hardwareMap.get(DcMotor.class, "motorDir");
            motor[1] = hardwareMap.get(DcMotor.class, "motorEsq");

            motor[1].setDirection(DcMotor.Direction.REVERSE);

            motor[0].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor[1].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        }
        public double distanceMeter() {
            Double[] distance = new Double[2];
            // Calcula a distância a partir dos ticks emitidos por cada motor individualmente
            for (int i = 0; i < 2; i++) {
                int ticks = motor[i].getCurrentPosition();
                double cm = (ticks / 537.6) * (9.66 * Math.PI);
                distance[i] = cm;
            }
            return (distance[0] + distance[1]) / 2.0;
        }

            public void moveRobot() {
                double drive = gamepad1.right_trigger - gamepad1.left_trigger;
                double turn = gamepad1.left_stick_x;

                //Zona morta do analógico
                if (Math.abs(turn) < 0.05) {
                    turn = 0;
                }
                //Mistura de aceleração e rotação
                double leftMove = drive - turn;
                double rightMove = drive + turn;

                //-------------- Ré ---------------//
                if (gamepad1.left_trigger_pressed) {
                    leftMove = -leftMove;
                    rightMove = -rightMove;
                }

                //Normalização das potências
                double power = Math.max(Math.abs(leftMove), Math.abs(rightMove));
                double maxPower = Math.max(1.0, power);

                leftMove /= maxPower;
                rightMove /= maxPower;

                motor[0].setPower(rightMove);
                motor[1].setPower(leftMove);

                //Modo de precisão
                double speedMultiplier = gamepad1.right_bumper ? 0.15 : 1.0;

                leftMove *= speedMultiplier;
                rightMove *= speedMultiplier;

                motor[0].setPower(rightMove);
                motor[1].setPower(leftMove);


            }
            public void init(){}
            public void loop(){}
        }