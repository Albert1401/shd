import checker.ProofChanger;
import checker.ProofInput;
import checker.Utils;
import expression.Expression;
import expression.ExpressionParser;

import java.io.*;
import java.util.ArrayList;

public class HW2 {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Enter path to test file");
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(args[0]));
                BufferedWriter writer = new BufferedWriter(new FileWriter(args[0] + ".log"));

                Utils.Head head = Utils.readHead(reader.readLine());
                Expression mainAssumption = head.assumptions.remove(head.assumptions.size() - 1);


                new ProofChanger(new ProofInput() {
                    @Override
                    public Expression nextLine() throws IOException {
                            String s;
                            if ((s = reader.readLine()) == null){
                                return null;
                            }
                            return ExpressionParser.parse(s);
                    }

                    @Override
                    public ArrayList<Expression> getAssumptions() {
                        return head.assumptions;
                    }

                    @Override
                    public Expression getMainAssumption() {
                        return mainAssumption;
                    }
                }, expression -> writer.write(expression.toString() + '\n')).change();
                writer.close();
            } catch (FileNotFoundException e) {
                System.out.println("no file");
                e.printStackTrace();
            } catch (IOException e){
                System.out.println("Output pronlem");
                e.printStackTrace();
            }
        }
    }
}