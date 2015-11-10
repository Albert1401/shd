package checker;

import expression.Expression;
import expression.ExpressionParser;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by ClitCommander on 9/28/2015.
 */

public class ProofChanger {
    private ArrayList<Expression> assumptions = new ArrayList<>();
    private ArrayList<Expression> approved = new ArrayList<>();
    private ExpressionParser parser = new ExpressionParser();
    private AxiomChecker axiomChecker;

    private Expression toProve;
    private Expression mainAssumption;

    private WritingUtil writingUtil;


    private BufferedReader reader;
    private BufferedWriter writer;

    public ProofChanger(String pathToAxioms, String pathToProof) throws IOException {
        axiomChecker = new AxiomChecker(pathToAxioms);
        reader = new BufferedReader(new FileReader(pathToProof), 8 * 1024);
        writer = new BufferedWriter(new FileWriter(pathToProof + ".log"));
    }


    private boolean isMp(Expression expression) throws IOException {
        for (int i = approved.size() - 1; i >= 0; i--) {
            if (approved.get(i).getOperator() == '-') {
                if (approved.get(i).getOperands(2).equals(expression)) {
                    for (int j = approved.size() - 1; j >= 0; j--) {
                        if (j != i) {
                            if (approved.get(j).equals(approved.get(i).getOperands(1))) {
                                writingUtil.forMP(approved.get(j), approved.get(i).getOperands(2));
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private int isAssumption(Expression expression) {
        for (int i = 0; i < assumptions.size(); i++) {
            if (expression.equals(assumptions.get(i))) {
                return i;
            }
        }
        return -1;
    }


    private void readAssumptions() throws IOException {
        Scanner scanner = new Scanner(reader.readLine());
        scanner.useDelimiter(",|\\|\\-");

        while (scanner.hasNext()){
            assumptions.add(parser.parse(scanner.next()));
        }
        toProve = assumptions.remove(assumptions.size() - 1);
        mainAssumption = assumptions.remove(assumptions.size() - 1);
    }

    public void writeAssumptions() throws IOException{
        for (int i = 0; i < assumptions.size(); i++){
            if (i != 0) writer.write(", ");
            writer.write(assumptions.get(i).toString());
        }
        writer.write("|-(" + mainAssumption + ")->" + toProve + '\n');
    }

    public void change() throws IOException {
        readAssumptions();
        writeAssumptions();
        writingUtil = new WritingUtil(writer, mainAssumption);

        String line;
        while ((line = reader.readLine()) != null) {
            Expression expression = parser.parse(line);
            int isAxiom = axiomChecker.isAxiom(expression);
            int isAssumption = isAssumption(expression);

            if (expression.equals(mainAssumption)) {
                writingUtil.forMainAssumption();
            } else {
                if (isAxiom == -1 && isAssumption == -1) {
                    isMp(expression);
                } else {
                    writingUtil.forAxiomsAndAssumptions(expression);
                }
            }
            approved.add(expression);
        }
        writer.close();
    }
}