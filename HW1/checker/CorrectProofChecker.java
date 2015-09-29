package checker;

import expression.Expression;
import expression.ExpressionParser;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

/**
 * Created by ClitCommander on 9/28/2015.
 */

public class CorrectProofChecker{
    private Vector<Expression> approved = new Vector<>();
    private ExpressionParser parser = new ExpressionParser();
    private AxiomChecker axiomChecker;


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
        for (int i = 0; i < approved.size(); i++){
            if (approved.elementAt(i).getOperator() == '-'){
                if (approved.elementAt(i).getOperands(2).isEqual(expression)){
                    for (int j = 0; j < approved.size(); j++){
                        if (j != i){
                            if (approved.elementAt(j).isEqual(approved.elementAt(i).getOperands(1))){
                                writer.write("(" + Integer.toString(lineNumber) + ") " + line + " (M.P. " + Integer.toString(i + 1) +
                                    ", " + Integer.toString(j + 1) + ")\n");
                                return true;
                            }
                        }
                    }
                }
            }
        }
        writer.write("(" + Integer.toString(lineNumber) + ") " + line + "( He dokazano)\n");
        return false;
    }

    public boolean check() throws IOException {
        lineNumber = 1;
        while ((line = reader.readLine()) != null){
            Expression expression = parser.parse(line);
            int isAxiom = axiomChecker.isAxiom(expression);
            if (isAxiom == -1){
                if (isMp(expression)){
                    approved.add(expression);
                } else {
                    return false;
                }
            } else {
                writer.write("(" + Integer.toString(lineNumber) + ") " + line + " (Cx. akc. " + Integer.toString(isAxiom + 1) + ")\n");
                approved.add(expression);
            }
            lineNumber++;
        }
        writer.close();
        return true;
    }

}
