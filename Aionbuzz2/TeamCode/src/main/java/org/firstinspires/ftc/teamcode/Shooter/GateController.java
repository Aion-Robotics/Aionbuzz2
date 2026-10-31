package org.firstinspires.ftc.teamcode.Shooter;

import com.qualcomm.robotcore.hardware.Servo;

public class GateController {

        private final Servo servoTrava;

        private boolean aberta = false;

        public GateController(Servo servo) {
            servoTrava = servo;
        }

        public double getMovimentoCnfigurado(){
            return ShooterCalibration.MOVIMENTO_TRAVA_GRAUS;
        }
        public boolean isAberta() {
            return aberta;
        }

        public boolean isCalibrada() {
            return ShooterCalibration.TRAVA_CALIBRADA;
        }

        public String getEstado() {
            return aberta ? "ABERTA" : "FECHADA";
        }
    }