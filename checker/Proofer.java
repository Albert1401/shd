package checker;

import expression.*;

import java.io.*;
import java.util.*;

public class Proofer {
    private BufferedWriter writer;
    private ProofChanger proofChanger = new ProofChanger();
    private PatternWriter patternWriter = new PatternWriter(System.out::println);

    public Proofer() throws IOException {
        writer = new BufferedWriter(new FileWriter("proof.txt"));
    }

    private void dfs(Expression e, Map<String, Boolean> vars) {
        if (e.getOperator() == 'v') {
            vars.putIfAbsent(e.toString(), false);
            return;
        }
        if (e.getOperator() == '!') {
            dfs(e.getOperands(0), vars);
        } else {
            dfs(e.getOperands(1), vars);
            dfs(e.getOperands(2), vars);
        }
    }



    private Map<String, Boolean> findVars(Expression e) {
        Map<String, Boolean> vars = new HashMap<>();
        dfs(e, vars);
        return vars;
    }

    public boolean make(Expression e) throws IOException {
        Map<String, Boolean> vars = findVars(e);
        Map.Entry<String, Boolean>[] b = new Map.Entry[vars.size()];
        int k = 0;
        for (Map.Entry<String, Boolean> entry : vars.entrySet()) {
            b[k++] = entry;
        }

        for (long i = 0; i < 1 << vars.size(); i++) {
            for (int j = 0; j < vars.size(); j++) {
                b[j].setValue((i & (1 << j)) != 0);
            }
            if (!e.evaluate(vars)) {
                return false;
            }
        }

        ArrayList<Expression> forDeductionStatements = new ArrayList<>();
        Utils.writeHead(writer, new Utils.Head(new ArrayList<>(), e));

        for (long i = 0; i < 1 << vars.size(); i++) {
            ArrayList<Expression> simpleproof = new ArrayList<>();
            for (int j = 0; j < vars.size(); j++) {
                boolean toSet = (i & (1 << j)) == 0;
                b[j].setValue(toSet);
            }

            final ArrayList<Expression> assumptions = new ArrayList<>();
            for (k = 0; k < b.length; k++) {
                Expression assumption = b[k].getValue() ? new Variable(b[k].getKey()) : new Negate(new Variable(b[k].getKey()));
                assumptions.add(assumption);
                simpleproof.add(assumption);
            }

            makeProof(e, vars, simpleproof);

            for (k = b.length - 1; k >= 0; k--) {
                final Expression mainAssumption = assumptions.remove(assumptions.size() - 1);
                final ArrayList<Expression> changed = new ArrayList<>();
                final ArrayList<Expression> csip = simpleproof;

                proofChanger.setIO(new ProofInput() {
                    int i = 0;
                    @Override
                    public ArrayList<Expression> getAssumptions() {
                        return assumptions;
                    }

                    @Override
                    public Expression getMainAssumption() {
                        return mainAssumption;
                    }

                    @Override
                    public Expression nextLine() throws IOException {
                        if (i == csip.size()){
                            return null;
                        }
                        return csip.get(i++);
                    }
                }, changed::add);
                proofChanger.change();

                simpleproof = changed;
            }

            for (Expression expression : simpleproof) {
                writer.write(expression.toString() + '\n');
            }
            forDeductionStatements.add(simpleproof.get(simpleproof.size() - 1));
        }
        patternWriter.setProofOutput(expression -> writer.write(expression.toString() + '\n'));

        for (Map.Entry<String, Boolean> aB : b) {
            patternWriter.useHelp1(new Variable(aB.getKey()));
        }

        ArrayList<Expression> newStatements = new ArrayList<>();
        for (int i = 0; i < vars.size(); i++) {
            while (forDeductionStatements.size() > 1) {
                newStatements.add(forDeductionStatements.get(0).getOperands(2));

                patternWriter.setProofOutput(expression -> writer.write(expression.toString() + '\n'));
                patternWriter.useHelp2(forDeductionStatements.get(0).getOperands(1), forDeductionStatements.get(0).getOperands(2));

                forDeductionStatements.remove(0);
                forDeductionStatements.remove(0);
            }
            ArrayList<Expression> co = forDeductionStatements;
            forDeductionStatements = newStatements;
            newStatements = co;
        }
        writer.close();
        return true;
    }

    private void makeProof(Expression e, Map<String, Boolean> args, ArrayList<Expression> simpleproof) throws IOException {
        if (e.getOperator() == 'v') {
            return;
        }
        if (e.getOperator() == '!') {
            makeProof(e.getOperands(0), args, simpleproof);
            loadPatternProof(e, args, simpleproof);
        } else {
            makeProof(e.getOperands(1), args, simpleproof);
            makeProof(e.getOperands(2), args, simpleproof);
            loadPatternProof(e, args, simpleproof);
        }
    }

    private void loadPatternProof(Expression e, Map<String, Boolean> args, ArrayList<Expression> simpleproof) throws IOException {
        patternWriter.setProofOutput(simpleproof::add);
        if (e.getOperator() == '!') {
            patternWriter.useUn(e.getOperands(0), e.getOperands(0).evaluate(args));
        } else {
            patternWriter.useBin(e.getOperands(1), e.getOperands(2), e.getOperator(),
                    e.getOperands(1).evaluate(args), e.getOperands(2).evaluate(args));
        }
    }
}
