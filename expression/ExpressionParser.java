package expression;


public class ExpressionParser {

    private static int implPriority = Integer.MAX_VALUE - 1;
    private static final int LowestPriority = Integer.MAX_VALUE;
    private static int position;

    public static Expression parse(String str) {
        Expression answer =  solve(splitToTokens(str), LowestPriority);
        implPriority = Integer.MAX_VALUE - 1;
        position = 0;
        return answer;
    }

    private static Expression solve(String[] tokens, int priority){
        Expression answ = null;
        while (position < tokens.length) {
            if (tokens[position].equals("(") || tokens[position].equals(")")) {
                if (tokens[position].equals("(")) {
                    position++;
                    answ = solve(tokens, LowestPriority);
                }else {
                    if (priority == LowestPriority) position++;
                    return answ;
                }
            } else {
                if (isVar(tokens[position].charAt(0))) {
                    answ = new Variable(tokens[position]);
                    position++;
                } else {
                    if (priority(tokens[position].charAt(0)) < priority || answ == null) {
                        position++;
                        answ = give(tokens[position - 1].charAt(0), answ, solve(tokens, priority(tokens[position - 1].charAt(0))));
                    } else {
                        return answ;
                    }
                }
            }
        }
        return answ;
    }

    private static boolean isVar(char ch){
        return ch >= 'A' && ch <= 'z' || ch >= '0' && ch <= '9';
    }

    private static int priority(char operation){
        if (operation == '!') return 0;
        if (operation == '&') return 1;
        if (operation == '|') return 2;
        if (operation == '-') return implPriority--;
        return LowestPriority;
    }

    private static String[] splitToTokens(String str){
        StringBuilder answ = new StringBuilder();
        for (int i = 0; i < str.length();) {
            if (str.charAt(i) == ' ') {
                i++;
                continue;
            }
            if(!isVar(str.charAt(i))){
                answ.append(str.charAt(i));
                if(str.charAt(i) == '-') i++;
                i++;
            } else {
                while (isVar(str.charAt(i))) {
                    answ.append(str.charAt(i++));
                    if (i == str.length()) break;
                }
            }
            answ.append(' ');
        }
        answ.trimToSize();
        return answ.toString().split("[ ]+");
    }

    private static Expression give(char ch, Expression x, Expression y) {
        switch (ch) {
            case '&':
                return new Conjunction(x, y);
            case '|':
                return new Disjunction(x, y);
            case '-':
                return new Implication(x, y);
            case '!':
                return new Negate(y);
        }
        return null;
    }
}