package main.parse;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Parser {

    public static String parseFileToCode(String toParse) throws ParseException {

        String actions = "";

        Scanner scanner = new Scanner(toParse);

        int lineNum = 0;
        while (scanner.hasNextLine()){
            lineNum ++;
            String linedata = scanner.nextLine();
            Scanner line = new Scanner(linedata);
            line.useDelimiter(Pattern.compile("\\s+"));
            if(!line.hasNext()) continue;
            String cmd = line.next();

            if(cmd.equalsIgnoreCase("set")) { // Move servos TODO move motors with encoders
                if (!line.hasNext()) throw new ParseException("Move command has no type! Line: " + lineNum +" \""+linedata +"\"");
                String type = line.next();
                if (type.equalsIgnoreCase("servo")) {
                    if (!line.hasNext()) throw new ParseException("Move command has no name! Line: " + lineNum +" \""+linedata +"\"");
                    String name = line.next();

                    if (!line.hasNext()) throw new ParseException("Move command has no action! Line: " + lineNum +" \""+linedata +"\"");
                    String action = line.next();

                    if(action.equalsIgnoreCase("position")){
                        if (!line.hasNextInt()) throw new ParseException("Move command has no position! Line: " + lineNum +" \""+linedata +"\"");
                        actions += "\nhardwareMap.servo.get(\"" + name + "\").setPosition(" + line.nextInt() + ");";

                    } else if (action.equalsIgnoreCase("direction")){
                        if (!line.hasNext()) throw new ParseException("Direction command has no value! Line: " + lineNum +" \""+linedata +"\"");
                        String dir = line.next();
                        if(dir.equalsIgnoreCase("forward")){
                            actions += "\nhardwareMap.servo.get(\"" + name + "\").setDirection(Servo.Direction." + dir.toUpperCase() + ");";
                        } else if(dir.equalsIgnoreCase("reverse")){
                            actions += "\nhardwareMap.servo.get(\"" + name + "\").setDirection(Servo.Direction." + dir.toUpperCase() + ");";
                        } else throw new ParseException("Unknown direction: "+dir +" expected \'forward\' or \'reverse\'! Line: " + lineNum);
                    }

                }

                else if (type.equalsIgnoreCase("motor")){
                    if (!line.hasNext()) throw new ParseException("Motor command has no name! Line: " + lineNum +" \""+linedata +"\"");
                    String name = line.next();

                    if (!line.hasNext()) throw new ParseException("Motor command has no action! Line: " + lineNum +" \""+linedata +"\"");
                    String action = line.next();

                    if(action.equalsIgnoreCase("speed")){
                        if (!line.hasNextInt()) throw new ParseException("Move command has no speed! Line: " + lineNum +" \""+linedata +"\"");
                        actions += "\nhardwareMap.motor.get(\"" + name + "\").setSpeed(" + line.nextInt() + ");";
                    }

                    if(action.equalsIgnoreCase("direction")){
                        if (!line.hasNext()) throw new ParseException("Direction command has no value! Line: " + lineNum +" \""+linedata +"\"");
                        String dir = line.next();
                        if(dir.equalsIgnoreCase("forward")){
                            actions += "\nhardwareMap.motor.get(\"" + name + "\").setDirection(DcMotor.Direction." + dir.toUpperCase() + ");";
                        } else if(dir.equalsIgnoreCase("reverse")){
                            actions += "\nhardwareMap.motor.get(\"" + name + "\").setDirection(DcMotor.Direction." + dir.toUpperCase() + ");";
                        } else throw new ParseException("Unknown direction: "+dir +" expected \'forward\' or \'reverse\'! Line: " + lineNum);
                    }

                }

                else if (type.equalsIgnoreCase("motors") || type.equalsIgnoreCase("servos")){
                    char zero = type.charAt(0);
                    String typeClass = zero +"".toUpperCase() + type.substring(1, type.length());

                    ArrayList<String> motors = new ArrayList<>();
                    if(!line.hasNext())throw new ParseException(typeClass + " command has no listed devices! Line: " + lineNum +"  \""+linedata +"\"");
                    String data = line.next();
                    do{
                        motors.add(data);
                        data = line.next();
                    } while (line.hasNext() && !(data.equalsIgnoreCase("direction") || data.equalsIgnoreCase("speed") || data.equalsIgnoreCase("position")));


                    if(motors.size() < 1)throw new ParseException(typeClass + " command has no listed devices! Line: " + lineNum +"  \""+linedata +"\"");
                    String action = data;
                    if(action.equalsIgnoreCase("speed") && type.equalsIgnoreCase("motors")){
                        if(!line.hasNextInt()) throw new ParseException(typeClass + " command has no speed! Line: "+ lineNum + " \""+linedata +"\"");
                        int speed = line.nextInt();
                        for(String name : motors){
                            actions += "\nhardwareMap.motor.get(\"" + name + "\").setSpeed(" + speed + ");";
                        }
                    } else if(action.equalsIgnoreCase("position") && type.equalsIgnoreCase("servos")){
                        if(!line.hasNextInt()) throw new ParseException(typeClass + " command has no speed! Line: "+ lineNum + " \""+linedata +"\"");
                        int pos = line.nextInt();
                        for(String name : motors){
                            actions += "\nhardwareMap.servo.get(\"" + name + "\").setPosition(" + pos + ");";
                        }
                    } else if (action.equalsIgnoreCase("direction")){
                        if (!line.hasNext()) throw new ParseException("Direction command has no value! Line: " + lineNum +" \""+linedata +"\"");
                        String dir = line.next();
                        if (!(dir.equalsIgnoreCase("forward") || dir.equalsIgnoreCase("reverse"))) throw new ParseException("Unknown direction: "+dir +" expected \'forward\' or \'reverse\'! Line: " + lineNum);
                        for(String name : motors){
                            actions += "\nhardwareMap."+type.toLowerCase().substring(0, type.length()-1)+".get(\"" + name + "\").setDirection("+typeClass+".Direction." + dir.toUpperCase() + ");";
                        }
                } else throw new ParseException("Unknown action: "+ action + "! Line: "+ lineNum + " \""+linedata +"\"");


                }

            } else if (cmd.equalsIgnoreCase("sleep") || cmd.equalsIgnoreCase("wait")){
                if(!line.hasNextInt()) throw new ParseException("Sleep command has no duration! Line: " + lineNum +"  \""+linedata +"\"");
                int time = line.nextInt();
                if(line.hasNext()){
                    String unit = line.next();
                    if(unit.equalsIgnoreCase("seconds") || unit.equalsIgnoreCase("sec") || unit.equalsIgnoreCase("s"))
                        time *= 1000;
                }
                actions += "\nsleep("+time +");";
            } else if (!cmd.startsWith("//")){
                if(cmd.length() > 7)
                    throw new ParseException("Unexpected command! Line: " + lineNum +"  \""+linedata.substring(0,7)  +"...\"");
                else throw new ParseException("Unexpected command! Line: " + lineNum +"  \""+linedata  +"\"");
            }

        }

        return actions;
    }



}
