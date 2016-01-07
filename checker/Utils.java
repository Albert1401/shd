package checker;

import expression.Expression;
import expression.ExpressionParser;
import expression.Implication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public final class Utils {
    private static ArrayList<Expression> axioms = new ArrayList<>();
    private static HashMap<String, Expression> map = new HashMap<>();

    public static final class MP {
        public int left;
        public int all;

        public MP(int left, int all) {
            this.left = left;
            this.all = all;
        }
    }

    public static final class Head{
        public Head(ArrayList<Expression> assumptions, Expression toProve) {
            this.assumptions = assumptions;
            this.toProve = toProve;
        }

        public ArrayList<Expression> assumptions;
        public Expression toProve;
    }

    static {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("axioms.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                axioms.add(ExpressionParser.parse(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static MP isModusPonens(ArrayList<Expression> approved, Expression expression) {
        for (int i = approved.size() - 1; i >= 0; i--) {
            if (approved.get(i).getOperator() == '-') {
                if (approved.get(i).getOperands(2).equals(expression)) {
                    for (int j = approved.size() - 1; j >= 0; j--) {
                        if (j != i) {
                            if (approved.get(j).equals(approved.get(i).getOperands(1))) {
                                return new MP(j, i);
                            }
                        }
                    }
                }
            }
        }
        return new MP(-1, -1);
    }

    public static int isAssumption(ArrayList<Expression> assumptions, Expression expression) {
        for (int i = 0; i < assumptions.size(); i++) {
            if (expression.equals(assumptions.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int isAxiom(Expression expression) {
        for (int i = 0; i < axioms.size(); i++) {
            if (is(i, expression)) {
                return i;
            }
        }
        return -1;
    }

    public static Head readHead(String s){
        Scanner scanner = new Scanner(s);
        ArrayList<Expression> assumptions = new ArrayList<>();
        Expression toProve;

        while (scanner.hasNext()){
            assumptions.add(ExpressionParser.parse(scanner.next()));;
        }
        toProve = assumptions.remove(assumptions.size() - 1);
        return new Head(assumptions, toProve);
    }

    public static void writeHead(Writer writer, Head head) throws IOException {
        for (int i = 0; i < head.assumptions.size(); i++){
            writer.write(head.assumptions.get(i) + (i == 0 ? "" : ","));
        }

        writer.write("|-" + head.toProve.toString() + '\n');
    }

    private static boolean dfs(Expression axiom, Expression expression) {
        if (axiom.getOperator() == 'v') {
            if (map.containsKey(axiom.toString())) {
                return map.get(axiom.toString()).equals(expression);
            } else {
                map.put(axiom.toString(), expression);
                return true;
            }
        } else {
            if (axiom.getOperator() != expression.getOperator()) {
                return false;
            } else {
                if (axiom.getOperator() == '!') {
                    return dfs(axiom.getOperands(0), expression.getOperands(0));
                } else {
                    return dfs(axiom.getOperands(1), expression.getOperands(1))
                            && dfs(axiom.getOperands(2), expression.getOperands(2));
                }
            }
        }
    }

    private static boolean is(int axiomIndex, Expression expression) {
        map.clear();
        return dfs(axioms.get(axiomIndex), expression);
    }

}
