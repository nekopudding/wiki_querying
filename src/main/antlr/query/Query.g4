grammar Query;


@header {
package query;
}

@members {
    // This method makes the lexer or parser stop running if it encounters
    // invalid input and throw a RuntimeException.
    public void reportErrorsAsExceptions() {
        //removeErrorListeners();

        addErrorListener(new ExceptionThrowingErrorListener());
    }

    private static class ExceptionThrowingErrorListener
                                              extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer,
                Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            throw new RuntimeException(msg);
        }
    }
}

LPAREN : '(';
RPAREN : ')';
ASC : 'asc';
DESC : 'desc';
AND: 'and' ;
OR: 'or' ;
TITLE: 'title' ;
AUTHOR: 'author' ;
CATEGORY: 'category' ;
WHITESPACE : [ \t\r\n]+ -> skip ;
STRING : '\'' ( ~'\'' | '\\\'' )* '\'' ;

query : 'get' item 'where' condition sorted? EOF;
condition : LPAREN condition AND condition RPAREN | LPAREN condition OR condition RPAREN | simple_condition;
simple_condition : TITLE 'is' STRING | AUTHOR 'is' STRING | CATEGORY 'is' STRING;
item : 'page' | 'author' | 'category';
sorted : ASC | DESC;


