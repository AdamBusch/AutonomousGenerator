package src.parse;

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
                if (line.next().equals("servo")) {
                    if (!line.hasNext()) throw new ParseException("Move command has no name! Line: " + lineNum +" \""+linedata +"\"");
                    String name = line.next();
                    if (!line.hasNextInt()) throw new ParseException("Move command has no position! Line: " + lineNum +" \""+linedata +"\"");
                    actions += "\nhardwareMap.servo.get(\"" + name + "\").setPosition(" + line.nextInt() + ");";
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
