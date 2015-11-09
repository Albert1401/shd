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


public class CorrectProofChecker{
    private ArrayList<Expression> assumptions = new ArrayList<>();
    private ArrayList<Expression> approved = new ArrayList<>();
    private ExpressionParser parser = new ExpressionParser();
    private AxiomChecker axiomChecker;
    private Expression toProve;

    private BufferedReader reader;
    private BufferedWriter writer;

    protected String line;
    protected int lineNumber;

    public CorrectProofChecker(String pathToAxioms, String pathToProof) throws IOException {
        axiomChecker = new AxiomChecker(pathToAxioms);
        reader = new BufferedReader(new FileReader(pathToProof), 8 * 1024);
        writer = new BufferedWriter(new FileWriter(pathToProof + ".log"));
    }

    private boolean isMp(Expression expression) throws IOException {
        for (int i = approved.size() - 1; i >= 0; i--){
            if (approved.get(i).getOperator() == '-'){
                if (approved.get(i).getOperands(2).equals(expression)){
                    for (int j = approved.size() - 1; j >= 0; j--){
                        if (j != i){
                            if (approved.get(j).equals(approved.get(i).getOperands(1))){
                                writer.write("(" + Integer.toString(lineNumber) + ") " + line + " (M.P. " + Integer.toString(j + 1) +
                                    ", " + Integer.toString(i + 1) + ")\n");
                                return true;
                            }
                        }
                    }
                }
            }
        }
        writer.write("(" + Integer.toString(lineNumber) + ") " + line + "( Not approved)\n");
        return false;
    }

    private int isAssumption(Expression expression){
        for (int i = 0; i < assumptions.size(); i++){
            if (expression.equals(assumptions.get(i))){
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
    }

    public boolean check() throws IOException {
        readAssumptions();
        boolean answer = false;
        lineNumber = 1;
        while ((line = reader.readLine()) != null){
            Expression expression = parser.parse(line);
            int isAxiom = axiomChecker.isAxiom(expression);
            int isAssumption = isAssumption(expression);
            if (isAxiom == -1 && isAssumption == -1){
                if (isMp(expression)){
                    if (toProve.equals(expression)) answer = true;
                    approved.add(expression);
                } else {
                    answer = false;
                }
            } else {
                if (isAssumption == -1) {
                    writer.write("(" + Integer.toString(lineNumber) + ") " + line + " (Axiom scheme " + Integer.toString(isAxiom + 1) + ")\n");
                } else {
                    writer.write("(" + Integer.toString(lineNumber) + ") " + line + " (Assumption " + Integer.toString(isAssumption + 1) + ")\n");
                }
                if (toProve.equals(expression)) answer = true;
                approved.add(expression);
            }
            lineNumber++;
        }
        writer.close();
        return answer;
    }
}