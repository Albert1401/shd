package checker;

import expression.Expression;

import java.io.IOException;
import java.util.ArrayList;


public class ProofChanger {

    private PatternWriter patternWriter;
    private ProofInput input;

    public ProofChanger(ProofInput input, ProofOutput output){
        this.input = input;
        patternWriter = new PatternWriter(output);
    }

    public ProofChanger() {};

    public void setIO(ProofInput input, ProofOutput output) {
        this.input = input;
        if (patternWriter == null) {
            patternWriter = new PatternWriter(output);
        } else {
            patternWriter.setProofOutput(output);
        }
    }

    public void change() throws IOException {
        if (input == null || patternWriter == null){
            throw new IllegalStateException("Using without setting input and output");
        }

        ArrayList<Expression> assumptions = input.getAssumptions();
        Expression mainAssumption = input.getMainAssumption();
        ArrayList<Expression> approved = new ArrayList<>();

        Expression expression;
        while((expression = input.nextLine()) != null){
            int isAxiom = Utils.isAxiom(expression);
            int isAssumption = Utils.isAssumption(assumptions, expression);

            if (expression.equals(mainAssumption)){
                patternWriter.forMainAssumption(mainAssumption);
            } else {
                if (isAxiom == -1 && isAssumption == -1){
                    Utils.MP mpcheck = Utils.isModusPonens(approved, expression);
                    patternWriter.forMP(approved.get(mpcheck.left), approved.get(mpcheck.all).getOperands(2), mainAssumption);
                } else {
                    patternWriter.forAxiomsAndAssumptions(expression, mainAssumption);
                }
            }
            approved.add(expression);
        }
    }
}