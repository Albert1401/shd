package checker;

import expression.Expression;
import expression.ExpressionParser;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;



public class CorrectProofChecker{
    private Vector<Expression> assumptions = new Vector<>();
    private Vector<Expression> approved = new Vector<>();
    private ExpressionParser parser = new ExpressionParser();
    private AxiomChecker axiomChecker;
    private Expression toProve;

    private BufferedReader reader;
    private BufferedWriter writer;

    protected String line;
    protected int lineNumber;

    public CorrectProofChecker(String pathToAxioms, String pathToProof) throws IOException {
        try {
            reader = new BufferedReader(new FileReader(pathToProof), 8 * 1024);
        } catch (IOException e) {
            System.out.println("Couldn't find test file");
        }
        try {
            axiomChecker = new AxiomChecker(pathToAxioms);
        } catch (IOException e){
            System.out.println("Couldn't find axioms file. \"axioms.txt\" must be in same directory");
        }
        writer = new BufferedWriter(new FileWriter(pathToProof + ".log"));
    }

    private boolean isMp(Expression expression) throws IOException {
        for (int i = approved.size() - 1; i >= 0; i--){
            if (approved.elementAt(i).getOperator() == '-'){
                if (approved.elementAt(i).getOperands(2).equals(expression)){
                    for (int j = approved.size() - 1; j >= 0; j--){
                        if (j != i){
                            if (approved.elementAt(j).equals(approved.elementAt(i).getOperands(1))){
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
            if (expression.equals(assumptions.elementAt(i))){
                return i;
            }
        }
        return -1;
    }

    private void readAssumptions() throws IOException {
        String line = reader.readLine();
        writer.write("Task: " + line + '\n');
        for (int l = 0, r = 0; r < line.length(); r++) {
            if (line.charAt(r) == ',') {
                assumptions.add(parser.parse(line.substring(l, r)));
                l = r + 1;
            }
            if (line.charAt(r) == '|' && line.charAt(r + 1) == '-') {
                if (r != 0) {
                    assumptions.add(parser.parse(line.substring(l, r)));
                }
                toProve = parser.parse(line.substring(r + 2));
            }
        }
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