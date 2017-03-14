package checker;

import expression.Expression;
import expression.ExpressionParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class FileProofChecker {
    private ArrayList<Expression> assumptions = new ArrayList<>();
    private ArrayList<Expression> approved = new ArrayList<>();
    private Expression toProve;

    private BufferedReader reader;
    private BufferedWriter writer;

    private PrintStream logStream;


    public FileProofChecker(String path, PrintStream logStream) throws IOException {
        this.logStream = logStream;
        reader = new BufferedReader(new FileReader(path), 8 * 1024);
        writer = new BufferedWriter(new FileWriter(path + ".log"));
    }

    private void readAssumptions() throws IOException {
        Scanner scanner = new Scanner(reader.readLine());
        scanner.useDelimiter(",|\\|\\-");

        while (scanner.hasNext()){
            assumptions.add(ExpressionParser.parse(scanner.next()));
        }
        toProve = assumptions.remove(assumptions.size() - 1);
    }

    public void writeAssumptions() throws IOException{
        for (int i = 0; i < assumptions.size(); i++){
            if (i != 0) writer.write(", ");
            writer.write(assumptions.get(i).toString());
        }
        writer.write("|-" + toProve + '\n');
    }

    public boolean check() throws IOException {
        readAssumptions();
        writeAssumptions();
        boolean proved = false;
        int errors = 0;

        int lineNumber = 1;
        String line;
        while ((line = reader.readLine()) != null){
            Expression expression = ExpressionParser.parse(line);

            int isAxiom = Utils.isAxiom(expression);
            int isAssumption = Utils.isAssumption(assumptions, expression);
            Utils.MP mpcheck = Utils.isModusPonens(approved, expression);

            writer.write("(" + Integer.toString(lineNumber) + ") " + line);
            if (isAxiom == -1 && isAssumption == -1 && mpcheck.left == -1){
                writer.write(" (Not approved)");
                errors++;
            } else {
                if (mpcheck.left != -1){
                    writer.write(" (M.P. " + Integer.toString(mpcheck.left + 1) + ", " + Integer.toString(mpcheck.all + 1) + ")");
                } else {
                    if (isAssumption != -1) {
                        writer.write(" (Assumption " + Integer.toString(isAssumption + 1) + ")");
                    } else {
                        writer.write(" (Axiom scheme " + Integer.toString(isAxiom + 1) + ")");
                    }
                }
                if (toProve.equals(expression)) proved = true;
                approved.add(expression);
            }
            writer.write('\n');
            lineNumber++;
        }

        if (logStream != null) {
            logStream.println("Expression " + (proved ? "proved" : "not prooved"));
            logStream.println("Proof has " + errors + " errors");
        }

        reader.close();
        writer.close();

        return proved && errors == 0;
    }
}