package org.ruleml.translation.p2ruleml;

import java.io.IOException;
import java.io.OutputStreamWriter;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author edmonluan@gmail.com (Meng Luan)
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Too many arguments!");
        }
        ANTLRInputStream in;
        try {
            if (args.length == 0) {
                in = new ANTLRInputStream(System.in);
            } else {
                in = new ANTLRFileStream(args[0]);
            }
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
            return;
        }
        final PParser parser = new PParser(new CommonTokenStream(new PLexer(in)));
        final ParseTree ptree = parser.start();
        final PTranslatingVisitor visitor =
            new PTranslatingVisitor(new OutputStreamWriter(System.out), parser);
        visitor.visit(ptree);
    }

}
