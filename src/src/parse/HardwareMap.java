package src.parse;

import java.util.ArrayList;

public class HardwareMap {

    private ArrayList<String> servos = new ArrayList<>();
    private ArrayList<String> motors = new ArrayList<>();

    public void addServo(String name){
        for (String s : servos){
            if(s.equals(name))
                return;
        }
        servos.add(name);
    }

    public void addMotor(String name){
        for (String s : motors){
            if(s.equals(name))
                return;
        }
        motors.add(name);
    }

    public String getServoInit(){
        String output = "";
        for (String s : servos){
            output += "hardwareMap.";
        }
        return output;
    }


}
